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

package proton.android.pass.data.impl

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import proton.android.pass.data.impl.crypto.EncryptInviteKeys
import proton.android.pass.data.impl.crypto.EncryptInviteKeysImpl
import proton.android.pass.data.impl.crypto.ReencryptInviteContents
import proton.android.pass.data.impl.crypto.ReencryptInviteContentsImpl
import proton.android.pass.data.impl.crypto.ReencryptShareContents
import proton.android.pass.data.impl.crypto.ReencryptShareContentsImpl
import proton.android.pass.data.impl.crypto.ReencryptShareKey
import proton.android.pass.data.impl.crypto.ReencryptShareKeyImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class DataCryptoModule {

    @Binds
    abstract fun bindReencryptShareContents(
        impl: ReencryptShareContentsImpl
    ): ReencryptShareContents

    @Binds
    abstract fun bindReencryptShareKey(
        impl: ReencryptShareKeyImpl
    ): ReencryptShareKey

    @Binds
    abstract fun bindReencryptInviteContents(
        impl: ReencryptInviteContentsImpl
    ): ReencryptInviteContents

    @Binds
    abstract fun bindEncryptInviteKeys(
        impl: EncryptInviteKeysImpl
    ): EncryptInviteKeys
}
