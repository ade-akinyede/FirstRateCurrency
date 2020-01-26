package com.firstratecurrency.app.data

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.firstratecurrency.app.FRCApp
import com.firstratecurrency.app.data.db.CurrenciesDao
import com.firstratecurrency.app.data.db.RatesDao
import com.firstratecurrency.app.data.model.Currency
import com.firstratecurrency.app.data.model.Rates
import com.firstratecurrency.app.data.network.RatesApiService
import com.firstratecurrency.app.di.component.DaggerRatesRepositoryComponent
import com.firstratecurrency.app.utils.RATES_REFRESH_RATE_IN_SECS
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatesRepository @Inject constructor(
    private val ratesDao: RatesDao,
    private val currenciesDao: CurrenciesDao,
    private val ratesApiService: RatesApiService
) {

    private val rates by lazy { MutableLiveData<ArrayList<Currency>>() }
    private val loading by lazy { MutableLiveData<Boolean>() }
    private val loadError by lazy { MutableLiveData<Boolean>() }

    private val requestDisposable = CompositeDisposable()
    private val scheduledUpdateDisposable = CompositeDisposable()

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
        requestDisposable.add(currenciesDao.getCurrencies()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<List<Currency>>() {
                override fun onSuccess(list: List<Currency>) {
                    if (list.isEmpty()) {
                        onLocalFetchUnresolved()
                    } else{
                        rates.value = list as ArrayList<Currency>
                        loading.value = false
                        checkLastUpdateAndResolve()
                    }
                }

                override fun onError(error: Throwable) {
                    Timber.e(error)
                    onLocalFetchUnresolved()
                }

            })
        )
    }

    private fun checkLastUpdateAndResolve() {
        requestDisposable.add(getLastNetworkFetchTimestamp()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribeWith(object: DisposableSingleObserver<Long>() {
                override fun onSuccess(timestamp: Long) {
                    // When the elapsed time is over a minute, refresh immediately.
                    // Otherwise, schedule according to elapsed.
                    val elapsedSecs = (System.currentTimeMillis() - timestamp) / 1000
                    if (elapsedSecs > RATES_REFRESH_RATE_IN_SECS) {
                        fetchRatesFromApiService()
                    } else{
                        scheduleNextRatesUpdate(elapsedSecs)
                    }
                }

                override fun onError(error: Throwable) {
                    Timber.e(error)
                }
            })
        )

    }

    @SuppressLint("CheckResult")
    private fun scheduleNextRatesUpdate(timeInSeconds: Long) {
        // Fetch new rates after given time
        scheduledUpdateDisposable.add(Observable.timer(timeInSeconds, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                fetchRatesFromApiService()
            }
        )
    }

    private fun onLocalFetchUnresolved() {
        fetchRatesFromApiService()
    }

    private fun getRatesApiDisposable(): Disposable =
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


    fun fetchRatesFromApiService() {
        requestDisposable.add(getRatesApiDisposable())
    }

    private fun onNetworkRequestSuccessful(result: LinkedHashMap<String, Currency>) {
        if (result.isNotEmpty()) {
            rates.value = rates.value?.let { currencyList ->
                // Update rates while maintaining the current ordering
                // by cycling through the current (rates) list and mapping
                // the result to the entry.
                val updatedRefRate = result[currencyList[0].code]?.rate

                currencyList.map { entry ->
                    result[entry.code]?.let { updatedCurrency ->
                        entry.rate = updatedCurrency.rate
                        if (updatedRefRate != null) {
                            entry.refRate = updatedRefRate
                        }
                    }
                }

                currencyList
            } ?: run {
                result.values.toList() as ArrayList<Currency>
            }

            ensureCurrencyListPersistence()
            scheduleNextRatesUpdate(RATES_REFRESH_RATE_IN_SECS)

            loadError.value = false
            loading.value = false
        } else {
            onNetworkRequestError()
        }
    }

    private fun onLoading() {
        loading.postValue(true)
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

    private fun getLastNetworkFetchTimestamp(): Single<Long> = ratesDao.getLastRefreshDate()

    fun updateRates(rates: Rates) {
        requestDisposable.add(ratesDao.updateRates(rates)
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
            requestDisposable.add(currenciesDao.insertCurrencies(toPersist)
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