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

package proton.android.pass.featurevault.impl.delete

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import me.proton.core.compose.theme.ProtonTheme
import me.proton.core.compose.theme.defaultNorm
import me.proton.core.compose.theme.defaultWeak
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.ThemePairPreviewProvider
import proton.android.pass.composecomponents.impl.container.roundedContainerNorm
import proton.android.pass.composecomponents.impl.dialogs.ConfirmWithLoadingDialog
import proton.android.pass.composecomponents.impl.form.ProtonTextField
import proton.android.pass.composecomponents.impl.form.ProtonTextFieldPlaceHolder
import proton.android.pass.composecomponents.impl.uievents.IsLoadingState
import proton.android.pass.composecomponents.impl.uievents.value
import proton.android.pass.featurevault.impl.R

private const val TAG_VAULT_NAME = "__VAULT_NAME__"

@Composable
fun DeleteVaultDialogContent(
    modifier: Modifier = Modifier,
    state: DeleteVaultUiState,
    onVaultTextChange: (String) -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    val bodyTextResource = stringResource(R.string.vault_delete_dialog_body)
    val bodyText = buildAnnotatedString {
        val textParts = bodyTextResource.split(TAG_VAULT_NAME)
        if (textParts.size == 2) {
            append(textParts[0])
            append(AnnotatedString(state.vaultName, SpanStyle(fontWeight = FontWeight.Bold)))
            append(textParts[1])
        } else {
            append(bodyTextResource)
        }
    }

    ConfirmWithLoadingDialog(
        modifier = modifier,
        show = true,
        isLoading = state.isLoadingState.value(),
        isConfirmActionDestructive = true,
        isConfirmEnabled = state.isButtonEnabled.value(),
        title = stringResource(R.string.vault_delete_dialog_title),
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = bodyText,
                    style = ProtonTheme.typography.defaultWeak
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .roundedContainerNorm()
                        .padding(16.dp)
                ) {
                    ProtonTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.vaultText,
                        onChange = onVaultTextChange,
                        editable = state.isLoadingState == IsLoadingState.NotLoading,
                        placeholder = {
                            ProtonTextFieldPlaceHolder(
                                text = stringResource(R.string.vault_delete_dialog_placeholder)
                            )
                        },
                        textStyle = ProtonTheme.typography.defaultNorm
                    )
                }
            }
        },
        confirmText = stringResource(R.string.vault_delete_dialog_delete_action),
        cancelText = stringResource(R.string.vault_delete_dialog_cancel_action),
        onDismiss = onDismiss,
        onConfirm = onDelete,
        onCancel = onCancel
    )
}

class DeleteVaultPreviewProvider :
    ThemePairPreviewProvider<DeleteVaultUiState>(DeleteVaultDialogPreviewProvider())

@Preview
@Composable
fun DeleteVaultDialogContentPreview(
    @PreviewParameter(DeleteVaultPreviewProvider::class) input: Pair<Boolean, DeleteVaultUiState>
) {
    PassTheme(isDark = input.first) {
        Surface {
            DeleteVaultDialogContent(
                state = input.second,
                onVaultTextChange = {},
                onDelete = {},
                onCancel = {},
                onDismiss = {}
            )
        }
    }
}
