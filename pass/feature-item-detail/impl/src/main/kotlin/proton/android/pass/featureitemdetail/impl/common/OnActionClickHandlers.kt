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

package proton.android.pass.featureitemdetail.impl.common

import proton.android.pass.commonuimodels.api.ItemUiModel
import proton.android.pass.data.api.usecases.ItemActions
import proton.android.pass.data.api.usecases.capabilities.CanShareVaultStatus
import proton.android.pass.featureitemdetail.impl.ItemDetailCannotPerformActionType
import proton.android.pass.featureitemdetail.impl.ItemDetailNavigation

fun onEditClick(
    itemActions: ItemActions,
    onNavigate: (ItemDetailNavigation) -> Unit,
    itemUiModel: ItemUiModel
) {
    when (val canEdit = itemActions.canEdit) {
        is ItemActions.CanEditActionState.Disabled -> {
            val type = when (canEdit.reason) {
                ItemActions.CanEditActionState.CanEditDisabledReason.Downgraded -> {
                    ItemDetailCannotPerformActionType.CannotEditBecauseNeedsUpgrade
                }

                ItemActions.CanEditActionState.CanEditDisabledReason.ItemInTrash -> {
                    ItemDetailCannotPerformActionType.CannotEditBecauseItemInTrash
                }

                ItemActions.CanEditActionState.CanEditDisabledReason.NotEnoughPermission -> {
                    ItemDetailCannotPerformActionType.CannotEditBecauseNoPermissions
                }
            }
            onNavigate(ItemDetailNavigation.CannotPerformAction(type))
        }

        ItemActions.CanEditActionState.Enabled -> {
            onNavigate(ItemDetailNavigation.OnEdit(itemUiModel))
        }
    }
}

fun onShareClick(
    itemActions: ItemActions,
    onNavigate: (ItemDetailNavigation) -> Unit,
    itemUiModel: ItemUiModel
) {
    when (val canShare = itemActions.canShare) {
        is CanShareVaultStatus.CanShare -> {
            onNavigate(
                ItemDetailNavigation.OnShareVault(
                    shareId = itemUiModel.shareId,
                    itemId = itemUiModel.id
                )
            )
        }

        is CanShareVaultStatus.CannotShare -> when (canShare.reason) {
            CanShareVaultStatus.CannotShareReason.NotEnoughInvites -> {
                onNavigate(
                    ItemDetailNavigation.CannotPerformAction(
                        type = ItemDetailCannotPerformActionType.CannotShareBecauseLimitReached
                    )
                )
            }

            CanShareVaultStatus.CannotShareReason.NotEnoughPermissions -> {
                onNavigate(
                    ItemDetailNavigation.CannotPerformAction(
                        ItemDetailCannotPerformActionType.CannotShareBecauseNoPermissions
                    )
                )
            }

            CanShareVaultStatus.CannotShareReason.Unknown -> {}
        }
    }
}
