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

package proton.android.pass.composecomponents.impl.item.details.titles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.Spacing
import proton.android.pass.commonui.api.applyIf
import proton.android.pass.commonui.api.body3Norm
import proton.android.pass.composecomponents.impl.extension.toColor
import proton.android.pass.composecomponents.impl.extension.toSmallResource
import proton.android.pass.domain.Vault

@Composable
internal fun PassItemDetailSubtitle(
    modifier: Modifier = Modifier,
    vault: Vault,
    onClick: () -> Unit
) {
    val vaultText = remember(vault.shared, vault.members) {
        if (vault.shared) {
            buildAnnotatedString {
                append(vault.name)
                append(" • ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(vault.members.toString())
                }
            }
        } else {
            AnnotatedString(vault.name)
        }
    }
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = vault.color.toColor(isBackground = true),
                shape = RoundedCornerShape(24.dp)
            )
            .applyIf(
                condition = vault.shared,
                ifTrue = {
                    background(
                        color = vault.color.toColor(isBackground = true),
                        shape = RoundedCornerShape(24.dp)
                    )
                        .clip(RoundedCornerShape(24.dp))
                        .clickable(onClick = onClick)
                }
            )
            .padding(
                horizontal = Spacing.small,
                vertical = Spacing.extraSmall
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.extraSmall)
    ) {
        Icon(
            modifier = Modifier.height(12.dp),
            painter = painterResource(vault.icon.toSmallResource()),
            contentDescription = null,
            tint = vault.color.toColor()
        )

        Text(
            text = vaultText,
            style = PassTheme.typography.body3Norm(),
            color = vault.color.toColor()
        )
    }
}
