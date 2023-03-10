package proton.android.pass.autofill.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import proton.android.pass.autofill.sample.databinding.ActivityLoginResultBinding

class LoginResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginResultBinding.inflate(layoutInflater)

        binding.goBackButton.setOnClickListener {
            finish()
        }

        setContentView(binding.root)
    }
}
