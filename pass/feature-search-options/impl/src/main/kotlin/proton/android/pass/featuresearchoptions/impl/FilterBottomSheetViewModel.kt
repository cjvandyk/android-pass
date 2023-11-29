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

package proton.android.pass.featuresearchoptions.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.zip
import proton.android.pass.data.api.ItemCountSummary
import proton.android.pass.data.api.usecases.ObserveItemCount
import proton.android.pass.domain.ItemState
import proton.android.pass.featuresearchoptions.api.FilterOption
import proton.android.pass.featuresearchoptions.api.HomeSearchOptionsRepository
import proton.android.pass.featuresearchoptions.api.SearchFilterType
import proton.android.pass.featuresearchoptions.api.VaultSelectionOption
import javax.inject.Inject

@HiltViewModel
class FilterBottomSheetViewModel @Inject constructor(
    private val homeSearchOptionsRepository: HomeSearchOptionsRepository,
    observeItemCount: ObserveItemCount
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<FilterOptionsUIState> = homeSearchOptionsRepository.observeSearchOptions()
        .flatMapLatest {
            when (val vault = it.vaultSelectionOption) {
                VaultSelectionOption.AllVaults -> observeItemCount()
                VaultSelectionOption.Trash -> observeItemCount(
                    itemState = ItemState.Trashed
                )

                is VaultSelectionOption.Vault -> observeItemCount(
                    selectedShareId = vault.shareId
                )
            }.zip(flowOf(it)) { itemCount, searchOptions ->
                itemCount to searchOptions
            }
        }
        .map { (summary, options) ->
            SuccessFilterOptionsUIState(
                filterType = options.filterOption.searchFilterType,
                summary = summary
            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, EmptyFilterOptionsUIState)

    fun onFilterTypeChanged(searchFilterType: SearchFilterType) {
        val value = FilterOption(searchFilterType)
        homeSearchOptionsRepository.setFilterOption(value)
    }
}

sealed interface FilterOptionsUIState
object EmptyFilterOptionsUIState : FilterOptionsUIState
data class SuccessFilterOptionsUIState(
    val filterType: SearchFilterType,
    val summary: ItemCountSummary
) : FilterOptionsUIState
