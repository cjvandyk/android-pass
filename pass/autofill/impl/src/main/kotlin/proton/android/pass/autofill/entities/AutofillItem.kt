package proton.android.pass.autofill.entities

sealed class AutofillItem {
    data class Login(
        val username: String,
        val password: String,
        val totp: String
    ) : AutofillItem()

    object Unknown : AutofillItem()
}
