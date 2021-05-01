package hu.bme.aut.android.scanmynotes.di

import co.zsmb.rainbowcake.dagger.RainbowCakeComponent
import co.zsmb.rainbowcake.dagger.RainbowCakeModule
import dagger.Component
import hu.bme.aut.android.scanmynotes.data.network.NetworkModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        RainbowCakeModule::class,
        ViewModelModule::class,
        NetworkModule::class
    ]
)
interface AppComponent : RainbowCakeComponent