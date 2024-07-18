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

package proton.android.pass.composecomponents.impl.item.details.sections.identity

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.collections.immutable.toPersistentList
import proton.android.pass.commonpresentation.api.items.details.domain.ItemDetailsFieldSection
import proton.android.pass.commonpresentation.api.items.details.domain.ItemDetailsFieldType
import proton.android.pass.composecomponents.impl.R
import proton.android.pass.composecomponents.impl.item.details.PassItemDetailsUiEvent
import proton.android.pass.composecomponents.impl.item.details.rows.addItemDetailsFieldRow
import proton.android.pass.composecomponents.impl.item.details.sections.identity.shared.rows.addCustomFieldRows
import proton.android.pass.composecomponents.impl.item.details.sections.identity.shared.sections.PassIdentityItemDetailsSection
import proton.android.pass.composecomponents.impl.utils.PassItemColors
import proton.android.pass.domain.ItemDiffs
import proton.android.pass.domain.PersonalDetailsContent

@Composable
internal fun PassIdentityItemDetailsPersonalSection(
    modifier: Modifier = Modifier,
    personalDetailsContent: PersonalDetailsContent,
    itemColors: PassItemColors,
    itemDiffs: ItemDiffs.Identity,
    onEvent: (PassItemDetailsUiEvent) -> Unit
) = with(personalDetailsContent) {
    val rows = mutableListOf<@Composable () -> Unit>()

    if (hasFirstName) {
        rows.addItemDetailsFieldRow(
            titleResId = R.string.item_details_identity_section_personal_first_name_title,
            section = firstName,
            field = ItemDetailsFieldType.Plain.FirstName,
            itemColors = itemColors,
            itemDiffType = itemDiffs.firstName,
            onEvent = onEvent
        )
    }

    if (hasMiddleName) {
        rows.addItemDetailsFieldRow(
            titleResId = R.string.item_details_identity_section_personal_middle_name_title,
            section = middleName,
            field = ItemDetailsFieldType.Plain.MiddleName,
            itemColors = itemColors,
            itemDiffType = itemDiffs.middleName,
            onEvent = onEvent
        )
    }

    if (hasLastName) {
        rows.addItemDetailsFieldRow(
            titleResId = R.string.item_details_identity_section_personal_last_name_title,
            section = lastName,
            field = ItemDetailsFieldType.Plain.LastName,
            itemColors = itemColors,
            itemDiffType = itemDiffs.lastName,
            onEvent = onEvent
        )
    }

    if (hasFullName) {
        rows.addItemDetailsFieldRow(
            titleResId = R.string.item_details_identity_section_personal_full_name_title,
            section = fullName,
            field = ItemDetailsFieldType.Plain.FullName,
            itemColors = itemColors,
            itemDiffType = itemDiffs.fullName,
            onEvent = onEvent
        )
    }

    if (hasEmail) {
        rows.addItemDetailsFieldRow(
            titleResId = R.string.item_details_identity_section_personal_email_title,
            section = email,
            field = ItemDetailsFieldType.Plain.Email,
            itemColors = itemColors,
            itemDiffType = itemDiffs.email,
            onEvent = onEvent
        )
    }

    if (hasGender) {
        rows.addItemDetailsFieldRow(
            titleResId = R.string.item_details_identity_section_personal_gender_title,
            section = gender,
            field = ItemDetailsFieldType.Plain.Gender,
            itemColors = itemColors,
            itemDiffType = itemDiffs.gender,
            onEvent = onEvent
        )
    }

    if (hasPhoneNumber) {
        rows.addItemDetailsFieldRow(
            titleResId = R.string.item_details_identity_section_personal_phone_title,
            section = phoneNumber,
            field = ItemDetailsFieldType.Plain.PhoneNumber,
            itemColors = itemColors,
            itemDiffType = itemDiffs.phoneNumber,
            onEvent = onEvent
        )
    }

    if (hasBirthdate) {
        rows.addItemDetailsFieldRow(
            titleResId = R.string.item_details_identity_section_personal_birthday_title,
            section = birthdate,
            field = ItemDetailsFieldType.Plain.BirthDate,
            itemColors = itemColors,
            itemDiffType = itemDiffs.birthdate,
            onEvent = onEvent
        )
    }

    if (hasCustomFields) {
        rows.addCustomFieldRows(
            customFields = customFields,
            customFieldSection = ItemDetailsFieldSection.Identity.Personal,
            itemColors = itemColors,
            onEvent = onEvent
        )
    }

    PassIdentityItemDetailsSection(
        modifier = modifier,
        title = stringResource(id = R.string.item_details_identity_section_personal_title),
        sections = rows.toPersistentList()
    )
}
