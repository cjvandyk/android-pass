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

package proton.android.pass.featureitemcreate.impl.bottomsheets.customfield

import androidx.annotation.StringRes
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.featureitemcreate.impl.R
import proton.android.pass.test.CallChecker
import proton.android.pass.test.HiltComponentActivity

@HiltAndroidTest
class AddCustomFieldBottomsheetTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltComponentActivity>()

    @Test
    fun testAddTextField() {
        performTest(R.string.bottomsheet_custom_field_type_text, AddCustomFieldNavigation.AddText)
    }

    @Test
    fun testAddTotpField() {
        performTest(R.string.bottomsheet_custom_field_type_totp, AddCustomFieldNavigation.AddTotp)
    }

    @Test
    fun testAddHiddenField() {
        performTest(R.string.bottomsheet_custom_field_type_hidden, AddCustomFieldNavigation.AddHidden)
    }

    private fun performTest(@StringRes text: Int, navigation: AddCustomFieldNavigation) {
        val checker = CallChecker<Unit>()
        composeTestRule.apply {
            setContent {
                PassTheme {
                    AddCustomFieldBottomSheet(
                        onNavigate = {
                            if (it == navigation) {
                                checker.call()
                            }
                        }
                    )
                }
            }

            onNodeWithText(activity.getString(text)).performClick()
            waitUntil { checker.isCalled }
        }
    }
}

