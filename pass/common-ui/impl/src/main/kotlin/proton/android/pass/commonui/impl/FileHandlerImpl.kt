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

package proton.android.pass.commonui.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.FileProvider
import proton.android.pass.appconfig.api.AppConfig
import proton.android.pass.commonui.api.FileHandler
import proton.android.pass.log.api.PassLogger
import java.io.File
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileHandlerImpl @Inject constructor(
    private val appConfig: AppConfig
) : FileHandler {

    private fun createContentUri(context: Context, file: File): Uri = FileProvider.getUriForFile(
        context,
        "${appConfig.applicationId}.fileprovider",
        file
    )

    override fun openFile(
        context: Context,
        uri: URI,
        mimeType: String,
        chooserTitle: String
    ) {
        val contentUri = Uri.parse(uri.toString())
        val intent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(contentUri, mimeType)
        performFileAction(context, intent, chooserTitle)
    }

    override fun shareFile(
        context: Context,
        file: File,
        chooserTitle: String
    ) {
        val contentUri = createContentUri(context, file)
        val intent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
        val bundle = Bundle().apply {
            putParcelable(Intent.EXTRA_STREAM, contentUri)
        }
        performFileAction(
            context = context,
            intent = intent,
            chooserTitle = chooserTitle,
            extras = bundle
        )
    }

    override fun shareFileWithEmail(
        context: Context,
        file: File,
        chooserTitle: String,
        email: String,
        subject: String
    ) {
        val contentUri = createContentUri(context, file)
        val intent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
        val bundle = Bundle().apply {
            putStringArray(Intent.EXTRA_EMAIL, arrayOf(email))
            putString(Intent.EXTRA_SUBJECT, subject)
            putParcelable(Intent.EXTRA_STREAM, contentUri)
        }
        performFileAction(
            context = context,
            intent = intent,
            chooserTitle = chooserTitle,
            extras = bundle
        )
    }

    override fun performFileAction(
        context: Context,
        intent: Intent,
        chooserTitle: String,
        extras: Bundle
    ) {
        val intentWithExtras = intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .putExtras(extras)
        val chooserIntent = Intent.createChooser(intentWithExtras, chooserTitle)
        runCatching {
            context.startActivity(chooserIntent)
        }.onFailure {
            PassLogger.w(TAG, "Could not start activity for intent")
        }
    }

    companion object {
        private const val TAG = "FileHandlerImpl"
    }
}
