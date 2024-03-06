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

package proton.android.pass.featureitemcreate.impl.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import me.proton.core.compose.theme.ProtonTheme
import me.proton.core.compose.theme.defaultNorm
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.ThemePairPreviewProvider
import proton.android.pass.composecomponents.impl.container.roundedContainerNorm
import proton.android.pass.composecomponents.impl.form.ProtonTextField
import proton.android.pass.composecomponents.impl.form.ProtonTextFieldLabel
import proton.android.pass.composecomponents.impl.form.ProtonTextFieldPlaceHolder
import proton.android.pass.composecomponents.impl.form.SmallCrossIconButton
import proton.android.pass.featureitemcreate.impl.R
import proton.android.pass.featureitemcreate.impl.login.WebsiteSectionEvent.AddWebsite
import proton.android.pass.featureitemcreate.impl.login.WebsiteSectionEvent.RemoveWebsite
import proton.android.pass.featureitemcreate.impl.login.WebsiteSectionEvent.WebsiteValueChanged

@Suppress("ComplexMethod")
@Composable
internal fun WebsitesSection(
    modifier: Modifier = Modifier,
    websites: ImmutableList<String>,
    websitesWithErrors: ImmutableList<Int>,
    focusLastWebsite: Boolean,
    isEditAllowed: Boolean,
    onWebsiteSectionEvent: (WebsiteSectionEvent) -> Unit
) {
    var isFocused: Boolean by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    Row(
        modifier = modifier
            .roundedContainerNorm()
            .padding(0.dp, 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(12.dp, 0.dp, 0.dp, 0.dp),
            painter = painterResource(me.proton.core.presentation.R.drawable.ic_proton_earth),
            contentDescription = "",
            tint = if (websitesWithErrors.isNotEmpty()) {
                PassTheme.colors.signalDanger
            } else {
                ProtonTheme.colors.iconWeak
            }
        )
        Column {
            websites.forEachIndexed { idx, value ->
                val textFieldModifier = if (idx < websites.count() - 1) {
                    Modifier
                } else {
                    Modifier.focusRequester(focusRequester)
                }
                ProtonTextField(
                    modifier = Modifier.heightIn(min = 48.dp),
                    textFieldModifier = textFieldModifier.fillMaxWidth(),
                    isError = websitesWithErrors.contains(idx),
                    errorMessage = stringResource(id = R.string.field_website_address_invalid),
                    value = value,
                    editable = isEditAllowed,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri
                    ),
                    textStyle = ProtonTheme.typography.defaultNorm(isEditAllowed),
                    moveToNextOnEnter = false,
                    onChange = {
                        if (it.isBlank() && websites.size > 1) {
                            onWebsiteSectionEvent(RemoveWebsite(idx))
                        } else {
                            onWebsiteSectionEvent(WebsiteValueChanged(it, idx))
                        }
                    },
                    onFocusChange = { isFocused = it },
                    label = if (idx == 0) {
                        {
                            ProtonTextFieldLabel(
                                text = stringResource(id = R.string.field_website_address_title),
                                isError = websitesWithErrors.isNotEmpty()
                            )
                        }
                    } else {
                        null
                    },
                    placeholder = {
                        ProtonTextFieldPlaceHolder(text = stringResource(id = R.string.field_website_address_hint))
                    },
                    trailingIcon = if (websites[idx].isNotEmpty() && isEditAllowed) {
                        {
                            SmallCrossIconButton {
                                if (websites[idx].isNotBlank() && websites.size > 1) {
                                    onWebsiteSectionEvent(RemoveWebsite(idx))
                                } else {
                                    onWebsiteSectionEvent(WebsiteValueChanged("", idx))
                                }
                            }
                        }
                    } else {
                        null
                    }
                )
            }

            // If we receive focusLastWebsite, call requestFocus
            LaunchedEffect(focusLastWebsite) {
                if (focusLastWebsite) {
                    focusRequester.requestFocus()
                }
            }

            val shouldShowAddWebsiteButton = (
                websites.count() == 1 && websites.last()
                    .isNotEmpty() || websites.count() > 1
                ) && isEditAllowed
            AnimatedVisibility(shouldShowAddWebsiteButton) {
                val ableToAddNewWebsite = websites.lastOrNull()?.isNotEmpty() ?: false
                Button(
                    enabled = ableToAddNewWebsite,
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        disabledElevation = 0.dp
                    ),
                    contentPadding = PaddingValues(0.dp),
                    onClick = { onWebsiteSectionEvent(AddWebsite) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                        disabledBackgroundColor = Color.Transparent,
                        contentColor = ProtonTheme.colors.brandNorm,
                        disabledContentColor = ProtonTheme.colors.interactionDisabled
                    )
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(me.proton.core.presentation.R.drawable.ic_proton_plus),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = stringResource(R.string.field_website_add_another),
                        style = ProtonTheme.typography.defaultNorm,
                        color = ProtonTheme.colors.brandNorm
                    )
                }
            }
        }
    }
}

class ThemedWebsitesSectionPP :
    ThemePairPreviewProvider<WebsitesPreviewParameter>(WebsitesSectionPreviewProvider())

@Preview
@Composable
fun WebsitesSectionPreview(
    @PreviewParameter(ThemedWebsitesSectionPP::class) input: Pair<Boolean, WebsitesPreviewParameter>
) {
    PassTheme(isDark = input.first) {
        Surface {
            WebsitesSection(
                websites = input.second.websites,
                focusLastWebsite = false,
                isEditAllowed = input.second.isEditAllowed,
                websitesWithErrors = persistentListOf(),
                onWebsiteSectionEvent = {}
            )
        }
    }
}

class ThemedWebsitesSectionErrorsPP :
    ThemePairPreviewProvider<WebsitesPreviewParameter>(WebsitesSectionPreviewProvider(withErrors = true))

@Preview
@Composable
fun WebsitesSectionWithErrorsPreview(
    @PreviewParameter(ThemedWebsitesSectionErrorsPP::class) input: Pair<Boolean, WebsitesPreviewParameter>
) {
    PassTheme(isDark = input.first) {
        Surface {
            WebsitesSection(
                websites = input.second.websites,
                focusLastWebsite = false,
                isEditAllowed = input.second.isEditAllowed,
                websitesWithErrors = input.second.websitesWithErrors,
                onWebsiteSectionEvent = {}
            )
        }
    }
}
