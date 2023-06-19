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

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun SortingBottomSheet(
    modifier: Modifier = Modifier,
    onNavigateEvent: (SortingNavigation) -> Unit,
    viewModel: SortingBottomSheetViewModel = hiltViewModel()
) {
    val sortingType by viewModel.state.collectAsStateWithLifecycle()
    SortingBottomSheetContents(
        modifier = modifier,
        sortingType = sortingType,
        onSortingTypeSelected = {
            viewModel.onSortingTypeChanged(it)
            onNavigateEvent(SortingNavigation.SelectSorting)
        }
    )
}

sealed interface SortingNavigation {
    object SelectSorting : SortingNavigation
}

