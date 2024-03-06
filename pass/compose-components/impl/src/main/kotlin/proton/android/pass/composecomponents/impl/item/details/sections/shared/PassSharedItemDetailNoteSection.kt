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

package proton.android.pass.composecomponents.impl.item.details.sections.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import proton.android.pass.composecomponents.impl.R
import proton.android.pass.composecomponents.impl.container.RoundedCornersColumn
import proton.android.pass.composecomponents.impl.item.details.rows.PassItemDetailFieldRow
import proton.android.pass.composecomponents.impl.utils.PassItemColors
import me.proton.core.presentation.R as CoreR

@Composable
internal fun PassSharedItemDetailNoteSection(
    modifier: Modifier = Modifier,
    note: String,
    itemColors: PassItemColors
) {
    RoundedCornersColumn(
        modifier = modifier
    ) {
        PassItemDetailFieldRow(
            icon = painterResource(CoreR.drawable.ic_proton_note),
            title = stringResource(R.string.item_details_shared_section_note_title),
            subtitle = note,
            itemColors = itemColors,
            isSelectable = true
        )
    }
}
