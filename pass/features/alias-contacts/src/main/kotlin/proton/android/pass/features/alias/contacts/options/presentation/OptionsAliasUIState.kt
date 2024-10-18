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

package proton.android.pass.features.alias.contacts.options.presentation

import androidx.compose.runtime.Stable
import proton.android.pass.domain.aliascontacts.Contact

@Stable
internal data class OptionsAliasUIState(
    val event: OptionsAliasEvent,
    val isBlockLoading: Boolean,
    val isDeleteLoading: Boolean,
    val contact: Contact?
) {
    val isAnyLoading: Boolean
        get() = isBlockLoading || isDeleteLoading

    internal companion object {
        internal val Initial = OptionsAliasUIState(
            event = OptionsAliasEvent.Idle,
            isBlockLoading = false,
            isDeleteLoading = false,
            contact = null
        )
    }
}
