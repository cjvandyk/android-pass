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

package proton.android.pass.data.impl.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import me.proton.core.crypto.common.keystore.EncryptedByteArray
import me.proton.core.user.data.entity.UserEntity

@Entity(
    tableName = InviteEntity.TABLE,
    primaryKeys = [InviteEntity.Columns.TOKEN],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [ExternalColumns.USER_ID],
            childColumns = [InviteEntity.Columns.USER_ID],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class InviteEntity(
    @ColumnInfo(name = Columns.TOKEN)
    val token: String,
    @ColumnInfo(name = Columns.USER_ID, index = true)
    val userId: String,
    @ColumnInfo(name = Columns.INVITER_EMAIL)
    val inviterEmail: String,
    @ColumnInfo(name = Columns.MEMBER_COUNT)
    val memberCount: Int,
    @ColumnInfo(name = Columns.ITEM_COUNT)
    val itemCount: Int,
    @ColumnInfo(name = Columns.REMINDER_COUNT)
    val reminderCount: Int,
    @ColumnInfo(name = Columns.SHARE_CONTENT)
    val shareContent: String,
    @ColumnInfo(name = Columns.SHARE_CONTENT_KEY_ROTATION)
    val shareContentKeyRotation: Long,
    @ColumnInfo(name = Columns.SHARE_CONTENT_FORMAT_VERSION)
    val shareContentFormatVersion: Int,

    @ColumnInfo(name = Columns.CREATE_TIME)
    val createTime: Long,

    // Reencrypted with local key
    @ColumnInfo(name = Columns.ENCRYPTED_CONTENT)
    val encryptedContent: EncryptedByteArray,
) {
    object Columns {
        const val TOKEN = "token"
        const val USER_ID = "user_id"
        const val INVITER_EMAIL = "inviter_email"
        const val MEMBER_COUNT = "member_count"
        const val ITEM_COUNT = "item_count"
        const val REMINDER_COUNT = "reminder_count"
        const val SHARE_CONTENT = "share_content"
        const val SHARE_CONTENT_KEY_ROTATION = "share_content_key_rotation"
        const val SHARE_CONTENT_FORMAT_VERSION = "share_content_format_version"
        const val CREATE_TIME = "create_time"
        const val ENCRYPTED_CONTENT = "encrypted_content"
    }

    companion object {
        const val TABLE = "InviteEntity"
    }
}
