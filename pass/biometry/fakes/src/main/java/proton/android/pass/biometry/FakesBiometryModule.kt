package proton.android.pass.biometry

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FakesBiometryModule {

    @Binds
    abstract fun bindBiometryManager(impl: TestBiometryManager): BiometryManager

    @Binds
    abstract fun bindAuthTimeHolder(impl: TestBiometryAuthTimeHolder): BiometryAuthTimeHolder
}
