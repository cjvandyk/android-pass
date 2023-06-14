package proton.android.pass.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import proton.android.pass.biometry.NeedsBiometricAuth
import proton.android.pass.common.api.LoadingResult
import proton.android.pass.common.api.asResultWithoutLoading
import proton.android.pass.log.api.PassLogger
import proton.android.pass.network.api.NetworkMonitor
import proton.android.pass.network.api.NetworkStatus
import proton.android.pass.notifications.api.SnackbarDispatcher
import proton.android.pass.preferences.HasAuthenticated
import proton.android.pass.preferences.ThemePreference
import proton.android.pass.preferences.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val preferenceRepository: UserPreferencesRepository,
    private val snackbarDispatcher: SnackbarDispatcher,
    private val needsBiometricAuth: NeedsBiometricAuth,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val themePreference: Flow<ThemePreference> = preferenceRepository
        .getThemePreference()
        .asResultWithoutLoading()
        .map(::getThemePreference)
        .onEach {
            when (it) {
                ThemePreference.Light -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                ThemePreference.Dark -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                ThemePreference.System ->
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }

    private val networkStatus: Flow<NetworkStatus> = networkMonitor
        .connectivity
        .distinctUntilChanged()

    init {
        viewModelScope.launch {
            preferenceRepository.setHasAuthenticated(HasAuthenticated.NotAuthenticated)
        }
    }

    val appUiState: StateFlow<AppUiState> = combine(
        snackbarDispatcher.snackbarMessage,
        themePreference,
        networkStatus,
        needsBiometricAuth(),
        ::AppUiState
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = run {
            val (theme, needsAuth) = runBlocking {
                preferenceRepository.getThemePreference().first() to needsBiometricAuth().first()
            }
            AppUiState.default(theme, needsAuth)
        }
    )

    fun onSnackbarMessageDelivered() {
        viewModelScope.launch {
            snackbarDispatcher.snackbarMessageDelivered()
        }
    }

    fun onStop() = viewModelScope.launch {
        preferenceRepository.setHasAuthenticated(HasAuthenticated.NotAuthenticated)
    }

    fun onResume() = viewModelScope.launch {
        if (!needsBiometricAuth().first()) {
            preferenceRepository.setHasAuthenticated(HasAuthenticated.Authenticated)
        }
    }

    private fun getThemePreference(state: LoadingResult<ThemePreference>): ThemePreference =
        when (state) {
            LoadingResult.Loading -> ThemePreference.System
            is LoadingResult.Success -> state.data
            is LoadingResult.Error -> {
                PassLogger.w(TAG, state.exception, "Error getting ThemePreference")
                ThemePreference.System
            }
        }

    companion object {
        private const val TAG = "AppViewModel"
    }
}
