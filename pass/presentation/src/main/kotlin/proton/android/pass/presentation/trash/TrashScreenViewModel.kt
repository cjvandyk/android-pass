package proton.android.pass.presentation.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.proton.core.accountmanager.domain.AccountManager
import me.proton.core.domain.entity.UserId
import proton.android.pass.common.api.Result
import proton.android.pass.common.api.onError
import proton.android.pass.common.api.onSuccess
import proton.android.pass.commonui.api.toUiModel
import proton.android.pass.commonuimodels.api.ItemUiModel
import proton.android.pass.composecomponents.impl.uievents.IsLoadingState
import proton.android.pass.composecomponents.impl.uievents.IsRefreshingState
import proton.android.pass.crypto.api.context.EncryptionContextProvider
import proton.android.pass.data.api.repositories.ItemRepository
import proton.android.pass.data.api.usecases.ObserveTrashedItems
import proton.android.pass.data.api.usecases.RefreshContent
import proton.android.pass.log.api.PassLogger
import proton.android.pass.notifications.api.SnackbarMessageRepository
import proton.android.pass.presentation.trash.TrashSnackbarMessage.ClearTrashError
import proton.android.pass.presentation.trash.TrashSnackbarMessage.DeleteItemError
import proton.android.pass.presentation.trash.TrashSnackbarMessage.ObserveItemsError
import proton.android.pass.presentation.trash.TrashSnackbarMessage.RefreshError
import proton.android.pass.presentation.trash.TrashSnackbarMessage.RestoreItemsError
import javax.inject.Inject

@HiltViewModel
class TrashScreenViewModel @Inject constructor(
    private val accountManager: AccountManager,
    observeTrashedItems: ObserveTrashedItems,
    private val itemRepository: ItemRepository,
    private val refreshContent: RefreshContent,
    private val snackbarMessageRepository: SnackbarMessageRepository,
    private val encryptionContextProvider: EncryptionContextProvider
) : ViewModel() {

    private val isLoading: MutableStateFlow<IsLoadingState> =
        MutableStateFlow(IsLoadingState.NotLoading)
    private val isRefreshing: MutableStateFlow<IsRefreshingState> =
        MutableStateFlow(IsRefreshingState.NotRefreshing)

    val uiState: StateFlow<TrashUiState> = combine(
        observeTrashedItems(),
        isRefreshing,
        isLoading
    ) { itemsResult, refreshing, loading ->

        val isLoading =
            IsLoadingState.from(itemsResult is Result.Loading || loading is IsLoadingState.Loading)
        val items = when (itemsResult) {
            Result.Loading -> emptyList()
            is Result.Error -> {
                val defaultMessage = "Observe trash items error"
                PassLogger.e(
                    TAG,
                    itemsResult.exception ?: Exception(defaultMessage),
                    defaultMessage
                )
                snackbarMessageRepository.emitSnackbarMessage(ObserveItemsError)
                emptyList()
            }
            is Result.Success -> {
                encryptionContextProvider.withEncryptionContext {
                    itemsResult.data.map { it.toUiModel(this@withEncryptionContext) }
                }
            }
        }

        TrashUiState(
            isLoading = isLoading,
            isRefreshing = refreshing,
            items = items.toImmutableList()
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TrashUiState.Loading
        )

    fun restoreItem(item: ItemUiModel) = viewModelScope.launch {
        withUserId {
            itemRepository.untrashItem(it, item.shareId, item.id)
                .onSuccess {
                    PassLogger.i(TAG, "Item restored successfully")
                }
                .onError {
                    val message = "Error restoring item"
                    PassLogger.e(TAG, it ?: Exception(message), message)
                    snackbarMessageRepository.emitSnackbarMessage(RestoreItemsError)
                }
        }
    }

    fun deleteItem(item: ItemUiModel) = viewModelScope.launch {
        withUserId {
            itemRepository.deleteItem(it, item.shareId, item.id)
                .onError {
                    val message = "Error deleting item"
                    PassLogger.e(TAG, it ?: Exception(message), message)
                    snackbarMessageRepository.emitSnackbarMessage(DeleteItemError)
                }
        }
    }

    fun clearTrash() = viewModelScope.launch {
        withUserId {
            isLoading.update { IsLoadingState.Loading }
            itemRepository.clearTrash(it)
                .onError {
                    val message = "Error clearing trash"
                    PassLogger.e(TAG, it ?: Exception(message), message)
                    snackbarMessageRepository.emitSnackbarMessage(ClearTrashError)
                }
            isLoading.update { IsLoadingState.NotLoading }
        }
    }

    fun restoreItems() = viewModelScope.launch {
        withUserId {
            isLoading.update { IsLoadingState.Loading }
            itemRepository.restoreItems(it)
                .onError {
                    val message = "Error restoring items"
                    PassLogger.e(TAG, it ?: Exception(message), message)
                    snackbarMessageRepository.emitSnackbarMessage(RestoreItemsError)
                }
            isLoading.update { IsLoadingState.NotLoading }
        }
    }

    fun onRefresh() = viewModelScope.launch {
        withUserId {
            isRefreshing.update { IsRefreshingState.Refreshing }
            refreshContent(it)
                .onError {
                    val message = "Error in refresh"
                    PassLogger.e(TAG, it ?: Exception(message), message)
                    snackbarMessageRepository.emitSnackbarMessage(RefreshError)
                }
            isRefreshing.update { IsRefreshingState.NotRefreshing }
        }
    }

    private suspend fun withUserId(block: suspend (UserId) -> Unit) {
        val userId = accountManager.getPrimaryUserId().first { userId -> userId != null }
        userId?.let { block(it) }
    }

    companion object {
        private const val TAG = "TrashScreenViewModel"
    }
}
