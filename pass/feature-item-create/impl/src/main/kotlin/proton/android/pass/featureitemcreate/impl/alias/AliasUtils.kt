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

package proton.android.pass.featureitemcreate.impl.alias

import proton.android.pass.common.api.removeAccents

data class PrefixSuffix(
    val prefix: String,
    val suffix: String
)

object AliasUtils {

    private const val SPACE_REPLACEMENT_CHAR = '-'
    private val ALLOWED_SPECIAL_CHARACTERS: List<Char> = listOf('_', '-', '.')

    fun formatAlias(value: String): String =
        value.replace(" ", SPACE_REPLACEMENT_CHAR.toString())
            .filter { it.isLetterOrDigit() || ALLOWED_SPECIAL_CHARACTERS.contains(it) }
            .lowercase()
            .removeAccents()

    fun areAllAliasCharactersValid(alias: String): Boolean {
        for (char in alias) {
            // If it's not a letter or a digit, check if it's one of the allowed symbols
            if (!char.isLetterOrDigit() && !ALLOWED_SPECIAL_CHARACTERS.contains(char)) return false

            // If it's a letter, must be lowercase
            if (char.isLetter() && char.isUpperCase()) return false
        }
        return true
    }

    fun extractPrefixSuffix(email: String): PrefixSuffix {
        // Imagine we have some.alias.suffix@domain.tld

        // Split by @
        // we then have
        // atSplits[0] = some.alias.suffix
        // atSplits[1] = domain.tld
        val atSplits = email.split("@")

        // Split the first part by dots
        // we then have
        // prefixParts[0] = some
        // prefixParts[1] = alias
        // prefixParts[2] = suffix
        val prefixParts = atSplits[0].split(".")

        // The suffix is composed by:
        // - the last part of the prefixPart
        // - the @
        // - the section after the @ of the original content
        // in our example case we will have
        // suffix@domain.tld
        val suffix = prefixParts.last() + "@" + atSplits[1]

        // The prefix consists on all the prefixParts except for the last one, joined by dots
        // so in our example we will have
        // prefixParts[0] = some
        // prefixParts[1] = alias
        // (prefixParts[2] is ignored, as we do not check the last one)
        // resulting in prefix = some.alias
        var prefix = ""
        for (idx in 0 until prefixParts.size - 1) {
            if (idx > 0) prefix += "."
            prefix += prefixParts[idx]
        }
        return PrefixSuffix(prefix, suffix)
    }
}
