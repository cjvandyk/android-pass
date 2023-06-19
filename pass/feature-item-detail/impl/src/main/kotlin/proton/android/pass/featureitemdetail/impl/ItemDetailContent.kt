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

package proton.android.pass.featureitemdetail.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonuimodels.api.ItemTypeUiState
import proton.android.pass.featureitemdetail.impl.alias.AliasDetail
import proton.android.pass.featureitemdetail.impl.login.LoginDetail
import proton.android.pass.featureitemdetail.impl.note.NoteDetail

@Composable
fun ItemDetailContent(
    modifier: Modifier = Modifier,
    uiState: ItemDetailScreenUiState,
    onNavigate: (ItemDetailNavigation) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PassTheme.colors.backgroundStrong)
    ) {
        when (uiState.itemTypeUiState) {
            ItemTypeUiState.Login -> LoginDetail(
                moreInfoUiState = uiState.moreInfoUiState,
                canLoadExternalImages = uiState.canLoadExternalImages,
                onNavigate = onNavigate
            )
            ItemTypeUiState.Note -> NoteDetail(
                moreInfoUiState = uiState.moreInfoUiState,
                onNavigate = onNavigate
            )
            ItemTypeUiState.Alias -> AliasDetail(
                moreInfoUiState = uiState.moreInfoUiState,
                onNavigate = onNavigate
            )
            ItemTypeUiState.Password -> {}
            else -> {}
        }
    }
}
