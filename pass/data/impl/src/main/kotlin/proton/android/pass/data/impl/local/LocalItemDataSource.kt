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
import kotlinx.datetime.Instant
import me.proton.core.domain.entity.UserId
import proton.android.pass.data.api.ItemCountSummary
import proton.android.pass.data.api.repositories.ShareItemCount
import proton.android.pass.data.api.usecases.ItemTypeFilter
import proton.android.pass.data.impl.db.entities.ItemEntity
import proton.android.pass.domain.ItemId
import proton.android.pass.domain.ItemState
import proton.android.pass.domain.ShareId

data class ItemWithTotp(
    val shareId: ShareId,
    val itemId: ItemId,
    val createTime: Instant
)

interface LocalItemDataSource {
    suspend fun upsertItem(item: ItemEntity)
    suspend fun upsertItems(items: List<ItemEntity>)

    fun observeItemsForShares(
        userId: UserId,
        shareIds: List<ShareId>,
        itemState: ItemState?,
        filter: ItemTypeFilter
    ): Flow<List<ItemEntity>>

    fun observeItems(
        userId: UserId,
        itemState: ItemState?,
        filter: ItemTypeFilter
    ): Flow<List<ItemEntity>>

    fun observePinnedItems(userId: UserId, filter: ItemTypeFilter): Flow<List<ItemEntity>>

    fun observeAllPinnedItemsForShares(
        userId: UserId,
        filter: ItemTypeFilter,
        shareIds: List<ShareId>,
    ): Flow<List<ItemEntity>>

    fun observeItem(
        shareId: ShareId,
        itemId: ItemId,
    ): Flow<ItemEntity>

    suspend fun getById(shareId: ShareId, itemId: ItemId): ItemEntity?
    suspend fun getByIdList(shareId: ShareId, itemIds: List<ItemId>): List<ItemEntity>
    suspend fun setItemState(shareId: ShareId, itemId: ItemId, itemState: ItemState)
    suspend fun getTrashedItems(userId: UserId): List<ItemEntity>
    suspend fun delete(shareId: ShareId, itemId: ItemId): Boolean
    suspend fun hasItemsForShare(userId: UserId, shareId: ShareId): Boolean
    fun observeItemCountSummary(
        userId: UserId,
        shareIds: List<ShareId>,
        itemState: ItemState?
    ): Flow<ItemCountSummary>

    suspend fun updateLastUsedTime(shareId: ShareId, itemId: ItemId, now: Long)
    fun observeItemCount(shareIds: List<ShareId>): Flow<Map<ShareId, ShareItemCount>>
    suspend fun getItemByAliasEmail(userId: UserId, aliasEmail: String): ItemEntity?

    suspend fun getItemsPendingForTotpMigration(): List<ItemEntity>
    fun observeAllItemsWithTotp(userId: UserId): Flow<List<ItemWithTotp>>
    fun observeItemsWithTotpForShare(userId: UserId, shareId: ShareId): Flow<List<ItemWithTotp>>
}
