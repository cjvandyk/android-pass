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

package proton.android.pass.featureprofile.impl

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.ThemePreviewProvider
import proton.android.pass.composecomponents.impl.container.roundedContainerNorm
import proton.android.pass.composecomponents.impl.setting.SettingOption

@Composable
fun AccountProfileSection(
    modifier: Modifier = Modifier,
    planInfo: PlanInfo,
    onAccountClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = modifier.roundedContainerNorm()
    ) {
        AccountSetting(
            modifier = Modifier.testTag(AccountProfileSectionTestTag.accountSetting),
            planInfo = planInfo,
            onClick = onAccountClick
        )
        Divider(color = PassTheme.colors.inputBorderNorm)
        SettingOption(
            text = stringResource(R.string.profile_option_settings),
            onClick = onSettingsClick
        )
    }
}

object AccountProfileSectionTestTag {
    const val accountSetting = "accountSetting"
}

@Preview
@Composable
fun AccountSettingsSectionPreview(@PreviewParameter(ThemePreviewProvider::class) isDark: Boolean) {
    PassTheme(isDark = isDark) {
        Surface {
            AccountProfileSection(
                planInfo = PlanInfo.Hide,
                onAccountClick = {},
                onSettingsClick = {}
            )
        }
    }
}
