package proton.android.pass.composecomponents.impl.topbar.iconbutton

import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@ExperimentalComposeUiApi
@Composable
fun NavigationIconButton(
    modifier: Modifier = Modifier,
    onUpClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    IconButton(
        modifier = modifier,
        onClick = {
            keyboardController?.hide()
            onUpClick()
        }
    ) {
        icon()
    }
}
