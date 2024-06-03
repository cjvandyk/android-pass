/*
 * Copyright (c) 2023-2024 Proton AG
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

import proton.android.pass.featureitemcreate.impl.bottomsheets.customfield.CustomFieldType
import proton.android.pass.featureitemcreate.impl.identity.navigation.bottomsheets.AddIdentityFieldType

sealed interface BaseIdentityNavigation {
    data object Close : BaseIdentityNavigation

    @JvmInline
    value class OpenExtraFieldBottomSheet(
        val addIdentityFieldType: AddIdentityFieldType
    ) : BaseIdentityNavigation

    data object OpenCustomFieldBottomSheet : BaseIdentityNavigation

    @JvmInline
    value class CustomFieldTypeSelected(val type: CustomFieldType) : BaseIdentityNavigation
}
