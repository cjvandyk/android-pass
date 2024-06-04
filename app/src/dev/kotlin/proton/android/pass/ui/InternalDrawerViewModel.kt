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

package proton.android.pass.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import me.proton.core.accountmanager.domain.AccountManager
import proton.android.pass.common.api.flatMap
import proton.android.pass.crypto.api.context.EncryptionContextProvider
import proton.android.pass.data.api.usecases.extrapassword.AuthWithAccessKey
import proton.android.pass.data.api.usecases.extrapassword.RemoveAccessKey
import proton.android.pass.data.api.usecases.extrapassword.SetupAccessKey
import proton.android.pass.image.api.ClearIconCache
import proton.android.pass.log.api.PassLogger
import proton.android.pass.notifications.api.SnackbarDispatcher
import proton.android.pass.preferences.InternalSettingsRepository
import proton.android.pass.preferences.UserPreferencesRepository
import proton.android.pass.preferences.tooltips.TooltipPreferencesRepository
import proton.android.pass.securitycenter.api.ObserveSecurityAnalysis
import proton.android.pass.ui.InternalDrawerSnackbarMessage.PreferencesClearError
import proton.android.pass.ui.InternalDrawerSnackbarMessage.PreferencesCleared
import javax.inject.Inject

@HiltViewModel
class InternalDrawerViewModel @Inject constructor(
    private val preferenceRepository: UserPreferencesRepository,
    private val internalSettingsRepository: InternalSettingsRepository,
    private val snackbarDispatcher: SnackbarDispatcher,
    private val clearCache: ClearIconCache,
    private val observeSecurityAnalysis: ObserveSecurityAnalysis,
    private val tooltipPreferencesRepository: TooltipPreferencesRepository,
    private val setupAccessKey: SetupAccessKey,
    private val authWithAccessKey: AuthWithAccessKey,
    private val removeAccessKey: RemoveAccessKey,
    private val encryptionContextProvider: EncryptionContextProvider,
    private val accountManager: AccountManager
) : ViewModel() {

    internal fun clearPreferences() = viewModelScope.launch {
        preferenceRepository.clearPreferences()
            .flatMap { internalSettingsRepository.clearSettings() }
            .flatMap { runCatching { tooltipPreferencesRepository.clear() } }
            .onSuccess {
                snackbarDispatcher(PreferencesCleared)
            }
            .onFailure {
                PassLogger.e(TAG, it, "Error clearing preferences")
                snackbarDispatcher(PreferencesClearError)
            }
    }

    fun clearIconCache() = viewModelScope.launch {
        clearCache()
    }

    fun runSecurityChecks() = viewModelScope.launch {
        observeSecurityAnalysis().collect { analysis ->
            PassLogger.i(TAG, "-----")
            PassLogger.i(TAG, "Security analysis: Breached Data: ${analysis.breachedData}")
            PassLogger.i(
                TAG,
                "Security analysis: Insecure Passwords: ${analysis.insecurePasswords}"
            )
            PassLogger.i(TAG, "Security analysis: Missing 2FA: ${analysis.missing2fa}")
            PassLogger.i(TAG, "Security analysis: Reused passwords: ${analysis.reusedPasswords}")
            PassLogger.i(TAG, "-----")
        }
    }

    fun setAccessKey() = viewModelScope.launch {
        val encrypted = encryptionContextProvider.withEncryptionContext { encrypt("MyPassword") }
        runCatching {
            setupAccessKey(encrypted)
        }.onSuccess {
            PassLogger.i(TAG, "Access key set successfully")
        }.onFailure {
            PassLogger.w(TAG, "Error setting access key")
            PassLogger.w(TAG, it)
        }
    }

    fun performSrp() = viewModelScope.launch {
        val encrypted = encryptionContextProvider.withEncryptionContext { encrypt("MyPassword") }
        val userId = accountManager.getPrimaryUserId().firstOrNull() ?: run {
            PassLogger.w(TAG, "No primary user id")
            return@launch
        }
        runCatching {
            authWithAccessKey(userId, encrypted)
        }.onSuccess {
            PassLogger.i(TAG, "SRP performed successfully. Result: $it")
        }.onFailure {
            PassLogger.w(TAG, "Error performing SRP key")
            PassLogger.w(TAG, it)
        }
    }

    fun removeAccessKey() = viewModelScope.launch {
        runCatching {
            removeAccessKey.invoke()
        }.onSuccess {
            PassLogger.i(TAG, "Access key removed successfully")
        }.onFailure {
            PassLogger.w(TAG, "Error removing access key")
            PassLogger.w(TAG, it)
        }
    }

    companion object {
        private const val TAG = "InternalDrawerViewModel"
    }
}
