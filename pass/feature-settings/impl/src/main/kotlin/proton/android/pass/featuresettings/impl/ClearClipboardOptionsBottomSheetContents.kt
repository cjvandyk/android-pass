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

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
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
import proton.android.pass.preferences.ClearClipboardPreference
import proton.android.pass.preferences.ClearClipboardPreference.Never
import proton.android.pass.preferences.ClearClipboardPreference.S180
import proton.android.pass.preferences.ClearClipboardPreference.S60

@Composable
fun ClearClipboardOptionsBottomSheetContents(
    modifier: Modifier = Modifier,
    clearClipboardPreference: ClearClipboardPreference,
    onClearClipboardSettingSelected: (ClearClipboardPreference) -> Unit
) {
    BottomSheetItemList(
        modifier = modifier.bottomSheet(),
        items = clearClipboardItemList(
            clearClipboardPreference,
            onClearClipboardSettingSelected
        ).withDividers().toPersistentList()
    )
}

private fun clearClipboardItemList(
    clearClipboardPreference: ClearClipboardPreference,
    onClearClipboardPreferenceSelected: (ClearClipboardPreference) -> Unit
): ImmutableList<BottomSheetItem> = listOf(S60, S180, Never)
    .map {
        object : BottomSheetItem {
            override val title: @Composable () -> Unit
                get() = {
                    val clearClipboardString = when (it) {
                        Never -> stringResource(R.string.clipboard_option_clear_clipboard_never)
                        S60 -> stringResource(R.string.clipboard_option_clear_clipboard_after_60_seconds)
                        S180 -> stringResource(R.string.clipboard_option_clear_clipboard_after_180_seconds)
                    }
                    val color = if (it == clearClipboardPreference) {
                        PassTheme.colors.interactionNorm
                    } else {
                        PassTheme.colors.textNorm
                    }
                    BottomSheetItemTitle(text = clearClipboardString, color = color)
                }
            override val subtitle: @Composable (() -> Unit)?
                get() = null
            override val leftIcon: @Composable (() -> Unit)?
                get() = null
            override val endIcon: @Composable (() -> Unit)?
                get() = if (it == clearClipboardPreference) {
                    {
                        BottomSheetItemIcon(
                            iconId = me.proton.core.presentation.R.drawable.ic_proton_checkmark,
                            tint = PassTheme.colors.interactionNormMajor1
                        )
                    }
                } else null
            override val onClick: () -> Unit
                get() = { onClearClipboardPreferenceSelected(it) }
            override val isDivider = false
        }
    }
    .toImmutableList()

@Preview
@Composable
fun ClearClipboardOptionsBSContentsPreview(@PreviewParameter(ThemePreviewProvider::class) isDark: Boolean) {
    PassTheme(isDark = isDark) {
        Surface {
            ClearClipboardOptionsBottomSheetContents(clearClipboardPreference = S180) {}
        }
    }
}
