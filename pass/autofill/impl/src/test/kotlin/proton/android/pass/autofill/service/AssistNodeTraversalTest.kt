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

package proton.android.pass.autofill.service

import android.text.InputType
import android.view.View
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import proton.android.pass.autofill.AssistNodeTraversal
import proton.android.pass.autofill.entities.AutofillFieldId
import proton.android.pass.autofill.entities.AutofillNode
import proton.android.pass.autofill.entities.FieldType
import proton.android.pass.autofill.entities.InputTypeValue
import proton.android.pass.autofill.service.utils.newAutofillFieldId
import proton.android.pass.common.api.Some
import proton.android.pass.common.api.toOption

@RunWith(JUnit4::class)
class AssistNodeTraversalTest {

    @Test
    fun allNodesAreVisited() {
        // Mix of valid nodes and invalid ones
        val rootNode = makeValidNode(
            children = listOf(
                makeNode(
                    children = listOf(makeNode(), makeValidNode())
                ),
                makeValidNode()
            )
        )

        val traversal = AssistNodeTraversal()
        traversal.traverse(rootNode)

        Assert.assertEquals(5, traversal.visitedNodes)
    }

/*    @Test
    fun canExtractFieldFromHints() {
        val rootNode = makeNode(
            autofillId = newAutofillFieldId(),
            autofillHints = listOf(View.AUTOFILL_HINT_EMAIL_ADDRESS),
            isImportantForAutofill = true
        )

        val result = AssistNodeTraversal().traverse(rootNode)

        Assert.assertEquals(FieldType.Email, result.firstOrNull()?.type)
    }*/

    @Test
    fun canExtractFieldFromInputType() {
        val rootNode = makeNode(
            autofillId = newAutofillFieldId(),
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
            isImportantForAutofill = true
        )

        val result = AssistNodeTraversal().traverse(rootNode)

        Assert.assertEquals(FieldType.Email, result.fields.firstOrNull()?.type)
    }

/*
    @Test
    fun canExtractFieldFromHtmlAttributes() {
        val rootNode = makeNode(
            autofillId = newAutofillFieldId(),
            htmlAttributes = listOf("type" to "text"),
            isImportantForAutofill = true
        )

        val result = AssistNodeTraversal().traverse(rootNode)

        Assert.assertEquals(FieldType.Other, result.firstOrNull()?.type)
    }
*/

    @Test
    fun autofillHintsParsingReturnsKnownFieldType() {
        val traversal = AssistNodeTraversal()

        val usernameType = traversal.detectFieldTypeUsingAutofillHint(View.AUTOFILL_HINT_USERNAME)
        val emailType = traversal.detectFieldTypeUsingAutofillHint(View.AUTOFILL_HINT_EMAIL_ADDRESS)
        val passwordType = traversal.detectFieldTypeUsingAutofillHint(View.AUTOFILL_HINT_PASSWORD)
        val creditCardType =
            traversal.detectFieldTypeUsingAutofillHint(View.AUTOFILL_HINT_CREDIT_CARD_NUMBER)

        Assert.assertEquals(FieldType.Username, usernameType)
        Assert.assertEquals(FieldType.Email, emailType)
        Assert.assertEquals(FieldType.Password, passwordType)
        // Still not supported
        Assert.assertEquals(FieldType.Unknown, creditCardType)
    }

    @Test
    fun `is able to extract URL`() {
        val domain = "somedomain.example"
        val structure = makeNode(
            children = listOf(
                makeNode(
                    webDomain = domain
                )
            )
        )
        val result = AssistNodeTraversal().traverse(structure)
        Assert.assertEquals(Some(domain), result.url)
    }

    @Test
    fun `return the first URL if it contains multiple ones`() {
        val domain1 = "somedomain.example"
        val domain2 = "other.example"
        val structure = makeNode(
            children = listOf(
                makeNode(webDomain = domain1),
                makeNode(webDomain = domain2)
            )
        )
        val result = AssistNodeTraversal().traverse(structure)
        Assert.assertEquals(Some(domain1), result.url)
    }

    @Test
    fun `return the parent URL if the child also contains a URL`() {
        val domain1 = "somedomain.example"
        val domain2 = "other.example"
        val structure = makeNode(
            webDomain = domain1,
            children = listOf(
                makeNode(
                    webDomain = domain2
                )
            )
        )
        val result = AssistNodeTraversal().traverse(structure)
        Assert.assertEquals(Some(domain1), result.url)


    }

/*    @Test
    fun inputTypeParsingReturnsKnownFieldType() {
        val traversal = AssistNodeTraversal()

        val fieldWithInputType = { inputType: Int ->
            traversal.detectFieldTypeUsingInputType(InputTypeValue(inputType))
        }

        val phoneType = fieldWithInputType(InputType.TYPE_CLASS_PHONE)
        val emailType = fieldWithInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        val webEmailType = fieldWithInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS)
        val passwordType = fieldWithInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
        val numPasswordType = fieldWithInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD)
        val webPasswordType = fieldWithInputType(InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD)
        val visiblePasswordType = fieldWithInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
        val nameType = fieldWithInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
        val longMessageType = fieldWithInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE)

        Assert.assertEquals(FieldType.Phone, phoneType)
        Assert.assertEquals(FieldType.Email, emailType)
        Assert.assertEquals(FieldType.Email, webEmailType)
        Assert.assertEquals(FieldType.Password, passwordType)
        Assert.assertEquals(FieldType.Password, numPasswordType)
        Assert.assertEquals(FieldType.Password, webPasswordType)
        Assert.assertEquals(FieldType.Password, visiblePasswordType)
        Assert.assertEquals(FieldType.FullName, nameType)
        // Not supported
        Assert.assertEquals(FieldType.Unknown, longMessageType)
    }*/

/*    @Test
    fun htmlAttributesParsingReturnsKnownFieldType() {
        val traversal = AssistNodeTraversal()

        val phoneType = traversal.detectFieldTypeUsingHtmlInfo(listOf("type" to "tel"))
        val emailType = traversal.detectFieldTypeUsingHtmlInfo(listOf("type" to "email"))
        val passwordType = traversal.detectFieldTypeUsingHtmlInfo(listOf("type" to "password"))
        val genericType = traversal.detectFieldTypeUsingHtmlInfo(listOf("type" to "text"))
        val buttonType = traversal.detectFieldTypeUsingHtmlInfo(listOf("type" to "button"))

        Assert.assertEquals(FieldType.Phone, phoneType)
        Assert.assertEquals(FieldType.Email, emailType)
        Assert.assertEquals(FieldType.Password, passwordType)
        Assert.assertEquals(FieldType.Other, genericType)
        // Not supported
        Assert.assertEquals(FieldType.Unknown, buttonType)
    }*/

    private fun makeValidNode(children: List<AutofillNode> = emptyList()) =
        makeNode(
            text = "a@b.com",
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
            children = children
        )

    private fun makeNode(
        autofillId: AutofillFieldId? = newAutofillFieldId(),
        className: String? = null,
        isImportantForAutofill: Boolean = false,
        text: String? = null,
        inputType: Int = 0,
        autofillHints: List<String> = emptyList(),
        htmlAttributes: List<Pair<String, String>> = emptyList(),
        children: List<AutofillNode> = emptyList(),
        webDomain: String? = null
    ) =
        AutofillNode(
            id = autofillId,
            className = className,
            isImportantForAutofill = isImportantForAutofill,
            text = text,
            autofillValue = null,
            inputType = InputTypeValue(inputType),
            autofillHints = autofillHints,
            htmlAttributes = htmlAttributes,
            children = children,
            url = webDomain.toOption(),
            hintKeywordList = emptyList(),
            isFocused = false
        )

}
