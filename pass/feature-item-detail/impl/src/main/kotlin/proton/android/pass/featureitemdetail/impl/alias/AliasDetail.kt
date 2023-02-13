package proton.android.pass.featureitemdetail.impl.alias

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.pass.domain.Item

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AliasDetail(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    item: Item
) {
    val viewModel: AliasDetailViewModel = hiltViewModel()
    LaunchedEffect(item) {
        viewModel.setItem(item)
    }

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = topBar
    ) { padding ->
        AliasDetailContent(
            modifier = modifier
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            state = viewState,
            emitSnackbarMessage = { viewModel.emitSnackbarMessage(it) }
        )
    }
}
