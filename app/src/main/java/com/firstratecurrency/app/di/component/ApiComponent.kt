package com.firstratecurrency.app.di.component

import com.firstratecurrency.app.data.RatesApiService
import com.firstratecurrency.app.di.module.ApiModule
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {

    fun inject(service: RatesApiService)
}