package com.firstratecurrency.app.data

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
import io.reactivex.observers.DisposableCompletableObserver
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
            inject()
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
        onLoading()
        getRates()
    }

    private fun getRates() {
        // Return the dB copy and do a refresh if
        disposable.add(currenciesDao.getCurrencies()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<List<Currency>>() {
                override fun onSuccess(list: List<Currency>) {
                    if (list.isEmpty()) {
                        onLocalFetchUnresolved()
                    } else{
                        rates.value = list as ArrayList<Currency>
                        loading.value = false
                    }
                }

                override fun onError(error: Throwable) {
                    Timber.e(error)
                    onLocalFetchUnresolved()
                }

            })
        )
    }

    private fun onLocalFetchUnresolved() {
        fetchRatesFromApiService()
    }

    fun fetchRatesFromApiService() {
        disposable.add(
            ratesApiService.getRates()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<Rates>() {
                    override fun onSuccess(rates: Rates) {
                        onNetworkRequestSuccessful(rates.currencies)
                        updateRates(rates)
                    }

                    override fun onError(error: Throwable) {
                        Timber.e(error)
                        onNetworkRequestError()
                    }

                    override fun onStart() {
                        onLoading()
                    }
                })
        )
    }

    private fun onNetworkRequestSuccessful(result: LinkedHashMap<String, Currency>) {
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

            ensureCurrencyListPersistence()

            loadError.value = false
            loading.value = false
        } else {
            onNetworkRequestError()
        }
    }

    private fun onLoading() {
        loading.value = true
    }

    private fun onNetworkRequestError() {
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

    fun onCurrencyValueChanged(value: Double) {
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

    fun getLastNetworkFetchTimestamp(): Single<Long> = ratesDao.getLastRefreshDate()

    fun updateRates(rates: Rates) {
        disposable.add(ratesDao.updateRates(rates)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.io())
            .subscribeWith(object: DisposableCompletableObserver() {
                override fun onComplete() {
                    Timber.i("Rates updated")
                }

                override fun onError(error: Throwable) {
                    Timber.e(error)
                }
            })
        )
    }

    fun ensureCurrencyListPersistence() {
        rates.value?.run {
            val toPersist = this.toList()

            // update currencies db
            disposable.add(currenciesDao.insertCurrencies(toPersist)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribeWith(object: DisposableCompletableObserver() {
                    override fun onComplete() {
                        Timber.i("Currencies updated")
                    }

                    override fun onError(error: Throwable) {
                        Timber.e(error)
                    }
                })
            )
        }
    }
}