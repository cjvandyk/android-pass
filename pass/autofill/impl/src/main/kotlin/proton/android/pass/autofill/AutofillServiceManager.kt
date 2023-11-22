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

package proton.android.pass.autofill

import android.content.Context
import android.graphics.BlendMode
import android.graphics.drawable.Icon
import android.os.Build
import android.service.autofill.Dataset
import android.view.View
import android.view.inputmethod.InlineSuggestionsRequest
import android.widget.RemoteViews
import android.widget.inline.InlinePresentationSpec
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import me.proton.core.util.kotlin.takeIfNotEmpty
import proton.android.pass.autofill.entities.AutofillData
import proton.android.pass.autofill.heuristics.NodeCluster
import proton.android.pass.autofill.service.R
import proton.android.pass.biometry.NeedsBiometricAuth
import proton.android.pass.common.api.None
import proton.android.pass.common.api.Option
import proton.android.pass.common.api.Some
import proton.android.pass.common.api.some
import proton.android.pass.common.api.toOption
import proton.android.pass.commonui.api.itemName
import proton.android.pass.crypto.api.context.EncryptionContext
import proton.android.pass.crypto.api.context.EncryptionContextProvider
import proton.android.pass.data.api.usecases.GetSuggestedCreditCardItems
import proton.android.pass.data.api.usecases.GetSuggestedLoginItems
import proton.android.pass.domain.Item
import proton.android.pass.domain.ItemType
import proton.android.pass.log.api.PassLogger
import proton.android.pass.preferences.value
import javax.inject.Inject
import kotlin.math.min

class AutofillServiceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSuggestedLoginItems: GetSuggestedLoginItems,
    private val getSuggestedCreditCardItems: GetSuggestedCreditCardItems,
    private val encryptionContextProvider: EncryptionContextProvider,
    private val needsBiometricAuth: NeedsBiometricAuth,
    @AppIcon private val appIcon: Int
) {

    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun createSuggestedItemsDatasetList(
        autofillData: AutofillData,
        inlineSuggestionsRequest: InlineSuggestionsRequest
    ): List<Dataset> {
        if (inlineSuggestionsRequest.maxSuggestionCount == 0) return emptyList()
        return when (autofillData.assistInfo.cluster) {
            NodeCluster.Empty -> emptyList()
            is NodeCluster.Login,
            is NodeCluster.SignUp ->
                handleSuggestions(inlineSuggestionsRequest, autofillData, SuggestionType.Login)

            is NodeCluster.CreditCard ->
                handleSuggestions(inlineSuggestionsRequest, autofillData, SuggestionType.CreditCard)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun handleSuggestions(
        request: InlineSuggestionsRequest,
        autofillData: AutofillData,
        suggestionType: SuggestionType
    ): List<Dataset> {
        val suggestedItemsResult = when (suggestionType) {
            SuggestionType.CreditCard -> getSuggestedCreditCardItems()
                .firstOrNull()
                .toOption()

            SuggestionType.Login -> getSuggestedLoginItems(
                packageName = autofillData.packageInfo.map { it.packageName.value },
                url = autofillData.assistInfo.url
            ).firstOrNull()
                .toOption()
        }

        val specs = request.inlinePresentationSpecs

        val pinnedIcon = { spec: InlinePresentationSpec ->
            createPinnedIcon(
                autofillData = autofillData,
                inlinePresentationSpec = spec
            )
        }
        val openApp = { spec: InlinePresentationSpec ->
            createOpenAppDataset(
                autofillData = autofillData,
                inlinePresentationSpec = spec
            )
        }

        return when (specs.size) {
            0 -> emptyList()
            1 -> listOf(pinnedIcon(specs.first()))
            2 -> listOf(openApp(specs.first()), pinnedIcon(specs.last()))
            else -> {
                val openAppDataSet = openApp(specs[specs.size - INLINE_SUGGESTIONS_OFFSET])
                if (suggestedItemsResult is Some && suggestedItemsResult.value.isNotEmpty()) {
                    createItemsDatasetList(
                        suggestedItems = suggestedItemsResult.value,
                        inlineSuggestionsRequest = request,
                        autofillData = autofillData
                    ).plus(listOf(openAppDataSet, pinnedIcon(specs.last())))
                } else {
                    listOf(openAppDataSet, pinnedIcon(specs.last()))
                }
            }
        }
    }

    suspend fun createMenuPresentationDataset(autofillData: AutofillData): List<Dataset> {
        val suggestedItemsResult = getSuggestedLoginItems(
            packageName = autofillData.packageInfo.map { it.packageName.value },
            url = autofillData.assistInfo.url
        ).firstOrNull().toOption()
        val openAppPendingIntent = PendingIntentUtils.getOpenAppPendingIntent(
            context = context,
            autofillData = autofillData,
            intentRequestCode = OPEN_PASS_MENU_REQUEST_CODE
        )
        val openAppRemoteView = RemoteViews(context.packageName, R.layout.autofill_item).apply {
            setTextViewText(R.id.title, context.getText(R.string.autofill_authenticate_prompt))
            setViewVisibility(R.id.subtitle, View.GONE)
            setImageViewResource(R.id.icon, appIcon)
        }
        val openAppDatasetOptions = DatasetBuilderOptions(
            id = "RemoteView-OpenApp".some(),
            remoteViewPresentation = openAppRemoteView.some(),
            pendingIntent = openAppPendingIntent.some()
        )
        val openAppDataSet = DatasetUtils.buildDataset(
            options = openAppDatasetOptions,
            cluster = autofillData.assistInfo.cluster
        )
        val shouldAuthenticate = runBlocking { needsBiometricAuth().first() }

        return (suggestedItemsResult.value() ?: emptyList())
            .take(2)
            .mapIndexed { index, value ->
                val decryptedTitle = encryptionContextProvider.withEncryptionContext {
                    decrypt(value.title)
                }
                val decryptedUsername = (value.itemType as ItemType.Login).username
                val pendingIntent = PendingIntentUtils.getInlineSuggestionPendingIntent(
                    context = context,
                    autofillData = autofillData,
                    item = value,
                    intentRequestCode = index,
                    shouldAuthenticate = shouldAuthenticate
                )
                val view = RemoteViews(context.packageName, R.layout.autofill_item).apply {
                    setTextViewText(R.id.title, decryptedTitle)
                    setTextViewText(R.id.subtitle, decryptedUsername.takeIfNotEmpty() ?: "---")
                }
                val options = DatasetBuilderOptions(
                    remoteViewPresentation = view.some(),
                    pendingIntent = pendingIntent.some()
                )
                DatasetUtils.buildDataset(
                    options = options,
                    cluster = autofillData.assistInfo.cluster
                )
            }
            .plus(openAppDataSet)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun createItemsDatasetList(
        suggestedItems: List<Item>,
        inlineSuggestionsRequest: InlineSuggestionsRequest,
        autofillData: AutofillData
    ): List<Dataset> = encryptionContextProvider.withEncryptionContext {
        PassLogger.i(TAG, "Suggested item count: ${suggestedItems.size}")

        val availableInlineSpots: Int = getAvailableSuggestionSpots(
            maxSuggestion = inlineSuggestionsRequest.maxSuggestionCount,
            itemsSize = suggestedItems.size
        )
        if (availableInlineSpots > 0) {
            val shouldAuthenticate = runBlocking {
                needsBiometricAuth().first()
            }
            inlineSuggestionsRequest.inlinePresentationSpecs
                .take(availableInlineSpots - INLINE_SUGGESTIONS_OFFSET)
                .zip(suggestedItems)
                .mapIndexed { index, pair ->
                    createItemDataset(autofillData, pair, index, shouldAuthenticate)
                }
                .toList()
        } else {
            emptyList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun EncryptionContext.createItemDataset(
        autofillData: AutofillData,
        pair: Pair<InlinePresentationSpec, Item>,
        index: Int,
        shouldAuthenticate: Boolean
    ): Dataset {
        val pendingIntent = PendingIntentUtils.getInlineSuggestionPendingIntent(
            context = context,
            autofillData = autofillData,
            item = pair.second,
            intentRequestCode = index,
            shouldAuthenticate = shouldAuthenticate
        )
        val inlinePresentation = when (val itemType = pair.second.itemType) {
            is ItemType.CreditCard -> InlinePresentationUtils.create(
                title = pair.second.itemName(this),
                subtitle = createCCSubtitle(itemType, this),
                inlinePresentationSpec = pair.first,
                pendingIntent = PendingIntentUtils
                    .getLongPressInlinePendingIntent(context)
            )

            is ItemType.Login -> InlinePresentationUtils.create(
                title = pair.second.itemName(this),
                subtitle = itemType.username.toOption(),
                inlinePresentationSpec = pair.first,
                pendingIntent = PendingIntentUtils
                    .getLongPressInlinePendingIntent(context)
            )

            else -> throw IllegalStateException("Unhandled item type")
        }

        val datasetBuilderOptions = DatasetBuilderOptions(
            id = "InlineSuggestion-$index".some(),
            inlinePresentation = inlinePresentation.toOption(),
            pendingIntent = pendingIntent.toOption()
        )
        return DatasetUtils.buildDataset(
            options = datasetBuilderOptions,
            cluster = autofillData.assistInfo.cluster
        )
    }

    private fun createCCSubtitle(
        itemType: ItemType.CreditCard,
        encryptionContext: EncryptionContext
    ): Option<String> {
        val decryptedNumber = encryptionContext.decrypt(itemType.number)
        val cleanNumber = decryptedNumber.replace(" ", "")
        val formattedNumber = when {
            decryptedNumber.length <= 4 -> cleanNumber
            decryptedNumber.length > 4 -> "** ${cleanNumber.takeLast(4)}"
            else -> null
        }

        val date = itemType.expirationDate.split('-')
        val formattedDate = if (date.size == 2) {
            "${date.last()}/${date.first().takeLast(2)}"
        } else {
            null
        }
        return listOfNotNull(formattedNumber, formattedDate).joinToString(" • ").some()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun createOpenAppDataset(
        autofillData: AutofillData,
        inlinePresentationSpec: InlinePresentationSpec
    ): Dataset {
        val inlinePresentation = InlinePresentationUtils.create(
            title = context.getString(R.string.inline_suggestions_open_app),
            subtitle = None,
            inlinePresentationSpec = inlinePresentationSpec,
            pendingIntent = PendingIntentUtils.getLongPressInlinePendingIntent(context),
            icon = getIcon().some()
        )
        val pendingIntent = PendingIntentUtils.getOpenAppPendingIntent(
            context = context,
            autofillData = autofillData,
            intentRequestCode = OPEN_PASS_SUGGESTION_REQUEST_CODE
        )
        val builderOptions = DatasetBuilderOptions(
            id = "InlineSuggestion-OpenApp".some(),
            inlinePresentation = inlinePresentation.toOption(),
            pendingIntent = pendingIntent.toOption()
        )
        return DatasetUtils.buildDataset(
            options = builderOptions,
            cluster = autofillData.assistInfo.cluster
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun createPinnedIcon(
        autofillData: AutofillData,
        inlinePresentationSpec: InlinePresentationSpec
    ): Dataset {
        val inlinePresentation = InlinePresentationUtils.createPinned(
            contentDescription = context.getString(R.string.inline_suggestions_open_app),
            icon = getIcon(),
            inlinePresentationSpec = inlinePresentationSpec,
            pendingIntent = PendingIntentUtils.getLongPressInlinePendingIntent(context)
        )
        val pendingIntent = PendingIntentUtils.getOpenAppPendingIntent(
            context = context,
            autofillData = autofillData,
            intentRequestCode = OPEN_PASS_PINNED_REQUEST_CODE
        )
        val builderOptions = DatasetBuilderOptions(
            id = "InlineSuggestion-PinnedIcon".some(),
            inlinePresentation = inlinePresentation.toOption(),
            pendingIntent = pendingIntent.toOption()
        )
        return DatasetUtils.buildDataset(
            options = builderOptions,
            cluster = autofillData.assistInfo.cluster
        )
    }


    private fun getAvailableSuggestionSpots(maxSuggestion: Int, itemsSize: Int): Int {
        val min = min(maxSuggestion, itemsSize)
        return if (maxSuggestion > itemsSize) {
            min + INLINE_SUGGESTIONS_OFFSET
        } else {
            min
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getIcon(): Icon {
        val icon = Icon.createWithResource(context, appIcon)
        icon.setTintBlendMode(BlendMode.DST)
        return icon
    }

    companion object {
        private const val INLINE_SUGGESTIONS_OFFSET = 2
        private const val OPEN_PASS_SUGGESTION_REQUEST_CODE = 1000
        private const val OPEN_PASS_MENU_REQUEST_CODE = 1001
        private const val OPEN_PASS_PINNED_REQUEST_CODE = 1002

        private const val TAG = "AutofillServiceManager"
    }
}

sealed interface SuggestionType {
    object Login : SuggestionType
    object CreditCard : SuggestionType
}
