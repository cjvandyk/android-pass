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

package proton.android.pass.features.item.details.detail.ui

import proton.android.pass.commonpresentation.api.items.details.domain.ItemDetailsFieldType
import proton.android.pass.commonuimodels.api.UIPasskeyContent
import proton.android.pass.domain.HiddenState
import proton.android.pass.domain.ItemId
import proton.android.pass.domain.ItemState
import proton.android.pass.domain.ShareId
import proton.android.pass.domain.items.ItemCategory

internal sealed interface ItemDetailsUiEvent {

    data object OnNavigateBack : ItemDetailsUiEvent

    data class OnEditClicked(
        internal val shareId: ShareId,
        internal val itemId: ItemId,
        internal val itemCategory: ItemCategory
    ) : ItemDetailsUiEvent

    data class OnFieldClicked(
        internal val text: String,
        internal val field: ItemDetailsFieldType.Plain
    ) : ItemDetailsUiEvent

    data class OnHiddenFieldClicked(
        internal val state: HiddenState,
        internal val field: ItemDetailsFieldType.Hidden
    ) : ItemDetailsUiEvent

    data class OnHiddenFieldToggled(
        internal val isVisible: Boolean,
        internal val state: HiddenState,
        internal val fieldType: ItemDetailsFieldType.Hidden
    ) : ItemDetailsUiEvent

    @JvmInline
    value class OnLinkClicked(internal val link: String) : ItemDetailsUiEvent

    @JvmInline
    value class OnPasskeyClicked(internal val passkeyContent: UIPasskeyContent) : ItemDetailsUiEvent

    data class OnViewItemHistoryClicked(
        internal val shareId: ShareId,
        internal val itemId: ItemId
    ) : ItemDetailsUiEvent

    data class OnShareItemClicked(
        internal val shareId: ShareId,
        internal val itemId: ItemId
    ) : ItemDetailsUiEvent

    data class OnSharedVaultClicked(
        internal val sharedVaultId: ShareId,
        internal val itemCategory: ItemCategory
    ) : ItemDetailsUiEvent

    data class OnMenuClicked(
        internal val shareId: ShareId,
        internal val itemId: ItemId,
        internal val itemState: ItemState
    ) : ItemDetailsUiEvent

}
