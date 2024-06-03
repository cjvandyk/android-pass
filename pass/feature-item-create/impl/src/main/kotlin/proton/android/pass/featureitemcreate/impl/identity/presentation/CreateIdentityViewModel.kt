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

package proton.android.pass.featureitemcreate.impl.identity.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import proton.android.pass.common.api.None
import proton.android.pass.common.api.Option
import proton.android.pass.common.api.Some
import proton.android.pass.common.api.asLoadingResult
import proton.android.pass.common.api.toOption
import proton.android.pass.commonui.api.SavedStateHandleProvider
import proton.android.pass.composecomponents.impl.uievents.IsLoadingState
import proton.android.pass.data.api.repositories.DRAFT_CUSTOM_FIELD_KEY
import proton.android.pass.data.api.repositories.DRAFT_CUSTOM_FIELD_TITLE_KEY
import proton.android.pass.data.api.repositories.DRAFT_IDENTITY_CUSTOM_FIELD_KEY
import proton.android.pass.data.api.repositories.DRAFT_REMOVE_CUSTOM_FIELD_KEY
import proton.android.pass.data.api.repositories.DraftRepository
import proton.android.pass.data.api.usecases.CreateItem
import proton.android.pass.data.api.usecases.ObserveVaultsWithItemCount
import proton.android.pass.data.api.usecases.defaultvault.ObserveDefaultVault
import proton.android.pass.domain.CustomFieldContent
import proton.android.pass.domain.ShareId
import proton.android.pass.featureitemcreate.impl.ItemCreate
import proton.android.pass.featureitemcreate.impl.common.CustomFieldIndexTitle
import proton.android.pass.featureitemcreate.impl.common.OptionShareIdSaver
import proton.android.pass.featureitemcreate.impl.common.ShareUiState
import proton.android.pass.featureitemcreate.impl.common.getShareUiStateFlow
import proton.android.pass.featureitemcreate.impl.identity.presentation.IdentitySnackbarMessage.ItemCreated
import proton.android.pass.featureitemcreate.impl.identity.presentation.IdentitySnackbarMessage.ItemCreationError
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.CustomExtraField
import proton.android.pass.inappreview.api.InAppReviewTriggerMetrics
import proton.android.pass.log.api.PassLogger
import proton.android.pass.navigation.api.CommonOptionalNavArgId
import proton.android.pass.notifications.api.SnackbarDispatcher
import proton.android.pass.telemetry.api.EventItemType
import proton.android.pass.telemetry.api.TelemetryManager
import javax.inject.Inject

@HiltViewModel
class CreateIdentityViewModel @Inject constructor(
    private val createItem: CreateItem,
    private val identityActionsProvider: IdentityActionsProvider,
    private val telemetryManager: TelemetryManager,
    private val inAppReviewTriggerMetrics: InAppReviewTriggerMetrics,
    private val snackbarDispatcher: SnackbarDispatcher,
    private val draftRepository: DraftRepository,
    observeVaults: ObserveVaultsWithItemCount,
    observeDefaultVault: ObserveDefaultVault,
    savedStateHandleProvider: SavedStateHandleProvider
) : ViewModel(), IdentityActionsProvider by identityActionsProvider {

    private val navShareId: Option<ShareId> =
        savedStateHandleProvider.get().get<String>(CommonOptionalNavArgId.ShareId.key)
            .toOption()
            .map(::ShareId)

    init {
        viewModelScope.launch {
            launch { observeNewCustomField() }
            launch { observeRemoveCustomField() }
            launch { observeRenameCustomField() }
        }
    }

    @OptIn(SavedStateHandleSaveableApi::class)
    private var selectedShareIdMutableState: Option<ShareId> by savedStateHandleProvider.get()
        .saveable(stateSaver = OptionShareIdSaver) { mutableStateOf(None) }
    private val selectedShareIdState: Flow<Option<ShareId>> =
        snapshotFlow { selectedShareIdMutableState }
            .filterNotNull()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = None
            )

    private val shareUiState: StateFlow<ShareUiState> = getShareUiStateFlow(
        navShareIdState = flowOf(navShareId),
        selectedShareIdState = selectedShareIdState,
        observeAllVaultsFlow = observeVaults().asLoadingResult(),
        observeDefaultVaultFlow = observeDefaultVault().asLoadingResult(),
        viewModelScope = viewModelScope,
        tag = TAG
    )

    val state: StateFlow<IdentityUiState> = combine(
        shareUiState,
        identityActionsProvider.observeSharedState()
    ) { shareUiState, sharedState ->
        when (shareUiState) {
            is ShareUiState.Error -> IdentityUiState.Error
            is ShareUiState.Loading -> IdentityUiState.Loading
            is ShareUiState.Success -> IdentityUiState.Success(shareUiState, sharedState)
            ShareUiState.NotInitialised -> IdentityUiState.NotInitialised
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = IdentityUiState.NotInitialised
    )

    fun onVaultSelect(shareId: ShareId) {
        selectedShareIdMutableState = Some(shareId)
    }

    fun onSubmit(shareId: ShareId) = viewModelScope.launch {
        if (!identityActionsProvider.isFormStateValid()) return@launch
        identityActionsProvider.updateLoadingState(IsLoadingState.Loading)
        runCatching {
            createItem(
                shareId = shareId,
                itemContents = identityActionsProvider.getFormState().toItemContents()
            )
        }.onSuccess { item ->
            inAppReviewTriggerMetrics.incrementItemCreatedCount()
            identityActionsProvider.onItemSavedState(item)
            telemetryManager.sendEvent(ItemCreate(EventItemType.Identity))
            snackbarDispatcher(ItemCreated)
        }.onFailure {
            PassLogger.w(TAG, "Could not create item")
            PassLogger.w(TAG, it)
            snackbarDispatcher(ItemCreationError)
        }
        identityActionsProvider.updateLoadingState(IsLoadingState.NotLoading)
    }

    override fun onCleared() {
        identityActionsProvider.clearState()
        super.onCleared()
    }

    private suspend fun observeNewCustomField() {
        draftRepository.get<CustomFieldContent>(DRAFT_CUSTOM_FIELD_KEY)
            .collect {
                if (it !is Some) return@collect
                draftRepository.delete<CustomFieldContent>(DRAFT_CUSTOM_FIELD_KEY)
                val extraFieldType =
                    draftRepository.delete<CustomExtraField>(DRAFT_IDENTITY_CUSTOM_FIELD_KEY)
                if (extraFieldType !is Some) return@collect
                identityActionsProvider.onAddCustomField(it.value, extraFieldType.value)
            }
    }

    private suspend fun observeRemoveCustomField() {
        draftRepository.get<Int>(DRAFT_REMOVE_CUSTOM_FIELD_KEY)
            .collect {
                if (it !is Some) return@collect
                draftRepository.delete<Int>(DRAFT_REMOVE_CUSTOM_FIELD_KEY)
                val extraFieldType =
                    draftRepository.delete<CustomExtraField>(DRAFT_IDENTITY_CUSTOM_FIELD_KEY)
                if (extraFieldType !is Some) return@collect
                identityActionsProvider.onRemoveCustomField(it.value, extraFieldType.value)
            }
    }

    private suspend fun observeRenameCustomField() {
        draftRepository.get<CustomFieldIndexTitle>(DRAFT_CUSTOM_FIELD_TITLE_KEY)
            .collect {
                if (it !is Some) return@collect
                draftRepository.delete<CustomFieldIndexTitle>(DRAFT_CUSTOM_FIELD_TITLE_KEY)
                val extraFieldType =
                    draftRepository.delete<CustomExtraField>(DRAFT_IDENTITY_CUSTOM_FIELD_KEY)
                if (extraFieldType !is Some) return@collect
                identityActionsProvider.onRenameCustomField(it.value, extraFieldType.value)
            }
    }


    companion object {
        private const val TAG = "CreateIdentityViewModel"
    }
}

