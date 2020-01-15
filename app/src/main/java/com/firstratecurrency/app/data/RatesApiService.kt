package com.firstratecurrency.app.data

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