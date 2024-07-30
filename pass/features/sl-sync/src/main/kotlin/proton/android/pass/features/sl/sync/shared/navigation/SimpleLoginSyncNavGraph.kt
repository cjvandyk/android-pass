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

package proton.android.pass.features.sl.sync.shared.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import proton.android.pass.common.api.toOption
import proton.android.pass.domain.ShareId
import proton.android.pass.features.sl.sync.details.navigation.SimpleLoginSyncDetailsNavItem
import proton.android.pass.features.sl.sync.details.ui.SimpleLoginSyncDetailsScreen
import proton.android.pass.features.sl.sync.settings.navigation.SimpleLoginSyncSettingsNavItem
import proton.android.pass.features.sl.sync.settings.ui.SimpleLoginSyncSettingsScreen
import proton.android.pass.navigation.api.composable

private const val SELECTED_SHARE_ID_KEY = "vaultSelected"

fun NavGraphBuilder.simpleLoginSyncNavGraph(onNavigated: (SimpleLoginSyncNavDestination) -> Unit) {

    composable(navItem = SimpleLoginSyncDetailsNavItem) {
        SimpleLoginSyncDetailsScreen(onNavigated = onNavigated)
    }

    composable(navItem = SimpleLoginSyncSettingsNavItem) { navBackStack ->
        val selectedShareIdArg by navBackStack.savedStateHandle
            .getStateFlow<String?>(key = SELECTED_SHARE_ID_KEY, initialValue = null)
            .collectAsStateWithLifecycle()

        SimpleLoginSyncSettingsScreen(
            onNavigated = onNavigated,
            selectedShareIdOption = selectedShareIdArg?.let(::ShareId).toOption()
        )
    }

}
