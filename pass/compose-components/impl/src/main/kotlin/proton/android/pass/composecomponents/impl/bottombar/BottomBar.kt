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

package proton.android.pass.composecomponents.impl.bottombar

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.ThemePreviewProvider
import proton.android.pass.composecomponents.impl.R

@Stable
enum class BottomBarSelected {
    Home,
    Profile,
    SecurityCenter
}

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    bottomBarSelected: BottomBarSelected,
    accountType: AccountType,
    onListClick: () -> Unit,
    onCreateClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSecurityCenterClick: () -> Unit
) {
    BottomNavigation(
        modifier = modifier,
        backgroundColor = PassTheme.colors.bottomBarBackground
    ) {
        BottomNavigationItem(
            selected = bottomBarSelected == BottomBarSelected.Home,
            selectedContentColor = PassTheme.colors.interactionNormMajor2,
            unselectedContentColor = PassTheme.colors.textNorm,
            onClick = onListClick,
            icon = {
                Icon(
                    painter = painterResource(me.proton.core.presentation.R.drawable.ic_proton_list_bullets),
                    contentDescription = stringResource(R.string.bottom_bar_list_items_icon_content_description)
                )
            }
        )

        BottomNavigationItem(
            selected = false,
            selectedContentColor = PassTheme.colors.interactionNormMajor2,
            unselectedContentColor = PassTheme.colors.textNorm,
            onClick = onCreateClick,
            icon = {
                Icon(
                    painter = painterResource(me.proton.core.presentation.R.drawable.ic_proton_plus),
                    contentDescription = stringResource(R.string.bottom_bar_add_item_icon_content_description)
                )
            }
        )

        BottomNavigationItem(
            selected = bottomBarSelected == BottomBarSelected.SecurityCenter,
            selectedContentColor = PassTheme.colors.interactionNormMajor2,
            unselectedContentColor = PassTheme.colors.textNorm,
            onClick = onSecurityCenterClick,
            icon = {
                Icon(
                    painter = painterResource(me.proton.core.presentation.R.drawable.ic_proton_shield_half_filled),
                    contentDescription = stringResource(R.string.bottom_bar_security_center_icon_content_description)
                )
            }
        )

        BottomNavigationItem(
            modifier = Modifier.testTag(BottomBarTestTag.PROFILE),
            selected = bottomBarSelected == BottomBarSelected.Profile,
            selectedContentColor = PassTheme.colors.interactionNormMajor2,
            unselectedContentColor = PassTheme.colors.textNorm,
            onClick = onProfileClick,
            icon = {
                ProfileBottomBarIcon(accountType = accountType)
            }
        )
    }
}

object BottomBarTestTag {
    const val PROFILE = "profile"
}

@Preview
@Composable
fun BottomBarPreview(@PreviewParameter(ThemePreviewProvider::class) isDark: Boolean) {
    PassTheme(isDark = isDark) {
        Surface {
            BottomBar(
                bottomBarSelected = BottomBarSelected.Home,
                accountType = AccountType.Free,
                onListClick = {},
                onCreateClick = {},
                onProfileClick = {},
                onSecurityCenterClick = {}
            )
        }
    }
}
