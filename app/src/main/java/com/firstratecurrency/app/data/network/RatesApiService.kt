package com.firstratecurrency.app.data.network

import com.firstratecurrency.app.data.model.Rates
import com.firstratecurrency.app.di.component.DaggerRatesApiComponent
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class RatesApiService {

    @Inject
    lateinit var api: RatesApi

    init {
        DaggerRatesApiComponent.create().inject(this)
    }

    fun getRates(): Single<Rates> {
        Timber.d("Fetching rates...")
        return api.getRates()
    }
}