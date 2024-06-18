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

package proton.android.pass.featureitemcreate.impl.identity.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import proton.android.pass.domain.ItemId
import proton.android.pass.domain.ShareId
import proton.android.pass.featureitemcreate.impl.bottomsheets.customfield.customFieldBottomSheetGraph
import proton.android.pass.featureitemcreate.impl.common.CustomFieldPrefix
import proton.android.pass.featureitemcreate.impl.dialogs.customfield.CustomFieldNameNavigation
import proton.android.pass.featureitemcreate.impl.dialogs.customfield.customFieldNameDialogGraph
import proton.android.pass.featureitemcreate.impl.identity.navigation.bottomsheets.IdentityFieldsNavigation
import proton.android.pass.featureitemcreate.impl.identity.navigation.bottomsheets.identityFieldsGraph
import proton.android.pass.featureitemcreate.impl.identity.navigation.customsection.ExtraSectionNavigation
import proton.android.pass.featureitemcreate.impl.identity.navigation.customsection.extraSectionGraph
import proton.android.pass.featureitemcreate.impl.identity.ui.UpdateIdentityScreen
import proton.android.pass.navigation.api.CommonNavArgId
import proton.android.pass.navigation.api.NavItem
import proton.android.pass.navigation.api.composable

const val UPDATE_IDENTITY_GRAPH = "update_identity_graph"

object UpdateIdentity : NavItem(
    baseRoute = "identity/update/screen",
    navArgIds = listOf(CommonNavArgId.ShareId, CommonNavArgId.ItemId)
) {
    fun createNavRoute(shareId: ShareId, itemId: ItemId) = "$baseRoute/${shareId.id}/${itemId.id}"
}

sealed interface UpdateIdentityNavigation {
    data class IdentityUpdated(val shareId: ShareId, val itemId: ItemId) : BaseIdentityNavigation
}

fun NavGraphBuilder.updateIdentityGraph(onNavigate: (BaseIdentityNavigation) -> Unit) {
    navigation(
        route = UPDATE_IDENTITY_GRAPH,
        startDestination = UpdateIdentity.route
    ) {
        composable(UpdateIdentity) {
            UpdateIdentityScreen(
                onNavigate = onNavigate
            )
        }
        identityFieldsGraph {
            when (it) {
                IdentityFieldsNavigation.Close -> onNavigate(BaseIdentityNavigation.Close)
                IdentityFieldsNavigation.AddCustomField ->
                    onNavigate(BaseIdentityNavigation.OpenCustomFieldBottomSheet)
            }
        }
        customFieldBottomSheetGraph(
            prefix = CustomFieldPrefix.UpdateIdentity,
            onAddCustomFieldNavigate = {
                onNavigate(BaseIdentityNavigation.CustomFieldTypeSelected(it))
            },
            onEditCustomFieldNavigate = { title: String, index: Int ->
                onNavigate(BaseIdentityNavigation.EditCustomField(title, index))
            },
            onRemoveCustomFieldNavigate = {
                onNavigate(BaseIdentityNavigation.RemovedCustomField)
            },
            onCloseNavigate = { onNavigate(BaseIdentityNavigation.Close) }
        )
        customFieldNameDialogGraph(CustomFieldPrefix.UpdateIdentity) {
            when (it) {
                is CustomFieldNameNavigation.Close -> onNavigate(BaseIdentityNavigation.Close)
            }
        }
        extraSectionGraph {
            when (it) {
                is ExtraSectionNavigation.Close -> onNavigate(BaseIdentityNavigation.Close)
                is ExtraSectionNavigation.EditCustomSection ->
                    onNavigate(BaseIdentityNavigation.EditCustomSection(it.title, it.index))

                ExtraSectionNavigation.RemoveCustomSection ->
                    onNavigate(BaseIdentityNavigation.RemoveCustomSection)
            }
        }
    }
}

