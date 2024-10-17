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

package proton.android.pass.features.sl.sync.domains.select.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import me.proton.core.compose.theme.ProtonTheme
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.Spacing
import proton.android.pass.composecomponents.impl.bottomsheet.BottomSheetItem
import proton.android.pass.composecomponents.impl.icon.PassUnlimitedIcon
import proton.android.pass.domain.simplelogin.SimpleLoginAliasDomain
import proton.android.pass.features.sl.sync.R

internal fun simpleLoginAliasDomainSelectItem(
    aliasDomain: SimpleLoginAliasDomain,
    canSelectPremiumDomains: Boolean,
    onClick: () -> Unit
) = object : BottomSheetItem {

    override val title: @Composable () -> Unit = {
        Text(
            text = aliasDomain.domain.ifEmpty {
                stringResource(id = R.string.simple_login_sync_domain_select_title_none)
            },
            style = ProtonTheme.typography.body2Regular,
            color = PassTheme.colors.textNorm
        )
    }

    override val subtitle: (@Composable () -> Unit) = {
        when {
            aliasDomain.domain.isEmpty() -> R.string.simple_login_sync_domain_select_description_none
            aliasDomain.isCustom -> R.string.simple_login_sync_domain_select_description_custom
            aliasDomain.isPremium -> R.string.simple_login_sync_domain_select_description_premium
            else -> R.string.simple_login_sync_domain_select_description_free
        }.also { subtitleId ->
            Text(
                modifier = Modifier.padding(top = Spacing.extraSmall),
                text = stringResource(id = subtitleId),
                style = ProtonTheme.typography.body2Regular,
                color = PassTheme.colors.textWeak
            )
        }
    }

    override val leftIcon: (@Composable () -> Unit)? = null

    override val endIcon: (@Composable () -> Unit) = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = Spacing.small)
        ) {
            if (aliasDomain.isDefault) {
                Icon(
                    painter = painterResource(me.proton.core.presentation.R.drawable.ic_proton_checkmark),
                    contentDescription = null,
                    tint = PassTheme.colors.interactionNormMajor1
                )
            }

            if (aliasDomain.isPremium && !canSelectPremiumDomains) {
                PassUnlimitedIcon()
            }
        }
    }

    override val onClick: (() -> Unit)? = onClick.takeIf { !aliasDomain.isDefault }

    override val isDivider = false

}
