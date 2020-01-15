package com.firstratecurrency.app

import com.firstratecurrency.app.data.RatesApiService
import com.firstratecurrency.app.di.module.RatesApiModule

class RatesApiModuleTest(private val mockService: RatesApiService): RatesApiModule() {

    override fun provideApiService(): RatesApiService {
        return mockService
    }
}