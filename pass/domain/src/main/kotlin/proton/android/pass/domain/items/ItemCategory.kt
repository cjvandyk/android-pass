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

package proton.android.pass.domain.items

private const val ITEM_CATEGORY_UNKNOWN = -1
private const val ITEM_CATEGORY_LOGIN = 0
private const val ITEM_CATEGORY_ALIAS = 1
private const val ITEM_CATEGORY_NOTE = 2
private const val ITEM_CATEGORY_PASSWORD = 3
private const val ITEM_CATEGORY_CREDIT_CARD = 4

enum class ItemCategory(val value: Int) {
    Unknown(ITEM_CATEGORY_UNKNOWN),
    Login(ITEM_CATEGORY_LOGIN),
    Alias(ITEM_CATEGORY_ALIAS),
    Note(ITEM_CATEGORY_NOTE),
    Password(ITEM_CATEGORY_PASSWORD),
    CreditCard(ITEM_CATEGORY_CREDIT_CARD),
}
