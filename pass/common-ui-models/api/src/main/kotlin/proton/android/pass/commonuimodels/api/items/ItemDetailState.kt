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

package proton.android.pass.commonuimodels.api.items

import androidx.compose.runtime.Stable
import kotlinx.datetime.Instant
import proton.android.pass.common.api.Option
import proton.android.pass.common.api.PasswordStrength
import proton.android.pass.commonuimodels.api.UIPasskeyContent
import proton.android.pass.domain.AliasMailbox
import proton.android.pass.domain.ItemContents
import proton.android.pass.domain.ItemId
import proton.android.pass.domain.ItemState
import proton.android.pass.domain.ShareId
import proton.android.pass.domain.Totp
import proton.android.pass.domain.Vault
import proton.android.pass.domain.items.ItemCategory

@Stable
sealed interface ItemDetailState {

    val itemContents: ItemContents

    val itemId: ItemId

    val shareId: ShareId

    val isItemPinned: Boolean

    val itemVault: Vault?

    val itemCategory: ItemCategory

    val itemCreatedAt: Instant

    val itemModifiedAt: Instant

    val itemLastAutofillAtOption: Option<Instant>

    val itemRevision: Long

    val itemState: ItemState

    fun update(itemContents: ItemContents): ItemDetailState

    @Stable
    data class Alias(
        override val itemContents: ItemContents.Alias,
        override val itemId: ItemId,
        override val shareId: ShareId,
        override val isItemPinned: Boolean,
        override val itemVault: Vault?,
        override val itemCreatedAt: Instant,
        override val itemModifiedAt: Instant,
        override val itemLastAutofillAtOption: Option<Instant>,
        override val itemRevision: Long,
        override val itemState: ItemState,
        val mailboxes: List<AliasMailbox>
    ) : ItemDetailState {

        override val itemCategory: ItemCategory = ItemCategory.Alias

        override fun update(itemContents: ItemContents): ItemDetailState {
            return if (itemContents is ItemContents.Alias) {
                this.copy(itemContents = itemContents)
            } else {
                this
            }
        }

    }

    @Stable
    data class CreditCard(
        override val itemContents: ItemContents.CreditCard,
        override val itemId: ItemId,
        override val shareId: ShareId,
        override val isItemPinned: Boolean,
        override val itemVault: Vault?,
        override val itemCreatedAt: Instant,
        override val itemModifiedAt: Instant,
        override val itemLastAutofillAtOption: Option<Instant>,
        override val itemRevision: Long,
        override val itemState: ItemState
    ) : ItemDetailState {

        override val itemCategory: ItemCategory = ItemCategory.CreditCard

        override fun update(itemContents: ItemContents): ItemDetailState {
            return if (itemContents is ItemContents.CreditCard) {
                this.copy(itemContents = itemContents)
            } else {
                this
            }
        }
    }

    @Stable
    data class Identity(
        override val itemContents: ItemContents.Identity,
        override val itemId: ItemId,
        override val shareId: ShareId,
        override val isItemPinned: Boolean,
        override val itemVault: Vault?,
        override val itemCreatedAt: Instant,
        override val itemModifiedAt: Instant,
        override val itemLastAutofillAtOption: Option<Instant>,
        override val itemRevision: Long,
        override val itemState: ItemState
    ) : ItemDetailState {

        override val itemCategory: ItemCategory = ItemCategory.Identity

        override fun update(itemContents: ItemContents): ItemDetailState {
            return if (itemContents is ItemContents.Identity) {
                this.copy(itemContents = itemContents)
            } else {
                this
            }
        }

    }

    @Stable
    data class Login(
        override val itemContents: ItemContents.Login,
        override val itemId: ItemId,
        override val shareId: ShareId,
        override val isItemPinned: Boolean,
        override val itemVault: Vault?,
        override val itemCreatedAt: Instant,
        override val itemModifiedAt: Instant,
        override val itemLastAutofillAtOption: Option<Instant>,
        override val itemRevision: Long,
        override val itemState: ItemState,
        val canLoadExternalImages: Boolean,
        val passwordStrength: PasswordStrength,
        val primaryTotp: Totp?,
        val secondaryTotps: Map<String, Totp?>,
        val passkeys: List<UIPasskeyContent>,
        val isUsernameSplitEnabled: Boolean
    ) : ItemDetailState {

        override val itemCategory: ItemCategory = ItemCategory.Login

        override fun update(itemContents: ItemContents): ItemDetailState {
            return if (itemContents is ItemContents.Login) {
                this.copy(itemContents = itemContents)
            } else {
                this
            }
        }

    }

    @Stable
    data class Note(
        override val itemContents: ItemContents.Note,
        override val itemId: ItemId,
        override val shareId: ShareId,
        override val isItemPinned: Boolean,
        override val itemVault: Vault?,
        override val itemCreatedAt: Instant,
        override val itemModifiedAt: Instant,
        override val itemLastAutofillAtOption: Option<Instant>,
        override val itemRevision: Long,
        override val itemState: ItemState
    ) : ItemDetailState {

        override val itemCategory: ItemCategory = ItemCategory.Note

        override fun update(itemContents: ItemContents): ItemDetailState {
            return if (itemContents is ItemContents.Note) {
                this.copy(itemContents = itemContents)
            } else {
                this
            }
        }

    }

    @Stable
    data class Unknown(
        override val itemContents: ItemContents.Unknown,
        override val itemId: ItemId,
        override val shareId: ShareId,
        override val isItemPinned: Boolean,
        override val itemVault: Vault?,
        override val itemCreatedAt: Instant,
        override val itemModifiedAt: Instant,
        override val itemLastAutofillAtOption: Option<Instant>,
        override val itemRevision: Long,
        override val itemState: ItemState
    ) : ItemDetailState {

        override val itemCategory: ItemCategory = ItemCategory.Unknown

        override fun update(itemContents: ItemContents): ItemDetailState {
            return if (itemContents is ItemContents.Unknown) {
                this.copy(itemContents = itemContents)
            } else {
                this
            }
        }

    }

}
