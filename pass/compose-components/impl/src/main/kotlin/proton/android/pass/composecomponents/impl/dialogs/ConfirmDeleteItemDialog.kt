package proton.android.pass.composecomponents.impl.dialogs

import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import me.proton.core.compose.component.ProtonAlertDialog
import me.proton.core.compose.component.ProtonAlertDialogText
import me.proton.core.compose.component.ProtonTextButton
import me.proton.core.compose.theme.ProtonTheme
import me.proton.core.compose.theme.headline
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.ThemePreviewProvider
import proton.android.pass.composecomponents.impl.R

@Composable
fun ConfirmDeleteItemDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    itemName: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    ProtonAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.alert_confirm_delete_item_dialog_title),
                style = ProtonTheme.typography.headline,
                color = ProtonTheme.colors.textNorm
            )
        },
        text = {
            ProtonAlertDialogText(
                text = stringResource(
                    R.string.alert_confirm_delete_item_dialog_message,
                    itemName
                )
            )
        },
        confirmButton = {
            ProtonTextButton(
                onClick = onConfirm,
                colors = buttonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = ProtonTheme.colors.notificationError
                )
            ) {
                Text(
                    text = stringResource(R.string.action_accept_move_to_trash),
                    color = ProtonTheme.colors.notificationError,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        dismissButton = {
            ProtonTextButton(onClick = onCancel) {
                Text(
                    text = stringResource(R.string.action_cancel_send_to_trash),
                    color = ProtonTheme.colors.brandNorm,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    )
}

@Preview
@Composable
fun ConfirmMoveItemToTrashDialogPreview(
    @PreviewParameter(ThemePreviewProvider::class) isDark: Boolean
) {
    PassTheme(isDark = isDark) {
        Surface {
            ConfirmDeleteItemDialog(
                itemName = "an item",
                show = true,
                onConfirm = {},
                onCancel = {},
                onDismiss = {}
            )
        }
    }
}
