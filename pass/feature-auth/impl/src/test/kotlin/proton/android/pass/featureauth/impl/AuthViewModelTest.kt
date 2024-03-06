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

package proton.android.pass.featureauth.impl

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import proton.android.pass.biometry.BiometryAuthError
import proton.android.pass.biometry.BiometryResult
import proton.android.pass.biometry.BiometryStartupError
import proton.android.pass.biometry.BiometryStatus
import proton.android.pass.biometry.TestBiometryManager
import proton.android.pass.biometry.TestStoreAuthSuccessful
import proton.android.pass.common.api.AppDispatchers
import proton.android.pass.common.api.None
import proton.android.pass.common.api.Some
import proton.android.pass.common.api.some
import proton.android.pass.commonui.api.ClassHolder
import proton.android.pass.composecomponents.impl.uievents.IsLoadingState
import proton.android.pass.data.fakes.usecases.TestCheckMasterPassword
import proton.android.pass.data.fakes.usecases.TestObservePrimaryUserEmail
import proton.android.pass.preferences.AppLockState
import proton.android.pass.preferences.AppLockTypePreference
import proton.android.pass.preferences.TestInternalSettingsRepository
import proton.android.pass.preferences.TestPreferenceRepository
import proton.android.pass.test.MainDispatcherRule

internal class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private lateinit var preferenceRepository: TestPreferenceRepository
    private lateinit var biometryManager: TestBiometryManager
    private lateinit var checkMasterPassword: TestCheckMasterPassword

    @get:Rule
    internal val dispatcherRule = MainDispatcherRule()

    @Before
    internal fun setUp() {
        preferenceRepository = TestPreferenceRepository()
        biometryManager = TestBiometryManager()
        checkMasterPassword = TestCheckMasterPassword()
        viewModel = AuthViewModel(
            preferenceRepository = preferenceRepository,
            biometryManager = biometryManager,
            checkMasterPassword = checkMasterPassword,
            storeAuthSuccessful = TestStoreAuthSuccessful(),
            internalSettingsRepository = TestInternalSettingsRepository(),
            appDispatchers = object : AppDispatchers {
                override val main = dispatcherRule.testDispatcher
                override val default = UnconfinedTestDispatcher()
                override val io = UnconfinedTestDispatcher()
            },
            observePrimaryUserEmail = TestObservePrimaryUserEmail().apply {
                emit(USER_EMAIL)
            }
        )
    }

    @Test
    internal fun `WHEN view model is initialized THEN emits initial state`() = runTest {
        val expectedState = AuthState.Initial.copy(
            event = None,
            content = AuthContent.default(USER_EMAIL.some()).copy(
                authMethod = Some(AuthMethod.Fingerprint)
            )
        )

        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(expectedState)
        }
    }

    @Test
    internal fun `GIVEN biometry success with enabled biometry WHEN requesting biometrics THEN emits Success event`() =
        runTest {
            preferenceRepository.setAppLockState(AppLockState.Enabled)
            biometryManager.setBiometryStatus(BiometryStatus.CanAuthenticate)
            biometryManager.emitResult(BiometryResult.Success)

            viewModel.onBiometricsRequired(ClassHolder(None))

            viewModel.state.test {
                assertThat(awaitItem().event).isEqualTo(AuthEvent.Success.some())
            }
        }

    @Test
    internal fun `GIVEN biometrics preferences is disabled WHEN requesting biometrics THEN don't require biometrics`() =
        runTest {
            biometryManager.setBiometryStatus(BiometryStatus.CanAuthenticate)
            preferenceRepository.setAppLockState(AppLockState.Disabled)

            // Set to failed so we can check it is not called
            biometryManager.emitResult(BiometryResult.Failed)

            viewModel.onBiometricsRequired(ClassHolder(None))

            viewModel.state.test {
                assertThat(awaitItem().event).isEqualTo(AuthEvent.Success.some())
            }

            assertThat(biometryManager.hasBeenCalled).isFalse()
        }

    @Test
    internal fun `GIVEN biometrics are not available WHEN requesting biometrics THEN emits Success event`() = runTest {
        biometryManager.setBiometryStatus(BiometryStatus.NotAvailable)

        viewModel.onBiometricsRequired(ClassHolder(None))

        viewModel.state.test {
            assertThat(awaitItem().event).isEqualTo(AuthEvent.Success.some())
        }
    }

    @Test
    internal fun `GIVEN biometrics are not enrolled WHEN requesting biometrics THEN emits Success event`() = runTest {
        biometryManager.setBiometryStatus(BiometryStatus.CanAuthenticate)
        biometryManager.setBiometryStatus(BiometryStatus.NotEnrolled)

        viewModel.onBiometricsRequired(ClassHolder(None))

        viewModel.state.test {
            assertThat(awaitItem().event).isEqualTo(AuthEvent.Success.some())
        }
    }

    @Test
    internal fun `GIVEN biometrics canceled error WHEN requesting biometrics THEN emits initial state`() = runTest {
        preferenceRepository.setAppLockState(AppLockState.Enabled)
        biometryManager.setBiometryStatus(BiometryStatus.CanAuthenticate)
        biometryManager.emitResult(BiometryResult.Error(BiometryAuthError.Canceled))

        viewModel.onBiometricsRequired(ClassHolder(None))

        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(
                AuthState(
                    event = None,
                    content = AuthContent.default(USER_EMAIL.some()).copy(
                        authMethod = Some(AuthMethod.Fingerprint)
                    )
                )
            )
        }
    }


    @Test
    internal fun `GIVEN an unknown biometrics error WHEN requesting biometrics THEN emits Failed event`() = runTest {
        preferenceRepository.setAppLockState(AppLockState.Enabled)
        biometryManager.setBiometryStatus(BiometryStatus.CanAuthenticate)
        biometryManager.emitResult(BiometryResult.Error(BiometryAuthError.Unknown))

        viewModel.onBiometricsRequired(ClassHolder(None))

        viewModel.state.test {
            assertThat(awaitItem().event).isEqualTo(AuthEvent.Failed.some())
        }
    }

    @Test
    internal fun `GIVEN biometrics startup error WHEN requesting biometrics THEN emits Failed event`() = runTest {
        preferenceRepository.setAppLockState(AppLockState.Enabled)
        biometryManager.setBiometryStatus(BiometryStatus.CanAuthenticate)
        biometryManager.emitResult(BiometryResult.FailedToStart(BiometryStartupError.Unknown))

        viewModel.onBiometricsRequired(ClassHolder(None))

        viewModel.state.test {
            assertThat(awaitItem().event).isEqualTo(AuthEvent.Failed.some())
        }
    }

    @Test
    internal fun `WHEN sign out is clicked THEN emits SignOut event`() = runTest {
        viewModel.onSignOut()

        viewModel.state.test {
            assertThat(awaitItem().event).isEqualTo(AuthEvent.SignOut.some())
        }
    }

    @Test
    internal fun `GIVEN correct password WHEN password is submitted THEN emits success event`() = runTest {
        viewModel.onPasswordChanged("password")

        viewModel.onSubmit()

        viewModel.state.test {
            assertThat(awaitItem().event).isEqualTo(AuthEvent.Success.some())
        }
    }

    @Test
    fun `empty password emits password error`() = runTest {
        setBiometryCanceled()

        viewModel.onPasswordChanged("")
        viewModel.onSubmit()
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.content.isLoadingState).isEqualTo(IsLoadingState.NotLoading)
            assertThat(state.content.passwordError.value()).isEqualTo(PasswordError.EmptyPassword)
        }
    }

    @Test
    fun `wrong password emits wrong password error`() = runTest {
        setBiometryCanceled()
        checkMasterPassword.setResult(false)

        val password = "test"

        viewModel.onPasswordChanged(password)
        viewModel.onSubmit()
        viewModel.state.test {
            val state1 = awaitItem()
            assertThat(state1.content.isLoadingState).isEqualTo(IsLoadingState.Loading)

            val state2 = awaitItem()
            assertThat(state2.content.isLoadingState).isEqualTo(IsLoadingState.NotLoading)
            assertThat(state2.content.error.value()).isEqualTo(
                AuthError.WrongPassword(
                    remainingAttempts = 4
                )
            )
            assertThat(state2.content.password).isEqualTo(password)
        }
    }

    @Test
    internal fun `WHEN introducing wrong password many times THEN emits logout`() = runTest {
        setBiometryCanceled()
        checkMasterPassword.setResult(false)

        val password = "test"

        viewModel.onPasswordChanged(password)

        viewModel.state.test {
            skipItems(1)

            // Fail 4 times
            for (attempt in 0 until 4) {
                viewModel.onSubmit()
                val stateLoading = awaitItem()
                assertThat(stateLoading.content.isLoadingState).isEqualTo(IsLoadingState.Loading)

                val stateError = awaitItem()
                assertThat(stateError.content.isLoadingState).isEqualTo(IsLoadingState.NotLoading)

                val expected = AuthError.WrongPassword(remainingAttempts = 4 - attempt)
                assertThat(stateError.content.error.value()).isEqualTo(expected)
            }

            // Fail one more time
            viewModel.onSubmit()
            val stateLoading = awaitItem()
            assertThat(stateLoading.content.isLoadingState).isEqualTo(IsLoadingState.Loading)

            val stateError = awaitItem()
            assertThat(stateError.content.isLoadingState).isEqualTo(IsLoadingState.NotLoading)
            assertThat(stateError.event).isEqualTo(AuthEvent.ForceSignOut.some())
        }
    }

    @Test
    internal fun `GIVEN lock type is None WHEN requesting auth method THEN emit Unknown event`() = runTest {
        val appLockTypePreference = AppLockTypePreference.None
        val expectedAuthEvent = AuthEvent.Unknown.some()
        preferenceRepository.setAppLockTypePreference(appLockTypePreference)

        viewModel.onAuthMethodRequested()

        viewModel.state.test {
            assertThat(awaitItem().event).isEqualTo(expectedAuthEvent)
        }
    }

    @Test
    internal fun `GIVEN lock type is Biometrics WHEN requesting auth method THEN emit EnterBiometrics event`() =
        runTest {
            val appLockTypePreference = AppLockTypePreference.Biometrics
            val expectedAuthEvent = AuthEvent.EnterBiometrics.some()
            preferenceRepository.setAppLockTypePreference(appLockTypePreference)

            viewModel.onAuthMethodRequested()

            viewModel.state.test {
                assertThat(awaitItem().event).isEqualTo(expectedAuthEvent)
            }
        }

    @Test
    internal fun `GIVEN lock type is Pin WHEN requesting auth method THEN emit EnterPin event`() = runTest {
        val appLockTypePreference = AppLockTypePreference.Pin
        val expectedAuthEvent = AuthEvent.EnterPin.some()
        preferenceRepository.setAppLockTypePreference(appLockTypePreference)

        viewModel.onAuthMethodRequested()

        viewModel.state.test {
            assertThat(awaitItem().event).isEqualTo(expectedAuthEvent)
        }
    }

    private fun setBiometryCanceled() {
        preferenceRepository.setAppLockState(AppLockState.Enabled)
        biometryManager.setBiometryStatus(BiometryStatus.CanAuthenticate)
        biometryManager.emitResult(BiometryResult.Error(BiometryAuthError.Canceled))
    }

    companion object {
        private const val USER_EMAIL = "test@test.test"
    }
}
