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

package proton.android.pass.featurevault.impl.bottomsheet

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import proton.android.pass.composecomponents.impl.uievents.IsLoadingState
import proton.android.pass.crypto.fakes.context.TestEncryptionContextProvider
import proton.android.pass.data.fakes.usecases.TestGetVaultById
import proton.android.pass.data.fakes.usecases.TestUpdateVault
import proton.android.pass.featurevault.impl.VaultSnackbarMessage
import proton.android.pass.navigation.api.CommonNavArgId
import proton.android.pass.notifications.fakes.TestSnackbarDispatcher
import proton.android.pass.test.MainDispatcherRule
import proton.android.pass.test.TestSavedStateHandle
import proton.android.pass.test.domain.TestShare
import proton.pass.domain.ShareColor
import proton.pass.domain.ShareIcon
import proton.pass.domain.ShareId
import proton.pass.domain.Vault

class EditVaultViewModelTest {

    @get:Rule
    val dispatcher = MainDispatcherRule()

    private lateinit var encryptionContextProvider: TestEncryptionContextProvider
    private lateinit var instance: EditVaultViewModel
    private lateinit var snackbar: TestSnackbarDispatcher
    private lateinit var updateVault: TestUpdateVault
    private lateinit var getVaultById: TestGetVaultById

    @Before
    fun setup() {
        snackbar = TestSnackbarDispatcher()
        updateVault = TestUpdateVault()
        getVaultById = TestGetVaultById()
        encryptionContextProvider = TestEncryptionContextProvider()
        instance = EditVaultViewModel(
            snackbarDispatcher = snackbar,
            updateVault = updateVault,
            encryptionContextProvider = encryptionContextProvider,
            getVaultById = getVaultById,
            savedStateHandle = TestSavedStateHandle.create().apply {
                set(CommonNavArgId.ShareId.key, SHARE_ID)
            }
        )
    }

    @Test
    fun `emits initial state`() = runTest {
        instance.state.test {
            assertThat(awaitItem()).isEqualTo(BaseVaultUiState.Initial)
        }
    }

    @Test
    fun `onStart sets share contents`() = runTest {
        val vault = Vault(
            shareId = ShareId(SHARE_ID),
            name = "some name",
            color = ShareColor.Color4,
            icon = ShareIcon.Icon7,
        )

        getVaultById.emitValue(vault)
        instance.onStart()

        instance.state.test {
            val item = awaitItem()
            assertThat(item.isLoading).isEqualTo(IsLoadingState.NotLoading)
            assertThat(item.name).isEqualTo(vault.name)
            assertThat(item.color).isEqualTo(vault.color)
            assertThat(item.icon).isEqualTo(vault.icon)
        }
    }

    @Test
    fun `onEditClick sends the proper values`() = runTest {
        // Given
        val vault = Vault(
            shareId = ShareId(SHARE_ID),
            name = "some name",
            color = ShareColor.Color4,
            icon = ShareIcon.Icon7,
        )
        getVaultById.emitValue(vault)
        updateVault.setResult(Result.success(TestShare.create().copy(id = vault.shareId)))

        // When
        instance.onStart()
        instance.onEditClick()

        // Then
        instance.state.test {
            val item = awaitItem()
            assertThat(item.isLoading).isEqualTo(IsLoadingState.NotLoading)
        }

        val payload = updateVault.getSentValue()
        assertThat(payload).isNotNull()

        val notNullPayload = payload!!
        val decryptedName = encryptionContextProvider.withEncryptionContext {
            decrypt(notNullPayload.vault.name)
        }

        assertThat(decryptedName).isEqualTo(vault.name)
        assertThat(notNullPayload.shareId).isEqualTo(vault.shareId)
        assertThat(notNullPayload.vault.color).isEqualTo(vault.color)
        assertThat(notNullPayload.vault.icon).isEqualTo(vault.icon)
    }

    @Test
    fun `onEditClick sends snackbar message on error`() = runTest {
        // Given
        val vault = Vault(
            shareId = ShareId(SHARE_ID),
            name = "some name",
            color = ShareColor.Color4,
            icon = ShareIcon.Icon7,
        )
        getVaultById.emitValue(vault)
        updateVault.setResult(Result.failure(IllegalStateException("test")))

        // When
        instance.onStart()
        instance.onEditClick()

        // Then
        instance.state.test {
            val item = awaitItem()
            assertThat(item.isLoading).isEqualTo(IsLoadingState.NotLoading)
        }

        val message = snackbar.snackbarMessage.first().value()
        assertThat(message).isNotNull()
        assertThat(message!!).isEqualTo(VaultSnackbarMessage.EditVaultError)
    }

    @Test
    fun `onStart with error shows error message`() = runTest {
        getVaultById.sendException(IllegalStateException("Test"))

        instance.onStart()
        instance.state.test {
            val item = awaitItem()
            assertThat(item.isLoading).isEqualTo(IsLoadingState.NotLoading)
        }

        val message = snackbar.snackbarMessage.first().value()
        assertThat(message).isNotNull()
        assertThat(message!!).isEqualTo(VaultSnackbarMessage.CannotRetrieveVaultError)
    }

    companion object {
        private const val SHARE_ID = "test_share_id"
    }

}
