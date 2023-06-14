package proton.android.pass.featureitemdetail.impl.login

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.ThemePairPreviewProvider
import proton.android.pass.composecomponents.impl.container.RoundedCornersColumn
import proton.android.pass.composecomponents.impl.item.SectionTitle
import proton.android.pass.featureitemdetail.impl.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WebsiteSection(
    modifier: Modifier = Modifier,
    websites: ImmutableList<String>,
    onEvent: (LoginDetailEvent) -> Unit
) {
    if (websites.isEmpty()) return
    RoundedCornersColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(me.proton.core.presentation.R.drawable.ic_proton_earth),
                contentDescription = null,
                tint = PassTheme.colors.loginInteractionNorm
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                SectionTitle(text = stringResource(R.string.websites_section_title))
                Spacer(modifier = Modifier.height(8.dp))
                websites.forEach { website ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = { onEvent(LoginDetailEvent.OnWebsiteClicked(website)) },
                                onLongClick = {
                                    onEvent(LoginDetailEvent.OnWebsiteLongClicked(website))
                                }
                            )
                            .padding(vertical = 8.dp),
                        text = website,
                        color = PassTheme.colors.loginInteractionNormMajor2,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

class ThemedWebsiteSectionPreviewProvider :
    ThemePairPreviewProvider<List<String>>(WebsiteProvider())

@Preview
@Composable
fun WebsitesSectionPreview(
    @PreviewParameter(ThemedWebsiteSectionPreviewProvider::class) input: Pair<Boolean, List<String>>
) {
    PassTheme(isDark = input.first) {
        Surface {
            WebsiteSection(
                websites = input.second.toImmutableList(),
                onEvent = {}
            )
        }
    }
}
