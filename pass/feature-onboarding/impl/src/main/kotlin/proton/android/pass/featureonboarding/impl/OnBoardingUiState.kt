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

package proton.android.pass.featureonboarding.impl

import androidx.compose.runtime.Stable
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import proton.android.pass.featureonboarding.impl.OnBoardingPageName.Autofill
import proton.android.pass.featureonboarding.impl.OnBoardingPageName.Fingerprint
import proton.android.pass.featureonboarding.impl.OnBoardingPageName.InvitePending
import proton.android.pass.featureonboarding.impl.OnBoardingPageName.Last

@Stable
data class OnBoardingUiState(
    val selectedPage: Int,
    val enabledPages: Set<OnBoardingPageName>,
    val isCompleted: Boolean
) {
    companion object {
        val Initial = OnBoardingUiState(0, emptySet(), false)
    }
}

@Stable
enum class OnBoardingPageName {
    Autofill, Fingerprint, Last, InvitePending
}

open class OnBoardingUiStatePreviewProvider : PreviewParameterProvider<OnBoardingUiState> {
    override val values: Sequence<OnBoardingUiState> = sequenceOf(
        OnBoardingUiState(0, setOf(Autofill), false),
        OnBoardingUiState(0, setOf(Fingerprint), false),
        OnBoardingUiState(0, setOf(Last), false),
        OnBoardingUiState(0, setOf(Autofill, Fingerprint), false),
        OnBoardingUiState(0, setOf(InvitePending), false)
    )
}
