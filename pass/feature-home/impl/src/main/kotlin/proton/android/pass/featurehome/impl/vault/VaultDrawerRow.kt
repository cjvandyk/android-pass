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

package proton.android.pass.featurehome.impl.vault

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import proton.android.pass.commonui.api.PassPalette
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.ThemePairPreviewProvider
import proton.android.pass.composecomponents.impl.icon.VaultIcon
import proton.android.pass.featurehome.impl.R

@Composable
fun VaultDrawerRow(
    modifier: Modifier = Modifier,
    name: String,
    itemCount: Long,
    icon: @Composable () -> Unit,
    isShared: Boolean,
    isSelected: Boolean,
    showMenuIcon: Boolean,
    onOptionsClick: () -> Unit = {},
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                vertical = 16.dp,
                horizontal = PassTheme.dimens.bottomsheetHorizontalPadding
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                color = PassTheme.colors.textNorm,
            )
            Text(
                text = stringResource(R.string.vault_drawer_vaults_item_count, itemCount),
                color = PassTheme.colors.textWeak,
            )
        }
        Box(modifier = Modifier.size(16.dp)) {
            if (isShared) {
                Icon(
                    painter = painterResource(me.proton.core.presentation.R.drawable.ic_proton_users),
                    contentDescription = null,
                    tint = PassTheme.colors.textWeak
                )
            }
        }
        Box(modifier = Modifier.size(16.dp)) {
            if (isSelected) {
                Icon(
                    painter = painterResource(me.proton.core.presentation.R.drawable.ic_proton_checkmark),
                    contentDescription = null,
                    tint = PassTheme.colors.loginInteractionNormMajor1
                )
            }
        }

        if (showMenuIcon) {
            IconButton(
                onClick = onOptionsClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        id = proton.android.pass.composecomponents.impl.R.drawable.ic_three_dots_vertical_24
                    ),
                    contentDescription = stringResource(
                        id = proton.android.pass.composecomponents.impl.R.string.action_content_description_menu
                    ),
                    tint = PassTheme.colors.textWeak
                )
            }
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }
    }
}

class ThemeVaultRowPreviewProvider :
    ThemePairPreviewProvider<VaultRowInput>(NavigationDrawerVaultRowPreviewProvider())

@Suppress("MagicNumber")
@Preview
@Composable
fun NavigationDrawerVaultRowPreview(
    @PreviewParameter(ThemeVaultRowPreviewProvider::class) input: Pair<Boolean, VaultRowInput>
) {
    PassTheme(isDark = input.first) {
        Surface {
            VaultDrawerRow(
                name = "test vault",
                itemCount = 123,
                icon = {
                    VaultIcon(
                        icon = me.proton.core.presentation.R.drawable.ic_proton_house,
                        iconColor = PassPalette.MacaroniAndCheese100,
                        backgroundColor = PassPalette.MacaroniAndCheese16,
                    )
                },
                isSelected = input.second.isSelected,
                isShared = input.second.isShared,
                showMenuIcon = input.second.showMenuIcon,
                onOptionsClick = {},
                onClick = {}
            )
        }
    }
}
