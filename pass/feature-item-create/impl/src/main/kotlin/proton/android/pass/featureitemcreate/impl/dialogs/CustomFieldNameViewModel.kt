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

package proton.android.pass.featureitemcreate.impl.dialogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import proton.android.pass.commonui.api.SavedStateHandleProvider
import proton.android.pass.commonui.api.require
import proton.android.pass.crypto.api.context.EncryptionContextProvider
import proton.android.pass.data.api.repositories.DRAFT_CUSTOM_FIELD_KEY
import proton.android.pass.data.api.repositories.DraftRepository
import proton.android.pass.domain.CustomFieldContent
import proton.android.pass.domain.HiddenState
import proton.android.pass.featureitemcreate.impl.bottomsheets.customfield.CustomFieldType
import javax.inject.Inject

@HiltViewModel
class CustomFieldNameViewModel @Inject constructor(
    private val draftRepository: DraftRepository,
    private val encryptionContextProvider: EncryptionContextProvider,
    savedStateHandleProvider: SavedStateHandleProvider
) : ViewModel() {

    private val customFieldType: CustomFieldType = CustomFieldType.valueOf(
        savedStateHandleProvider
            .get()
            .require(CustomFieldTypeNavArgId.key)
    )

    private val eventFlow: MutableStateFlow<CustomFieldEvent> =
        MutableStateFlow(CustomFieldEvent.Unknown)
    private val nameFlow: MutableStateFlow<String> = MutableStateFlow("")

    val state: StateFlow<CustomFieldNameUiState> = combine(
        eventFlow,
        nameFlow
    ) { event, value ->
        CustomFieldNameUiState(
            value = value,
            canConfirm = value.isNotBlank(),
            event = event
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = CustomFieldNameUiState.Initial
    )

    fun onNameChanged(name: String) {
        if (name.contains("\n")) return
        nameFlow.update { name }
    }

    fun onSave() = viewModelScope.launch {
        val field = when (customFieldType) {
            CustomFieldType.Text -> CustomFieldContent.Text(label = nameFlow.value, value = "")
            CustomFieldType.Hidden -> {
                val value = encryptionContextProvider.withEncryptionContext { encrypt("") }
                CustomFieldContent.Hidden(
                    label = nameFlow.value.trim(),
                    value = HiddenState.Empty(encrypted = value)
                )
            }

            CustomFieldType.Totp -> {
                val value = encryptionContextProvider.withEncryptionContext { encrypt("") }
                CustomFieldContent.Totp(
                    label = nameFlow.value,
                    value = HiddenState.Empty(encrypted = value)
                )
            }
        }

        draftRepository.save(DRAFT_CUSTOM_FIELD_KEY, field)
        eventFlow.update { CustomFieldEvent.Close }
    }

}
