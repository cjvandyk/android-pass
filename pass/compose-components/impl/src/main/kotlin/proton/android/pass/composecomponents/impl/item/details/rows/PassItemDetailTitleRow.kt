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

package proton.android.pass.composecomponents.impl.item.details.rows

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.Spacing
import proton.android.pass.commonuimodels.api.items.ItemDetailState
import proton.android.pass.composecomponents.impl.item.details.titles.PassItemDetailSubtitle
import proton.android.pass.composecomponents.impl.item.details.titles.PassItemDetailTitle
import proton.android.pass.composecomponents.impl.item.icon.AliasIcon
import proton.android.pass.composecomponents.impl.item.icon.CreditCardIcon
import proton.android.pass.composecomponents.impl.item.icon.LoginIcon
import proton.android.pass.composecomponents.impl.pinning.BoxedPin
import proton.android.pass.composecomponents.impl.pinning.CircledPin
import proton.android.pass.composecomponents.impl.utils.ProtonItemColors
import proton.android.pass.domain.Vault

@Composable
internal fun PassItemDetailTitleRow(
    modifier: Modifier = Modifier,
    itemDetailState: ItemDetailState,
    itemColors: ProtonItemColors,
) = with(itemDetailState) {
    when (this) {
        is ItemDetailState.Alias -> {
            ItemDetailTitleRow(
                modifier = modifier,
                title = contents.title,
                isPinned = isItemPinned,
                itemColors = itemColors,
                vault = itemVault,
            ) {
                AliasIcon(
                    size = 60,
                    shape = PassTheme.shapes.squircleMediumLargeShape,
                )
            }
        }

        is ItemDetailState.CreditCard -> {
            ItemDetailTitleRow(
                modifier = modifier,
                title = contents.title,
                isPinned = isItemPinned,
                itemColors = itemColors,
                vault = itemVault,
            ) {
                CreditCardIcon(
                    size = 60,
                    shape = PassTheme.shapes.squircleMediumLargeShape,
                )
            }
        }

        is ItemDetailState.Login -> {
            ItemDetailTitleRow(
                modifier = modifier,
                title = contents.title,
                isPinned = isItemPinned,
                itemColors = itemColors,
                vault = itemVault,
            ) {
                LoginIcon(
                    size = 60,
                    shape = PassTheme.shapes.squircleMediumLargeShape,
                    text = contents.title,
                    website = contents.websiteUrl,
                    packageName = contents.packageName,
                    canLoadExternalImages = canLoadExternalImages,
                )
            }
        }

        is ItemDetailState.Note -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(Spacing.large),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                    ) {
                        AnimatedVisibility(
                            visible = isItemPinned,
                            enter = expandHorizontally(),
                        ) {
                            CircledPin(
                                ratio = 1f,
                                backgroundColor = itemColors.majorPrimary,
                            )
                        }

                        PassItemDetailTitle(
                            text = contents.title,
                            maxLines = Int.MAX_VALUE,
                        )
                    }

                    itemVault?.let { vault ->
                        PassItemDetailSubtitle(
                            vault = vault,
                            onClick = {},
                        )
                    }
                }
            }
        }

        is ItemDetailState.Unknown -> {
            ItemDetailTitleRow(
                modifier = modifier,
                title = contents.title,
                isPinned = false,
                itemColors = itemColors,
                vault = itemVault,
            ) {}
        }
    }
}

@Composable
private fun ItemDetailTitleRow(
    modifier: Modifier = Modifier,
    title: String,
    isPinned: Boolean,
    itemColors: ProtonItemColors,
    vault: Vault?,
    iconContent: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        BoxedPin(
            isShown = isPinned,
            pin = {
                CircledPin(
                    backgroundColor = itemColors.majorPrimary,
                )
            },
            content = { iconContent() },
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            PassItemDetailTitle(
                text = title,
            )

            vault?.let { itemVault ->
                PassItemDetailSubtitle(
                    vault = itemVault,
                    onClick = {},
                )
            }
        }
    }
}
