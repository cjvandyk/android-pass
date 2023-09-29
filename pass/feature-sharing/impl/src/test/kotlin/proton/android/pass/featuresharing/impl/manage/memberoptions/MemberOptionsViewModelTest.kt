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

package proton.android.pass.featuresharing.impl.manage.memberoptions

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import proton.android.pass.commonui.fakes.TestSavedStateHandleProvider
import proton.android.pass.data.fakes.usecases.TestObserveVaults
import proton.android.pass.data.fakes.usecases.TestRemoveMemberFromVault
import proton.android.pass.data.fakes.usecases.TestSetVaultMemberPermission
import proton.android.pass.featuresharing.impl.SharingSnackbarMessage
import proton.android.pass.featuresharing.impl.manage.bottomsheet.MemberEmailArg
import proton.android.pass.featuresharing.impl.manage.bottomsheet.MemberShareIdArg
import proton.android.pass.featuresharing.impl.manage.bottomsheet.ShareRoleArg
import proton.android.pass.featuresharing.impl.manage.bottomsheet.memberoptions.MemberOptionsEvent
import proton.android.pass.featuresharing.impl.manage.bottomsheet.memberoptions.MemberOptionsUiState
import proton.android.pass.featuresharing.impl.manage.bottomsheet.memberoptions.MemberOptionsViewModel
import proton.android.pass.featuresharing.impl.manage.bottomsheet.memberoptions.MemberPermissionLevel
import proton.android.pass.featuresharing.impl.manage.bottomsheet.memberoptions.TransferOwnershipState
import proton.android.pass.navigation.api.CommonNavArgId
import proton.android.pass.navigation.api.NavParamEncoder
import proton.android.pass.notifications.fakes.TestSnackbarDispatcher
import proton.android.pass.preferences.FeatureFlag
import proton.android.pass.preferences.TestFeatureFlagsPreferenceRepository
import proton.android.pass.test.MainDispatcherRule
import proton.pass.domain.ShareId
import proton.pass.domain.ShareRole
import proton.pass.domain.Vault

class MemberOptionsViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var instance: MemberOptionsViewModel

    private lateinit var snackbarDispatcher: TestSnackbarDispatcher
    private lateinit var removeMemberFromVault: TestRemoveMemberFromVault
    private lateinit var setVaultMemberPermission: TestSetVaultMemberPermission
    private lateinit var observeVaults: TestObserveVaults
    private lateinit var ffRepo: TestFeatureFlagsPreferenceRepository


    @Before
    fun setup() {
        snackbarDispatcher = TestSnackbarDispatcher()
        removeMemberFromVault = TestRemoveMemberFromVault()
        setVaultMemberPermission = TestSetVaultMemberPermission()
        observeVaults = TestObserveVaults()
        ffRepo = TestFeatureFlagsPreferenceRepository()
    }

    @Test
    fun `emit initial state`() = runTest {
        setupTest()
        emitVault(false)
        instance.state.test {
            assertThat(awaitItem()).isEqualTo(MemberOptionsUiState.Initial)
        }
    }

    @Test
    fun `shows transfer ownership if vault is owned and member role is admin`() = runTest {
        setupTest(memberRole = ShareRole.Admin)
        emitVault(owned = true)
        instance.state.test {
            val item = awaitItem()
            assertThat(item.transferOwnership).isEqualTo(TransferOwnershipState.Enabled)
        }
    }

    @Test
    fun `does not show transfer ownership if vault is owned but member role is not admin`() =
        runTest {
            setupTest(memberRole = ShareRole.Write)
            emitVault(owned = true)
            instance.state.test {
                val item = awaitItem()
                assertThat(item.transferOwnership).isEqualTo(TransferOwnershipState.Hide)
            }
        }

    @Test
    fun `does not show transfer ownership if vault is not owned`() = runTest {
        setupTest()
        emitVault(owned = false)
        instance.state.test {
            val item = awaitItem()
            assertThat(item.transferOwnership).isEqualTo(TransferOwnershipState.Hide)
        }
    }

    @Test
    fun `can remove member`() = runTest {
        setupTest()
        emitVault()

        instance.removeFromVault()

        instance.state.test {
            assertThat(awaitItem().event).isEqualTo(MemberOptionsEvent.Close(true))
        }

        val memory = removeMemberFromVault.getMemory()
        val expected = TestRemoveMemberFromVault.Payload(
            shareId = ShareId(USER_SHARE_ID),
            memberShareId = ShareId(MEMBER_SHARE_ID)
        )
        assertThat(memory).isEqualTo(listOf(expected))

        val message = snackbarDispatcher.snackbarMessage.first().value()
        assertThat(message).isInstanceOf(SharingSnackbarMessage.RemoveMemberSuccess::class.java)
    }

    @Test
    fun `can handle error in remove member`() = runTest {
        setupTest()
        emitVault()
        removeMemberFromVault.setResult(Result.failure(IllegalStateException("test")))

        instance.removeFromVault()

        val message = snackbarDispatcher.snackbarMessage.first().value()
        assertThat(message).isInstanceOf(SharingSnackbarMessage.RemoveMemberError::class.java)
    }

    @Test
    fun `forwards change permission`() = runTest {
        setupTest(memberRole = ShareRole.Read)
        emitVault()

        instance.setPermissions(MemberPermissionLevel.Admin)

        instance.state.test {
            assertThat(awaitItem().event).isEqualTo(MemberOptionsEvent.Close(true))
        }

        val memory = setVaultMemberPermission.getMemory()
        val expected = TestSetVaultMemberPermission.Payload(
            shareId = ShareId(USER_SHARE_ID),
            memberShareId = ShareId(MEMBER_SHARE_ID),
            role = ShareRole.Admin
        )
        assertThat(memory).isEqualTo(listOf(expected))

        val message = snackbarDispatcher.snackbarMessage.first().value()
        assertThat(message).isInstanceOf(SharingSnackbarMessage.ChangeMemberPermissionSuccess::class.java)
    }

    @Test
    fun `can handle error in change permission`() = runTest {
        setupTest(memberRole = ShareRole.Read)
        emitVault()
        setVaultMemberPermission.setResult(Result.failure(IllegalStateException("test")))

        instance.setPermissions(MemberPermissionLevel.Admin)

        val memory = setVaultMemberPermission.getMemory()
        val expected = TestSetVaultMemberPermission.Payload(
            shareId = ShareId(USER_SHARE_ID),
            memberShareId = ShareId(MEMBER_SHARE_ID),
            role = ShareRole.Admin
        )
        assertThat(memory).isEqualTo(listOf(expected))

        val message = snackbarDispatcher.snackbarMessage.first().value()
        assertThat(message).isInstanceOf(SharingSnackbarMessage.ChangeMemberPermissionError::class.java)
    }

    // RemovePrimaryVault tests
    @Test
    fun `if RemovePrimaryVault enabled allow to transfer ownership if not the last owned vault`() = runTest {
        ffRepo.set(FeatureFlag.REMOVE_PRIMARY_VAULT, true)
        val ownedVault = vaultWith(USER_SHARE_ID, owned = true)
        val vaults = listOf(
            vaultWith(shareId = "shareId-also-owned", owned = true),
            ownedVault,
            vaultWith(shareId = "shareId-not-owned", owned = false)
        )
        observeVaults.sendResult(Result.success(vaults))
        setupTest(memberRole = ShareRole.Admin)

        instance.state.test {
            val item = awaitItem()
            assertThat(item.transferOwnership).isEqualTo(TransferOwnershipState.Enabled)
        }
    }

    @Test
    fun `if RemovePrimaryVault enabled do not allow to transfer ownership if is the last owned vault`() = runTest {
        ffRepo.set(FeatureFlag.REMOVE_PRIMARY_VAULT, true)
        val ownedVault = vaultWith(USER_SHARE_ID, owned = true)
        val vaults = listOf(
            vaultWith(shareId = "shareId-not-owned", owned = false),
            ownedVault,
            vaultWith(shareId = "shareId-not-owned", owned = false)
        )
        observeVaults.sendResult(Result.success(vaults))
        setupTest(memberRole = ShareRole.Admin)

        instance.state.test {
            val item = awaitItem()
            assertThat(item.transferOwnership).isEqualTo(TransferOwnershipState.Disabled)
        }
    }

    @Test
    fun `if RemovePrimaryVault enabled hide transfer ownership if member is not admin`() = runTest {
        ffRepo.set(FeatureFlag.REMOVE_PRIMARY_VAULT, true)
        val ownedVault = vaultWith(USER_SHARE_ID, owned = true)
        val vaults = listOf(
            vaultWith(shareId = "shareId-also-owned", owned = true),
            ownedVault,
            vaultWith(shareId = "shareId-not-owned", owned = false)
        )
        observeVaults.sendResult(Result.success(vaults))
        setupTest(memberRole = ShareRole.Write)

        instance.state.test {
            val item = awaitItem()
            assertThat(item.transferOwnership).isEqualTo(TransferOwnershipState.Hide)
        }
    }

    @Test
    fun `if RemovePrimaryVault enabled hide transfer ownership if vault is not owned`() = runTest {
        ffRepo.set(FeatureFlag.REMOVE_PRIMARY_VAULT, true)
        val ownedVault = vaultWith(USER_SHARE_ID, owned = false)
        val vaults = listOf(
            vaultWith(shareId = "shareId-also-owned", owned = true),
            ownedVault,
            vaultWith(shareId = "shareId-not-owned", owned = false)
        )
        observeVaults.sendResult(Result.success(vaults))
        setupTest(memberRole = ShareRole.Admin)

        instance.state.test {
            val item = awaitItem()
            assertThat(item.transferOwnership).isEqualTo(TransferOwnershipState.Hide)
        }
    }


    private fun setupTest(memberRole: ShareRole = ShareRole.Read) {
        val savedStateHandle = TestSavedStateHandleProvider()
        savedStateHandle.get().apply {
            set(CommonNavArgId.ShareId.key, USER_SHARE_ID)
            set(MemberShareIdArg.key, MEMBER_SHARE_ID)
            set(ShareRoleArg.key, memberRole.value)
            set(MemberEmailArg.key, MEMBER_EMAIL)
        }

        instance = MemberOptionsViewModel(
            snackbarDispatcher = snackbarDispatcher,
            removeMemberFromVault = removeMemberFromVault,
            setVaultMemberPermission = setVaultMemberPermission,
            savedState = savedStateHandle,
            observeVaults = observeVaults,
            ffRepo = ffRepo
        )
    }

    private fun emitVault(owned: Boolean = true) {
        val vault = Vault(
            name = "test",
            shareId = ShareId(USER_SHARE_ID),
            isPrimary = false,
            isOwned = owned
        )
        observeVaults.sendResult(Result.success(listOf(vault)))
    }

    private fun vaultWith(shareId: String, owned: Boolean) = Vault(
        name = "Some vault $shareId",
        shareId = ShareId(shareId),
        isPrimary = false,
        isOwned = owned
    )

    companion object {
        private const val USER_SHARE_ID = "MemberOptionsViewModelTest-USER_SHARE_ID"
        private const val MEMBER_SHARE_ID = "MemberOptionsViewModelTest-MEMBER_SHARE_ID"
        private val MEMBER_EMAIL = NavParamEncoder.encode("MemberOptionsViewModelTest-MEMBER_EMAIL")
    }
}
