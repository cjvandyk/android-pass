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

package proton.android.pass.features.item.history.restore.ui.tabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import proton.android.pass.commonui.api.Spacing
import proton.android.pass.commonuimodels.api.items.ItemDetailState
import proton.android.pass.composecomponents.impl.item.details.PassItemDetailsContent
import proton.android.pass.composecomponents.impl.item.details.PassItemDetailsUiEvent
import proton.android.pass.composecomponents.impl.utils.PassItemColors
import proton.android.pass.features.item.history.restore.ItemHistoryRestoreUiEvent
import proton.android.pass.features.item.history.restore.presentation.ItemHistoryRestoreSelection
import proton.android.pass.features.item.history.restore.ui.ItemHistoryRestoreTopBar

@Composable
internal fun ItemHistoryRestoreTab(
    modifier: Modifier = Modifier,
    itemDetailState: ItemDetailState,
    itemColors: PassItemColors,
    onEvent: (ItemHistoryRestoreUiEvent) -> Unit,
    selection: ItemHistoryRestoreSelection
) {
    PassItemDetailsContent(
        modifier = modifier,
        itemDetailState = itemDetailState,
        itemColors = itemColors,
        topBar = {
            ItemHistoryRestoreTopBar(
                colors = itemColors,
                onUpClick = { onEvent(ItemHistoryRestoreUiEvent.OnBackClick) },
                onRestoreClick = { onEvent(ItemHistoryRestoreUiEvent.OnRestoreClick) }
            )
        },
        shouldDisplayItemHistorySection = false,
        shouldDisplayItemHistoryButton = false,
        extraBottomSpacing = Spacing.extraLarge,
        onEvent = { uiEvent ->
            when (uiEvent) {
                is PassItemDetailsUiEvent.OnSectionClick -> ItemHistoryRestoreUiEvent.OnSectionClick(
                    section = uiEvent.section,
                    field = uiEvent.field
                ).also(onEvent)

                is PassItemDetailsUiEvent.OnHiddenSectionClick -> ItemHistoryRestoreUiEvent.OnHiddenSectionClick(
                    state = uiEvent.state,
                    field = uiEvent.field
                ).also(onEvent)

                is PassItemDetailsUiEvent.OnHiddenSectionToggle -> ItemHistoryRestoreUiEvent.OnHiddenSectionToggle(
                    selection = selection,
                    isVisible = uiEvent.isVisible,
                    hiddenState = uiEvent.hiddenState,
                    fieldType = uiEvent.fieldType
                ).also(onEvent)

                is PassItemDetailsUiEvent.OnLinkClick -> ItemHistoryRestoreUiEvent.OnLinkClick(
                    linkUrl = uiEvent.link
                ).also(onEvent)

                is PassItemDetailsUiEvent.OnPasskeyClick -> ItemHistoryRestoreUiEvent.OnPasskeyClick(
                    passkey = uiEvent.passkey
                ).also(onEvent)

                PassItemDetailsUiEvent.OnViewItemHistoryClick -> {
                    // We do nothing since item history widget shouldn't appear on restore screen
                }

                is PassItemDetailsUiEvent.OnSharedVaultClick -> {
                    // We do nothing since we don't allow shared vault management from restore screen
                }
            }
        }
    )
}
