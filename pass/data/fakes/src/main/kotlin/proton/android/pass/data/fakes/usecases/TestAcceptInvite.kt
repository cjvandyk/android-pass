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

package proton.android.pass.data.fakes.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import proton.android.pass.data.api.usecases.AcceptInvite
import proton.android.pass.data.api.usecases.AcceptInviteStatus
import proton.pass.domain.InviteToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestAcceptInvite @Inject constructor() : AcceptInvite {

    private var result: MutableStateFlow<Result<AcceptInviteStatus>> =
        MutableStateFlow(Result.success(AcceptInviteStatus.Done(0)))
    private val memory: MutableList<InviteToken> = mutableListOf()

    fun getMemory(): List<InviteToken> = memory

    fun emitValue(value: Result<AcceptInviteStatus>) {
        result.update { value }
    }

    override fun invoke(invite: InviteToken): Flow<AcceptInviteStatus> {
        memory.add(invite)
        return result.map { it.getOrThrow() }
    }

}
