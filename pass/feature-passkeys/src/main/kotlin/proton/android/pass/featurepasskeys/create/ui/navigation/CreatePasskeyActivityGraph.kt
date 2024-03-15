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

package proton.android.pass.featurepasskeys.create.ui.navigation

import androidx.navigation.NavGraphBuilder
import proton.android.pass.commonui.impl.ui.bottomsheet.itemoptions.ItemOptionsBottomSheet
import proton.android.pass.commonui.impl.ui.bottomsheet.itemoptions.ItemOptionsNavigation
import proton.android.pass.commonui.impl.ui.bottomsheet.itemoptions.itemOptionsGraph
import proton.android.pass.featureauth.impl.AuthNavigation
import proton.android.pass.featureauth.impl.EnterPin
import proton.android.pass.featureauth.impl.authGraph
import proton.android.pass.featureitemcreate.impl.alias.CreateAliasBottomSheet
import proton.android.pass.featureitemcreate.impl.alias.CreateAliasNavigation
import proton.android.pass.featureitemcreate.impl.alias.createAliasGraph
import proton.android.pass.featureitemcreate.impl.bottomsheets.customfield.AddCustomFieldBottomSheet
import proton.android.pass.featureitemcreate.impl.bottomsheets.customfield.CustomFieldOptionsBottomSheet
import proton.android.pass.featureitemcreate.impl.common.KEY_VAULT_SELECTED
import proton.android.pass.featureitemcreate.impl.dialogs.CustomFieldNameDialog
import proton.android.pass.featureitemcreate.impl.dialogs.EditCustomFieldNameDialog
import proton.android.pass.featureitemcreate.impl.login.BaseLoginNavigation
import proton.android.pass.featureitemcreate.impl.login.CreateLogin
import proton.android.pass.featureitemcreate.impl.login.CreateLoginNavigation
import proton.android.pass.featureitemcreate.impl.login.bottomsheet.aliasoptions.AliasOptionsBottomSheet
import proton.android.pass.featureitemcreate.impl.login.bottomsheet.aliasoptions.CLEAR_ALIAS_NAV_PARAMETER_KEY
import proton.android.pass.featureitemcreate.impl.login.createUpdateLoginGraph
import proton.android.pass.featureitemcreate.impl.totp.CameraTotp
import proton.android.pass.featureitemcreate.impl.totp.PhotoPickerTotp
import proton.android.pass.featurepasskeys.create.presentation.CreatePasskeyNavState
import proton.android.pass.featurepasskeys.create.ui.app.CreatePasskeyEvent
import proton.android.pass.featurepasskeys.create.ui.app.CreatePasskeyNavigation
import proton.android.pass.featurepassword.impl.GeneratePasswordBottomsheet
import proton.android.pass.featurepassword.impl.GeneratePasswordBottomsheetModeValue
import proton.android.pass.featurepassword.impl.GeneratePasswordNavigation
import proton.android.pass.featurepassword.impl.dialog.mode.PasswordModeDialog
import proton.android.pass.featurepassword.impl.dialog.separator.WordSeparatorDialog
import proton.android.pass.featurepassword.impl.generatePasswordBottomsheetGraph
import proton.android.pass.featuresearchoptions.impl.SearchOptionsNavigation
import proton.android.pass.featuresearchoptions.impl.SortingBottomsheet
import proton.android.pass.featuresearchoptions.impl.SortingLocation
import proton.android.pass.featuresearchoptions.impl.searchOptionsGraph
import proton.android.pass.featureselectitem.navigation.SelectItem
import proton.android.pass.featureselectitem.navigation.SelectItemNavigation
import proton.android.pass.featureselectitem.navigation.selectItemGraph
import proton.android.pass.featurevault.impl.VaultNavigation
import proton.android.pass.featurevault.impl.bottomsheet.select.SelectVaultBottomsheet
import proton.android.pass.featurevault.impl.vaultGraph
import proton.android.pass.navigation.api.AppNavigator

@Suppress("LongMethod", "CyclomaticComplexMethod", "ComplexMethod", "ThrowsCount")
fun NavGraphBuilder.createPasskeyActivityGraph(
    appNavigator: AppNavigator,
    navState: CreatePasskeyNavState.Ready,
    onEvent: (CreatePasskeyEvent) -> Unit,
    onNavigate: (CreatePasskeyNavigation) -> Unit,
    dismissBottomSheet: (() -> Unit) -> Unit
) {
    authGraph(
        canLogout = false,
        navigation = {
            when (it) {
                AuthNavigation.Back -> onNavigate(CreatePasskeyNavigation.Cancel)
                AuthNavigation.Success -> appNavigator.navigate(SelectItem)
                AuthNavigation.Dismissed -> onNavigate(CreatePasskeyNavigation.Cancel)
                AuthNavigation.Failed -> onNavigate(CreatePasskeyNavigation.Cancel)
                AuthNavigation.SignOut -> {}
                AuthNavigation.ForceSignOut -> onNavigate(CreatePasskeyNavigation.ForceSignOut)
                AuthNavigation.EnterPin -> appNavigator.navigate(EnterPin)
            }
        }
    )

    selectItemGraph(
        state = navState.selectItemState,
        onScreenShown = {},
        onNavigate = {
            when (it) {
                SelectItemNavigation.AddItem -> {
                    appNavigator.navigate(
                        destination = CreateLogin,
                        route = CreateLogin.createNavRoute()
                    )
                }
                SelectItemNavigation.Cancel -> {
                    onNavigate(CreatePasskeyNavigation.Cancel)
                }

                is SelectItemNavigation.ItemSelected -> {
                    onEvent(CreatePasskeyEvent.OnItemSelected(it.item))
                }

                is SelectItemNavigation.SuggestionSelected -> {
                    onEvent(CreatePasskeyEvent.OnItemSelected(it.item))
                }

                is SelectItemNavigation.SortingBottomsheet -> appNavigator.navigate(
                    SortingBottomsheet,
                    SortingBottomsheet.createNavRoute(
                        location = SortingLocation.Autofill
                    )
                )

                is SelectItemNavigation.ItemOptions -> appNavigator.navigate(
                    destination = ItemOptionsBottomSheet,
                    route = ItemOptionsBottomSheet.createRoute(it.shareId, it.itemId)
                )

                SelectItemNavigation.Upgrade -> {
                    onNavigate(CreatePasskeyNavigation.Upgrade)
                }
            }
        }
    )

    createUpdateLoginGraph(
        initialCreateLoginUiState = navState.createLoginUiState,
        onNavigate = {
            when (it) {
                BaseLoginNavigation.Close -> dismissBottomSheet {
                    appNavigator.navigateBack(comesFromBottomsheet = true)
                }

                is BaseLoginNavigation.CreateAlias -> appNavigator.navigate(
                    destination = CreateAliasBottomSheet,
                    route = CreateAliasBottomSheet.createNavRoute(
                        it.shareId,
                        it.showUpgrade,
                        it.title
                    )
                )

                BaseLoginNavigation.GeneratePassword -> appNavigator.navigate(
                    destination = GeneratePasswordBottomsheet,
                    route = GeneratePasswordBottomsheet.buildRoute(
                        mode = GeneratePasswordBottomsheetModeValue.CancelConfirm
                    )
                )

                is BaseLoginNavigation.OnCreateLoginEvent -> when (val event = it.event) {
                    is CreateLoginNavigation.LoginCreated -> {
                        throw IllegalStateException("Should not invoke this on CreatePasskey")
                    }

                    is CreateLoginNavigation.LoginCreatedWithPasskey -> {
                        onNavigate(CreatePasskeyNavigation.SendResponse(event.createPasskeyResponse))
                    }

                    is CreateLoginNavigation.SelectVault -> {
                        appNavigator.navigate(
                            destination = SelectVaultBottomsheet,
                            route = SelectVaultBottomsheet.createNavRoute(event.shareId)
                        )
                    }
                }

                is BaseLoginNavigation.ScanTotp -> appNavigator.navigate(
                    destination = CameraTotp,
                    route = CameraTotp.createNavRoute(it.index)
                )

                BaseLoginNavigation.Upgrade -> onNavigate(CreatePasskeyNavigation.Upgrade)

                is BaseLoginNavigation.AliasOptions -> appNavigator.navigate(
                    destination = AliasOptionsBottomSheet,
                    route = AliasOptionsBottomSheet.createNavRoute(it.shareId, it.showUpgrade)
                )

                BaseLoginNavigation.DeleteAlias -> appNavigator.navigateBackWithResult(
                    key = CLEAR_ALIAS_NAV_PARAMETER_KEY,
                    value = true
                )

                is BaseLoginNavigation.EditAlias -> {
                    appNavigator.navigate(
                        destination = CreateAliasBottomSheet,
                        route = CreateAliasBottomSheet.createNavRoute(
                            it.shareId,
                            it.showUpgrade,
                            isEdit = true
                        )
                    )
                }

                BaseLoginNavigation.AddCustomField -> appNavigator.navigate(
                    destination = AddCustomFieldBottomSheet
                )

                is BaseLoginNavigation.CustomFieldTypeSelected -> dismissBottomSheet {
                    appNavigator.navigate(
                        destination = CustomFieldNameDialog,
                        route = CustomFieldNameDialog.buildRoute(it.type),
                        backDestination = CreateLogin
                    )
                }

                is BaseLoginNavigation.CustomFieldOptions -> appNavigator.navigate(
                    destination = CustomFieldOptionsBottomSheet,
                    route = CustomFieldOptionsBottomSheet.buildRoute(it.index, it.currentValue)
                )

                is BaseLoginNavigation.EditCustomField -> dismissBottomSheet {
                    appNavigator.navigate(
                        destination = EditCustomFieldNameDialog,
                        route = EditCustomFieldNameDialog.buildRoute(it.index, it.currentValue),
                        backDestination = CreateLogin
                    )
                }

                BaseLoginNavigation.RemovedCustomField -> dismissBottomSheet {
                    appNavigator.navigateBack(comesFromBottomsheet = true)
                }

                // Updates cannot happen
                is BaseLoginNavigation.OnUpdateLoginEvent -> {}
                is BaseLoginNavigation.OpenImagePicker -> appNavigator.navigate(
                    destination = PhotoPickerTotp,
                    route = PhotoPickerTotp.createNavRoute(it.index),
                    backDestination = CreateLogin
                )

                BaseLoginNavigation.TotpCancel -> appNavigator.navigateBack()
                is BaseLoginNavigation.TotpSuccess ->
                    appNavigator.navigateBackWithResult(it.results)
            }
        }
    )

    generatePasswordBottomsheetGraph(
        onNavigate = {
            when (it) {
                GeneratePasswordNavigation.CloseDialog -> appNavigator.navigateBack()
                GeneratePasswordNavigation.DismissBottomsheet -> dismissBottomSheet {
                    appNavigator.navigateBack(comesFromBottomsheet = true)
                }

                GeneratePasswordNavigation.OnSelectWordSeparator -> appNavigator.navigate(
                    destination = WordSeparatorDialog
                )

                GeneratePasswordNavigation.OnSelectPasswordMode -> appNavigator.navigate(
                    destination = PasswordModeDialog
                )
            }
        }
    )

    createAliasGraph(
        onNavigate = {
            when (it) {
                CreateAliasNavigation.Close -> appNavigator.navigateBack()
                CreateAliasNavigation.CloseBottomsheet -> dismissBottomSheet {
                    appNavigator.navigateBack(comesFromBottomsheet = true)
                }

                is CreateAliasNavigation.CreatedFromBottomsheet -> dismissBottomSheet {
                    appNavigator.navigateBack(comesFromBottomsheet = true)
                }


                is CreateAliasNavigation.Created -> {
                    throw IllegalStateException("Cannot create alias from CreatePasskey")
                }

                CreateAliasNavigation.Upgrade -> onNavigate(CreatePasskeyNavigation.Upgrade)
                is CreateAliasNavigation.SelectVault -> {
                    appNavigator.navigate(
                        destination = SelectVaultBottomsheet,
                        route = SelectVaultBottomsheet.createNavRoute(it.shareId)
                    )
                }
            }
        }
    )

    vaultGraph(
        onNavigate = {
            when (it) {
                VaultNavigation.Close -> appNavigator.navigateBack()
                VaultNavigation.Upgrade -> onNavigate(CreatePasskeyNavigation.Upgrade)
                is VaultNavigation.VaultSelected -> dismissBottomSheet {
                    appNavigator.navigateBackWithResult(
                        key = KEY_VAULT_SELECTED,
                        value = it.shareId.id,
                        comesFromBottomsheet = true
                    )
                }

                is VaultNavigation.VaultEdit,
                is VaultNavigation.VaultMigrate,
                is VaultNavigation.VaultRemove,
                is VaultNavigation.VaultShare,
                is VaultNavigation.VaultLeave,
                is VaultNavigation.VaultAccess -> {
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
                    throw IllegalStateException("Cannot Filter on CreatePasskey")
                }

                SearchOptionsNavigation.Sorting -> {
                    throw IllegalStateException("Cannot change Sorting on CreatePasskey")
                }

                SearchOptionsNavigation.BulkActions -> {
                    throw IllegalStateException("Cannot perform bulk actions on CreatePasskey")
                }
            }
        }
    )

    itemOptionsGraph {
        when (it) {
            ItemOptionsNavigation.Close -> dismissBottomSheet {
                appNavigator.navigateBack(comesFromBottomsheet = true)
            }
        }
    }

}
