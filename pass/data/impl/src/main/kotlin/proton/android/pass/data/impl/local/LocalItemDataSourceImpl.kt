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

package proton.android.pass.data.impl.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import me.proton.core.domain.entity.UserId
import proton.android.pass.data.api.ItemCountSummary
import proton.android.pass.data.api.repositories.ShareItemCount
import proton.android.pass.data.api.usecases.ItemTypeFilter
import proton.android.pass.data.impl.db.PassDatabase
import proton.android.pass.data.impl.db.entities.ItemEntity
import proton.android.pass.domain.ItemId
import proton.android.pass.domain.ItemState
import proton.android.pass.domain.ShareId
import proton.android.pass.domain.items.ItemCategory
import proton.android.pass.log.api.PassLogger
import javax.inject.Inject

@Suppress("TooManyFunctions")
class LocalItemDataSourceImpl @Inject constructor(
    private val database: PassDatabase
) : LocalItemDataSource {

    override suspend fun upsertItem(item: ItemEntity) = upsertItems(listOf(item))

    override suspend fun upsertItems(items: List<ItemEntity>) =
        database.itemsDao().insertOrUpdate(*items.toTypedArray())

    override fun observeItemsForShares(
        userId: UserId,
        shareIds: List<ShareId>,
        itemState: ItemState?,
        filter: ItemTypeFilter
    ): Flow<List<ItemEntity>> = if (filter == ItemTypeFilter.All) {
        database.itemsDao().observeAllForShares(
            userId = userId.id,
            shareIds = shareIds.map { it.id },
            itemState = itemState?.value
        )
    } else {
        database.itemsDao().observeAllForShare(
            userId = userId.id,
            shareIds = shareIds.map { it.id },
            itemState = itemState?.value,
            itemType = filter.value()
        )
    }

    override fun observeItems(
        userId: UserId,
        itemState: ItemState?,
        filter: ItemTypeFilter
    ): Flow<List<ItemEntity>> = if (filter == ItemTypeFilter.All) {
        database.itemsDao().observeAllForAddress(userId.id, itemState?.value)
    } else {
        database.itemsDao().observeAllForAddress(userId.id, itemState?.value, filter.value())
    }

    override fun observePinnedItems(userId: UserId, filter: ItemTypeFilter): Flow<List<ItemEntity>> =
        if (filter == ItemTypeFilter.All) {
            database.itemsDao().observeAllPinnedItems(userId.id)
        } else {
            database.itemsDao().observeAllPinnedItems(userId.id, filter.value())
        }

    override fun observeAllPinnedItemsForShares(
        userId: UserId,
        filter: ItemTypeFilter,
        shareIds: List<ShareId>
    ): Flow<List<ItemEntity>> = if (filter == ItemTypeFilter.All) {
        database.itemsDao().observeAllPinnedItemsForShares(
            userId = userId.id,
            shareIds = shareIds.map { it.id }
        )
    } else {
        database.itemsDao().observeAllPinnedItemsForShares(
            userId = userId.id,
            itemType = filter.value(),
            shareIds = shareIds.map { it.id }
        )
    }

    override fun observeItem(shareId: ShareId, itemId: ItemId): Flow<ItemEntity> = database.itemsDao()
        .observeById(
            shareId = shareId.id,
            itemId = itemId.id
        )

    override suspend fun getById(shareId: ShareId, itemId: ItemId): ItemEntity? =
        database.itemsDao().getById(shareId.id, itemId.id)

    override suspend fun getByIdList(shareId: ShareId, itemIds: List<ItemId>): List<ItemEntity> =
        database.itemsDao().getByIdList(shareId.id, itemIds.map { it.id })

    override suspend fun setItemState(
        shareId: ShareId,
        itemId: ItemId,
        itemState: ItemState
    ) = database.itemsDao().setItemState(shareId.id, itemId.id, itemState.value)

    override suspend fun setItemStates(
        shareId: ShareId,
        itemIds: List<ItemId>,
        itemState: ItemState
    ) = database.itemsDao().setItemStates(shareId.id, itemIds.map(ItemId::id), itemState.value)

    override suspend fun getTrashedItems(userId: UserId): List<ItemEntity> =
        database.itemsDao().getItemsWithState(userId.id, ItemState.Trashed.value)

    override suspend fun delete(shareId: ShareId, itemId: ItemId): Boolean {
        PassLogger.i(TAG, "Deleting item [shareId=${shareId.id}] [itemId=${itemId.id}]")
        return database.itemsDao().delete(shareId.id, itemId.id) > 0
    }

    override suspend fun deleteList(shareId: ShareId, itemIds: List<ItemId>): Boolean = if (itemIds.isEmpty()) {
        true
    } else {
        PassLogger.i(
            TAG,
            "Deleting items [shareId=${shareId.id}] [itemIds=${itemIds.map { it.id }}]"
        )
        database.itemsDao().deleteList(shareId.id, itemIds.map(ItemId::id)) > 0
    }

    override suspend fun hasItemsForShare(userId: UserId, shareId: ShareId): Boolean =
        database.itemsDao().countItems(userId.id, shareId.id) > 0

    override fun observeItemCountSummary(
        userId: UserId,
        shareIds: List<ShareId>,
        itemState: ItemState?
    ): Flow<ItemCountSummary> = database.itemsDao()
        .itemSummary(userId.id, shareIds.map { it.id }, itemState?.value)
        .map { values ->
            val logins =
                values.firstOrNull { it.itemKind == ItemCategory.Login.value }?.itemCount ?: 0
            val aliases =
                values.firstOrNull { it.itemKind == ItemCategory.Alias.value }?.itemCount ?: 0
            val notes =
                values.firstOrNull { it.itemKind == ItemCategory.Note.value }?.itemCount ?: 0
            val creditCards =
                values.firstOrNull { it.itemKind == ItemCategory.CreditCard.value }?.itemCount
                    ?: 0
            ItemCountSummary(
                total = logins + aliases + notes + creditCards,
                login = logins,
                alias = aliases,
                note = notes,
                creditCard = creditCards
            )
        }

    override suspend fun updateLastUsedTime(
        shareId: ShareId,
        itemId: ItemId,
        now: Long
    ) {
        database.itemsDao().updateLastUsedTime(shareId.id, itemId.id, now)
    }

    override fun observeItemCount(shareIds: List<ShareId>): Flow<Map<ShareId, ShareItemCount>> = database.itemsDao()
        .countItemsForShares(shareIds.map { it.id })
        .map { values ->
            shareIds.associate { shareId ->
                val rowForShare = values.firstOrNull { it.shareId == shareId.id }
                if (rowForShare == null) {
                    shareId to ShareItemCount(0, 0)
                } else {
                    shareId to ShareItemCount(
                        activeItems = rowForShare.activeItemCount,
                        trashedItems = rowForShare.trashedItemCount
                    )
                }
            }
        }

    override suspend fun getItemByAliasEmail(userId: UserId, aliasEmail: String): ItemEntity? =
        database.itemsDao().getItemByAliasEmail(userId.id, aliasEmail)

    override suspend fun getItemsPendingForTotpMigration(): List<ItemEntity> =
        database.itemsDao().getItemsPendingForTotpMigration()

    override suspend fun getItemsPendingForPasskeyMigration(): List<ItemEntity> =
        database.itemsDao().getItemsPendingForPasskeyMigration()

    override fun observeAllItemsWithTotp(userId: UserId): Flow<List<ItemWithTotp>> = database.itemsDao()
        .observeAllItemsWithTotp(userId.id)
        .map { items -> items.map { it.toItemWithTotp() } }

    override fun observeItemsWithTotpForShare(userId: UserId, shareId: ShareId): Flow<List<ItemWithTotp>> =
        database.itemsDao()
            .observeItemsWithTotpForShare(userId = userId.id, shareId = shareId.id)
            .map { items -> items.map { it.toItemWithTotp() } }

    override fun observeAllItemsWithPasskeys(userId: UserId): Flow<List<ItemEntity>> =
        database.itemsDao().observeAllItemsWithPasskeys(userId = userId.id)

    override fun observeItemsWithPasskeys(userId: UserId, shareIds: List<ShareId>): Flow<List<ItemEntity>> =
        database.itemsDao().observeItemsWithPasskeys(
            userId = userId.id,
            shareIds = shareIds.map(ShareId::id)
        )

    private fun ItemEntity.toItemWithTotp(): ItemWithTotp = ItemWithTotp(
        shareId = ShareId(shareId),
        itemId = ItemId(id),
        createTime = Instant.fromEpochSeconds(createTime)
    )

    private fun ItemTypeFilter.value(): Int = when (this) {
        ItemTypeFilter.Logins -> ItemCategory.Login.value
        ItemTypeFilter.Aliases -> ItemCategory.Alias.value
        ItemTypeFilter.Notes -> ItemCategory.Note.value
        ItemTypeFilter.CreditCards -> ItemCategory.CreditCard.value
        ItemTypeFilter.All -> throw IllegalStateException("Cannot call value to ItemTypeFilter.All")
    }

    companion object {
        private const val TAG = "LocalItemDataSourceImpl"
    }

}
