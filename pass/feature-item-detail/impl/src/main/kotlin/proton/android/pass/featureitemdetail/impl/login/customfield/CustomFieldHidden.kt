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

package proton.android.pass.featureitemdetail.impl.login.customfield

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import proton.android.pass.composecomponents.impl.container.RoundedCornersColumn
import proton.android.pass.featureitemdetail.impl.login.CustomFieldUiContent
import proton.android.pass.featureitemdetail.impl.login.LoginPasswordRow
import me.proton.core.presentation.R as CoreR

@Composable
fun CustomFieldHidden(
    modifier: Modifier = Modifier,
    entry: CustomFieldUiContent.Hidden,
    onToggleVisibility: () -> Unit,
    onCopyValue: () -> Unit
) {
    RoundedCornersColumn(modifier) {
        LoginPasswordRow(
            passwordHiddenState = entry.content,
            label = entry.label,
            iconRes = CoreR.drawable.ic_proton_eye_slash,
            onTogglePasswordClick = onToggleVisibility,
            onCopyPasswordClick = onCopyValue
        )
    }
}
