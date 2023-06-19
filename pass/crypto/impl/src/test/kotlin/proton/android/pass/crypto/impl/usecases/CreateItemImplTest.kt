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

package proton.android.pass.crypto.impl.usecases

import me.proton.core.crypto.common.keystore.EncryptedByteArray
import org.junit.Test
import proton.android.pass.crypto.api.Base64
import proton.android.pass.crypto.fakes.context.TestEncryptionContextProvider
import proton.android.pass.crypto.fakes.utils.TestUtils
import proton.pass.domain.ItemContents
import proton_pass_item_v1.ItemV1
import kotlin.test.assertEquals

class CreateItemImplTest {

    private val encryptionContextProvider = TestEncryptionContextProvider()

    @Test
    fun testCanCreateItem() {
        val contents = ItemContents.Note(
            title = proton.android.pass.test.TestUtils.randomString(),
            note = proton.android.pass.test.TestUtils.randomString()
        )
        val (shareKey, decryptedShareKey) = TestUtils.createShareKey()

        val instance = CreateItemImpl(encryptionContextProvider)
        val output = instance.create(shareKey, contents)
        val request = output.request

        assertEquals(request.contentFormatVersion, CreateItemImpl.CONTENT_FORMAT_VERSION)
        assertEquals(request.keyRotation, shareKey.rotation)

        val decryptedContent = encryptionContextProvider.withEncryptionContext(decryptedShareKey) {
            decrypt(EncryptedByteArray(Base64.decodeBase64(request.content)))
        }
        val parsed = ItemV1.Item.parseFrom(decryptedContent)
        assertEquals(parsed.metadata.name, contents.title)
        assertEquals(parsed.metadata.note, contents.note)
    }
}
