package proton.android.pass.autofill.sample.simplefragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import proton.android.pass.autofill.sample.R
import proton.android.pass.autofill.sample.databinding.ActivitySimpleFragmentLoginBinding

class SimpleFragmentLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySimpleFragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySimpleFragmentLoginBinding.inflate(layoutInflater)

        if (supportFragmentManager.fragments.isEmpty()) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SimpleLoginFragment())
                .commit()
        }

        setContentView(binding.root)
    }

}
