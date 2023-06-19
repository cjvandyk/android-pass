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

package proton.android.pass.featurehome.impl.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import proton.android.pass.common.api.LoadingResult
import proton.android.pass.common.api.asLoadingResult
import proton.android.pass.commonuimodels.api.ShareUiModelWithItemCount
import proton.android.pass.data.api.usecases.ObserveVaultsWithItemCount
import proton.android.pass.featuresearchoptions.api.SearchOptionsRepository
import proton.android.pass.featuresearchoptions.api.VaultSelectionOption
import proton.android.pass.log.api.PassLogger
import javax.inject.Inject

@HiltViewModel
class VaultDrawerViewModel @Inject constructor(
    observeVaultsWithItemCount: ObserveVaultsWithItemCount,
    private val searchOptionsRepository: SearchOptionsRepository
) : ViewModel() {

    val drawerUiState: StateFlow<VaultDrawerUiState> = combine(
        observeVaultsWithItemCount().asLoadingResult(),
        searchOptionsRepository.observeVaultSelectionOption()
    ) { shares, selectedVault ->
        when (shares) {
            LoadingResult.Loading -> VaultDrawerUiState(
                vaultSelection = selectedVault,
                shares = persistentListOf(),
                totalTrashedItems = 0
            )

            is LoadingResult.Error -> {
                PassLogger.w(TAG, shares.exception, "Cannot retrieve all shares")
                VaultDrawerUiState(
                    vaultSelection = selectedVault,
                    shares = persistentListOf(),
                    totalTrashedItems = 0
                )
            }

            is LoadingResult.Success -> {
                val sharesWithCount = shares.data
                    .map {
                        ShareUiModelWithItemCount(
                            id = it.vault.shareId,
                            name = it.vault.name,
                            activeItemCount = it.activeItemCount,
                            trashedItemCount = it.trashedItemCount,
                            color = it.vault.color,
                            icon = it.vault.icon,
                            isPrimary = it.vault.isPrimary
                        )
                    }
                    .toImmutableList()
                val totalTrashed = shares.data.sumOf { it.trashedItemCount }
                VaultDrawerUiState(
                    vaultSelection = selectedVault,
                    shares = sharesWithCount,
                    totalTrashedItems = totalTrashed
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = VaultDrawerUiState(
            VaultSelectionOption.AllVaults,
            persistentListOf(),
            0
        )
    )

    fun setVaultSelection(vaultSelection: VaultSelectionOption) =
        searchOptionsRepository.setVaultSelectionOption(vaultSelection)

    companion object {
        private const val TAG = "VaultDrawerViewModel"
    }
}
