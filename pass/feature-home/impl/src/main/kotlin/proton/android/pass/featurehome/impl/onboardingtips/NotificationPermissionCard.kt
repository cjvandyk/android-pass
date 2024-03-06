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

package proton.android.pass.featurehome.impl.onboardingtips

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.ThemePreviewProvider
import proton.android.pass.featurehome.impl.R


@Composable
fun NotificationPermissionCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDismiss: () -> Unit
) {
    SpotlightCard(
        modifier = modifier,
        backgroundColor = PassTheme.colors.interactionNormMajor1,
        title = stringResource(id = R.string.home_notification_permission_banner_title),
        body = stringResource(id = R.string.home_notification_permission_banner_text),
        buttonText = stringResource(id = R.string.home_notification_permission_banner_settings),
        image = {
            Image(
                modifier = Modifier.size(100.dp),
                alignment = Alignment.CenterEnd,
                painter = painterResource(id = R.drawable.spotlight_notifications),
                contentDescription = null
            )
        },
        onClick = onClick,
        onDismiss = onDismiss
    )
}

@Preview
@Composable
fun NotificationPermissionCardPreview(@PreviewParameter(ThemePreviewProvider::class) isDark: Boolean) {
    PassTheme(isDark = isDark) {
        Surface {
            NotificationPermissionCard(
                onClick = {},
                onDismiss = {}
            )
        }
    }
}
