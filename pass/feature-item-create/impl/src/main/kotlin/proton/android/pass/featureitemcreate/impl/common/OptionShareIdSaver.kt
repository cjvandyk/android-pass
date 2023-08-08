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

package proton.android.pass.featureitemcreate.impl.common

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import proton.android.pass.common.api.None
import proton.android.pass.common.api.Option
import proton.android.pass.common.api.Some
import proton.pass.domain.ShareId

val OptionShareIdSaver: Saver<Option<ShareId>, Any> = run {
    val shareId = "share_id"
    mapSaver(
        save = {
            when (it) {
                None -> emptyMap()
                is Some -> mapOf(shareId to it.value.id)
            }
        },
        restore = { values ->
            if (values.isNotEmpty()) {
                Some(ShareId(values[shareId] as String))
            } else {
                None
            }
        }
    )
}
