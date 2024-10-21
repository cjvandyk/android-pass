/*
 * Copyright (c) 2024 Proton AG
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

package proton.android.pass.composecomponents.impl.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import me.proton.core.compose.theme.ProtonTheme
import me.proton.core.compose.theme.captionWeak
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.PassTopBarBackButtonType
import proton.android.pass.commonui.api.Radius
import proton.android.pass.commonui.api.Spacing
import proton.android.pass.composecomponents.impl.R
import proton.android.pass.composecomponents.impl.buttons.LoadingCircleButton
import proton.android.pass.composecomponents.impl.form.PassVerificationCodeTextField
import proton.android.pass.composecomponents.impl.text.PassTextWithLink
import proton.android.pass.composecomponents.impl.text.Text
import proton.android.pass.composecomponents.impl.topbar.PassExtendedTopBar
import me.proton.core.presentation.R as CoreR

private const val VERIFICATION_CODE_LENGTH = 6

@Composable
fun PassCodeVerificationScreen(
    modifier: Modifier = Modifier,
    topBarTitle: String,
    topBarSubtitle: String,
    onUpClick: () -> Unit,
    isActionLoading: Boolean,
    onActionClick: () -> Unit,
    verificationCode: String,
    canEnterVerificationCode: Boolean,
    onVerificationCodeChange: (String) -> Unit,
    onResendVerificationCodeClick: () -> Unit,
    verificationCodeLength: Int = VERIFICATION_CODE_LENGTH
) {
    val isActionEnabled = remember(verificationCode) {
        verificationCode.length == verificationCodeLength
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            PassExtendedTopBar(
                backButton = PassTopBarBackButtonType.Cross,
                title = topBarTitle,
                subtitle = topBarSubtitle,
                onUpClick = onUpClick,
                actions = {
                    LoadingCircleButton(
                        modifier = Modifier.padding(vertical = Spacing.small),
                        isLoading = isActionLoading,
                        buttonEnabled = isActionEnabled,
                        color = if (isActionEnabled) {
                            PassTheme.colors.interactionNormMajor1
                        } else {
                            PassTheme.colors.interactionNormMajor1.copy(alpha = 0.6f)
                        },
                        text = {
                            Text.Body2Regular(
                                text = stringResource(id = R.string.action_continue),
                                color = PassTheme.colors.textInvert
                            )
                        },
                        onClick = onActionClick
                    )
                }
            )
        }
    ) { innerPaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues = innerPaddingValues)
                .padding(
                    horizontal = Spacing.medium,
                    vertical = Spacing.large
                ),
            verticalArrangement = Arrangement.spacedBy(space = Spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PassVerificationCodeTextField(
                verificationCode = verificationCode,
                verificationCodeLength = verificationCodeLength,
                canEnterVerificationCode = canEnterVerificationCode,
                onVerificationCodeChange = onVerificationCodeChange
            )

            PassRequestVerificationCode(
                showRequestVerificationCodeOptions = canEnterVerificationCode,
                onResendVerificationCodeClick = onResendVerificationCodeClick
            )
        }
    }
}

private const val CHEVRON_ROTATION_INITIAL = 0F
private const val CHEVRON_ROTATION_TOGGLED = -180F
private const val CHEVRON_ROTATION_LABEL = "chevron_rotation_label"

@Composable
private fun PassRequestVerificationCode(
    modifier: Modifier = Modifier,
    showRequestVerificationCodeOptions: Boolean,
    onResendVerificationCodeClick: () -> Unit
) {
    var showVerificationCodeSuggestions by remember { mutableStateOf(false) }

    val chevronRotation by remember(showVerificationCodeSuggestions) {
        derivedStateOf {
            if (showVerificationCodeSuggestions) CHEVRON_ROTATION_TOGGLED else CHEVRON_ROTATION_INITIAL
        }
    }

    val chevronRotationDegrees by animateFloatAsState(
        targetValue = chevronRotation,
        label = CHEVRON_ROTATION_LABEL
    )

    AnimatedVisibility(
        visible = showRequestVerificationCodeOptions,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(space = Spacing.small)
        ) {
            Row(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(size = Radius.small))
                    .align(alignment = Alignment.CenterHorizontally)
                    .clickable {
                        showVerificationCodeSuggestions = !showVerificationCodeSuggestions
                    }
                    .padding(
                        start = Spacing.small,
                        top = Spacing.small,
                        end = Spacing.extraSmall,
                        bottom = Spacing.small
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space = Spacing.extraSmall)
            ) {
                Text.DefaultSmallWeak(
                    text = stringResource(id = R.string.verification_code_not_received),
                    color = PassTheme.colors.textWeak
                )

                Icon(
                    modifier = Modifier.rotate(degrees = chevronRotationDegrees),
                    painter = painterResource(id = CoreR.drawable.ic_proton_chevron_tiny_down),
                    contentDescription = null,
                    tint = PassTheme.colors.textWeak
                )
            }

            AnimatedVisibility(visible = showVerificationCodeSuggestions) {
                Column(
                    modifier = Modifier.padding(horizontal = Spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(space = Spacing.small)
                ) {
                    Text.CaptionWeak(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.verification_code_suggestion_spam),
                        color = PassTheme.colors.textWeak,
                        textAlign = TextAlign.Center
                    )

                    PassTextWithLink(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = stringResource(
                            id = R.string.verification_code_suggestion_resend,
                            stringResource(id = R.string.verification_code_suggestion_resend_link)
                        ),
                        textStyle = ProtonTheme.typography.captionWeak.copy(
                            color = PassTheme.colors.textWeak
                        ),
                        linkText = stringResource(id = R.string.verification_code_suggestion_resend_link),
                        linkStyle = ProtonTheme.typography.captionWeak.copy(
                            color = PassTheme.colors.interactionNormMajor1
                        ),
                        onLinkClick = { onResendVerificationCodeClick() }
                    )
                }
            }
        }
    }
}
