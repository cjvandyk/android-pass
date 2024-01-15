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

package proton.android.pass.featureitemdetail.impl.creditcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.composecomponents.impl.item.icon.CreditCardIcon
import proton.android.pass.composecomponents.impl.pinning.BoxedPin
import proton.android.pass.domain.Vault
import proton.android.pass.featureitemdetail.impl.common.ItemTitleInput
import proton.android.pass.featureitemdetail.impl.common.ItemTitleText
import proton.android.pass.featureitemdetail.impl.common.ThemeItemTitleProvider
import proton.android.pass.featureitemdetail.impl.common.VaultNameSubtitle

@Composable
fun CreditCardTitle(
    modifier: Modifier = Modifier,
    title: String,
    vault: Vault?,
    onVaultClick: () -> Unit,
    isPinned: Boolean,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BoxedPin(
            pinBackgroundColor = PassTheme.colors.cardInteractionNormMajor2,
            isShown = isPinned,
        ) {
            CreditCardIcon(size = 60, shape = PassTheme.shapes.squircleMediumLargeShape)
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ItemTitleText(text = title)
            VaultNameSubtitle(vault = vault, onClick = onVaultClick)
        }
    }
}

@Preview
@Composable
fun AliasTitlePreview(
    @PreviewParameter(ThemeItemTitleProvider::class) input: Pair<Boolean, ItemTitleInput>
) {
    PassTheme(isDark = input.first) {
        Surface {
            CreditCardTitle(
                title = input.second.title,
                vault = input.second.vault,
                onVaultClick = {},
                isPinned = false,
            )
        }
    }
}

