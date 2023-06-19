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

import android.app.assist.AssistStructure
import android.content.Context
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import proton.android.pass.autofill.Utils.getApplicationPackageName
import proton.android.pass.autofill.Utils.getWindowNodes
import proton.android.pass.autofill.entities.SaveInformation
import proton.android.pass.autofill.service.R
import proton.android.pass.autofill.ui.autosave.AutoSaveActivity
import proton.android.pass.autofill.ui.autosave.LinkedAppInfo
import proton.android.pass.common.api.None
import proton.android.pass.common.api.Option
import proton.android.pass.common.api.Some
import proton.android.pass.commonui.api.AndroidUtils.getApplicationName
import proton.android.pass.data.api.url.UrlSanitizer

object AutoSaveHandler {
    fun handleOnSave(context: Context, request: SaveRequest, callback: SaveCallback) {
        val windowNode = getWindowNodes(request.fillContexts.last()).lastOrNull()
        if (windowNode?.rootViewNode == null) {
            callback.onFailure(context.getString(R.string.error_cant_find_matching_fields))
            return
        }

        runCatching {
            saveCredentials(context, windowNode)
        }.onSuccess {
            callback.onSuccess()
        }.onFailure {
            callback.onFailure(context.getString(R.string.error_credentials_not_saved))
        }
    }

    private fun saveCredentials(
        context: Context,
        windowNode: AssistStructure.WindowNode
    ) {
        val assistInfo = AssistNodeTraversal().traverse(windowNode.rootViewNode)

        val packageName = getApplicationPackageName(windowNode)
        val itemTitle = getItemTitle(context, packageName, assistInfo.url)

        val saveInformation = SaveFieldExtractor.extract(assistInfo.fields)
        val linkedAppInfo = if (BROWSERS.contains(packageName)) {
            null
        } else {
            val appName = getApplicationName(context, packageName).value() ?: ""
            LinkedAppInfo(packageName, appName)
        }

        // We should handle what happens if there are multiple credentials
        saveInformation.firstOrNull()?.let {
            launchSaveCredentialScreen(
                context = context,
                saveInformation = it,
                title = itemTitle,
                website = assistInfo.url,
                linkedAppInfo = linkedAppInfo
            )
        }
    }

    private fun getItemTitle(context: Context, packageName: String, url: Option<String>): String =
        if (BROWSERS.contains(packageName)) {
            when (url) {
                None -> ""
                is Some -> UrlSanitizer.getDomain(url.value).fold(
                    onSuccess = { it },
                    onFailure = { "" }
                )
            }
        } else {
            getApplicationName(context, packageName).value() ?: ""
        }


    private fun launchSaveCredentialScreen(
        context: Context,
        saveInformation: SaveInformation,
        linkedAppInfo: LinkedAppInfo?,
        title: String,
        website: Option<String>
    ) {
        val intent = AutoSaveActivity.newIntent(
            context = context,
            saveInformation = saveInformation,
            title = title,
            website = website.value(),
            linkedAppInfo = linkedAppInfo
        )
        context.startActivity(intent)
    }
}
