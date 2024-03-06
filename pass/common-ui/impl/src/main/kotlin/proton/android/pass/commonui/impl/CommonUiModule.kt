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

package proton.android.pass.commonui.impl

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import proton.android.pass.commonui.api.PassAppLifecycleProvider
import proton.android.pass.commonui.api.SavedStateHandleProvider

@Module
@InstallIn(ViewModelComponent::class)
abstract class CommonUiModule {

    @Binds
    abstract fun bindSavedStateHandleProvider(impl: SavedStateHandleProviderImpl): SavedStateHandleProvider

}


@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonCommonUiModule {

    @Binds
    abstract fun bindAppLifecycleProvider(impl: PassAppLifecycleObserverImpl): PassAppLifecycleProvider
}
