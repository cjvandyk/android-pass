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

package proton.android.pass.passkeys.fakes

import proton.android.pass.passkeys.api.CreatePasskeyRequestData
import proton.android.pass.passkeys.api.ParseCreatePasskeyRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestParseCreatePasskeyRequest @Inject constructor() : ParseCreatePasskeyRequest {

    private var result: Result<CreatePasskeyRequestData> = Result.success(
        CreatePasskeyRequestData(
            rpId = null,
            rpName = "rpName",
            userName = "userName",
            userDisplayName = "userDisplayName"
        )
    )

    fun setResult(value: Result<CreatePasskeyRequestData>) {
        result = value
    }

    override fun invoke(request: String): CreatePasskeyRequestData = result.getOrThrow()
}
