package com.firstratecurrency.app.di.module

import com.firstratecurrency.app.BuildConfig
import com.firstratecurrency.app.data.model.Rates
import com.firstratecurrency.app.data.network.RatesApi
import com.firstratecurrency.app.data.network.RatesApiDeserializer
import com.firstratecurrency.app.data.network.RatesApiService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit


@Module
open class RatesApiModule {

    private val BASE_URL = "https://revolut.duckdns.org"
    private val logging: HttpLoggingInterceptor = HttpLoggingInterceptor()

    private fun provideOkHttpClientBuilder(): OkHttpClient.Builder {
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.connectTimeout(10, TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(10, TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(10, TimeUnit.SECONDS)
        return okHttpClientBuilder
    }

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
        val httpClient = provideOkHttpClientBuilder()
        httpClient.addInterceptor(logging)

        val gsonConverterFactory = GsonConverterFactory.create(GsonBuilder().registerTypeAdapter(
            Rates::class.java,
            RatesApiDeserializer()
        ).create())

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient.build())
            .build()
            .create(RatesApi::class.java)
    }

    @Provides
    open fun provideApiService(): RatesApiService =
        RatesApiService()
}