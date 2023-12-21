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

package proton.android.pass.ui.shortcuts

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import proton.android.pass.log.api.PassLogger
import proton.android.pass.log.api.ShareLogs
import javax.inject.Inject

@AndroidEntryPoint
class ShortcutActivity : FragmentActivity() {

    @Inject
    lateinit var shareLogs: ShareLogs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val shortcutAction = intent.extras?.getString("shortcutaction")
        PassLogger.i(TAG, "Started from shortcut $shortcutAction")
        when (shortcutAction) {
            "sharelogs" -> {
                onShareLogs()
            }

            else -> finish()
        }
    }

    private fun onShareLogs() {
        val intent = runBlocking { withContext(Dispatchers.IO) { shareLogs.createIntent() } }
        if (intent != null) {
            startActivity(intent)
        }
        finish()

    }

    companion object {
        private const val TAG = "ShortcutActivity"
    }
}
