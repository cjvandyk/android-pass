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

package proton.android.pass.features.sl.sync.mailboxes.options.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.composecomponents.impl.bottomsheet.BottomSheetItem
import proton.android.pass.composecomponents.impl.bottomsheet.BottomSheetItemIcon
import proton.android.pass.composecomponents.impl.bottomsheet.BottomSheetItemTitle
import proton.android.pass.composecomponents.impl.R as CompR
import me.proton.core.presentation.R as CoreR

internal fun delete(onClick: () -> Unit) = object : BottomSheetItem {

    override val title: @Composable () -> Unit = {
        BottomSheetItemTitle(
            text = stringResource(id = CompR.string.action_delete),
            color = PassTheme.colors.textNorm
        )
    }

    override val subtitle: @Composable (() -> Unit)? = null

    override val leftIcon: @Composable (() -> Unit) = {
        BottomSheetItemIcon(iconId = CoreR.drawable.ic_proton_trash)
    }

    override val endIcon: @Composable (() -> Unit)? = {

    }

    override val onClick: (() -> Unit) = onClick

    override val isDivider: Boolean = false

}

internal fun setAsDefault(onClick: () -> Unit) = object : BottomSheetItem {

    override val title: @Composable () -> Unit = {
        BottomSheetItemTitle(
            text = stringResource(id = CompR.string.action_make_default),
            color = PassTheme.colors.textNorm
        )
    }

    override val subtitle: @Composable (() -> Unit)? = null

    override val leftIcon: @Composable (() -> Unit) = {
        BottomSheetItemIcon(iconId = CoreR.drawable.ic_proton_star)
    }

    override val endIcon: @Composable (() -> Unit)? = {

    }

    override val onClick: (() -> Unit) = onClick

    override val isDivider: Boolean = false

}

internal fun verify(onClick: () -> Unit) = object : BottomSheetItem {

    override val title: @Composable () -> Unit = {
        BottomSheetItemTitle(
            text = stringResource(id = CompR.string.action_verify),
            color = PassTheme.colors.textNorm
        )
    }

    override val subtitle: @Composable (() -> Unit)? = null

    override val leftIcon: @Composable (() -> Unit) = {
        BottomSheetItemIcon(iconId = CoreR.drawable.ic_proton_checkmark_circle)
    }

    override val endIcon: @Composable (() -> Unit)? = {

    }

    override val onClick: (() -> Unit) = onClick

    override val isDivider: Boolean = false

}
