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

package proton.android.pass.featurepassword.impl.bottomsheet.random

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.ThemePreviewProvider
import proton.android.pass.composecomponents.impl.buttons.ShowAdvancedOptionsButton
import proton.android.pass.composecomponents.impl.container.AnimatedVisibilityWithOnComplete
import proton.android.pass.composecomponents.impl.container.rememberAnimatedVisibilityState
import proton.android.pass.composecomponents.impl.form.PassDivider
import proton.android.pass.featurepassword.R
import proton.android.pass.featurepassword.impl.bottomsheet.GeneratePasswordContent
import proton.android.pass.featurepassword.impl.bottomsheet.GeneratePasswordEvent
import proton.android.pass.featurepassword.impl.bottomsheet.GeneratePasswordToggleRow
import proton.android.pass.featurepassword.impl.bottomsheet.GeneratePasswordTypeRow
import proton.android.pass.preferences.PasswordGenerationMode

@Composable
fun GeneratePasswordRandomContent(
    modifier: Modifier = Modifier,
    content: GeneratePasswordContent.RandomPassword,
    onEvent: (GeneratePasswordEvent) -> Unit
) {
    var showAdvancedOptions by rememberSaveable { mutableStateOf(false) }
    val state = rememberAnimatedVisibilityState(initialState = true)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GeneratePasswordTypeRow(
            current = PasswordGenerationMode.Random,
            onClick = {
                onEvent(GeneratePasswordEvent.OnPasswordModeChangeClick)
            }
        )
        PassDivider()
        GeneratePasswordRandomCountRow(
            length = content.length,
            onLengthChange = {
                onEvent(GeneratePasswordEvent.OnRandomLengthChange(it))
            }
        )
        PassDivider()
        GeneratePasswordToggleRow(
            text = stringResource(R.string.special_characters),
            value = content.hasSpecialCharacters,
            onChange = {
                onEvent(GeneratePasswordEvent.OnRandomUseSpecialCharactersChange(it))
            }
        )
        PassDivider()

        AnimatedVisibilityWithOnComplete(
            visibilityState = state,
            onComplete = { showAdvancedOptions = true }
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                ShowAdvancedOptionsButton(
                    currentValue = false,
                    onClick = { state.toggle() }
                )
            }
        }

        AnimatedVisibility(visible = showAdvancedOptions) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GeneratePasswordToggleRow(
                    text = stringResource(R.string.bottomsheet_option_capital_letters),
                    value = content.hasCapitalLetters,
                    onChange = {
                        onEvent(GeneratePasswordEvent.OnRandomUseCapitalLettersChange(it))
                    }
                )
                PassDivider()
                GeneratePasswordToggleRow(
                    text = stringResource(R.string.bottomsheet_option_include_numbers),
                    value = content.includeNumbers,
                    onChange = {
                        onEvent(GeneratePasswordEvent.OnRandomIncludeNumbersChange(it))
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun GeneratePasswordRandomContentPreview(@PreviewParameter(ThemePreviewProvider::class) isDark: Boolean) {
    PassTheme(isDark = isDark) {
        Surface {
            GeneratePasswordRandomContent(
                content = GeneratePasswordContent.RandomPassword(
                    length = 12,
                    hasSpecialCharacters = true,
                    hasCapitalLetters = false,
                    includeNumbers = true
                ),
                onEvent = {}
            )
        }
    }
}
