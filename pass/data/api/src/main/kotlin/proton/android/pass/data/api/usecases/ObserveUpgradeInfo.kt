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

package proton.android.pass.data.api.usecases

import kotlinx.coroutines.flow.Flow
import proton.pass.domain.Plan
import proton.pass.domain.PlanLimit

interface ObserveUpgradeInfo {
    operator fun invoke(forceRefresh: Boolean = false): Flow<UpgradeInfo>
}

data class UpgradeInfo(
    val isUpgradeAvailable: Boolean,
    val plan: Plan,
    val totalVaults: Int,
    val totalAlias: Int,
    val totalTotp: Int,
) {
    fun hasReachedVaultLimit() = hasReachedLimit(plan.vaultLimit, totalVaults)
    fun hasReachedAliasLimit() = hasReachedLimit(plan.aliasLimit, totalAlias)
    fun hasReachedTotpLimit() = hasReachedLimit(plan.totpLimit, totalTotp)

    private fun hasReachedLimit(planLimit: PlanLimit, count: Int): Boolean {
        if (!isUpgradeAvailable) return false

        return when (planLimit) {
            PlanLimit.Unlimited -> false
            is PlanLimit.Limited -> count >= planLimit.limit
        }
    }
}
