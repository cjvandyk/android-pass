package proton.android.pass.featurecreateitem.impl.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import proton.android.pass.composecomponents.impl.uievents.IsLoadingState
import proton.android.pass.data.api.url.UrlSanitizer
import proton.android.pass.data.api.usecases.CreateAlias
import proton.android.pass.data.api.usecases.ObserveActiveShare
import proton.android.pass.featurecreateitem.impl.IsSentToTrashState
import proton.android.pass.featurecreateitem.impl.ItemSavedState
import proton.android.pass.featurecreateitem.impl.alias.AliasItem
import proton.android.pass.featurecreateitem.impl.alias.AliasMailboxUiModel
import proton.android.pass.featurecreateitem.impl.alias.AliasSnackbarMessage
import proton.android.pass.log.api.PassLogger
import proton.android.pass.notifications.api.SnackbarMessageRepository
import me.proton.core.accountmanager.domain.AccountManager
import me.proton.core.domain.entity.UserId
import proton.android.pass.common.api.None
import proton.android.pass.common.api.Option
import proton.android.pass.common.api.Result
import proton.android.pass.common.api.Some
import proton.pass.domain.Item
import proton.pass.domain.ShareId
import proton.pass.domain.entity.NewAlias

abstract class BaseLoginViewModel(
    private val createAlias: CreateAlias,
    protected val accountManager: AccountManager,
    private val snackbarMessageRepository: SnackbarMessageRepository,
    observeActiveShare: ObserveActiveShare,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    protected val shareId: Option<ShareId> =
        Option.fromNullable(savedStateHandle.get<String>("shareId")?.let { ShareId(it) })

    private val activeShareIdState: StateFlow<Option<ShareId>> = MutableStateFlow(shareId)
        .flatMapLatest { option ->
            when (option) {
                None -> observeActiveShare()
                    .distinctUntilChanged()
                    .map { result: Result<ShareId?> ->
                        when (result) {
                            is Result.Error -> None
                            Result.Loading -> None
                            is Result.Success -> Option.fromNullable(result.data)
                        }
                    }
                is Some -> flowOf(option)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = shareId
        )

    protected val loginItemState: MutableStateFlow<LoginItem> = MutableStateFlow(LoginItem.Empty)
    protected val aliasItemState: MutableStateFlow<Option<AliasItem>> = MutableStateFlow(None)
    protected val isLoadingState: MutableStateFlow<IsLoadingState> =
        MutableStateFlow(IsLoadingState.NotLoading)
    protected val isItemSavedState: MutableStateFlow<ItemSavedState> =
        MutableStateFlow(ItemSavedState.Unknown)
    protected val isItemSentToTrashState: MutableStateFlow<IsSentToTrashState> =
        MutableStateFlow(IsSentToTrashState.NotSent)
    protected val loginItemValidationErrorsState: MutableStateFlow<Set<LoginItemValidationErrors>> =
        MutableStateFlow(emptySet())
    protected val focusLastWebsiteState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    protected val canUpdateUsernameState: MutableStateFlow<Boolean> = MutableStateFlow(true)

    private val loginItemWrapperState = combine(
        loginItemState,
        loginItemValidationErrorsState,
        canUpdateUsernameState,
        isItemSentToTrashState
    ) { loginItem, loginItemValidationErrors, updateUsername, sentToTrash ->
        LoginItemWrapper(loginItem, loginItemValidationErrors, updateUsername, sentToTrash)
    }

    private data class LoginItemWrapper(
        val loginItem: LoginItem,
        val loginItemValidationErrors: Set<LoginItemValidationErrors>,
        val canUpdateUsername: Boolean,
        val itemSentToTrash: IsSentToTrashState
    )

    val loginUiState: StateFlow<CreateUpdateLoginUiState> = combine(
        activeShareIdState,
        loginItemWrapperState,
        isLoadingState,
        isItemSavedState,
        focusLastWebsiteState
    ) { shareId, loginItemWrapper, isLoading, isItemSaved, focusLastWebsite ->
        CreateUpdateLoginUiState(
            shareId = shareId,
            loginItem = loginItemWrapper.loginItem,
            validationErrors = loginItemWrapper.loginItemValidationErrors,
            isLoadingState = isLoading,
            isItemSaved = isItemSaved,
            focusLastWebsite = focusLastWebsite,
            canUpdateUsername = loginItemWrapper.canUpdateUsername,
            isItemSentToTrash = loginItemWrapper.itemSentToTrash
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CreateUpdateLoginUiState.Initial
        )

    fun onTitleChange(value: String) {
        loginItemState.update { it.copy(title = value) }
        loginItemValidationErrorsState.update {
            it.toMutableSet().apply { remove(LoginItemValidationErrors.BlankTitle) }
        }
    }

    fun onUsernameChange(value: String) {
        loginItemState.update { it.copy(username = value) }
    }

    fun onPasswordChange(value: String) {
        loginItemState.update { it.copy(password = value) }
    }

    fun onWebsiteChange(value: String, index: Int) {
        val newValue = value.replace(" ", "").replace("\n", "")
        loginItemState.update {
            it.copy(
                websiteAddresses = it.websiteAddresses.toMutableList()
                    .apply { this[index] = newValue }
            )
        }
        focusLastWebsiteState.update { false }
    }

    fun onAddWebsite() {
        loginItemState.update {
            val websites = sanitizeWebsites(it.websiteAddresses).toMutableList()
            websites.add("")

            it.copy(websiteAddresses = websites)
        }
        focusLastWebsiteState.update { true }
    }

    fun onRemoveWebsite(index: Int) {
        loginItemState.update {
            it.copy(
                websiteAddresses = it.websiteAddresses.toMutableList().apply { removeAt(index) }
            )
        }
        focusLastWebsiteState.update { false }
    }

    fun onNoteChange(value: String) {
        loginItemState.update { it.copy(note = value) }
    }

    fun onEmitSnackbarMessage(snackbarMessage: LoginSnackbarMessages) =
        viewModelScope.launch {
            snackbarMessageRepository.emitSnackbarMessage(snackbarMessage)
        }

    suspend fun performCreateAlias(
        userId: UserId,
        shareId: ShareId,
        aliasItem: AliasItem
    ): Result<Item> =
        if (aliasItem.selectedSuffix != null) {
            createAlias(
                userId = userId,
                shareId = shareId,
                newAlias = NewAlias(
                    title = aliasItem.title,
                    note = aliasItem.note,
                    prefix = aliasItem.alias,
                    suffix = aliasItem.selectedSuffix.toDomain(),
                    mailboxes = aliasItem.mailboxes
                        .filter { it.selected }
                        .map { it.model }
                        .map(AliasMailboxUiModel::toDomain)
                )
            )
        } else {
            PassLogger.i(TAG, "Empty suffix on create alias")
            snackbarMessageRepository.emitSnackbarMessage(AliasSnackbarMessage.ItemCreationError)
            Result.Error()
        }

    protected fun validateItem(): Boolean {
        loginItemState.update {
            val websites = sanitizeWebsites(it.websiteAddresses)
            it.copy(websiteAddresses = websites)
        }
        val loginItem = loginItemState.value
        val loginItemValidationErrors = loginItem.validate()
        if (loginItemValidationErrors.isNotEmpty()) {
            loginItemValidationErrorsState.update { loginItemValidationErrors }
            return false
        }
        return true
    }

    private fun sanitizeWebsites(websites: List<String>): List<String> =
        websites.map { url ->
            if (url.isBlank()) {
                ""
            } else {
                when (val res = UrlSanitizer.sanitize(url)) {
                    is Result.Success -> res.data
                    else -> url
                }
            }
        }

    companion object {
        private const val TAG = "BaseLoginViewModel"
    }
}

