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

package proton.android.pass.commonpresentation.api.items.details.handlers

import kotlinx.coroutines.flow.Flow
import proton.android.pass.commonpresentation.api.items.details.domain.ItemDetailsFieldType
import proton.android.pass.commonuimodels.api.items.ItemDetailState
import proton.android.pass.crypto.api.context.EncryptionContext
import proton.android.pass.domain.CustomFieldContent
import proton.android.pass.domain.HiddenState
import proton.android.pass.domain.Item
import proton.android.pass.domain.ItemContents
import proton.android.pass.domain.ItemCustomFieldSection
import proton.android.pass.domain.ItemDiffType
import proton.android.pass.domain.ItemDiffs

abstract class ItemDetailsHandlerObserver<in ITEM_CONTENTS : ItemContents> {

    abstract fun observe(item: Item): Flow<ItemDetailState>

    abstract fun updateItemContents(
        itemContents: ITEM_CONTENTS,
        hiddenFieldType: ItemDetailsFieldType.Hidden,
        hiddenFieldSection: ItemCustomFieldSection,
        hiddenState: HiddenState
    ): ItemContents

    abstract fun calculateItemDiffs(baseItemContents: ITEM_CONTENTS, otherItemContents: ITEM_CONTENTS): ItemDiffs

    protected fun calculateItemDiffTypes(
        encryptionContext: EncryptionContext,
        baseItemCustomFieldsContent: List<CustomFieldContent>,
        otherItemCustomFieldsContent: List<CustomFieldContent>
    ): List<ItemDiffType> {
        val baseCustomFieldsMap = baseItemCustomFieldsContent.associateBy { it.label }
        val otherCustomFieldsMap = otherItemCustomFieldsContent.associateBy { it.label }

        return baseCustomFieldsMap.map { (baseLabel, baseContent) ->
            val otherContent = otherCustomFieldsMap[baseLabel]

            when {
                otherContent == null -> {
                    ItemDiffType.Field
                }

                baseContent is CustomFieldContent.Text && otherContent is CustomFieldContent.Text -> {
                    calculateItemDiffType(
                        baseItemFieldValue = baseContent.value,
                        otherItemFieldValue = otherContent.value
                    )
                }

                baseContent is CustomFieldContent.Hidden && otherContent is CustomFieldContent.Hidden -> {
                    calculateItemDiffType(
                        encryptionContext = encryptionContext,
                        baseItemFieldHiddenState = baseContent.value,
                        otherItemFieldHiddenState = otherContent.value
                    )
                }

                baseContent is CustomFieldContent.Totp && otherContent is CustomFieldContent.Totp -> {
                    calculateItemDiffType(
                        encryptionContext = encryptionContext,
                        baseItemFieldHiddenState = baseContent.value,
                        otherItemFieldHiddenState = otherContent.value
                    )
                }

                else -> ItemDiffType.Content
            }
        }
    }

    protected fun calculateItemDiffTypes(
        baseItemFieldValues: List<String>,
        otherItemFieldValues: List<String>
    ): Pair<ItemDiffType, List<ItemDiffType>> = when {
        baseItemFieldValues.isEmpty() -> {
            ItemDiffType.None to emptyList()
        }

        otherItemFieldValues.isEmpty() -> {
            ItemDiffType.Field to List(baseItemFieldValues.size) { ItemDiffType.None }
        }

        else -> {
            ItemDiffType.None to baseItemFieldValues.mapIndexed { index, baseItemFieldValue ->
                calculateItemDiffType(
                    baseItemFieldValue = baseItemFieldValue,
                    otherItemFieldValue = otherItemFieldValues.getOrNull(index).orEmpty()
                )
            }
        }
    }

    protected fun calculateItemDiffType(
        encryptionContext: EncryptionContext,
        baseItemFieldHiddenState: HiddenState,
        otherItemFieldHiddenState: HiddenState
    ): ItemDiffType = with(encryptionContext) {
        decrypt(baseItemFieldHiddenState.encrypted) to decrypt(otherItemFieldHiddenState.encrypted)
    }.let { (baseItemFieldValue, otherItemFieldValue) ->
        calculateItemDiffType(baseItemFieldValue, otherItemFieldValue)
    }

    protected fun calculateItemDiffType(baseItemFieldValue: String, otherItemFieldValue: String): ItemDiffType = when {
        baseItemFieldValue.isEmpty() && otherItemFieldValue.isEmpty() -> {
            ItemDiffType.None
        }

        baseItemFieldValue.isNotEmpty() && otherItemFieldValue.isNotEmpty() -> {
            if (baseItemFieldValue == otherItemFieldValue) {
                ItemDiffType.None
            } else {
                ItemDiffType.Content
            }
        }

        else -> {
            ItemDiffType.Field
        }
    }

    protected fun toggleHiddenCustomField(
        customFieldsContent: List<CustomFieldContent>,
        hiddenFieldType: ItemDetailsFieldType.Hidden.CustomField,
        hiddenState: HiddenState
    ): List<CustomFieldContent> = customFieldsContent.mapIndexed { index, customFieldContent ->
        if (index == hiddenFieldType.index && customFieldContent is CustomFieldContent.Hidden) {
            customFieldContent.copy(value = hiddenState)
        } else {
            customFieldContent
        }
    }

}
