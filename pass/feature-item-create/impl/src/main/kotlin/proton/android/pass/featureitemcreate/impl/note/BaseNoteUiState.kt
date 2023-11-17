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

package proton.android.pass.featureitemcreate.impl.note

import androidx.compose.runtime.Immutable
import proton.android.pass.composecomponents.impl.uievents.IsLoadingState
import proton.android.pass.featureitemcreate.impl.ItemSavedState
import proton.android.pass.featureitemcreate.impl.common.ShareUiState
import proton.android.pass.domain.ShareId

@Immutable
data class BaseNoteUiState(
    val errorList: Set<NoteItemValidationErrors>,
    val isLoadingState: IsLoadingState,
    val itemSavedState: ItemSavedState,
    val hasUserEditedContent: Boolean
) {
    companion object {
        val Initial = BaseNoteUiState(
            isLoadingState = IsLoadingState.NotLoading,
            errorList = emptySet(),
            itemSavedState = ItemSavedState.Unknown,
            hasUserEditedContent = false
        )
    }
}

@Immutable
data class CreateNoteUiState(
    val shareUiState: ShareUiState,
    val baseNoteUiState: BaseNoteUiState
) {
    companion object {
        val Initial = CreateNoteUiState(
            shareUiState = ShareUiState.NotInitialised,
            baseNoteUiState = BaseNoteUiState.Initial
        )
    }
}

@Immutable
data class UpdateNoteUiState(
    val selectedShareId: ShareId?,
    val baseNoteUiState: BaseNoteUiState
) {
    companion object {
        val Initial = UpdateNoteUiState(
            selectedShareId = null,
            baseNoteUiState = BaseNoteUiState.Initial
        )
    }
}
