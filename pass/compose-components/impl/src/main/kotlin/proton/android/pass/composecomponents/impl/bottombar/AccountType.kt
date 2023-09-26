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

package proton.android.pass.composecomponents.impl.bottombar

import androidx.compose.runtime.Stable
import proton.android.pass.common.api.LoadingResult
import proton.android.pass.common.api.getOrNull
import proton.android.pass.common.api.map
import proton.pass.domain.Plan
import proton.pass.domain.PlanType

@Stable
enum class AccountType {
    Free,
    Trial,
    Unlimited;

    companion object {
        fun fromPlan(planType: PlanType): AccountType = when (planType) {
            is PlanType.Free -> Free
            is PlanType.Paid -> Unlimited
            is PlanType.Trial -> Trial
            is PlanType.Unknown -> Free
        }

        fun fromPlan(result: LoadingResult<Plan>): AccountType = result.map {
            fromPlan(it.planType)
        }.getOrNull() ?: Free
    }
}
