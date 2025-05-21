/*
 * Copyright (c) 2025 Proton AG
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

package proton.android.pass.features.credentials.passkeys.creation.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.provider.BeginCreatePublicKeyCredentialRequest
import androidx.credentials.provider.PendingIntentHandler
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.composecomponents.impl.theme.SystemUIDisposableEffect
import proton.android.pass.composecomponents.impl.theme.isDark
import proton.android.pass.features.credentials.passkeys.creation.navigation.PasskeyCredentialCreationNavEvent
import proton.android.pass.features.credentials.passkeys.creation.presentation.PasskeyCredentialCreationEvent
import proton.android.pass.features.credentials.passkeys.creation.presentation.PasskeyCredentialCreationRequest
import proton.android.pass.features.credentials.passkeys.creation.presentation.PasskeyCredentialCreationState
import proton.android.pass.features.credentials.passkeys.creation.presentation.PasskeyCredentialCreationViewModel
import proton.android.pass.log.api.PassLogger
import proton.android.pass.passkeys.api.ParseCreatePasskeyRequest
import javax.inject.Inject

@[AndroidEntryPoint RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)]
internal class PasskeyCredentialCreationActivity : FragmentActivity() {

    @Inject
    internal lateinit var parseCreatePasskeyRequest: ParseCreatePasskeyRequest

    private val viewModel: PasskeyCredentialCreationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val request = getPasskeyCredentialCreationRequest()
        println("JIBIRI: Create passkey credential request: $request")
        viewModel.onUpdateRequest(request)

        viewModel.onRegister(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collectLatest { state ->
                    when (state) {
                        PasskeyCredentialCreationState.NotReady -> Unit
                        PasskeyCredentialCreationState.Close -> onCancelCreationRequest()
                        is PasskeyCredentialCreationState.Ready -> setContent(state)
                    }
                }
            }
        }
    }

    private fun setContent(state: PasskeyCredentialCreationState.Ready) {
        enableEdgeToEdge()

        setContent {
            val isDark = isDark(state.themePreference)

            SystemUIDisposableEffect(isDark)

            PassTheme(isDark = isDark) {
                PasskeyCredentialCreationScreen(
                    state = state,
                    onNavigate = { destination ->
                        when (destination) {
                            PasskeyCredentialCreationNavEvent.Cancel -> {
                                onCancelCreationRequest()
                            }

                            is PasskeyCredentialCreationNavEvent.ForceSignOut -> {
                                viewModel.onSignOut(userId = destination.userId)
                            }

                            is PasskeyCredentialCreationNavEvent.SendResponse -> {
                                onProceedCreationRequest(registrationResponseJson = destination.response)
                            }

                            PasskeyCredentialCreationNavEvent.Upgrade -> {
                                viewModel.onUpgrade()
                            }
                        }
                    },
                    onEvent = { event ->
                        when (event) {
                            is PasskeyCredentialCreationEvent.OnEventConsumed -> {
                                viewModel.onConsumeEvent(event = event.event)
                            }

                            is PasskeyCredentialCreationEvent.OnItemSelected -> {
                                viewModel.onItemSelected(itemUiModel = event.itemUiModel)
                            }

                            is PasskeyCredentialCreationEvent.OnItemSelectionConfirmed -> {
                                viewModel.onItemSelectionConfirmed(
                                    itemUiModel = event.itemUiModel,
                                    request = state.request
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    private fun getPasskeyCredentialCreationRequest(): PasskeyCredentialCreationRequest? {
        val requestJson = intent.getStringExtra(EXTRAS_REQUEST_JSON) ?: run {
            PassLogger.w(TAG, "Passkey creation request does not contain requestJson")
            return null
        }

        return runCatching { parseCreatePasskeyRequest(request = requestJson) }
            .fold(
                onSuccess = { createPasskeyRequestData ->
                    PasskeyCredentialCreationRequest(
                        data = createPasskeyRequestData,
                        requestJson = requestJson
                    )
                },
                onFailure = { error ->
                    PassLogger.w(TAG, "Error parsing Passkey credential creation request")
                    PassLogger.w(TAG, error)

                    null
                }
            )
    }

    private fun onCancelCreationRequest() {
        setResult(RESULT_CANCELED)

        finish()
    }

    private fun onProceedCreationRequest(registrationResponseJson: String) {
        CreatePublicKeyCredentialResponse(registrationResponseJson = registrationResponseJson)
            .also {
                viewModel.onResponseSent()
            }
            .also { createCredentialResponse ->
                val responseIntent = Intent()

                PendingIntentHandler.setCreateCredentialResponse(
                    intent = responseIntent,
                    response = createCredentialResponse
                )

                setResult(RESULT_OK, responseIntent)

                finish()
            }
    }

    internal companion object {

        private const val TAG = "PasskeyCredentialCreationActivity"

        private const val EXTRAS_REQUEST_JSON = "REQUEST_JSON"

        internal fun createPasskeyCredentialIntent(
            context: Context,
            request: BeginCreatePublicKeyCredentialRequest
        ): Intent = Intent(
            context,
            PasskeyCredentialCreationActivity::class.java
        ).apply {
            setPackage(context.packageName)

            putExtra(EXTRAS_REQUEST_JSON, request.requestJson)
        }
    }

}
