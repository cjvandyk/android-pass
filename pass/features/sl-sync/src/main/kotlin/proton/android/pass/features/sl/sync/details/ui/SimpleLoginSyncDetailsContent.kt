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

package proton.android.pass.features.sl.sync.details.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import proton.android.pass.commonui.api.PassTopBarBackButtonType
import proton.android.pass.composecomponents.impl.topbar.PassExtendedTopBar
import proton.android.pass.features.sl.sync.R
import proton.android.pass.features.sl.sync.details.presentation.SimpleLoginSyncDetailsState

@Composable
internal fun SimpleLoginSyncDetailsContent(
    modifier: Modifier = Modifier,
    onUiEvent: (SimpleLoginSyncDetailsUiEvent) -> Unit,
    state: SimpleLoginSyncDetailsState
) = with(state) {
    Scaffold(
        modifier = modifier,
        topBar = {
            PassExtendedTopBar(
                modifier = modifier,
                backButton = PassTopBarBackButtonType.BackArrow,
                title = stringResource(id = R.string.simple_login_sync_details_title),
                onUpClick = { onUiEvent(SimpleLoginSyncDetailsUiEvent.OnBackClicked) },
            )
        }
    ) { innerPaddingValue ->
        SimpleLoginSyncDetailsSections(
            modifier = modifier.padding(paddingValues = innerPaddingValue),
            defaultDomain = defaultDomain,
            defaultMailbox = defaultMailbox,
            defaultVaultOption = defaultVaultOption,
            onUiEvent = onUiEvent
        )
    }
}
