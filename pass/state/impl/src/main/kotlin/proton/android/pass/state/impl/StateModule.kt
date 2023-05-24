package proton.android.pass.state.impl

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import proton.android.pass.state.api.SavedStateInterface

@Module
@InstallIn(ViewModelComponent::class)
abstract class StateModule {

    @Binds
    abstract fun bindSavedStateInterface(impl: SavedStateInterfaceImpl): SavedStateInterface

}
