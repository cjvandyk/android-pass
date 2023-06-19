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

package proton.android.pass.featureitemcreate.impl.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import me.proton.core.compose.component.appbar.ProtonTopAppBar
import me.proton.core.compose.theme.ProtonTheme
import me.proton.core.compose.theme.defaultSmallInverted
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.ThemePairPreviewProvider
import proton.android.pass.composecomponents.impl.buttons.LoadingCircleButton
import proton.android.pass.composecomponents.impl.buttons.UpgradeButton
import proton.android.pass.composecomponents.impl.container.Circle
import proton.android.pass.featureitemcreate.impl.R

@ExperimentalComposeUiApi
@Composable
internal fun CreateUpdateTopBar(
    modifier: Modifier = Modifier,
    text: String,
    isLoading: Boolean,
    actionColor: Color,
    iconColor: Color,
    showUpgrade: Boolean = false,
    iconBackgroundColor: Color,
    onCloseClick: () -> Unit,
    onActionClick: () -> Unit,
    onUpgrade: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    ProtonTopAppBar(
        modifier = modifier,
        title = { },
        navigationIcon = {
            Circle(
                modifier = Modifier.padding(12.dp, 4.dp),
                backgroundColor = iconBackgroundColor,
                onClick = {
                    keyboardController?.hide()
                    onCloseClick()
                }
            ) {
                Icon(
                    painter = painterResource(me.proton.core.presentation.R.drawable.ic_proton_cross_small),
                    contentDescription = stringResource(R.string.close_scree_icon_content_description),
                    tint = iconColor
                )
            }
        },
        actions = {
            if (showUpgrade) {
                UpgradeButton(
                    modifier = Modifier.padding(12.dp, 0.dp),
                    color = actionColor,
                    onUpgradeClick = onUpgrade
                )
            } else {
                LoadingCircleButton(
                    modifier = Modifier.padding(12.dp, 4.dp),
                    color = actionColor,
                    isLoading = isLoading,
                    text = {
                        Text(
                            text = text,
                            style = ProtonTheme.typography.defaultSmallInverted,
                            color = PassTheme.colors.textInvert
                        )
                    },
                    onClick = {
                        keyboardController?.hide()
                        onActionClick()
                    }
                )
            }
        }
    )
}

class ThemeAndCreateUpdateTopBarProvider :
    ThemePairPreviewProvider<CreateUpdateTopBarPreview>(CreateUpdateTopBarPreviewProvider())

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun CreateUpdateTopBarPreview(
    @PreviewParameter(ThemeAndCreateUpdateTopBarProvider::class) input: Pair<Boolean, CreateUpdateTopBarPreview>
) {
    PassTheme(isDark = input.first) {
        Surface {
            CreateUpdateTopBar(
                text = "Save",
                isLoading = input.second.isLoading,
                actionColor = input.second.actionColor,
                iconBackgroundColor = input.second.closeBackgroundColor,
                iconColor = input.second.closeIconColor,
                showUpgrade = input.second.showUpgrade,
                onCloseClick = {},
                onActionClick = {},
                onUpgrade = {}
            )
        }
    }
}
