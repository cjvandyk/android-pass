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

package proton.android.pass.securitycenter.impl.checkers

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import proton.android.pass.crypto.fakes.context.TestEncryptionContextProvider
import proton.android.pass.securitycenter.api.passwords.RepeatedPasswordsReport
import proton.android.pass.test.TestUtils
import proton.android.pass.test.domain.TestItem
import proton.android.pass.test.domain.TestItemType

class RepeatedPasswordCheckerTest {

    private lateinit var instance: RepeatedPasswordCheckerImpl
    private lateinit var encryptionContextProvider: TestEncryptionContextProvider

    @Before
    fun setup() {
        encryptionContextProvider = TestEncryptionContextProvider()
        instance = RepeatedPasswordCheckerImpl(
            encryptionContextProvider = encryptionContextProvider
        )
    }

    @Test
    fun `can handle empty list`() = runTest {
        val res = instance(emptyList())
        assertThat(res).isEqualTo(RepeatedPasswordsReport(emptyMap()))
    }

    @Test
    fun `can detect duplicated passwords`() = runTest {
        val password1 = TestUtils.randomString()
        val password2 = TestUtils.randomString()
        val password3 = TestUtils.randomString()

        val encryptedPassword1 = encrypt(password1)
        val encryptedPassword2 = encrypt(password2)

        val item11 = TestItem.random(TestItemType.login(password = encryptedPassword1))
        val item12 = TestItem.random(TestItemType.login(password = encryptedPassword1))
        val item21 = TestItem.random(TestItemType.login(password = encryptedPassword2))
        val item22 = TestItem.random(TestItemType.login(password = encryptedPassword2))
        val item23 = TestItem.random(TestItemType.login(password = encryptedPassword2))
        val item3 = TestItem.random(TestItemType.login(password = encrypt(password3)))

        val items = listOf(
            item11, item12,
            item21, item22, item23,
            item3
        )
        val res = instance(items)

        assertThat(res).isEqualTo(
            RepeatedPasswordsReport(
                repeatedPasswords = mapOf(
                    encryptedPassword1 to listOf(item11, item12),
                    encryptedPassword2 to listOf(item21, item22, item23)
                )
            )
        )
    }

    private fun encrypt(input: String) = encryptionContextProvider.withEncryptionContext {
        encrypt(input)
    }


}
