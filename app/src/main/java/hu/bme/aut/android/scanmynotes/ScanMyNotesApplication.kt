package hu.bme.aut.android.scanmynotes

import co.zsmb.rainbowcake.dagger.RainbowCakeApplication
import co.zsmb.rainbowcake.dagger.RainbowCakeComponent
import hu.bme.aut.android.scanmynotes.di.ApplicationModule
import hu.bme.aut.android.scanmynotes.di.DaggerAppComponent

class ScanMyNotesApplication : RainbowCakeApplication() {

    override lateinit var injector: RainbowCakeComponent

    override fun setupInjector() {
        injector = DaggerAppComponent.create()
    }
}