package com.firstratecurrency.app.di.module

import android.app.Application
import com.firstratecurrency.app.data.db.CurrenciesDao
import com.firstratecurrency.app.data.db.AppDatabase
import com.firstratecurrency.app.data.db.RatesDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppDatabaseModule(val app: Application) {

    private val appDatabase: AppDatabase = AppDatabase.getDatabase(app)

    @Singleton
    @Provides
    fun provideDatabase(): AppDatabase = appDatabase

    @Singleton
    @Provides
    fun provideRatesDao(): RatesDao =
        AppDatabase.getDatabase(app).ratesDao()

    @Singleton
    @Provides
    fun provideCurrenciesDao(): CurrenciesDao =
        AppDatabase.getDatabase(app).currenciesDao()
}