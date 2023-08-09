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

package proton.pass.domain

data class Vault(
    val shareId: ShareId,
    val name: String,
    val color: ShareColor = ShareColor.Color1,
    val icon: ShareIcon = ShareIcon.Icon1,
    val isPrimary: Boolean,
    val isOwned: Boolean = true,
    val role: ShareRole = ShareRole.Admin,
    val members: Int = 1,
    val shared: Boolean = false
)

fun List<Vault>.sorted(): List<Vault> = sortedBy { it.name.lowercase() }
