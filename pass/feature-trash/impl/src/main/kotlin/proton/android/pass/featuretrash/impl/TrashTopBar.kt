package proton.android.pass.featuretrash.impl

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import proton.android.pass.composecomponents.impl.topbar.icon.HamburgerIcon
import proton.android.pass.composecomponents.impl.topbar.TopBarTitleView
import me.proton.core.compose.component.appbar.ProtonTopAppBar
import me.proton.core.compose.theme.ProtonTheme
import proton.android.pass.commonui.api.ThemedBooleanPreviewProvider

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun TrashTopBar(
    modifier: Modifier = Modifier,
    showActions: Boolean,
    onDrawerIconClick: () -> Unit,
    onMoreOptionsClick: () -> Unit
) {
    ProtonTopAppBar(
        modifier = modifier,
        title = { TopBarTitleView(title = stringResource(id = R.string.title_trash)) },
        navigationIcon = {
            HamburgerIcon(
                onClick = onDrawerIconClick
            )
        },
        actions = {
            if (showActions) {
                IconButton(onClick = onMoreOptionsClick) {
                    Icon(
                        painterResource(me.proton.core.presentation.R.drawable.ic_proton_three_dots_vertical),
                        contentDescription = null,
                        tint = ProtonTheme.colors.iconNorm
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun TrashTopBarPreview(
    @PreviewParameter(ThemedBooleanPreviewProvider::class) input: Pair<Boolean, Boolean>
) {
    ProtonTheme(isDark = input.first) {
        Surface {
            TrashTopBar(
                showActions = input.second,
                onDrawerIconClick = {},
                onMoreOptionsClick = {}
            )
        }
    }
}
