package com.firstratecurrency.app.di.module

import com.firstratecurrency.app.BuildConfig
import com.firstratecurrency.app.data.Rates
import com.firstratecurrency.app.data.RatesApi
import com.firstratecurrency.app.data.RatesApiDeserializer
import com.firstratecurrency.app.data.RatesApiService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

@Module
class ApiModule {

    private val BASE_URL = "https://revolut.duckdns.org"
    private val logging: HttpLoggingInterceptor = HttpLoggingInterceptor()

    @Provides
    fun provideRatesApi(): RatesApi {
        Timber.d("Initialising RatesApiService...")
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else{
            // redact headers that may contain sensitive information
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
            logging.redactHeader("Authorization")
            logging.redactHeader("Cookie")
        }
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().registerTypeAdapter(Rates::class.java, RatesApiDeserializer()).create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient.build())
            .build()
            .create(RatesApi::class.java)
    }

    @Provides
    fun provideApiService(): RatesApiService = RatesApiService()
}