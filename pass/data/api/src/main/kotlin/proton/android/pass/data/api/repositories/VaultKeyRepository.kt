package proton.android.pass.data.api.repositories

import proton.android.pass.common.api.LoadingResult
import proton.pass.domain.ShareId
import proton.pass.domain.key.ItemKey
import proton.pass.domain.key.SigningKey
import proton.pass.domain.key.VaultKey
import me.proton.core.user.domain.entity.UserAddress

data class VaultItemKeyList(
    val vaultKeyList: List<VaultKey>,
    val itemKeyList: List<ItemKey>
)

interface VaultKeyRepository {
    suspend fun getVaultKeys(
        userAddress: UserAddress,
        shareId: ShareId,
        signingKey: SigningKey,
        forceRefresh: Boolean = false,
        shouldStoreLocally: Boolean = true
    ): LoadingResult<List<VaultKey>>

    suspend fun getVaultKeyById(
        userAddress: UserAddress,
        shareId: ShareId,
        signingKey: SigningKey,
        keyId: String
    ): LoadingResult<VaultKey>

    suspend fun getItemKeyById(
        userAddress: UserAddress,
        shareId: ShareId,
        signingKey: SigningKey,
        keyId: String
    ): LoadingResult<ItemKey>

    suspend fun getLatestVaultKey(
        userAddress: UserAddress,
        shareId: ShareId,
        signingKey: SigningKey,
        forceRefresh: Boolean = false
    ): LoadingResult<VaultKey>

    suspend fun getLatestVaultItemKey(
        userAddress: UserAddress,
        shareId: ShareId,
        signingKey: SigningKey,
        forceRefresh: Boolean = false
    ): LoadingResult<Pair<VaultKey, ItemKey>>
}
