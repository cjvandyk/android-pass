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

package proton.android.pass.telemetry.impl.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.proton.core.network.domain.ApiException
import me.proton.core.network.domain.isRetryable
import proton.android.pass.data.api.repositories.TelemetryRepository
import proton.android.pass.log.api.PassLogger
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@HiltWorker
open class TelemetrySenderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val telemetryRepository: TelemetryRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        PassLogger.i(TAG, "Starting telemetry worker")
        return kotlin.runCatching {
            telemetryRepository.sendEvents()
        }.fold(
            onSuccess = { Result.success() },
            onFailure = {
                PassLogger.w(TAG, it, "Error sending telemetry")
                if (it is ApiException && it.isRetryable()) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        )
    }

    companion object {
        private const val TAG = "TelemetrySenderWorker"

        const val WORKER_UNIQUE_NAME = "telemetry_worker"

        fun getRequestFor(repeatInterval: Duration, initialDelay: Duration): PeriodicWorkRequest {
            val initialDelaySeconds = initialDelay.inWholeSeconds
            val backoffDelaySeconds = 30.seconds
            return PeriodicWorkRequestBuilder<TelemetrySenderWorker>(repeatInterval.inWholeSeconds, TimeUnit.SECONDS)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    backoffDelaySeconds.inWholeSeconds,
                    TimeUnit.SECONDS
                )
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .setInitialDelay(initialDelaySeconds, TimeUnit.SECONDS)
                .build()
        }
    }
}
