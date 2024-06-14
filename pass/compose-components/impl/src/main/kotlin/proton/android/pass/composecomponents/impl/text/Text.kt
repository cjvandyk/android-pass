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

package proton.android.pass.composecomponents.impl.text

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.proton.core.compose.theme.ProtonTheme
import me.proton.core.compose.theme.defaultNorm
import me.proton.core.compose.theme.headlineNorm
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.body3Norm

object Text {
    @Composable
    fun Headline(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = ProtonTheme.typography.headlineNorm.color
    ) {
        Text(
            text = text,
            style = ProtonTheme.typography.headlineNorm.copy(color = color),
            modifier = modifier
        )
    }

    @Composable
    fun Body1Regular(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = ProtonTheme.typography.defaultNorm.color
    ) {
        Text(
            text = text,
            style = ProtonTheme.typography.defaultNorm.copy(color = color),
            modifier = modifier
        )
    }

    @Composable
    fun Body3Regular(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = PassTheme.typography.body3Norm().color
    ) {
        Text(
            text = text,
            style = PassTheme.typography.body3Norm().copy(color = color),
            modifier = modifier
        )
    }
}
