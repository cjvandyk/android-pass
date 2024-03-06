/*
 * Copyright (c) 2024 Proton AG
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

package proton.android.pass.data.impl.usecases.passkeys

import kotlinx.coroutines.flow.first
import me.proton.core.accountmanager.domain.AccountManager
import proton.android.pass.data.api.repositories.ItemRepository
import proton.android.pass.data.api.usecases.passkeys.StorePasskey
import proton.android.pass.domain.ItemId
import proton.android.pass.domain.ItemType
import proton.android.pass.domain.Passkey
import proton.android.pass.domain.ShareId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorePasskeyImpl @Inject constructor(
    private val accountManager: AccountManager,
    private val itemRepository: ItemRepository
) : StorePasskey {
    override suspend fun invoke(
        shareId: ShareId,
        itemId: ItemId,
        passkey: Passkey
    ) {
        val userId = requireNotNull(accountManager.getPrimaryUserId().first())

        val item = itemRepository.getById(shareId, itemId)
        val itemType = item.itemType
        if (itemType is ItemType.Login) {
            val passkeys = itemType.passkeys.toMutableList()
            val index = passkeys.indexOfFirst { it.id == passkey.id }
            if (index != -1) {
                passkeys[index] = passkey
            } else {
                passkeys.add(passkey)
            }

            itemRepository.addPasskeyToItem(userId, shareId, itemId, passkey)
        }
    }

}
