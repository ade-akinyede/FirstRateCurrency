package com.firstratecurrency.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.firstratecurrency.app.FRCApp
import com.firstratecurrency.app.data.RatesRepository
import com.firstratecurrency.app.di.component.DaggerRatesListViewModelComponent
import com.firstratecurrency.app.di.module.AppDatabaseModule
import com.firstratecurrency.app.di.module.RatesApiModule
import com.firstratecurrency.app.di.module.RatesRepositoryModule
import javax.inject.Inject

class RatesListViewModel(private val app: Application): AndroidViewModel(app) {

    @Inject
    lateinit var ratesRepository: RatesRepository

    init {
        if (!FRCApp.Test.running) {
            inject()
        }
    }

    private fun inject() {
        if (!FRCApp.Test.running) {
            DaggerRatesListViewModelComponent.builder()
                .appDatabaseModule(AppDatabaseModule(app))
                .ratesRepositoryModule(RatesRepositoryModule())
                .ratesApiModule(RatesApiModule())
                .build()
                .inject(this)
        }
    }

    fun getRatesLiveData() = ratesRepository.getRatesLiveData()
    fun getRatesLoadingState() = ratesRepository.getRatesLoadingState()
    fun getLoadErrorState() = ratesRepository.getLoadErrorState()

    fun movePositionToTop(position: Int) {
        ratesRepository.movePositionToTop(position)
    }

    fun onRateValueChanged(value: Double) {
        ratesRepository.onCurrencyValueChanged(value)
    }

    fun ensureCurrencyListPersistence() {
        ratesRepository.ensureCurrencyListPersistence()
    }
}