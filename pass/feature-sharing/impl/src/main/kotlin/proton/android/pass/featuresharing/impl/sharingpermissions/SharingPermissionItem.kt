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

package proton.android.pass.featuresharing.impl.sharingpermissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.body3Norm
import proton.android.pass.commonui.api.body3Weak
import proton.android.pass.composecomponents.impl.R
import proton.android.pass.composecomponents.impl.container.CircleTextIcon
import proton.android.pass.featuresharing.impl.common.AddressPermissionUiState
import proton.android.pass.featuresharing.impl.extensions.toStringResource

@Composable
fun SharingPermissionItem(
    modifier: Modifier = Modifier,
    address: AddressPermissionUiState,
    onPermissionChangeClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircleTextIcon(
            text = address.address,
            backgroundColor = PassTheme.colors.interactionNormMinor1,
            textColor = PassTheme.colors.interactionNormMajor2,
            shape = PassTheme.shapes.squircleMediumShape
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = address.address,
                style = PassTheme.typography.body3Norm()
            )

            Text(
                text = stringResource(address.permission.toStringResource()),
                style = PassTheme.typography.body3Weak()
            )
        }

        IconButton(
            onClick = onPermissionChangeClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_three_dots_vertical_24),
                contentDescription = stringResource(id = R.string.action_content_description_menu),
                tint = PassTheme.colors.textHint
            )
        }
    }
}
