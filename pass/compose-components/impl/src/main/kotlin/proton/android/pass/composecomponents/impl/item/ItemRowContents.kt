package proton.android.pass.composecomponents.impl.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import proton.android.pass.commonuimodels.api.ItemUiModel
import proton.pass.domain.ItemType

@Composable
internal fun ItemRowContents(
    modifier: Modifier = Modifier,
    item: ItemUiModel,
    highlight: String,
    vaultIcon: Int? = null
) {
    when (item.itemType) {
        is ItemType.Login -> LoginRow(
            modifier = modifier,
            item = item,
            highlight = highlight,
            vaultIcon = vaultIcon
        )
        is ItemType.Note -> NoteRow(
            modifier = modifier,
            item = item,
            highlight = highlight,
            vaultIcon = vaultIcon
        )
        is ItemType.Alias -> AliasRow(
            modifier = modifier,
            item = item,
            highlight = highlight,
            vaultIcon = vaultIcon
        )
        ItemType.Password -> {}
    }
}
