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

package proton.android.pass.featureitemcreate.impl.identity.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import proton.android.pass.common.api.Option
import proton.android.pass.commonui.api.RequestFocusLaunchedEffect
import proton.android.pass.commonui.api.Spacing
import proton.android.pass.featureitemcreate.impl.common.customfields.AddMoreButton
import proton.android.pass.featureitemcreate.impl.common.customfields.CustomFieldEntry
import proton.android.pass.featureitemcreate.impl.identity.navigation.IdentityContentEvent
import proton.android.pass.featureitemcreate.impl.identity.navigation.IdentityContentEvent.OnAddExtraSectionCustomField
import proton.android.pass.featureitemcreate.impl.identity.navigation.IdentityContentEvent.OnCustomFieldFocused
import proton.android.pass.featureitemcreate.impl.identity.navigation.IdentityContentEvent.OnCustomFieldOptions
import proton.android.pass.featureitemcreate.impl.identity.navigation.IdentityContentEvent.OnFieldChange
import proton.android.pass.featureitemcreate.impl.identity.presentation.FieldChange
import proton.android.pass.featureitemcreate.impl.identity.presentation.UIExtraSection
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.ExtraSectionCustomField
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.FocusedField

@Composable
fun ExtraSection(
    modifier: Modifier = Modifier,
    section: UIExtraSection,
    enabled: Boolean,
    sectionIndex: Int,
    focusedField: Option<FocusedField>,
    onEvent: (IdentityContentEvent) -> Unit
) {
    val field = focusedField.value()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        section.customFields.forEachIndexed { index, value ->
            val focusRequester = remember { FocusRequester() }
            CustomFieldEntry(
                modifier = Modifier.focusRequester(focusRequester),
                entry = value,
                canEdit = enabled,
                isError = false,
                errorMessage = "",
                index = index,
                onValueChange = {
                    val fieldChange = FieldChange.CustomField(
                        sectionType = IdentitySectionType.ExtraSection(sectionIndex),
                        customFieldType = value.toCustomFieldType(),
                        index = index,
                        value = it
                    )
                    onEvent(OnFieldChange(fieldChange))
                },
                onFocusChange = { idx, isFocused ->
                    onEvent(OnCustomFieldFocused(idx, isFocused, ExtraSectionCustomField(sectionIndex)))
                },
                onOptionsClick = {
                    onEvent(
                        OnCustomFieldOptions(
                            index = index,
                            label = value.label,
                            customExtraField = ExtraSectionCustomField(sectionIndex)
                        )
                    )
                }
            )
            RequestFocusLaunchedEffect(
                focusRequester = focusRequester,
                requestFocus = field?.extraField is ExtraSectionCustomField &&
                    (field.extraField as? ExtraSectionCustomField)?.index == sectionIndex &&
                    field.index == index,
                callback = { onEvent(IdentityContentEvent.ClearLastAddedFieldFocus) }
            )
        }
        AddMoreButton(onClick = { onEvent(OnAddExtraSectionCustomField(sectionIndex)) })
    }
}
