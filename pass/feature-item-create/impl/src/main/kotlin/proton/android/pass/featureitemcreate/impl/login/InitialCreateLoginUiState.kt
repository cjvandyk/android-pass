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

package proton.android.pass.featureitemcreate.impl.login

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import proton.android.pass.commonuimodels.api.PackageInfoUi
import proton.android.pass.featureitemcreate.impl.alias.AliasItemFormState

data class InitialCreateLoginUiState(
    val title: String? = null,
    val username: String? = null,
    val password: String? = null,
    val url: String? = null,
    val packageInfoUi: PackageInfoUi? = null,
    val aliasItemFormState: AliasItemFormState? = null,
    val navTotpUri: String? = null,
    val navTotpIndex: Int = -1,
    val passkeyOrigin: String? = null,
    val passkeyRequest: String? = null
)

val InitialCreateLoginUiStateSaver: Saver<InitialCreateLoginUiState?, Any> = run {
    val title = "title"
    val username = "username"
    val password = "password"
    val url = "url"
    val packageName = "packageName"
    val appName = "appName"
    val aliasItem = "aliasItem"
    val primaryTotp = "primaryTotp"
    val passkeyOrigin = "passkeyOrigin"
    val passkeyRequest = "passkeyRequest"
    mapSaver(
        save = {
            if (it != null) {
                mapOf(
                    title to it.title,
                    username to it.username,
                    password to it.password,
                    url to it.url,
                    packageName to it.packageInfoUi?.packageName,
                    appName to it.packageInfoUi?.appName,
                    aliasItem to it.aliasItemFormState,
                    primaryTotp to it.navTotpUri,
                    passkeyOrigin to it.passkeyOrigin,
                    passkeyRequest to it.passkeyRequest
                )
            } else {
                emptyMap()
            }
        },
        restore = { values ->
            if (values.isNotEmpty()) {
                val packageNameValue = values[packageName] as? String
                val appNameValue = values[appName] as? String
                val packageInfoUi = if (packageNameValue != null && appNameValue != null) {
                    PackageInfoUi(packageName = packageNameValue, appName = appNameValue)
                } else {
                    null
                }

                val (passkeyOriginValue, passkeyRequestValue) =
                    if (values[passkeyOrigin] != null && values[passkeyRequest] != null) {
                        values[passkeyOrigin] as String to values[passkeyRequest] as String
                    } else {
                        null to null
                    }

                InitialCreateLoginUiState(
                    title = values[title] as? String,
                    username = values[username] as? String,
                    password = values[password] as? String,
                    url = values[url] as? String,
                    packageInfoUi = packageInfoUi,
                    aliasItemFormState = values[aliasItem] as? AliasItemFormState,
                    navTotpUri = values[primaryTotp] as? String,
                    passkeyOrigin = passkeyOriginValue,
                    passkeyRequest = passkeyRequestValue

                )
            } else {
                null
            }
        }
    )
}
