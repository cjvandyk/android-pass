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

package proton.android.pass.featuresharing.impl.sharefromitem

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import me.proton.core.compose.theme.ProtonTheme
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.Spacing
import proton.android.pass.commonui.api.ThemedBooleanPreviewProvider
import proton.android.pass.commonui.api.body3Weak
import proton.android.pass.composecomponents.impl.container.roundedContainerNorm
import proton.android.pass.composecomponents.impl.icon.PassPlusIcon
import me.proton.core.presentation.R as CoreR

@Composable
internal fun ShareItemSecureLinkRow(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    title: String,
    description: String,
    shouldShowPlusIcon: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .roundedContainerNorm()
            .clickable(onClick = onClick)
            .padding(Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(PassTheme.colors.interactionNormMinor1)
                .padding(all = Spacing.small),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = iconResId),
                contentDescription = null,
                tint = PassTheme.colors.interactionNormMajor2
            )
        }

        Column(
            modifier = Modifier.weight(1f, fill = true),
            verticalArrangement = Arrangement.spacedBy(space = Spacing.extraSmall)
        ) {
            Text(
                text = title,
                style = ProtonTheme.typography.body2Regular,
                color = PassTheme.colors.textNorm
            )

            Text(
                text = description,
                style = PassTheme.typography.body3Weak(),
                color = PassTheme.colors.textWeak
            )
        }

        if (shouldShowPlusIcon) {
            PassPlusIcon()
        }
    }
}

@[Preview Composable]
internal fun ShareItemSecureLinkRowPreview(
    @PreviewParameter(ThemedBooleanPreviewProvider::class) input: Pair<Boolean, Boolean>
) {
    val (isDark, shouldShowPlusIcon) = input

    PassTheme(isDark = isDark) {
        Surface {
            ShareItemSecureLinkRow(
                iconResId = CoreR.drawable.ic_proton_link,
                title = "Share item row title",
                description = "Share item row description.",
                shouldShowPlusIcon = shouldShowPlusIcon,
                onClick = {}
            )
        }
    }
}
