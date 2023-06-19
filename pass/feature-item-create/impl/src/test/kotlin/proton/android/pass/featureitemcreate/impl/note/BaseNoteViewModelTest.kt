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

package proton.android.pass.featureitemcreate.impl.note

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import proton.android.pass.featureitemcreate.impl.note.BaseNoteUiState.Companion.Initial
import proton.android.pass.notifications.fakes.TestSnackbarDispatcher
import proton.android.pass.test.MainDispatcherRule

internal class BaseNoteViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var snackbarDispatcher: TestSnackbarDispatcher
    private lateinit var baseNoteViewModel: BaseNoteViewModel

    @Before
    fun setUp() {
        snackbarDispatcher = TestSnackbarDispatcher()
        baseNoteViewModel = object : BaseNoteViewModel(
            snackbarDispatcher
        ) {}
    }

    @Test
    fun `should start with the initial state`() = runTest {
        baseNoteViewModel.baseNoteUiState.test {
            assertThat(awaitItem()).isEqualTo(Initial)
        }
    }

    @Test
    fun `when the title has changed, the state should hold it`() = runTest {
        val titleInput = "Title Changed"
        baseNoteViewModel.onTitleChange(titleInput)
        baseNoteViewModel.baseNoteUiState.test {
            assertThat(awaitItem().noteItem)
                .isEqualTo(Initial.noteItem.copy(title = titleInput))
        }
    }

    @Test
    fun `when the note has changed, the state should hold it`() = runTest {
        val noteInput = "Note Changed"
        baseNoteViewModel.onNoteChange(noteInput)
        baseNoteViewModel.baseNoteUiState.test {
            assertThat(awaitItem().noteItem)
                .isEqualTo(Initial.noteItem.copy(note = noteInput))
        }
    }
}
