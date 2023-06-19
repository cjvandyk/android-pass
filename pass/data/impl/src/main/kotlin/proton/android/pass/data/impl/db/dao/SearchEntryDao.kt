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

package proton.android.pass.data.impl.db.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.proton.core.data.room.db.BaseDao
import proton.android.pass.data.impl.db.entities.SearchEntryEntity

@Dao
abstract class SearchEntryDao : BaseDao<SearchEntryEntity>() {

    @Query(
        """
        DELETE FROM ${SearchEntryEntity.TABLE} 
        WHERE ${SearchEntryEntity.Columns.USER_ID} = :userId
      """
    )
    abstract suspend fun deleteAll(userId: String)

    @Query(
        """
        DELETE FROM ${SearchEntryEntity.TABLE} 
        WHERE ${SearchEntryEntity.Columns.SHARE_ID} = :shareId
      """
    )
    abstract suspend fun deleteAllByShare(shareId: String)

    @Query(
        """
        DELETE FROM ${SearchEntryEntity.TABLE} 
        WHERE ${SearchEntryEntity.Columns.SHARE_ID} = :shareId
          AND ${SearchEntryEntity.Columns.ITEM_ID} = :itemId
      """
    )
    abstract suspend fun deleteEntry(shareId: String, itemId: String)

    @Query(
        """
        SELECT * FROM ${SearchEntryEntity.TABLE} 
        WHERE ${SearchEntryEntity.Columns.USER_ID} = :userId
        ORDER BY ${SearchEntryEntity.Columns.CREATE_TIME} ASC
        """
    )
    abstract fun observeAll(userId: String): Flow<List<SearchEntryEntity>>

    @Query(
        """
        SELECT * FROM ${SearchEntryEntity.TABLE} 
        WHERE ${SearchEntryEntity.Columns.SHARE_ID} = :shareId
        ORDER BY ${SearchEntryEntity.Columns.CREATE_TIME} ASC
        """
    )
    abstract fun observeAllByShare(shareId: String): Flow<List<SearchEntryEntity>>
}
