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

package proton.android.pass.featuresettings.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.ThemePreviewProvider
import proton.android.pass.commonui.api.bottomSheet
import proton.android.pass.composecomponents.impl.bottomsheet.BottomSheetItem
import proton.android.pass.composecomponents.impl.bottomsheet.BottomSheetItemIcon
import proton.android.pass.composecomponents.impl.bottomsheet.BottomSheetItemList
import proton.android.pass.composecomponents.impl.bottomsheet.BottomSheetItemTitle
import proton.android.pass.composecomponents.impl.bottomsheet.withDividers
import proton.android.pass.preferences.ThemePreference
import proton.android.pass.preferences.ThemePreference.Dark
import proton.android.pass.preferences.ThemePreference.Light
import proton.android.pass.preferences.ThemePreference.System

@Composable
fun ThemeSelectionBottomSheetContents(
    modifier: Modifier = Modifier,
    themePreference: ThemePreference,
    onThemeSelected: (ThemePreference) -> Unit
) {
    Column(
        modifier = modifier.bottomSheet(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BottomSheetItemList(
            items = themeItemList(
                themePreference = themePreference,
                onThemeSelected = onThemeSelected
            )
                .withDividers()
                .toPersistentList()
        )
    }
}

private fun themeItemList(
    themePreference: ThemePreference,
    onThemeSelected: (ThemePreference) -> Unit
): ImmutableList<BottomSheetItem> =
    ThemePreference.values()
        .map {
            object : BottomSheetItem {
                override val title: @Composable () -> Unit
                    get() = {
                        val title = when (it) {
                            System -> stringResource(R.string.settings_appearance_preference_subtitle_match_system)
                            Dark -> stringResource(R.string.settings_appearance_preference_subtitle_dark)
                            Light -> stringResource(R.string.settings_appearance_preference_subtitle_light)
                        }
                        val color = if (it == themePreference) {
                            PassTheme.colors.interactionNorm
                        } else {
                            PassTheme.colors.textNorm
                        }
                        BottomSheetItemTitle(text = title, color = color)
                    }
                override val subtitle: @Composable (() -> Unit)? = null
                override val leftIcon: @Composable (() -> Unit)? = null
                override val endIcon: (@Composable () -> Unit)?
                    get() = if (it == themePreference) {
                        {
                            BottomSheetItemIcon(
                                iconId = me.proton.core.presentation.R.drawable.ic_proton_checkmark,
                                tint = PassTheme.colors.interactionNormMajor1
                            )
                        }
                    } else null
                override val onClick: () -> Unit
                    get() = { onThemeSelected(it) }
                override val isDivider = false
            }
        }
        .toImmutableList()

@Preview
@Composable
fun ThemeSelectionBottomSheetContentsPreview(
    @PreviewParameter(ThemePreviewProvider::class) isDark: Boolean
) {
    PassTheme(isDark = isDark) {
        Surface {
            ThemeSelectionBottomSheetContents(
                themePreference = Light,
                onThemeSelected = {}
            )
        }
    }
}
