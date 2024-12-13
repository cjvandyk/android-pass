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

package proton.android.pass.domain.shares

import proton.android.pass.domain.InviteId
import proton.android.pass.domain.ShareRole

sealed interface SharePendingInvite {

    val email: String

    val inviteId: InviteId

    val isForNewUser: Boolean

    data class ExistingUser(
        override val email: String,
        override val inviteId: InviteId
    ) : SharePendingInvite {

        override val isForNewUser: Boolean = false

    }

    data class NewUser(
        override val email: String,
        override val inviteId: InviteId,
        val inviteState: InviteState,
        val role: ShareRole
    ) : SharePendingInvite {

        override val isForNewUser: Boolean = true

        enum class InviteState(val value: Int) {
            PendingAccountCreation(value = 1),
            PendingAcceptance(value = 2);

            companion object {

                fun fromValue(value: Int): InviteState = when (value) {
                    PendingAccountCreation.value -> PendingAccountCreation
                    PendingAcceptance.value -> PendingAcceptance
                    else -> throw IllegalArgumentException("Unknown InviteState value: $value")
                }

            }
        }
    }

}
