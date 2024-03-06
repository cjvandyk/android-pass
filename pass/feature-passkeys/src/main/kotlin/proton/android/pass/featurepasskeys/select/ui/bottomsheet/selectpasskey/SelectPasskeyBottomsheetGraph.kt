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

package proton.android.pass.featurepasskeys.select.ui.bottomsheet.selectpasskey

import androidx.activity.compose.BackHandler
import androidx.navigation.NavGraphBuilder
import proton.android.pass.domain.ItemId
import proton.android.pass.domain.Passkey
import proton.android.pass.domain.ShareId
import proton.android.pass.navigation.api.CommonNavArgId
import proton.android.pass.navigation.api.NavItem
import proton.android.pass.navigation.api.bottomSheet

object SelectPasskeyBottomsheet : NavItem(
    baseRoute = "passkey/select/bottomsheet",
    navArgIds = listOf(CommonNavArgId.ShareId, CommonNavArgId.ItemId)
) {
    fun buildRoute(shareId: ShareId, itemId: ItemId): String = "$baseRoute/${shareId.id}/${itemId.id}"
}

fun NavGraphBuilder.selectPasskeyBottomsheetGraph(onPasskeySelected: (Passkey) -> Unit, onDismiss: () -> Unit) {
    bottomSheet(SelectPasskeyBottomsheet) {
        BackHandler(onBack = onDismiss)
        SelectPasskeyBottomsheet(
            onPasskeySelected = onPasskeySelected,
            onDismiss = onDismiss
        )
    }
}
