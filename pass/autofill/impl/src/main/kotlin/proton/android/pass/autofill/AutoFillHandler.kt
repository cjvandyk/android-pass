package proton.android.pass.autofill

import android.app.assist.AssistStructure
import android.content.Context
import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.widget.RemoteViews
import android.widget.inline.InlinePresentationSpec
import androidx.annotation.ChecksSdkIntAtLeast
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.proton.pass.autofill.service.R
import proton.android.pass.autofill.PendingIntentUtils.getOpenAppPendingIntent
import proton.android.pass.autofill.Utils.getWindowNodes
import proton.android.pass.autofill.entities.AutofillData
import proton.android.pass.autofill.extensions.addItemInlineSuggestion
import proton.android.pass.autofill.extensions.addOpenAppInlineSuggestion
import proton.android.pass.autofill.extensions.addSaveInfo
import proton.android.pass.common.api.None
import proton.android.pass.common.api.Option
import proton.android.pass.common.api.Result
import proton.android.pass.common.api.Some
import proton.android.pass.common.api.toOption
import proton.android.pass.crypto.api.context.EncryptionContextProvider
import proton.android.pass.data.api.usecases.GetSuggestedLoginItems
import proton.android.pass.log.api.PassLogger
import proton.pass.domain.Item
import kotlin.coroutines.coroutineContext
import kotlin.math.min

object AutoFillHandler {

    private const val TAG = "AutoFillHandler"
    private const val INLINE_SUGGESTIONS_OFFSET = 1

    @Suppress("LongParameterList")
    fun handleAutofill(
        context: Context,
        encryptionContextProvider: EncryptionContextProvider,
        request: FillRequest,
        callback: FillCallback,
        cancellationSignal: CancellationSignal,
        getSuggestedLoginItems: GetSuggestedLoginItems
    ) {
        val windowNode = getWindowNodes(request.fillContexts.last()).lastOrNull()
        if (windowNode?.rootViewNode == null) {
            callback.onFailure(context.getString(R.string.error_cant_find_matching_fields))
            return
        }

        val handler = CoroutineExceptionHandler { _, exception ->
            PassLogger.e(TAG, exception)
        }
        val job = CoroutineScope(Dispatchers.IO)
            .launch(handler) {
                searchAndFill(
                    context = context,
                    encryptionContextProvider = encryptionContextProvider,
                    windowNode = windowNode,
                    callback = callback,
                    request = request,
                    getSuggestedLoginItems = getSuggestedLoginItems
                )
            }

        cancellationSignal.setOnCancelListener {
            job.cancel()
        }
    }

    @Suppress("ReturnCount", "LongParameterList", "LongMethod")
    private suspend fun searchAndFill(
        context: Context,
        encryptionContextProvider: EncryptionContextProvider,
        windowNode: AssistStructure.WindowNode,
        callback: FillCallback,
        request: FillRequest,
        getSuggestedLoginItems: GetSuggestedLoginItems
    ) {
        val assistInfo = AssistNodeTraversal().traverse(windowNode.rootViewNode)
        if (assistInfo.fields.isEmpty()) return

        val packageName = Utils.getApplicationPackageName(windowNode)
        val title = Utils.getTitle(context, assistInfo, packageName)
        val autofillData = AutofillData(assistInfo, packageName, title)
        val responseBuilder = FillResponse.Builder()
        if (hasSupportForInlineSuggestions(request)) {
            val inlineRequest = request.inlineSuggestionsRequest ?: return
            val maxSuggestion = inlineRequest.maxSuggestionCount
            val autofillPackageName = if (BROWSERS.contains(packageName)) {
                None
            } else {
                Some(packageName)
            }
            val suggestedItemsResult: Option<Result.Success<List<Item>>> =
                getSuggestedLoginItems(
                    packageName = autofillPackageName,
                    url = autofillData.assistInfo.url
                )
                    .filterIsInstance<Result.Success<List<Item>>>()
                    .firstOrNull()
                    .toOption()

            encryptionContextProvider.withEncryptionContext {
                if (suggestedItemsResult is Some) {
                    val suggestedItems = suggestedItemsResult.value.data
                    PassLogger.i(TAG, "Suggested item count: ${suggestedItems.size}")

                    val size: Int = getAvailableSuggestionSpots(
                        maxSuggestion = maxSuggestion,
                        itemsSize = suggestedItems.size
                    )
                    if (size > 0) {
                        val specs: List<InlinePresentationSpec> =
                            inlineRequest.inlinePresentationSpecs.take(size - INLINE_SUGGESTIONS_OFFSET)
                        for ((index, spec) in specs.withIndex()) {
                            val item: Option<Item> = suggestedItems
                                .getOrNull(index)
                                .toOption()
                            responseBuilder.addItemInlineSuggestion(
                                context = context,
                                encryptionContext = this@withEncryptionContext,
                                itemOption = item,
                                inlinePresentationSpec = spec,
                                assistFields = assistInfo.fields
                            )
                        }
                    }
                }
                responseBuilder.addOpenAppInlineSuggestion(
                    context = context,
                    encryptionContext = this@withEncryptionContext,
                    inlinePresentationSpec = inlineRequest.inlinePresentationSpecs.last(),
                    pendingIntent = getOpenAppPendingIntent(context, autofillData),
                    assistFields = assistInfo.fields
                )
            }
        } else {
            addMenuPresentationDataset(context, autofillData, responseBuilder)
        }
        if (!coroutineContext.isActive) {
            callback.onFailure(context.getString(R.string.error_credentials_not_found))
            return
        }
        responseBuilder.addSaveInfo(assistInfo)
        callback.onSuccess(responseBuilder.build())
    }

    private fun addMenuPresentationDataset(
        context: Context,
        autofillData: AutofillData,
        responseBuilder: FillResponse.Builder
    ) {
        val defaultDataset = DatasetUtils.buildDataset(
            context = context,
            autofillMappings = None,
            dsbOptions = DatasetBuilderOptions(
                authenticateView = getMenuPresentationView(context).toOption(),
                pendingIntent = getOpenAppPendingIntent(context, autofillData).toOption()
            ),
            assistFields = autofillData.assistInfo.fields
        )
        responseBuilder.addDataset(defaultDataset)
    }

    private fun getAvailableSuggestionSpots(maxSuggestion: Int, itemsSize: Int): Int {
        val min = min(maxSuggestion, itemsSize)
        return if (maxSuggestion > itemsSize) {
            min + INLINE_SUGGESTIONS_OFFSET
        } else {
            min
        }
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
    private fun hasSupportForInlineSuggestions(request: FillRequest): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            request.inlineSuggestionsRequest?.let {
                val maxSuggestion = it.maxSuggestionCount
                val specCount = it.inlinePresentationSpecs.count()
                maxSuggestion > 0 && specCount > 0
            } ?: false
        } else {
            false
        }

    private fun getMenuPresentationView(context: Context): RemoteViews {
        val view = RemoteViews(
            context.packageName,
            android.R.layout.simple_list_item_1
        )
        view.setTextViewText(
            android.R.id.text1,
            context.getString(R.string.autofill_authenticate_prompt)
        )
        return view
    }
}
