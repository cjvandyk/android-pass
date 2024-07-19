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

package proton.android.pass.featureitemcreate.impl.identity.presentation

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import proton.android.pass.common.api.None
import proton.android.pass.common.api.Option
import proton.android.pass.composecomponents.impl.uievents.IsLoadingState
import proton.android.pass.domain.Item
import proton.android.pass.featureitemcreate.impl.ItemSavedState
import proton.android.pass.featureitemcreate.impl.common.CustomFieldIndexTitle
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.Birthdate
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.County
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.CustomExtraField
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.ExtraField
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.Facebook
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.FirstName
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.Floor
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.FocusedField
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.Gender
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.Instagram
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.LastName
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.Linkedin
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.MiddleName
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.PersonalWebsite
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.Reddit
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.WorkEmail
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.WorkPhoneNumber
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.Yahoo

@Suppress("ComplexInterface", "TooManyFunctions")
interface IdentityFormActions {
    fun onFieldChange(field: FieldChange)
    fun observeDraftChanges(coroutineScope: CoroutineScope)
    fun onRenameCustomField(value: CustomFieldIndexTitle, customExtraField: CustomExtraField)
    fun getFormState(): IdentityItemFormState
    fun isFormStateValid(): Boolean
    fun clearState()
    fun onCustomFieldFocusChange(
        index: Int,
        focused: Boolean,
        customExtraField: CustomExtraField
    )
}

interface IdentityActionsProvider : IdentityFormActions {
    fun observeSharedState(): Flow<IdentitySharedUiState>
    fun updateLoadingState(loadingState: IsLoadingState)
    fun onItemSavedState(item: Item)
    fun updateSelectedSection(customExtraField: CustomExtraField)
    fun onItemReceivedState(item: Item)
    fun getReceivedItem(): Item
    fun observeReceivedItem(): Flow<Option<Item>>
    fun resetLastAddedFieldFocus()
}

data class IdentitySharedUiState(
    val isLoadingState: IsLoadingState,
    val hasUserEditedContent: Boolean,
    val validationErrors: PersistentSet<IdentityValidationErrors>,
    val isItemSaved: ItemSavedState,
    val extraFields: PersistentSet<ExtraField>,
    val focusedField: Option<FocusedField>,
    val canUseCustomFields: Boolean
) {

    val showAddPersonalDetailsButton: Boolean = if (canUseCustomFields) {
        true
    } else {
        !extraFields.containsAll(setOf(FirstName, MiddleName, LastName, Birthdate, Gender))
    }

    val showAddAddressDetailsButton: Boolean = if (canUseCustomFields) {
        true
    } else {
        !extraFields.containsAll(setOf(Floor, County))
    }

    val showAddContactDetailsButton: Boolean = if (canUseCustomFields) {
        true
    } else {
        !extraFields.containsAll(setOf(Linkedin, Reddit, Facebook, Yahoo, Instagram))
    }

    val showAddWorkDetailsButton: Boolean = if (canUseCustomFields) {
        true
    } else {
        !extraFields.containsAll(setOf(PersonalWebsite, WorkPhoneNumber, WorkEmail))
    }

    companion object {
        val Initial = IdentitySharedUiState(
            isLoadingState = IsLoadingState.NotLoading,
            hasUserEditedContent = false,
            validationErrors = persistentSetOf(),
            isItemSaved = ItemSavedState.Unknown,
            extraFields = persistentSetOf(),
            focusedField = None,
            canUseCustomFields = false
        )
    }
}
