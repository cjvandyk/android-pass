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

package proton.android.pass.featureitemcreate.impl.attachments

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import proton.android.pass.commonui.api.ClassHolder
import proton.android.pass.commonuimodels.api.attachments.AttachmentsState
import proton.android.pass.domain.ItemId
import proton.android.pass.domain.ShareId
import proton.android.pass.domain.attachments.Attachment
import proton.android.pass.featureitemcreate.impl.common.attachments.AttachmentsHandler
import java.net.URI

class FakeAttachmentHandler : AttachmentsHandler {

    override val isUploadingAttachment: Flow<Set<URI>>
        get() = flowOf(emptySet())
    override val attachmentState: Flow<AttachmentsState>
        get() = flowOf(AttachmentsState.Initial)

    override fun openDraftAttachment(
        contextHolder: ClassHolder<Context>,
        uri: URI,
        mimetype: String
    ) {
        // no-op
    }

    override suspend fun uploadNewAttachment(uri: URI) {
        // no-op
    }

    override fun onClearAttachments() {
        // no-op
    }

    override fun observeNewAttachments(onNewAttachment: (Set<URI>) -> Unit): Flow<Set<URI>> = flowOf(emptySet())

    override suspend fun getAttachmentsForItem(shareId: ShareId, itemId: ItemId) {
        // no-op
    }

    override suspend fun openAttachment(contextHolder: ClassHolder<Context>, attachment: Attachment) {
        // no-op
    }
}
