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

package proton.android.pass.featurevault.impl.bottomsheet.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import proton.android.pass.common.api.LoadingResult
import proton.android.pass.common.api.asLoadingResult
import proton.android.pass.common.api.getOrNull
import proton.android.pass.commonui.api.SavedStateHandleProvider
import proton.android.pass.commonui.api.require
import proton.android.pass.data.api.usecases.CanPerformPaidAction
import proton.android.pass.data.api.usecases.ObserveUpgradeInfo
import proton.android.pass.data.api.usecases.ObserveVaultsWithItemCount
import proton.android.pass.featurevault.impl.VaultSnackbarMessage
import proton.android.pass.log.api.PassLogger
import proton.android.pass.notifications.api.SnackbarDispatcher
import proton.pass.domain.ShareId
import javax.inject.Inject

@HiltViewModel
class SelectVaultViewModel @Inject constructor(
    observeVaultsWithItemCount: ObserveVaultsWithItemCount,
    observeUpgradeInfo: ObserveUpgradeInfo,
    snackbarDispatcher: SnackbarDispatcher,
    savedStateHandle: SavedStateHandleProvider,
    canPerformPaidAction: CanPerformPaidAction
) : ViewModel() {

    private val selected: ShareId = ShareId(savedStateHandle.get().require(SelectedVaultArg.key))

    val state: StateFlow<SelectVaultUiState> = combine(
        observeVaultsWithItemCount().asLoadingResult(),
        observeUpgradeInfo().asLoadingResult(),
        canPerformPaidAction().asLoadingResult()
    ) { vaultsResult, upgradeResult, selectOtherVaultResult ->
        val canSelectOtherVault = selectOtherVaultResult.getOrNull() ?: false

        val showUpgradeMessage = if (canSelectOtherVault) {
            false
        } else {
            upgradeResult.getOrNull()?.isUpgradeAvailable ?: false
        }

        when (vaultsResult) {
            LoadingResult.Loading -> SelectVaultUiState.Loading
            is LoadingResult.Success -> {
                val shares = vaultsResult.data.map { it.vault.shareId }
                if (shares.contains(selected)) {
                    SelectVaultUiState.Success(
                        vaults = vaultsResult.data.toPersistentList(),
                        selected = vaultsResult.data.first { it.vault.shareId == selected },
                        showUpgradeMessage = showUpgradeMessage,
                        canSelectOtherVaults = canSelectOtherVault
                    )
                } else {
                    PassLogger.w(TAG, "Error finding current vault")
                    snackbarDispatcher(VaultSnackbarMessage.CannotFindVaultError)
                    SelectVaultUiState.Error
                }
            }

            is LoadingResult.Error -> {
                PassLogger.w(TAG, vaultsResult.exception, "Error observing vaults")
                snackbarDispatcher(VaultSnackbarMessage.CannotGetVaultListError)
                SelectVaultUiState.Error
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SelectVaultUiState.Uninitialised
        )

    companion object {
        private const val TAG = "SelectVaultViewModel"
    }
}
