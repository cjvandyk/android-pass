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

package proton.android.pass.ui.shortcuts

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import proton.android.pass.log.api.ShareLogs
import javax.inject.Inject

@HiltViewModel
class ShortcutViewModel @Inject constructor(
    private val shareLogs: ShareLogs
): ViewModel() {

    private val _closeFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val closeState: StateFlow<Boolean> = _closeFlow

    fun onShareLogs(context: Context) = viewModelScope.launch {
        val intent = shareLogs()
        if (intent != null) {
            context.startActivity(intent)
        }
        _closeFlow.update { true }
    }

}
