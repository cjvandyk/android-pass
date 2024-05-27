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

package proton.android.pass.featureitemcreate.impl.login

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import me.proton.core.compose.theme.ProtonTheme
import me.proton.core.compose.theme.defaultNorm
import proton.android.pass.commonui.api.Spacing
import proton.android.pass.composecomponents.impl.form.ProtonTextField
import proton.android.pass.composecomponents.impl.form.ProtonTextFieldLabel
import proton.android.pass.composecomponents.impl.form.ProtonTextFieldPlaceHolder
import proton.android.pass.featureitemcreate.impl.R

@Composable
internal fun EmailInput(
    modifier: Modifier = Modifier,
    email: String,
    onEmailChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    leadingIcon: @Composable () -> Unit,
    trailingIcon: @Composable () -> Unit,
    isEditable: Boolean,
    isInvalid: Boolean
) {
    ProtonTextField(
        modifier = modifier.padding(
            start = Spacing.none,
            top = Spacing.medium,
            end = Spacing.extraSmall,
            bottom = Spacing.medium
        ),
        value = email,
        isError = isInvalid,
        errorMessage = stringResource(id = R.string.field_email_error),
        onChange = onEmailChange,
        onFocusChange = onFocusChange,
        textStyle = ProtonTheme.typography.defaultNorm(isEditable),
        leadingIcon = leadingIcon,
        label = {
            ProtonTextFieldLabel(
                text = stringResource(id = R.string.field_email_title),
                isError = isInvalid
            )
        },
        placeholder = {
            ProtonTextFieldPlaceHolder(text = stringResource(id = R.string.field_email_hint))
        },
        trailingIcon = trailingIcon
    )
}
