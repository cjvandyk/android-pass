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

package proton.android.pass.featuremigrate.impl.selectvault

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import proton.android.pass.commonui.fakes.TestSavedStateHandleProvider
import proton.android.pass.data.fakes.repositories.TestBulkMoveToVaultRepository
import proton.android.pass.data.fakes.usecases.TestObserveVaultsWithItemCount
import proton.android.pass.domain.ShareId
import proton.android.pass.domain.Vault
import proton.android.pass.domain.VaultWithItemCount
import proton.android.pass.featuremigrate.impl.MigrateModeArg
import proton.android.pass.featuremigrate.impl.MigrateModeValue
import proton.android.pass.navigation.api.CommonNavArgId
import proton.android.pass.notifications.fakes.TestSnackbarDispatcher
import proton.android.pass.test.MainDispatcherRule

class MigrateSelectVaultForMigrateAllVaultItemsViewModelTest {

    @get:Rule
    val dispatcher = MainDispatcherRule()

    private lateinit var instance: MigrateSelectVaultViewModel
    private lateinit var observeVaults: TestObserveVaultsWithItemCount
    private lateinit var snackbarDispatcher: TestSnackbarDispatcher
    private lateinit var bulkMoveToVaultRepository: TestBulkMoveToVaultRepository

    @Before
    fun setup() {
        observeVaults = TestObserveVaultsWithItemCount()
        snackbarDispatcher = TestSnackbarDispatcher()
        bulkMoveToVaultRepository = TestBulkMoveToVaultRepository()
        instance = MigrateSelectVaultViewModel(
            observeVaults = observeVaults,
            snackbarDispatcher = snackbarDispatcher,
            bulkMoveToVaultRepository = bulkMoveToVaultRepository,
            savedStateHandle = TestSavedStateHandleProvider().apply {
                get()[CommonNavArgId.ShareId.key] = SHARE_ID.id
                get()[MigrateModeArg.key] = MODE.name
            }
        )
    }

    @Test
    fun `emits success when vault selected`() = runTest {
        val (currentVault, otherVault) = initialVaults()
        observeVaults.sendResult(Result.success(listOf(currentVault, otherVault)))

        instance.onVaultSelected(otherVault.vault.shareId)
        instance.state.test {
            val item = awaitItem()
            require(item is MigrateSelectVaultUiState.Success)
            val event = item.event.value()
            assertThat(event).isNotNull()
            assertThat(event!!).isInstanceOf(SelectVaultEvent.VaultSelectedForMigrateAll::class.java)

            val castedEvent = event as SelectVaultEvent.VaultSelectedForMigrateAll
            assertThat(castedEvent.sourceShareId).isEqualTo(SHARE_ID)
            assertThat(castedEvent.destinationShareId).isEqualTo(otherVault.vault.shareId)
        }
    }

    private fun initialVaults(): Pair<VaultWithItemCount, VaultWithItemCount> =
        Pair(
            VaultWithItemCount(
                vault = Vault(
                    shareId = SHARE_ID,
                    name = "vault1",
                ),
                activeItemCount = 1,
                trashedItemCount = 0
            ),
            VaultWithItemCount(
                vault = Vault(
                    shareId = ShareId("OTHER_SHARE_ID"),
                    name = "vault2",
                ),
                activeItemCount = 1,
                trashedItemCount = 0
            )
        )

    companion object {
        private val SHARE_ID = ShareId("123")

        private val MODE = MigrateModeValue.AllVaultItems
    }
}
