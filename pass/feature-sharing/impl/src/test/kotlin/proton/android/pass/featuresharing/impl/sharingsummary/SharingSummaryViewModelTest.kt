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

package proton.android.pass.featuresharing.impl.sharingsummary

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import proton.android.pass.commonui.fakes.TestSavedStateHandleProvider
import proton.android.pass.data.fakes.repositories.TestBulkInviteRepository
import proton.android.pass.data.fakes.usecases.TestGetVaultWithItemCountById
import proton.android.pass.data.fakes.usecases.TestInviteToVault
import proton.android.pass.domain.ShareColor
import proton.android.pass.domain.ShareIcon
import proton.android.pass.domain.ShareId
import proton.android.pass.domain.Vault
import proton.android.pass.domain.VaultWithItemCount
import proton.android.pass.featuresharing.impl.SharingSnackbarMessage.InviteSentError
import proton.android.pass.featuresharing.impl.SharingSnackbarMessage.InviteSentSuccess
import proton.android.pass.featuresharing.impl.SharingSnackbarMessage.VaultNotFound
import proton.android.pass.featuresharing.impl.common.AddressPermissionUiState
import proton.android.pass.featuresharing.impl.sharingpermissions.SharingType
import proton.android.pass.navigation.api.CommonNavArgId
import proton.android.pass.notifications.fakes.TestSnackbarDispatcher
import proton.android.pass.test.MainDispatcherRule

class SharingSummaryViewModelTest {

    private lateinit var viewModel: SharingSummaryViewModel
    private lateinit var getVaultWithItemCountById: TestGetVaultWithItemCountById
    private lateinit var inviteToVault: TestInviteToVault
    private lateinit var snackbarDispatcher: TestSnackbarDispatcher
    private lateinit var savedStateHandleProvider: TestSavedStateHandleProvider
    private lateinit var bulkInviteRepository: TestBulkInviteRepository

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        getVaultWithItemCountById = TestGetVaultWithItemCountById()
        inviteToVault = TestInviteToVault()
        snackbarDispatcher = TestSnackbarDispatcher()
        bulkInviteRepository = TestBulkInviteRepository().apply {
            runBlocking { storeAddresses(listOf(TEST_EMAIL)) }
        }

        savedStateHandleProvider = TestSavedStateHandleProvider().apply {
            get()[CommonNavArgId.ShareId.key] = TEST_SHARE_ID
        }
        viewModel = SharingSummaryViewModel(
            getVaultWithItemCountById = getVaultWithItemCountById,
            inviteToVault = inviteToVault,
            snackbarDispatcher = snackbarDispatcher,
            savedStateHandleProvider = savedStateHandleProvider,
            bulkInviteRepository = bulkInviteRepository
        )
    }

    @Test
    fun `test view model initialization`() = runTest {
        viewModel.state.test {
            val initialState = awaitItem()

            assertThat(initialState.vaultWithItemCount).isNull()
            assertThat(initialState.isLoading).isTrue()

            val expected = AddressPermissionUiState(TEST_EMAIL, SharingType.Read)
            assertThat(initialState.addresses).isEqualTo(listOf(expected))
        }
    }

    @Test
    fun `test view model state with successful vault loading`() = runTest {
        val vaultData = createVaultWithItemCount()
        getVaultWithItemCountById.emitValue(vaultData)

        viewModel.state.test {
            val initialState = awaitItem()

            val addresses = listOf(AddressPermissionUiState(TEST_EMAIL, SharingType.Read))
            val expectedState = SharingSummaryUIState(
                addresses = addresses.toPersistentList(),
                vaultWithItemCount = vaultData,
                isLoading = false,
            )
            assertThat(initialState).isEqualTo(expectedState)
        }
    }


    @Test
    fun `test view model state with error in vault loading`() = runTest {
        getVaultWithItemCountById.sendException(RuntimeException("test exception"))

        viewModel.state.test {
            val initialState = awaitItem()
            val expectedState = SharingSummaryUIState(
                addresses = persistentListOf(
                    AddressPermissionUiState(
                        TEST_EMAIL,
                        SharingType.Read
                    )
                ),
                vaultWithItemCount = null,
                isLoading = false,
                event = SharingSummaryEvent.BackToHome
            )
            assertThat(initialState).isEqualTo(expectedState)
        }
        val message = snackbarDispatcher.snackbarMessage.first().value()
        assertThat(message).isNotNull()
        assertThat(message).isEqualTo(VaultNotFound)
    }

    @Test
    fun `test view model onSubmit success`() = runTest {
        inviteToVault.setResult(Result.success(Unit))

        viewModel.onSubmit()

        val message = snackbarDispatcher.snackbarMessage.first().value()
        assertThat(message).isNotNull()
        assertThat(message).isEqualTo(InviteSentSuccess)
    }


    @Test
    fun `test view model onSubmit failure`() = runTest {
        inviteToVault.setResult(Result.failure(RuntimeException("test exception")))

        viewModel.onSubmit()

        val message = snackbarDispatcher.snackbarMessage.first().value()
        assertThat(message).isNotNull()
        assertThat(message).isEqualTo(InviteSentError)
    }

    @Test
    fun `if addresses is empty send back to home`() = runTest {
        bulkInviteRepository.clear()
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.event).isEqualTo(SharingSummaryEvent.BackToHome)
        }
    }

    private fun createVaultWithItemCount() = VaultWithItemCount(
        vault = Vault(
            shareId = ShareId(id = TEST_SHARE_ID),
            name = "Evangeline Potter",
            color = ShareColor.Color1,
            icon = ShareIcon.Icon1,
        ),
        activeItemCount = 5521, trashedItemCount = 6902
    )

    companion object {
        private const val TEST_SHARE_ID = "SharingSummaryViewModelTest-ShareID"
        private const val TEST_EMAIL = "myemail@proton.me"
    }
}
