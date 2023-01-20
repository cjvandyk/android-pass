package proton.android.pass.presentation.components.navigation.drawer

import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import me.proton.core.compose.component.ProtonAlertDialog
import me.proton.core.compose.theme.ProtonTheme
import me.proton.core.compose.theme.default
import me.proton.core.compose.theme.headline
import me.proton.core.presentation.R
import proton.android.pass.log.api.PassLogger
import proton.android.pass.presentation.navigation.drawer.NavigationDrawerListItem

@Suppress("UnusedPrivateMember", "OptionalUnit")
@Composable
fun InternalDrawerItem(
    modifier: Modifier = Modifier,
    closeDrawerAction: () -> Unit,
    onClick: () -> Unit,
    viewModel: InternalDrawerItemViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    NavigationDrawerListItem(
        title = "(alpha) Share logs",
        isSelected = false,
        closeDrawerAction = closeDrawerAction,
        modifier = modifier,
        onClick = { showDialog = true },
        startContent = {
            Icon(
                painter = painterResource(me.proton.pass.presentation.R.drawable.ic_settings),
                contentDescription = null,
                tint = ProtonTheme.colors.iconWeak
            )
        }
    )

    if (showDialog) {
        val randomId by remember {
            mutableStateOf(
                List(size = 8) {
                    (('a'..'z') + ('A'..'Z') + ('0'..'9'))
                        .random()
                }.joinToString("")
            )
        }
        ProtonTheme {
            ProtonAlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        text = "Share this id with us:",
                        style = ProtonTheme.typography.headline
                    )
                },
                text = {
                    Text(
                        text = randomId,
                        style = ProtonTheme.typography.default
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            PassLogger.e("Alpha", AlphaException(randomId))
                            coroutineScope.launch {
                                context.getSystemService(ClipboardManager::class.java)
                                    ?.setPrimaryClip(
                                        ClipData.newPlainText("pass-contents", randomId)
                                    )
                                viewModel.shareLogCatOutput(context)
                            }
                            showDialog = false
                        }
                    ) {
                        Text(text = "Share log file and copy id")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(text = stringResource(id = R.string.presentation_alert_cancel))
                    }
                }
            )
        }
    }
}

internal class AlphaException(message: String) : Exception(message)
