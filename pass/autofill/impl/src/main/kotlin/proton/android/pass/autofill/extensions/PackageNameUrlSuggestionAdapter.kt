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

package proton.android.pass.autofill.extensions

import proton.android.pass.data.api.usecases.Suggestion
import proton.android.pass.domain.entity.PackageName

sealed interface SuggestionSource {
    data class WithPackageName(val packageName: String) : SuggestionSource
    data class WithUrl(val url: String) : SuggestionSource

    fun toSuggestion(): Suggestion = when (this) {
        is WithPackageName -> Suggestion.PackageName(packageName)
        is WithUrl -> Suggestion.Url(url)
    }
}

object PackageNameUrlSuggestionAdapter {

    fun adapt(packageName: PackageName, url: String): SuggestionSource {
        val autofillDataPackageName = packageName
            .takeIf { !it.isBrowser() }
            ?.value

        return when {
            // App with a webview
            !autofillDataPackageName.isNullOrBlank() && url.isNotBlank() ->
                SuggestionSource.WithUrl(url)

            !autofillDataPackageName.isNullOrBlank() && url.isBlank() ->
                SuggestionSource.WithPackageName(autofillDataPackageName)

            autofillDataPackageName.isNullOrBlank() -> SuggestionSource.WithUrl(url)

            // Should not happen
            else -> throw IllegalStateException("Unexpected state")
        }
    }
}
