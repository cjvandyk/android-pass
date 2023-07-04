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

package proton.android.featuresearchoptions.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import proton.android.pass.commonui.api.SavedStateHandleProvider
import proton.android.pass.commonui.api.require
import proton.android.pass.featuresearchoptions.api.AutofillSearchOptionsRepository
import proton.android.pass.featuresearchoptions.api.HomeSearchOptionsRepository
import proton.android.pass.featuresearchoptions.api.SearchSortingType
import proton.android.pass.featuresearchoptions.api.SortingOption
import proton.android.pass.navigation.api.SortingTypeNavArgId
import javax.inject.Inject

@HiltViewModel
class SortingBottomSheetViewModel @Inject constructor(
    private val homeSearchOptionsRepository: HomeSearchOptionsRepository,
    private val autofillSearchOptionsRepository: AutofillSearchOptionsRepository,
    savedStateHandle: SavedStateHandleProvider
) : ViewModel() {
    private val sortingType: SearchSortingType = SearchSortingType.valueOf(
        savedStateHandle.get().require(SortingTypeNavArgId.key)
    )

    private val sortingLocation: SortingLocation = SortingLocation.valueOf(
        savedStateHandle.get().require(SortingLocationNavArgId.key)
    )

    val state: StateFlow<SearchSortingType> = MutableStateFlow(sortingType)
        .stateIn(viewModelScope, SharingStarted.Eagerly, sortingType)

    fun onSortingTypeChanged(searchSortingType: SearchSortingType) {
        val value = SortingOption(searchSortingType)
        when (sortingLocation) {
            SortingLocation.Home -> homeSearchOptionsRepository.setSortingOption(value)
            SortingLocation.Autofill -> autofillSearchOptionsRepository.setSortingOption(value)
        }
    }
}
