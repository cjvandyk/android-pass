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

package proton.android.pass.enterextrapassword

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import kotlinx.coroutines.CoroutineScope
import me.proton.core.domain.entity.UserId
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.composecomponents.impl.bottomsheet.PassModalBottomSheetLayout
import proton.android.pass.composecomponents.impl.messages.OfflineIndicator
import proton.android.pass.composecomponents.impl.messages.PassSnackbarHost
import proton.android.pass.composecomponents.impl.messages.rememberPassSnackbarHostState
import proton.android.pass.composecomponents.impl.snackbar.SnackBarLaunchedEffect
import proton.android.pass.composecomponents.impl.theme.SystemUIEffect
import proton.android.pass.composecomponents.impl.theme.isDark
import proton.android.pass.navigation.api.rememberAppNavigator
import proton.android.pass.navigation.api.rememberBottomSheetNavigator
import proton.android.pass.network.api.NetworkStatus
import proton.android.pass.ui.PassNavHost
import proton.android.pass.ui.navigation.UN_AUTH_GRAPH
import proton.android.pass.ui.navigation.unAuthGraph
import proton.android.pass.ui.onBottomSheetDismissed

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
fun EnterExtraPasswordApp(
    modifier: Modifier = Modifier,
    userId: UserId,
    onSuccess: () -> Unit,
    onLogout: (UserId) -> Unit,
    appViewModel: EnterExtraPasswordAppViewModel = hiltViewModel()
) {
    val appUiState by appViewModel.appUiState.collectAsStateWithLifecycle()
    val isDark = isDark(appUiState.theme)
    SystemUIEffect(isDark = isDark)

    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val bottomSheetNavigator = rememberBottomSheetNavigator(bottomSheetState)
    val appNavigator = rememberAppNavigator(bottomSheetNavigator)

    val scaffoldState = rememberScaffoldState()
    val passSnackbarHostState = rememberPassSnackbarHostState(scaffoldState.snackbarHostState)

    SnackBarLaunchedEffect(
        snackBarMessage = appUiState.snackbarMessage.value(),
        passSnackBarHostState = passSnackbarHostState,
        onSnackBarMessageDelivered = { appViewModel.onSnackbarMessageDelivered() }
    )

    PassTheme(isDark = isDark) {
        Scaffold(
            modifier = modifier
                .background(PassTheme.colors.backgroundStrong)
                .systemBarsPadding()
                .imePadding()
                .padding(),
            scaffoldState = scaffoldState,
            snackbarHost = { PassSnackbarHost(snackbarHostState = passSnackbarHostState) }
        ) { contentPadding ->
            Column(modifier = Modifier.padding(contentPadding)) {
                AnimatedVisibility(
                    visible = appUiState.networkStatus == NetworkStatus.Offline,
                    label = "EnterExtraPasswordApp-OfflineIndicator"
                ) {
                    OfflineIndicator()
                }
                PassModalBottomSheetLayout(appNavigator.passBottomSheetNavigator) {
                    PassNavHost(
                        modifier = Modifier.weight(1f),
                        appNavigator = appNavigator,
                        startDestination = UN_AUTH_GRAPH,
                        graph = {
                            unAuthGraph(
                                appNavigator = appNavigator,
                                onNavigate = {
                                    TODO()
                                },
                                dismissBottomSheet = { block ->
                                    onBottomSheetDismissed(
                                        coroutineScope = coroutineScope,
                                        modalBottomSheetState = bottomSheetState,
                                        block = block
                                    )
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}
