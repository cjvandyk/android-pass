/*
 * Copyright (c) 2023 Proton AG
 * This file is part of Proton AG and Proton Pass.
 *
 * Proton Pass is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Proton Pass is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Proton Pass.  If not, see <https://www.gnu.org/licenses/>.
 */

package proton.android.pass.data.impl.repositories

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.proton.core.domain.entity.UserId
import proton.android.pass.crypto.api.context.EncryptionContext
import proton.android.pass.crypto.api.context.EncryptionContextProvider
import proton.android.pass.data.api.repositories.InviteRepository
import proton.android.pass.data.api.usecases.ObserveHasConfirmedInvite
import proton.android.pass.data.impl.crypto.EncryptInviteKeys
import proton.android.pass.data.impl.crypto.ReencryptInviteContents
import proton.android.pass.data.impl.db.entities.InviteEntity
import proton.android.pass.data.impl.db.entities.InviteKeyEntity
import proton.android.pass.data.impl.extensions.toDomain
import proton.android.pass.data.impl.local.InviteAndKeysEntity
import proton.android.pass.data.impl.local.LocalInviteDataSource
import proton.android.pass.data.impl.remote.RemoteInviteDataSource
import proton.android.pass.data.impl.requests.AcceptInviteRequest
import proton.android.pass.data.impl.responses.PendingInviteResponse
import proton.android.pass.domain.InviteRecommendations
import proton.android.pass.domain.InviteToken
import proton.android.pass.domain.PendingInvite
import proton.android.pass.domain.ShareId
import proton.android.pass.log.api.PassLogger
import proton_pass_vault_v1.VaultV1
import javax.inject.Inject

class InviteRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteInviteDataSource,
    private val localDatasource: LocalInviteDataSource,
    private val encryptionContextProvider: EncryptionContextProvider,
    private val reencryptInviteContents: ReencryptInviteContents,
    private val encryptInviteKeys: EncryptInviteKeys,
    private val observeHasConfirmedInvite: ObserveHasConfirmedInvite
) : InviteRepository {
    override fun observeInvites(userId: UserId): Flow<List<PendingInvite>> = localDatasource
        .observeAllInvites(userId)
        .map { entities ->
            encryptionContextProvider.withEncryptionContext {
                entities.map { it.toDomain(this@withEncryptionContext) }
            }
        }

    override suspend fun refreshInvites(userId: UserId): Boolean = withContext(Dispatchers.IO) {
        PassLogger.i(TAG, "Refresh invites started")
        val deferredRemoteInvites: Deferred<List<PendingInviteResponse>> =
            async { remoteDataSource.fetchInvites(userId) }
        deferredRemoteInvites.invokeOnCompletion {
            if (it != null) {
                PassLogger.w(TAG, it)
            } else {
                PassLogger.i(TAG, "Fetched remote invites")
            }
        }
        val deferredLocalInvites: Deferred<List<InviteEntity>> =
            async { localDatasource.observeAllInvites(userId).first() }
        deferredLocalInvites.invokeOnCompletion {
            if (it != null) {
                PassLogger.w(TAG, it)
            } else {
                PassLogger.i(TAG, "Retrieved local invites")
            }
        }
        val sources = awaitAll(deferredRemoteInvites, deferredLocalInvites)
        val remoteInvites = sources[0].filterIsInstance<PendingInviteResponse>()
        PassLogger.i(TAG, "Fetched ${remoteInvites.size} remote invites")

        val localInvites = sources[1].filterIsInstance<InviteEntity>()
        PassLogger.i(TAG, "Retrieved ${localInvites.size} local invites")

        // Remove deleted invites
        val deletedInvites = localInvites.filter { local ->
            remoteInvites.none { remote -> remote.inviteToken == local.token }
        }
        if (deletedInvites.isNotEmpty()) {
            PassLogger.i(TAG, "Deleting ${deletedInvites.size} invites")
            localDatasource.removeInvites(deletedInvites)
        }

        // Insert new invites
        val newInvites = remoteInvites.filter { remote ->
            localInvites.none { local -> local.token == remote.inviteToken }
        }
        val hasNewInvites = newInvites.isNotEmpty()

        // Detect if we have a new confirmed invite
        if (newInvites.any { it.fromNewUser }) {
            observeHasConfirmedInvite.send(true)
        }

        val invitesWithKeys: List<InviteAndKeysEntity> = newInvites
            .map { invite -> inviteAndKeysEntity(invite, userId) }

        if (invitesWithKeys.isNotEmpty()) {
            PassLogger.i(TAG, "Inserting ${invitesWithKeys.size} invites")
            localDatasource.storeInvites(invitesWithKeys)
        }
        hasNewInvites
    }

    private suspend fun inviteAndKeysEntity(
        invite: PendingInviteResponse,
        userId: UserId
    ): InviteAndKeysEntity {
        val vaultData = invite.vaultData
        val reencryptedInviteContent = reencryptInviteContents(userId, invite)
        val inviteEntity = InviteEntity(
            token = invite.inviteToken,
            userId = userId.id,
            inviterEmail = invite.inviterEmail,
            invitedEmail = invite.invitedEmail,
            invitedAddressId = invite.invitedAddressId,
            memberCount = vaultData.memberCount,
            itemCount = vaultData.itemCount,
            reminderCount = invite.remindersSent,
            shareContent = vaultData.content,
            shareContentKeyRotation = vaultData.contentKeyRotation,
            shareContentFormatVersion = vaultData.contentFormatVersion,
            createTime = invite.createTime,
            encryptedContent = reencryptedInviteContent,
            fromNewUser = invite.fromNewUser
        )

        val inviteKeys = invite.keys.map { key ->
            InviteKeyEntity(
                inviteToken = invite.inviteToken,
                key = key.key,
                keyRotation = key.keyRotation,
                createTime = invite.createTime
            )
        }

        return InviteAndKeysEntity(
            inviteEntity = inviteEntity,
            inviteKeys = inviteKeys
        )
    }

    override suspend fun acceptInvite(userId: UserId, inviteToken: InviteToken): ShareId {
        val invite = localDatasource.getInviteWithKeys(userId, inviteToken).value()
            ?: throw IllegalStateException("Could not find the invite: ${inviteToken.value}")

        val keys = encryptInviteKeys(
            userId = userId,
            invite = invite
        )
        val request = AcceptInviteRequest(keys)
        val responseShare = remoteDataSource.acceptInvite(userId, inviteToken, request)
        localDatasource.removeInvite(userId, inviteToken)
        return ShareId(responseShare.shareId)
    }

    override suspend fun rejectInvite(userId: UserId, inviteToken: InviteToken) {
        remoteDataSource.rejectInvite(userId, inviteToken)
        localDatasource.removeInvite(userId, inviteToken)
    }

    override fun observeInviteRecommendations(
        userId: UserId,
        shareId: ShareId,
        lastToken: String?,
        startsWith: String?
    ): Flow<InviteRecommendations> = flow {
        val result = remoteDataSource.fetchInviteRecommendations(
            userId,
            shareId,
            lastToken,
            startsWith
        )
        emit(result.toDomain())
    }

    private fun InviteEntity.toDomain(encryptionContext: EncryptionContext): PendingInvite {
        val content = encryptionContext.decrypt(encryptedContent)
        val decoded = VaultV1.Vault.parseFrom(content)
        return PendingInvite(
            inviteToken = InviteToken(token),
            inviterEmail = inviterEmail,
            invitedAddressId = invitedAddressId,
            memberCount = memberCount,
            itemCount = itemCount,
            name = decoded.name,
            icon = decoded.display.icon.toDomain(),
            color = decoded.display.color.toDomain(),
            fromNewUser = fromNewUser
        )
    }

    companion object {
        private const val TAG = "InviteRepositoryImpl"
    }
}
