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

package proton.android.pass.featureitemcreate.impl.note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import me.proton.core.compose.theme.ProtonTheme
import me.proton.core.compose.theme.defaultNorm
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.ThemePairPreviewProvider
import proton.android.pass.composecomponents.impl.form.NoteInputPreviewParameter
import proton.android.pass.composecomponents.impl.form.NoteInputPreviewProvider
import proton.android.pass.composecomponents.impl.form.ProtonTextField
import proton.android.pass.composecomponents.impl.form.ProtonTextFieldPlaceHolder
import proton.android.pass.composecomponents.impl.form.SimpleNoteSection
import proton.android.pass.featureitemcreate.impl.R

@Composable
fun FullNoteSection(
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    value: String,
    enabled: Boolean = true,
    onChange: (String) -> Unit
) {
    ProtonTextField(
        modifier = modifier,
        textFieldModifier = textFieldModifier,
        textStyle = ProtonTheme.typography.defaultNorm(enabled),
        placeholder = { ProtonTextFieldPlaceHolder(text = stringResource(id = R.string.note_hint)) },
        editable = enabled,
        value = value,
        onChange = onChange,
        singleLine = false,
        moveToNextOnEnter = false,
        verticalArrangement = Arrangement.Top,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    )
}

class ThemedFullNoteInputPreviewProvider :
    ThemePairPreviewProvider<NoteInputPreviewParameter>(NoteInputPreviewProvider())

@Preview
@Composable
fun FullNoteSectionPreview(
    @PreviewParameter(ThemedFullNoteInputPreviewProvider::class) input: Pair<Boolean, NoteInputPreviewParameter>
) {
    PassTheme(isDark = input.first) {
        Surface {
            SimpleNoteSection(
                value = input.second.value,
                enabled = input.second.enabled,
                onChange = {}
            )
        }
    }
}
