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

package proton.android.pass.data.impl.url

import proton.android.pass.common.api.None
import proton.android.pass.common.api.flatMap
import proton.android.pass.common.api.some
import proton.android.pass.data.api.url.HostInfo
import proton.android.pass.data.api.url.HostParser
import proton.android.pass.data.api.url.UrlSanitizer
import proton.android.pass.data.api.usecases.GetPublicSuffixList
import javax.inject.Inject

class HostParserImpl @Inject constructor(
    private val getPublicSuffixList: GetPublicSuffixList
) : HostParser {

    override fun parse(url: String): Result<HostInfo> = UrlSanitizer.getDomain(url)
        .flatMap { domain ->
            val protocol = UrlSanitizer.getProtocol(url).getOrDefault("https")
            getHostInfoFromDomain(protocol, domain)
        }

    private fun getHostInfoFromDomain(protocol: String, domain: String): Result<HostInfo> =
        if (isIp(domain)) {
            Result.success(HostInfo.Ip(domain))
        } else {
            parseHostInfo(protocol, domain)
        }

    @Suppress("ReturnCount")
    private fun parseHostInfo(protocol: String, domain: String): Result<HostInfo.Host> {
        val publicSuffixes = getPublicSuffixList()
        val parts: List<String> = domain.split('.')
        if (parts.isEmpty()) {
            return Result.failure(IllegalArgumentException("host is empty"))
        } else if (parts.size == 1) {
            return handleDomainWithSinglePart(
                protocol = protocol,
                domain = domain,
                publicSuffixes = publicSuffixes
            )
        }

        // Has multiple parts, find the widest match that is a TLD
        for (i in parts.indices) {
            val portion = stringFromParts(parts, i)
            if (publicSuffixes.contains(portion)) {

                // Check if the widest match is only the TLD
                return if (i == 0) {
                    Result.failure(IllegalArgumentException("host is a TLD"))
                } else {
                    // We found the TLD
                    val res = hostWithTld(
                        protocol = protocol,
                        parts = parts,
                        tldStartingPart = i,
                        tld = portion
                    )
                    Result.success(res)
                }
            }
        }

        // We did not find a TLD
        val res = hostWithoutTld(
            protocol = protocol,
            parts = parts
        )
        return Result.success(res)
    }

    private fun handleDomainWithSinglePart(
        protocol: String,
        domain: String,
        publicSuffixes: Set<String>
    ): Result<HostInfo.Host> =
        if (publicSuffixes.contains(domain)) {
            Result.failure(IllegalArgumentException("host is a TLD"))
        } else {
            Result.success(
                HostInfo.Host(
                    protocol = protocol,
                    subdomain = None,
                    domain = domain,
                    tld = None
                )
            )
        }

    private fun hostWithTld(
        protocol: String,
        parts: List<String>,
        tldStartingPart: Int,
        tld: String
    ): HostInfo.Host {
        val domain = parts[tldStartingPart - 1]
        val subdomain = if (tldStartingPart == 1) {
            // It means that we have no subdomain, as the part 0 is the domain
            None
        } else {
            buildString {
                for ((portions, i) in (0 until tldStartingPart - 1).withIndex()) {
                    if (portions > 0) append('.')
                    append(parts[i])
                }
            }.some()
        }


        return HostInfo.Host(
            protocol = protocol,
            subdomain = subdomain,
            domain = domain,
            tld = tld.some()
        )
    }

    private fun hostWithoutTld(protocol: String, parts: List<String>): HostInfo.Host {
        // We did not find a TLD, so we'll just assume that:
        // - The last portion is the tld
        // - The second to last is the domain
        // - The rest is the subdomain

        val tld = parts.last()

        val hostInfo = if (parts.size > 2) {
            val subdomain = buildString {
                for ((portions, i) in (0 until parts.size - 2).withIndex()) {
                    if (portions > 0) append('.')
                    append(parts[i])
                }
            }


            HostInfo.Host(
                protocol = protocol,
                subdomain = subdomain.some(),
                domain = parts[parts.size - 2],
                tld = tld.some()
            )
        } else {
            // It's of the form a.b, so domain=a, tld=b and no subdomain
            HostInfo.Host(
                protocol = protocol,
                subdomain = None,
                domain = parts[0],
                tld = tld.some()
            )
        }

        return hostInfo
    }

    private fun stringFromParts(parts: List<String>, startingFrom: Int): String = buildString {
        for ((portions, i) in (startingFrom until parts.size).withIndex()) {
            if (portions > 0) append('.')
            append(parts[i])
        }
    }


    private fun isIp(domain: String): Boolean = ipRegex.matches(domain)

    companion object {
        private val ipRegex = Regex("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}\$")
    }
}
