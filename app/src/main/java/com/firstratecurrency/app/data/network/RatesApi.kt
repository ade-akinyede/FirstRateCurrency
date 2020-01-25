package com.firstratecurrency.app.data.network

import com.firstratecurrency.app.data.model.Rates
import io.reactivex.Single
import retrofit2.http.GET

interface RatesApi {

    @GET("latest?base=EUR")
    fun getRates(): Single<Rates>
}