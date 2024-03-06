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

package proton.android.pass.featureitemcreate.impl.login

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import proton.android.pass.common.api.toOption
import proton.android.pass.domain.ItemId
import proton.android.pass.domain.ShareId
import proton.android.pass.featureitemcreate.impl.bottomsheets.customfield.customFieldBottomSheetGraph
import proton.android.pass.featureitemcreate.impl.dialogs.CustomFieldNameNavigation
import proton.android.pass.featureitemcreate.impl.dialogs.customFieldNameDialogGraph
import proton.android.pass.featureitemcreate.impl.login.bottomsheet.aliasoptions.aliasOptionsBottomSheetGraph
import proton.android.pass.featureitemcreate.impl.totp.INDEX_NAV_PARAMETER_KEY
import proton.android.pass.featureitemcreate.impl.totp.TOTP_NAV_PARAMETER_KEY
import proton.android.pass.featureitemcreate.impl.totp.createTotpGraph
import proton.android.pass.navigation.api.CommonNavArgId
import proton.android.pass.navigation.api.NavItem
import proton.android.pass.navigation.api.composable

private const val EDIT_LOGIN_GRAPH = "edit_login_graph"

object EditLogin : NavItem(
    baseRoute = "login/edit",
    navArgIds = listOf(CommonNavArgId.ShareId, CommonNavArgId.ItemId)
) {
    fun createNavRoute(shareId: ShareId, itemId: ItemId) = "$baseRoute/${shareId.id}/${itemId.id}"
}

@Suppress("LongParameterList")
fun NavGraphBuilder.updateLoginGraph(onNavigate: (BaseLoginNavigation) -> Unit) {
    navigation(
        route = EDIT_LOGIN_GRAPH,
        startDestination = EditLogin.route
    ) {
        composable(EditLogin) { navBackStack ->
            val navTotpUri by navBackStack.savedStateHandle
                .getStateFlow<String?>(TOTP_NAV_PARAMETER_KEY, null)
                .collectAsStateWithLifecycle()
            LaunchedEffect(navTotpUri) {
                navBackStack.savedStateHandle.remove<String?>(TOTP_NAV_PARAMETER_KEY)
            }
            val navTotpIndex by navBackStack.savedStateHandle
                .getStateFlow<Int?>(INDEX_NAV_PARAMETER_KEY, null)
                .collectAsStateWithLifecycle()
            LaunchedEffect(navTotpIndex) {
                navBackStack.savedStateHandle.remove<Int?>(INDEX_NAV_PARAMETER_KEY)
            }
            UpdateLogin(
                navTotpUri = navTotpUri,
                navTotpIndex = navTotpIndex,
                onNavigate = onNavigate
            )
        }

        aliasOptionsBottomSheetGraph(onNavigate)
        customFieldBottomSheetGraph(onNavigate)
        customFieldNameDialogGraph {
            when (it) {
                is CustomFieldNameNavigation.Close -> {
                    onNavigate(BaseLoginNavigation.Close)
                }
            }
        }
        createTotpGraph(
            onSuccess = { totp, index ->
                val values = mutableMapOf<String, Any>(TOTP_NAV_PARAMETER_KEY to totp)
                index?.let { values.put(INDEX_NAV_PARAMETER_KEY, it) }
                onNavigate(BaseLoginNavigation.TotpSuccess(values))
            },
            onCloseTotp = { onNavigate(BaseLoginNavigation.TotpCancel) },
            onOpenImagePicker = {
                onNavigate(BaseLoginNavigation.OpenImagePicker(it.toOption()))
            }
        )
    }
}
