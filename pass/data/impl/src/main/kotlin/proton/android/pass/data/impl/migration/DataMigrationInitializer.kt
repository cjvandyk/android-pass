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

package proton.android.pass.data.impl.migration

import android.content.Context
import androidx.startup.Initializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class DataMigrationInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        EntryPointAccessors
            .fromApplication(
                context.applicationContext,
                DataMigrationInitializerEntryPoint::class.java
            )
            .scheduler()
            .schedule()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf()

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface DataMigrationInitializerEntryPoint {
        fun scheduler(): DataMigrationScheduler
    }
}
