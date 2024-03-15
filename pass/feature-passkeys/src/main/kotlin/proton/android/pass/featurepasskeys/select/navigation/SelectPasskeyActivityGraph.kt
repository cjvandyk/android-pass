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

package proton.android.pass.featurepasskeys.select.navigation

import androidx.navigation.NavGraphBuilder
import proton.android.pass.common.api.None
import proton.android.pass.commonui.impl.ui.bottomsheet.itemoptions.ItemOptionsBottomSheet
import proton.android.pass.featureauth.impl.AuthNavigation
import proton.android.pass.featureauth.impl.EnterPin
import proton.android.pass.featureauth.impl.authGraph
import proton.android.pass.featurepasskeys.select.presentation.SelectPasskeyActionAfterAuth
import proton.android.pass.featurepasskeys.select.ui.app.SelectPasskeyEvent
import proton.android.pass.featurepasskeys.select.ui.bottomsheet.selectpasskey.selectPasskeyBottomsheetGraph
import proton.android.pass.featuresearchoptions.impl.SearchOptionsNavigation
import proton.android.pass.featuresearchoptions.impl.SortingBottomsheet
import proton.android.pass.featuresearchoptions.impl.SortingLocation
import proton.android.pass.featuresearchoptions.impl.searchOptionsGraph
import proton.android.pass.featureselectitem.navigation.SelectItem
import proton.android.pass.featureselectitem.navigation.SelectItemNavigation
import proton.android.pass.featureselectitem.navigation.SelectItemState
import proton.android.pass.featureselectitem.navigation.selectItemGraph
import proton.android.pass.navigation.api.AppNavigator

@Suppress("CyclomaticComplexMethod", "ComplexMethod", "LongMethod", "LongParameterList")
fun NavGraphBuilder.selectPasskeyActivityGraph(
    appNavigator: AppNavigator,
    domain: String,
    actionAfterAuth: SelectPasskeyActionAfterAuth,
    onEvent: (SelectPasskeyEvent) -> Unit,
    onNavigate: (SelectPasskeyNavigation) -> Unit,
    dismissBottomSheet: (() -> Unit) -> Unit
) {
    authGraph(
        canLogout = false,
        navigation = {
            when (it) {
                AuthNavigation.Back -> onNavigate(SelectPasskeyNavigation.Cancel)
                AuthNavigation.Success -> when (actionAfterAuth) {
                    SelectPasskeyActionAfterAuth.SelectItem -> appNavigator.navigate(SelectItem)
                    SelectPasskeyActionAfterAuth.EmitEvent -> {
                        onEvent(SelectPasskeyEvent.OnAuthPerformed)
                    }
                }
                AuthNavigation.Dismissed -> onNavigate(SelectPasskeyNavigation.Cancel)
                AuthNavigation.Failed -> onNavigate(SelectPasskeyNavigation.Cancel)
                AuthNavigation.SignOut -> {}
                AuthNavigation.ForceSignOut -> onNavigate(SelectPasskeyNavigation.ForceSignOut)
                AuthNavigation.EnterPin -> appNavigator.navigate(EnterPin)
            }
        }
    )

    selectItemGraph(
        state = SelectItemState.Passkey.Select(
            title = domain,
            suggestionsUrl = None
        ),
        onScreenShown = {
            onEvent(SelectPasskeyEvent.OnSelectScreenShown)
        },
        onNavigate = {
            when (it) {
                SelectItemNavigation.AddItem -> {}
                SelectItemNavigation.Cancel -> {
                    onNavigate(SelectPasskeyNavigation.Cancel)
                }

                is SelectItemNavigation.ItemSelected -> {
                    onEvent(SelectPasskeyEvent.OnItemSelected(it.item))
                }

                is SelectItemNavigation.SuggestionSelected -> {
                    onEvent(SelectPasskeyEvent.OnItemSelected(it.item))
                }

                is SelectItemNavigation.SortingBottomsheet ->
                    appNavigator.navigate(
                        SortingBottomsheet,
                        SortingBottomsheet.createNavRoute(
                            location = SortingLocation.Autofill
                        )
                    )

                is SelectItemNavigation.ItemOptions -> {
                    appNavigator.navigate(
                        destination = ItemOptionsBottomSheet,
                        route = ItemOptionsBottomSheet.createRoute(it.shareId, it.itemId)
                    )
                }

                SelectItemNavigation.Upgrade -> {
                    onNavigate(SelectPasskeyNavigation.Upgrade)
                }
            }
        }
    )

    searchOptionsGraph(
        onNavigateEvent = {
            when (it) {
                is SearchOptionsNavigation.SelectSorting -> dismissBottomSheet {
                    appNavigator.navigateBack(comesFromBottomsheet = true)
                }

                SearchOptionsNavigation.Filter -> {
                    throw IllegalStateException("Cannot Filter on SelectPasskey")
                }

                SearchOptionsNavigation.Sorting -> {
                    throw IllegalStateException("Cannot change Sorting on SelectPasskey")
                }

                SearchOptionsNavigation.BulkActions -> {
                    throw IllegalStateException("Cannot perform bulk actions on SelectPasskey")
                }
            }
        }
    )

    selectPasskeyBottomsheetGraph(
        onPasskeySelected = {
            onEvent(SelectPasskeyEvent.OnPasskeySelected(it))
        },
        onDismiss = {
            dismissBottomSheet {
                appNavigator.navigateBack(comesFromBottomsheet = true)
            }
        }
    )
}
