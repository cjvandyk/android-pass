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
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.Spacing
import proton.android.pass.commonui.api.ThemePreviewProvider
import proton.android.pass.composecomponents.impl.container.roundedContainerNorm
import proton.android.pass.composecomponents.impl.form.PassDivider
import proton.android.pass.featureitemcreate.impl.common.customfields.CustomFieldEntry
import proton.android.pass.featureitemcreate.impl.identity.navigation.IdentityContentEvent
import proton.android.pass.featureitemcreate.impl.identity.navigation.IdentityContentEvent.OnFieldChange
import proton.android.pass.featureitemcreate.impl.identity.presentation.FieldChange
import proton.android.pass.featureitemcreate.impl.identity.presentation.UIPersonalDetails
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.Birthdate
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.FirstName
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.Gender
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.LastName
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.MiddleName
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.PersonalCustomField
import proton.android.pass.featureitemcreate.impl.identity.presentation.bottomsheets.PersonalDetailsField
import proton.android.pass.featureitemcreate.impl.identity.ui.inputfields.BirthdateInput
import proton.android.pass.featureitemcreate.impl.identity.ui.inputfields.EmailInput
import proton.android.pass.featureitemcreate.impl.identity.ui.inputfields.FirstNameInput
import proton.android.pass.featureitemcreate.impl.identity.ui.inputfields.FullNameInput
import proton.android.pass.featureitemcreate.impl.identity.ui.inputfields.GenderInput
import proton.android.pass.featureitemcreate.impl.identity.ui.inputfields.LastNameInput
import proton.android.pass.featureitemcreate.impl.identity.ui.inputfields.MiddleNameInput
import proton.android.pass.featureitemcreate.impl.identity.ui.inputfields.PhoneNumberInput

@Composable
internal fun PersonalDetails(
    modifier: Modifier = Modifier,
    uiPersonalDetails: UIPersonalDetails,
    enabled: Boolean,
    extraFields: PersistentSet<PersonalDetailsField>,
    onEvent: (IdentityContentEvent) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        Column(
            modifier = Modifier.roundedContainerNorm()
        ) {
            FullNameInput(
                value = uiPersonalDetails.fullName,
                enabled = enabled,
                onChange = { onEvent(OnFieldChange(FieldChange.FullName(it))) }
            )
            PassDivider()
            EmailInput(
                value = uiPersonalDetails.email,
                enabled = enabled,
                onChange = { onEvent(OnFieldChange(FieldChange.Email(it))) }
            )
            PassDivider()
            PhoneNumberInput(
                value = uiPersonalDetails.phoneNumber,
                enabled = enabled,
                onChange = { onEvent(OnFieldChange(FieldChange.PhoneNumber(it))) }
            )

            if (extraFields.contains(FirstName)) {
                PassDivider()
                FirstNameInput(
                    value = uiPersonalDetails.firstName,
                    enabled = enabled,
                    onChange = { onEvent(OnFieldChange(FieldChange.FirstName(it))) }
                )
            }
            if (extraFields.contains(MiddleName)) {
                PassDivider()
                MiddleNameInput(
                    value = uiPersonalDetails.middleName,
                    enabled = enabled,
                    onChange = { onEvent(OnFieldChange(FieldChange.MiddleName(it))) }
                )
            }
            if (extraFields.contains(LastName)) {
                PassDivider()
                LastNameInput(
                    value = uiPersonalDetails.lastName,
                    enabled = enabled,
                    onChange = { onEvent(OnFieldChange(FieldChange.LastName(it))) }
                )
            }
            if (extraFields.contains(Birthdate)) {
                PassDivider()
                BirthdateInput(
                    value = uiPersonalDetails.birthdate,
                    enabled = enabled,
                    onChange = { onEvent(OnFieldChange(FieldChange.Birthdate(it))) }
                )
            }
            if (extraFields.contains(Gender)) {
                PassDivider()
                GenderInput(
                    value = uiPersonalDetails.gender,
                    enabled = enabled,
                    onChange = { onEvent(OnFieldChange(FieldChange.Gender(it))) }
                )
            }
        }
        if (extraFields.contains(PersonalCustomField)) {
            uiPersonalDetails.customFields.forEachIndexed { index, value ->
                CustomFieldEntry(
                    entry = value,
                    canEdit = enabled,
                    isError = false,
                    errorMessage = "",
                    index = index,
                    onValueChange = {
                        val fieldChange = FieldChange.CustomField(
                            sectionType = IdentitySectionType.PersonalDetails,
                            customFieldType = value.toCustomFieldType(),
                            index = index,
                            value = it
                        )
                        onEvent(OnFieldChange(fieldChange))
                    },
                    onFocusChange = { _, _ -> },
                    onOptionsClick = { }
                )
            }
        }
        AddMoreButton(onClick = { onEvent(IdentityContentEvent.OnAddPersonalDetailField) })
    }
}

@Preview
@Composable
fun PersonalDetailsPreview(@PreviewParameter(ThemePreviewProvider::class) isDark: Boolean) {
    PassTheme(isDark = isDark) {
        Surface {
            PersonalDetails(
                uiPersonalDetails = UIPersonalDetails.EMPTY,
                enabled = true,
                extraFields = persistentSetOf(),
                onEvent = { }
            )
        }
    }
}
