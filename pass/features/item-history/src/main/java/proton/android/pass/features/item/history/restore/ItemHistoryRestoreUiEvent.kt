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

package proton.android.pass.features.item.history.restore

import proton.android.pass.commonpresentation.api.items.details.domain.ItemDetailsFieldType
import proton.android.pass.commonuimodels.api.UIPasskeyContent
import proton.android.pass.domain.HiddenState
import proton.android.pass.domain.ItemContents
import proton.android.pass.domain.ItemId
import proton.android.pass.domain.ShareId
import proton.android.pass.features.item.history.restore.presentation.ItemHistoryRestoreEvent

internal sealed interface ItemHistoryRestoreUiEvent {
    @JvmInline
    value class OnEventConsumed(val event: ItemHistoryRestoreEvent) : ItemHistoryRestoreUiEvent

    data object OnRestoreClick : ItemHistoryRestoreUiEvent

    @JvmInline
    value class OnRestoreConfirmClick(val contents: ItemContents) : ItemHistoryRestoreUiEvent

    data object OnRestoreCancelClick : ItemHistoryRestoreUiEvent

    data class OnSectionClick(
        val section: String,
        val field: ItemDetailsFieldType.Plain
    ) : ItemHistoryRestoreUiEvent

    data class OnHiddenSectionClick(
        val state: HiddenState,
        val field: ItemDetailsFieldType.Hidden
    ) : ItemHistoryRestoreUiEvent

    data class OnHiddenSectionToggle(
        val state: Boolean,
        val hiddenState: HiddenState,
        val field: ItemDetailsFieldType.Hidden
    ) : ItemHistoryRestoreUiEvent

    data class OnPasskeyClick(
        val shareId: ShareId,
        val itemId: ItemId,
        val passkey: UIPasskeyContent
    ) : ItemHistoryRestoreUiEvent


}
