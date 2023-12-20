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

package proton.android.pass.commonrust.fakes

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import proton.android.pass.commonrust.api.AliasPrefixValidator
import proton.android.pass.commonrust.api.EmailValidator
import proton.android.pass.commonrust.api.NewUserInviteSignatureBodyCreator
import proton.android.pass.commonrust.api.PasswordGenerator
import proton.android.pass.commonrust.api.PasswordScorer
import proton.android.pass.commonrust.api.passwords.strengths.PasswordStrengthCalculator
import proton.android.pass.commonrust.fakes.passwords.strengths.TestPasswordStrengthCalculator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FakesCommonRustModule {

    @Binds
    abstract fun bindAliasPrefixValidator(impl: TestAliasPrefixValidator): AliasPrefixValidator

    @Binds
    abstract fun bindEmailValidator(impl: TestEmailValidator): EmailValidator

    @Binds
    abstract fun bindNewUserInviteSignatureBodyCreator(
        impl: TestNewUserInviteSignatureBodyCreator
    ): NewUserInviteSignatureBodyCreator

    @Binds
    abstract fun bindPasswordGenerator(impl: TestPasswordGenerator): PasswordGenerator

    @Binds
    abstract fun bindPasswordScorer(impl: TestPasswordScorer): PasswordScorer

    @[Binds Singleton]
    abstract fun bindPasswordStrengthCalculator(calculator: TestPasswordStrengthCalculator): PasswordStrengthCalculator

}
