package com.firstratecurrency.app.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firstratecurrency.app.FRCApp
import com.firstratecurrency.app.data.db.CurrenciesDao
import com.firstratecurrency.app.data.db.RatesDao
import com.firstratecurrency.app.data.model.Currency
import com.firstratecurrency.app.data.model.Rates
import com.firstratecurrency.app.data.network.RatesApiService
import com.firstratecurrency.app.di.component.DaggerRatesRepositoryComponent
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatesRepository @Inject constructor(
    private val ratesDao: RatesDao,
    private val currenciesDao: CurrenciesDao,
    private val ratesApiService: RatesApiService
) {

    var lastRefreshDate: Single<Long> = ratesDao.getLastRefreshDate()

    private val rates by lazy { MutableLiveData<ArrayList<Currency>>() }
    private val loading by lazy { MutableLiveData<Boolean>() }
    private val loadError by lazy { MutableLiveData<Boolean>() }

    private val disposable = CompositeDisposable()
//    @Inject
//    lateinit var ratesApiService: RatesApiService

    init {
        if (!FRCApp.Test.running) {
            refresh()
        }
    }

    fun getRatesLiveData() = rates
    fun getRatesLoadingState() = loading
    fun getLoadErrorState() = loadError

    private fun inject() {
        if (!FRCApp.Test.running) {
            DaggerRatesRepositoryComponent.create().inject(this)
        }
    }

    private fun refresh() {
        inject()

        fetchRatesFromApiService()
    }

//    fun getCurrencies(): LiveData<LinkedHashMap<String, Currency>> {
//        // Retrieve dB copy or fetch from remote
//        val currencies = currenciesDao.getCurrencies()
//
//    }

    private fun fetchRatesFromApiService() {
        disposable.add(
            ratesApiService.getRates()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<Rates>() {
                    override fun onSuccess(ratesList: Rates) {
                        onResponse(ratesList.currencies)
                    }

                    override fun onError(error: Throwable) {
                        Timber.e(error)
                        onError()
                    }

                    override fun onStart() {
                        onLoading()
                    }
                })
        )
    }

    private fun onResponse(result: LinkedHashMap<String, Currency>) {
        if (result.isNotEmpty()) {
            rates.value = rates.value?.let { currencyList ->
                // Update rates while maintaining the current ordering
                // by cycling through the current (rates) list and mapping
                // the result to the entry.
                currencyList.map { entry ->
                    result[entry.code]?.let { updatedCurrency ->
                        entry.rate = updatedCurrency.rate
                    }
                }

                currencyList
            } ?: run {
                result.values.toList() as ArrayList<Currency>
            }

            loadError.value = false
            loading.value = false
        } else {
            onError()
        }
    }

    private fun onLoading() {
        loading.value = true
    }

    private fun onError() {
        loading.value = false
        rates.value = null
        loadError.value = true
    }

    fun movePositionToTop(position: Int) {
        if (position > 0) {
            rates.value = rates.value?.run {
                // Remove and move to top position
                this.add(0, this.removeAt(position))
                this
            }
        }
    }

    fun onRateValueChanged(value: Double) {
        rates.value = rates.value?.let { list ->
            val firstResponder = list[0]
            val currentValue = firstResponder.getCurrencyValue()
            if (currentValue != value) {
                list.map { entry ->
                    entry.refValue = value
                    entry.refRate = firstResponder.rate
                }
            }

            list
        }
    }

    fun updateCurrencies(currencies: List<Currency>) {
        currenciesDao.updateCurrencies(currencies)
    }

//    fun updateLastRefreshDate(date: String) {
//        ratesDao.updateLastRefreshDate(date)
//    }
}