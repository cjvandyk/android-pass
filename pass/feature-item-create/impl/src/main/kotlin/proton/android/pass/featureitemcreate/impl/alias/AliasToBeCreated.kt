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

package proton.android.pass.featureitemcreate.impl.alias

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.ThemePairPreviewProvider
import proton.android.pass.composecomponents.impl.container.roundedContainerNorm
import proton.android.pass.composecomponents.impl.form.ProtonTextFieldLabel
import proton.android.pass.featureitemcreate.impl.R
import proton.android.pass.featureitemcreate.impl.alias.mailboxes.AliasToBeCreatedInput
import proton.android.pass.featureitemcreate.impl.alias.mailboxes.AliasToBeCreatedPreviewProvider

@Composable
fun AliasToBeCreated(
    modifier: Modifier = Modifier,
    prefix: String,
    suffix: AliasSuffixUiModel?,
    isError: Boolean = false
) {
    Row(
        modifier = modifier
            .roundedContainerNorm()
            .background(PassTheme.colors.backgroundNorm)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(me.proton.core.presentation.R.drawable.ic_proton_alias),
            contentDescription = null,
            tint = PassTheme.colors.aliasInteractionNorm
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            ProtonTextFieldLabel(
                text = stringResource(id = R.string.field_alias_you_are_about_to_create),
                isError = isError
            )
            AliasPrefixSuffixText(
                prefix = prefix,
                suffix = suffix?.suffix ?: "",
                suffixColor = PassTheme.colors.aliasInteractionNormMajor2
            )
        }
    }
}

class ThemedAliasToBeCreatedPreviewProvider :
    ThemePairPreviewProvider<AliasToBeCreatedInput>(AliasToBeCreatedPreviewProvider())

@Preview
@Composable
fun AliasToBeCreatedPreview(
    @PreviewParameter(ThemedAliasToBeCreatedPreviewProvider::class) input: Pair<Boolean, AliasToBeCreatedInput>
) {
    PassTheme(isDark = input.first) {
        Surface {
            AliasToBeCreated(
                prefix = input.second.prefix,
                suffix = input.second.suffix
            )
        }
    }
}
