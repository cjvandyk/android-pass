package proton.android.pass.composecomponents.impl.item

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import kotlinx.collections.immutable.persistentListOf
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.ThemePairPreviewProvider
import proton.android.pass.commonuimodels.api.ItemUiModel
import proton.android.pass.composecomponents.impl.item.icon.NoteIcon
import proton.pass.domain.ItemType

private const val MAX_LINES_NOTE_DETAIL = 2

@Composable
fun NoteRow(
    modifier: Modifier = Modifier,
    item: ItemUiModel,
    highlight: String = ""
) {
    with(item.itemType as ItemType.Note) {
        var title = AnnotatedString(item.name)
        val note = if (highlight.isNotBlank()) {
            val processedText = this.text.replace("\n", " ")
            val regex = highlight.toRegex(setOf(RegexOption.IGNORE_CASE))
            val titleMatches = regex.findAll(item.name)
            if (titleMatches.any()) {
                title = item.name.highlight(titleMatches)
            }
            val noteMatches = regex.findAll(processedText)
            if (noteMatches.any()) {
                processedText.highlight(noteMatches)
            } else {
                AnnotatedString(processedText)
            }
        } else {
            val firstLines = this.text.lines().take(MAX_LINES_NOTE_DETAIL)
            AnnotatedString(firstLines.joinToString(" "))
        }

        ItemRow(
            icon = { NoteIcon() },
            title = title,
            subtitles = persistentListOf(note),
            modifier = modifier
        )
    }
}

class ThemedNoteItemPreviewProvider :
    ThemePairPreviewProvider<NoteRowParameter>(NoteRowPreviewProvider())

@Preview
@Composable
fun NoteRowPreview(
    @PreviewParameter(ThemedNoteItemPreviewProvider::class) input: Pair<Boolean, NoteRowParameter>
) {
    PassTheme(isDark = input.first) {
        Surface {
            NoteRow(
                item = input.second.model,
                highlight = input.second.highlight
            )
        }
    }
}
