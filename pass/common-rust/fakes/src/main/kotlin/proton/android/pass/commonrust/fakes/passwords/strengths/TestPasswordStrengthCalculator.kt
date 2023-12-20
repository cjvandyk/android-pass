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

package proton.android.pass.commonrust.fakes.passwords.strengths

import proton.android.pass.commonrust.api.passwords.strengths.PasswordStrength
import proton.android.pass.commonrust.api.passwords.strengths.PasswordStrengthCalculator
import javax.inject.Inject

class TestPasswordStrengthCalculator @Inject constructor() : PasswordStrengthCalculator {

    private var passwordStrength = PasswordStrength.None

    fun setPasswordStrength(expectedPasswordStrength: PasswordStrength) {
        passwordStrength = expectedPasswordStrength
    }

    override fun calculateStrength(password: String): PasswordStrength = passwordStrength

}
