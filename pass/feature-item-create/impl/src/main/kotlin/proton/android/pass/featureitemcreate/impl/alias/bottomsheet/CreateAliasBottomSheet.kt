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

package proton.android.pass.featureitemcreate.impl.alias.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.pass.featureitemcreate.impl.alias.AliasDraftSavedState
import proton.android.pass.featureitemcreate.impl.alias.CloseScreenEvent
import proton.android.pass.featureitemcreate.impl.alias.CreateAliasNavigation
import proton.android.pass.featureitemcreate.impl.common.ShareUiState

@Composable
fun CreateAliasBottomSheet(
    modifier: Modifier = Modifier,
    itemTitle: String,
    onNavigate: (CreateAliasNavigation) -> Unit,
    viewModel: CreateAliasBottomSheetViewModel = hiltViewModel()
) {
    LaunchedEffect(itemTitle) {
        viewModel.setInitialState(itemTitle)
    }

    val state by viewModel.createAliasUiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.baseAliasUiState.closeScreenEvent) {
        if (state.baseAliasUiState.closeScreenEvent is CloseScreenEvent.Close) {
            onNavigate(CreateAliasNavigation.CloseBottomsheet)
        }
    }

    val isAliasDraftSaved = state.baseAliasUiState.isAliasDraftSavedState
    if (isAliasDraftSaved is AliasDraftSavedState.Success) {
        LaunchedEffect(state.shareUiState) {
            if (state.shareUiState is ShareUiState.Success) {
                val event = CreateAliasNavigation.CreatedFromBottomsheet(
                    alias = isAliasDraftSaved.aliasItem.aliasToBeCreated ?: "",
                )
                onNavigate(event)
                viewModel.resetAliasDraftSavedState()
            }
        }
    }

    CreateAliasBottomSheetContent(
        modifier = modifier,
        state = state.baseAliasUiState,
        onCancel = { onNavigate(CreateAliasNavigation.Close) },
        onConfirm = {
            val shareUiState = state.shareUiState
            if (shareUiState is ShareUiState.Success) {
                viewModel.createAlias(shareUiState.currentVault.vault.shareId)
            }
        },
        onPrefixChanged = { viewModel.onPrefixChange(it) },
        onSuffixChanged = { viewModel.onSuffixChange(it) },
        onMailboxesChanged = { viewModel.onMailboxesChanged(it) },
        onNavigate = onNavigate
    )
}
