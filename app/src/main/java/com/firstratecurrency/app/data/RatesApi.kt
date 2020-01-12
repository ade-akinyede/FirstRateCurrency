package com.firstratecurrency.app.data

import io.reactivex.Single
import retrofit2.http.GET

interface RatesApi {

    @GET("latest?base=EUR")
    fun getRates(): Single<Rates>
}