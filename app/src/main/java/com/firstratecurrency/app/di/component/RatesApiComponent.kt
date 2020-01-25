package com.firstratecurrency.app.di.component

import com.firstratecurrency.app.data.network.RatesApiService
import com.firstratecurrency.app.di.module.RatesApiModule
import dagger.Component

@Component(modules = [RatesApiModule::class])
interface RatesApiComponent {

    fun inject(service: RatesApiService)
}