package com.firstratecurrency.app.di.module

import com.firstratecurrency.app.data.RatesRepository
import com.firstratecurrency.app.data.db.CurrenciesDao
import com.firstratecurrency.app.data.db.RatesDao
import com.firstratecurrency.app.data.network.RatesApiService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RatesRepositoryModule {

    @Singleton
    @Provides
    fun providesRatesRepository(
        ratesDao: RatesDao,
        currenciesDao: CurrenciesDao,
        ratesApiService: RatesApiService
    ): RatesRepository {
        return RatesRepository(ratesDao, currenciesDao, ratesApiService)
    }
}