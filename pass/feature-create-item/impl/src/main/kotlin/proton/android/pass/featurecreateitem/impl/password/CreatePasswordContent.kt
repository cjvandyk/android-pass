package proton.android.pass.featurecreateitem.impl.password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import me.proton.core.compose.component.ProtonSolidButton
import me.proton.core.compose.component.appbar.ProtonTopAppBar
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.ThemePairPreviewProvider
import proton.android.pass.composecomponents.impl.topbar.TopBarTitleView
import proton.android.pass.composecomponents.impl.topbar.icon.ArrowBackIcon
import proton.android.pass.featurecreateitem.impl.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreatePasswordContent(
    modifier: Modifier = Modifier,
    state: CreatePasswordUiState,
    onUpClick: () -> Unit,
    onLengthChange: (Int) -> Unit,
    onRegenerateClick: () -> Unit,
    onHasSpecialCharactersChange: (Boolean) -> Unit,
    onConfirm: (String) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ProtonTopAppBar(
                title = { TopBarTitleView(title = R.string.title_create_password) },
                navigationIcon = { ArrowBackIcon(onUpClick = onUpClick) },
                actions = {}
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            CreatePasswordViewContent(
                state = state,
                onLengthChange = onLengthChange,
                onRegenerateClick = onRegenerateClick,
                onSpecialCharactersChange = onHasSpecialCharactersChange
            )

            Spacer(modifier = Modifier.weight(1f))

            ProtonSolidButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(48.dp),
                onClick = { onConfirm(state.password) }
            ) {
                Text(stringResource(R.string.generate_password_copy))
            }
        }
    }
}

class ThemeAndCreatePasswordUiStateProvider :
    ThemePairPreviewProvider<CreatePasswordUiState>(CreatePasswordStatePreviewProvider())

@Preview
@Composable
fun CreatePasswordContentPreview(
    @PreviewParameter(ThemeAndCreatePasswordUiStateProvider::class) input: Pair<Boolean, CreatePasswordUiState>
) {
    PassTheme(isDark = input.first) {
        Surface {
            CreatePasswordContent(
                state = input.second,
                onUpClick = {},
                onLengthChange = {},
                onRegenerateClick = {},
                onHasSpecialCharactersChange = {},
                onConfirm = {}
            )
        }
    }
}
