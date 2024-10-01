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

package proton.android.pass.featureselectitem.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import proton.android.pass.common.api.None
import proton.android.pass.common.api.Some
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.Spacing
import proton.android.pass.composecomponents.impl.buttons.PassFloatingActionButton
import proton.android.pass.composecomponents.impl.buttons.TransparentTextButton
import proton.android.pass.composecomponents.impl.pinning.PinCarousel
import proton.android.pass.composecomponents.impl.text.Text
import proton.android.pass.composecomponents.impl.topbar.SearchTopBar
import proton.android.pass.composecomponents.impl.topbar.iconbutton.BackArrowCircleIconButton
import proton.android.pass.featureselectitem.R
import proton.android.pass.featureselectitem.navigation.SelectItemNavigation
import proton.android.pass.composecomponents.impl.R as CompR

@Suppress("ComplexMethod")
@Composable
internal fun SelectItemScreenContent(
    modifier: Modifier = Modifier,
    uiState: SelectItemUiState,
    onEvent: (SelectItemEvent) -> Unit,
    onNavigate: (SelectItemNavigation) -> Unit
) {
    val verticalScroll = rememberLazyListState()
    var showFab by remember { mutableStateOf(true) }
    val isPinningOrSearch =
        remember(uiState.pinningUiState.inPinningMode, uiState.searchUiState.inSearchMode) {
            uiState.pinningUiState.inPinningMode || uiState.searchUiState.inSearchMode
        }

    LaunchedEffect(verticalScroll) {
        var prev = 0
        snapshotFlow { verticalScroll.firstVisibleItemIndex }
            .collect {
                showFab = it <= prev
                prev = it
            }
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            if (uiState.listUiState.displayCreateButton) {
                PassFloatingActionButton(
                    visible = showFab,
                    onClick = { onNavigate(SelectItemNavigation.AddItem) }
                )
            }
        },
        topBar = {
            val placeholder = if (!uiState.pinningUiState.inPinningMode) {
                when (uiState.searchUiState.searchInMode) {
                    SearchInMode.AllVaults -> stringResource(id = R.string.topbar_search_query)
                    SearchInMode.OldestVaults -> stringResource(id = R.string.topbar_search_query_oldest_vaults)
                    SearchInMode.Uninitialized -> stringResource(id = R.string.topbar_search_query_uninitialized)
                }
            } else {
                stringResource(id = R.string.topbar_search_pinned_items)
            }

            SearchTopBar(
                placeholderText = placeholder,
                searchQuery = uiState.searchUiState.searchQuery,
                inSearchMode = uiState.searchUiState.inSearchMode,
                onSearchQueryChange = { onEvent(SelectItemEvent.SearchQueryChange(it)) },
                onStopSearch = { onEvent(SelectItemEvent.StopSearching) },
                onEnterSearch = { onEvent(SelectItemEvent.EnterSearch) },
                drawerIcon = {
                    BackArrowCircleIconButton(
                        color = PassTheme.colors.loginInteractionNorm,
                        backgroundColor = PassTheme.colors.loginInteractionNormMinor1,
                        onUpClick = {
                            when {
                                uiState.searchUiState.inSearchMode -> {
                                    onEvent(SelectItemEvent.StopSearching)
                                }

                                uiState.pinningUiState.inPinningMode -> {
                                    onEvent(SelectItemEvent.StopPinningMode)
                                }

                                else -> {
                                    onNavigate(SelectItemNavigation.Cancel)
                                }
                            }
                        }
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            with(uiState.listUiState.accountSwitchState) {
                if (accountList.size > 1) {
                    var isExpanded by remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        val selectedAccountText =
                            accountList.find { it.userId == selectedAccount.value() }?.email
                                ?: stringResource(R.string.all_accounts_item, accountList.size)

                        TransparentTextButton(
                            text = selectedAccountText,
                            color = PassTheme.colors.textNorm,
                            suffixIcon = CompR.drawable.ic_chevron_tiny_down,
                            onClick = { isExpanded = !isExpanded }
                        )
                        DropdownMenu(
                            modifier = Modifier
                                .background(PassTheme.colors.inputBackgroundNorm),
                            expanded = isExpanded,
                            offset = DpOffset(x = Spacing.medium, y = 0.dp),
                            onDismissRequest = { isExpanded = false }
                        ) {
                            if (selectedAccount is Some) {
                                DropdownMenuItem(
                                    onClick = {
                                        isExpanded = false
                                        onEvent(SelectItemEvent.SwitchAccount(None))
                                    }
                                ) {
                                    Text.Body1Regular(
                                        text = stringResource(
                                            R.string.all_accounts_item,
                                            accountList.size
                                        )
                                    )
                                }
                            }
                            accountList.filterNot { it.userId == selectedAccount.value() }.forEach {
                                DropdownMenuItem(
                                    onClick = {
                                        isExpanded = false
                                        onEvent(SelectItemEvent.SwitchAccount(Some(it.userId)))
                                    }
                                ) {
                                    Text.Body1Regular(text = it.email)
                                }
                            }
                        }
                    }
                }
            }

            if (!isPinningOrSearch) {
                PinCarousel(
                    modifier = Modifier.height(48.dp),
                    list = uiState.pinningUiState.unFilteredItems,
                    canLoadExternalImages = uiState.listUiState.canLoadExternalImages,
                    onItemClick = {
                        onEvent(SelectItemEvent.ItemClicked(it, false))
                    },
                    onSeeAllClick = { onEvent(SelectItemEvent.SeeAllPinned) }
                )

                if (uiState.pinningUiState.unFilteredItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Spacing.medium))
                }
            }
            SelectItemList(
                uiState = uiState,
                scrollState = verticalScroll,
                onScrolledToTop = { onEvent(SelectItemEvent.ScrolledToTop) },
                onItemOptionsClicked = { onEvent(SelectItemEvent.ItemOptionsClicked(it)) },
                onItemClicked = { item, isLongClick ->
                    onEvent(
                        SelectItemEvent.ItemClicked(
                            item,
                            isLongClick
                        )
                    )
                },
                onNavigate = onNavigate
            )
        }
    }
}
