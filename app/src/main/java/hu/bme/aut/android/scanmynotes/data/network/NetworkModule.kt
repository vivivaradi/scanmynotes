package hu.bme.aut.android.scanmynotes.data.network

import okhttp3.OkHttpClient
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: okhttp3.OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://google.com")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFirebaseApi(retrofit: Retrofit): FirebaseApi {
        return retrofit.create(FirebaseApi::class.java)
    }

    @Provides
    @Singleton
    fun provideVisionApi(retrofit: Retrofit): VisionApi {
        return retrofit.create(VisionApi::class.java)
    }
}
