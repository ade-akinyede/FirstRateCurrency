package com.firstratecurrency.app.di.component

import com.firstratecurrency.app.data.RatesRepository
import com.firstratecurrency.app.di.module.AppDatabaseModule
import com.firstratecurrency.app.di.module.RatesApiModule
import dagger.Component

@Component(modules = [RatesApiModule::class, AppDatabaseModule::class])
interface RatesRepositoryComponent {

    fun inject(ratesRepository: RatesRepository)
}