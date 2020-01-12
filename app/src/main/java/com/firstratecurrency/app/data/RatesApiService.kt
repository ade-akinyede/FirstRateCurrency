package com.firstratecurrency.app.data

import com.firstratecurrency.app.BuildConfig
import com.google.gson.GsonBuilder
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class RatesApiService {

    private val BASE_URL = "https://revolut.duckdns.org"

    private val logging: HttpLoggingInterceptor = HttpLoggingInterceptor()
    private var api: RatesApi

    init {
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

        api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().registerTypeAdapter(Rates::class.java, RatesApiDeserializer()).create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient.build())
            .build()
            .create(RatesApi::class.java)
    }

    fun getRates(): Single<Rates> {
        Timber.d("Fetching rates...")
        return api.getRates()
    }
}