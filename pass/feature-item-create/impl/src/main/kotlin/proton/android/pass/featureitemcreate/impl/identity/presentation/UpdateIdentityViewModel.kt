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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.proton.core.accountmanager.domain.AccountManager
import proton.android.pass.commonui.api.SavedStateHandleProvider
import proton.android.pass.commonui.api.require
import proton.android.pass.composecomponents.impl.uievents.IsLoadingState
import proton.android.pass.data.api.usecases.GetItemById
import proton.android.pass.data.api.usecases.UpdateItem
import proton.android.pass.domain.ItemId
import proton.android.pass.domain.ShareId
import proton.android.pass.featureitemcreate.impl.ItemCreate
import proton.android.pass.featureitemcreate.impl.identity.presentation.IdentitySnackbarMessage.InitError
import proton.android.pass.featureitemcreate.impl.identity.presentation.IdentitySnackbarMessage.ItemUpdateError
import proton.android.pass.featureitemcreate.impl.identity.presentation.IdentitySnackbarMessage.ItemUpdated
import proton.android.pass.log.api.PassLogger
import proton.android.pass.navigation.api.CommonNavArgId
import proton.android.pass.notifications.api.SnackbarDispatcher
import proton.android.pass.telemetry.api.EventItemType
import proton.android.pass.telemetry.api.TelemetryManager
import javax.inject.Inject

@HiltViewModel
class UpdateIdentityViewModel @Inject constructor(
    private val getItemById: GetItemById,
    private val updateItem: UpdateItem,
    private val identityActionsProvider: IdentityActionsProvider,
    private val telemetryManager: TelemetryManager,
    private val snackbarDispatcher: SnackbarDispatcher,
    private val accountManager: AccountManager,
    savedStateHandleProvider: SavedStateHandleProvider
) : ViewModel(), IdentityActionsProvider by identityActionsProvider {

    private val navShareId: ShareId =
        savedStateHandleProvider.get().require<String>(CommonNavArgId.ShareId.key)
            .let(::ShareId)
    private val navItemId: ItemId =
        savedStateHandleProvider.get().require<String>(CommonNavArgId.ItemId.key)
            .let(::ItemId)

    init {
        viewModelScope.launch {
            identityActionsProvider.observeDraftChanges(this)
            launch { getItem() }
        }
    }

    val state: StateFlow<IdentityUiState> = identityActionsProvider.observeSharedState()
        .mapLatest { IdentityUiState.UpdateIdentity(navShareId, it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = IdentityUiState.NotInitialised
        )

    private suspend fun getItem() {
        identityActionsProvider.updateLoadingState(IsLoadingState.Loading)
        runCatching { getItemById(navShareId, navItemId) }
            .onSuccess(identityActionsProvider::onItemReceivedState)
            .onFailure {
                PassLogger.i(TAG, it, "Get by id error")
                snackbarDispatcher(InitError)
            }
        identityActionsProvider.updateLoadingState(IsLoadingState.NotLoading)
    }

    fun onSubmit(shareId: ShareId) = viewModelScope.launch {
        if (!identityActionsProvider.isFormStateValid()) return@launch
        identityActionsProvider.updateLoadingState(IsLoadingState.Loading)
        runCatching {
            val userId = accountManager.getPrimaryUserId().first()
                ?: throw IllegalStateException("User id is null")
            updateItem(
                userId = userId,
                shareId = shareId,
                item = identityActionsProvider.getReceivedItem(),
                contents = identityActionsProvider.getFormState().toItemContents()
            )
        }.onSuccess { item ->
            identityActionsProvider.onItemSavedState(item)
            telemetryManager.sendEvent(ItemCreate(EventItemType.Identity))
            snackbarDispatcher(ItemUpdated)
        }.onFailure {
            PassLogger.w(TAG, "Could not update item")
            PassLogger.w(TAG, it)
            snackbarDispatcher(ItemUpdateError)
        }
        identityActionsProvider.updateLoadingState(IsLoadingState.NotLoading)
    }

    override fun onCleared() {
        identityActionsProvider.clearState()
        super.onCleared()
    }

    companion object {
        private const val TAG = "UpdateIdentityViewModel"
    }
}

