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

package proton.android.pass.composecomponents.impl.item.details.sections.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import me.proton.core.compose.theme.ProtonTheme
import me.proton.core.compose.theme.defaultNorm
import proton.android.pass.commonpresentation.api.items.details.domain.ItemDetailsFieldType
import proton.android.pass.commonuimodels.api.masks.TextMask
import proton.android.pass.composecomponents.impl.container.RoundedCornersColumn
import proton.android.pass.composecomponents.impl.item.details.rows.PassItemDetailFieldRow
import proton.android.pass.composecomponents.impl.item.details.rows.PassItemDetailMaskedFieldRow
import proton.android.pass.composecomponents.impl.item.details.rows.PassItemDetailsHiddenFieldRow
import proton.android.pass.composecomponents.impl.progress.PassTotpProgress
import proton.android.pass.composecomponents.impl.utils.ProtonItemColors
import proton.android.pass.domain.HiddenState
import proton.android.pass.domain.items.ItemCustomField
import me.proton.core.presentation.R as CoreR

private const val HIDDEN_CUSTOM_FIELD_TEXT_LENGTH = 12

@Composable
internal fun PassLoginItemDetailCustomFieldsSection(
    modifier: Modifier = Modifier,
    customFields: ImmutableList<ItemCustomField>,
    itemColors: ProtonItemColors,
    onSectionClick: (String, ItemDetailsFieldType.Plain) -> Unit,
    onHiddenSectionClick: (HiddenState, ItemDetailsFieldType.Hidden) -> Unit,
    onHiddenSectionToggle: (Boolean, HiddenState, ItemDetailsFieldType.Hidden) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        customFields.forEachIndexed { index, customField ->
            RoundedCornersColumn {
                when (customField) {
                    is ItemCustomField.Plain -> PassItemDetailFieldRow(
                        icon = painterResource(CoreR.drawable.ic_proton_text_align_left),
                        title = customField.title,
                        subtitle = customField.content,
                        itemColors = itemColors,
                        onClick = {
                            onSectionClick(
                                customField.content,
                                ItemDetailsFieldType.Plain.CustomField,
                            )
                        },
                    )

                    is ItemCustomField.Hidden -> PassItemDetailsHiddenFieldRow(
                        icon = painterResource(CoreR.drawable.ic_proton_eye_slash),
                        title = customField.title,
                        hiddenState = customField.hiddenState,
                        hiddenTextLength = HIDDEN_CUSTOM_FIELD_TEXT_LENGTH,
                        itemColors = itemColors,
                        hiddenTextStyle = ProtonTheme.typography.defaultNorm,
                        onClick = {
                            onHiddenSectionClick(
                                customField.hiddenState,
                                ItemDetailsFieldType.Hidden.CustomField(index),
                            )
                        },
                        onToggle = { isVisible ->
                            onHiddenSectionToggle(
                                isVisible,
                                customField.hiddenState,
                                ItemDetailsFieldType.Hidden.CustomField(index),
                            )
                        },
                    )

                    is ItemCustomField.Totp -> customField.totp?.let { customFieldTotp ->
                        PassItemDetailMaskedFieldRow(
                            icon = painterResource(CoreR.drawable.ic_proton_lock),
                            title = customField.title,
                            maskedSubtitle = TextMask.TotpCode(customFieldTotp.code),
                            itemColors = itemColors,
                            onClick = {
                                onSectionClick(
                                    customFieldTotp.code,
                                    ItemDetailsFieldType.Plain.TotpCode,
                                )
                            },
                            contentInBetween = {
                                PassTotpProgress(
                                    remainingSeconds = customFieldTotp.remainingSeconds,
                                    totalSeconds = customFieldTotp.totalSeconds,
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}
