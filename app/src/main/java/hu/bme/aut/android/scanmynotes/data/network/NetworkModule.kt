package hu.bme.aut.android.scanmynotes.data.network

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideFirebaseApi() = FirebaseApi()

    @Provides
    @Singleton
    fun provideVisionApi() = VisionApi()
}