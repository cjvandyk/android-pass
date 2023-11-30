package proton.android.pass.featurehome.demoapp

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.datetime.Clock
import me.proton.core.domain.entity.UserId
import proton.android.pass.account.fakes.TestAccountManager
import proton.android.pass.autofill.api.AutofillStatus
import proton.android.pass.autofill.api.AutofillSupportedStatus
import proton.android.pass.autofill.fakes.TestAutofillManager
import proton.android.pass.data.fakes.usecases.TestObserveItems
import proton.android.pass.data.fakes.usecases.TestObserveVaults
import proton.android.pass.data.fakes.usecases.TestObserveVaultsWithItemCount
import proton.android.pass.domain.Item
import proton.android.pass.domain.ItemId
import proton.android.pass.domain.ShareId
import proton.android.pass.domain.Vault
import proton.android.pass.domain.VaultWithItemCount
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidApp
class FeatureHomeApp : Application() {

    @Inject
    lateinit var accountManager: TestAccountManager

    @Inject
    lateinit var observeItems: TestObserveItems

    @Inject
    lateinit var observeVaults: TestObserveVaults

    @Inject
    lateinit var observeVaultsWithItemCount: TestObserveVaultsWithItemCount

    @Inject
    lateinit var autofillManager: TestAutofillManager

    override fun onCreate() {
        super.onCreate()
        setupAccount()
        setupItems()
        setupVaults()
        setupLogger()

        autofillManager.emitStatus(AutofillSupportedStatus.Supported(AutofillStatus.EnabledByOurService))

    }

    private fun setupVaults() {
        val vaults = listOf(
            Vault(
                shareId = ShareId(SHARE_ID),
                name = "Main vault"
            ),
            Vault(
                shareId = ShareId("OtherShareID"),
                name = "Other vault"
            )
        )
        val vaultsWithItemCount = vaults.map {
            VaultWithItemCount(vault = it, activeItemCount = 100, trashedItemCount = 0)
        }
        observeVaults.sendResult(Result.success(vaults))
        observeVaultsWithItemCount.sendResult(Result.success(vaultsWithItemCount))
    }

    private fun setupItems() {
        val items = createItems(50)
        observeItems.emitValue(items)
    }

    private fun setupLogger() {
        Timber.plant(Timber.DebugTree())
    }

    private fun setupAccount() {
        accountManager.sendPrimaryUserId(UserId("user1"))
    }

    private fun createItems(itemsPerType: Int): List<Item> {
        val items = mutableListOf<Item>()
        for (i in 0..itemsPerType) {
            val login = TestObserveItems.createLogin(
                shareId = ShareId(SHARE_ID),
                itemId = ItemId("item_login_$i"),
                title = "Login $i",
                username = "Username $i",
                password = "Password$i"
            )
            items.add(login)

            val creditCard = TestObserveItems.createCreditCard(
                shareId = ShareId(SHARE_ID),
                itemId = ItemId("item_cc_$i"),
                title = "Card $i",
                holder = "Holder $i",
                number = "${i}23423423",
            )
            items.add(creditCard)

            val note = TestObserveItems.createNote(
                shareId = ShareId(SHARE_ID),
                itemId = ItemId("item_note_$i"),
                title = "Note $i",
                note = "Note $i"
            )
            items.add(note)

            val alias = TestObserveItems.createAlias(
                shareId = ShareId(SHARE_ID),
                itemId = ItemId("item_alias_$i"),
                title = "Alias $i",
                alias = "alias$i@domain.test"
            )
            items.add(alias)
        }
        return items
    }

    companion object {
        private const val SHARE_ID = "DemoApp-ShareId"
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.System

}
