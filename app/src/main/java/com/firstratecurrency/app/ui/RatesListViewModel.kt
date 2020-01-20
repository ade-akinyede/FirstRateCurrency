package com.firstratecurrency.app.ui

import android.app.Application
import androidx.collection.ArrayMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.firstratecurrency.app.FRCApp
import com.firstratecurrency.app.data.Currency
import com.firstratecurrency.app.data.Rates
import com.firstratecurrency.app.data.RatesApiService
import com.firstratecurrency.app.di.component.DaggerViewModelComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class RatesListViewModel(app: Application): AndroidViewModel(app) {

    private val rates by lazy { MutableLiveData<ArrayMap<String, Currency>>() }
    private val loading by lazy { MutableLiveData<Boolean>() }
    private val loadError by lazy { MutableLiveData<Boolean>() }

    private val disposable = CompositeDisposable()
    @Inject
    lateinit var ratesApiService:RatesApiService

    init {
        if (!FRCApp.Test.running) {
            refresh()
        }
    }

    private fun inject() {
        if (!FRCApp.Test.running) {
            DaggerViewModelComponent.create().inject(this)
        }
    }

    fun refresh() {
        inject()
        getRates()
    }

    fun getRatesLiveData() = rates
    fun getRatesLoadingState() = loading
    fun getLoadErrorState() = loadError

    private fun getRates() {
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

    private fun onResponse(result: List<Currency>) {
        if (result.isNotEmpty()) {
            val mapper = ArrayMap<String, Currency>(result.size)
            result.map {
                mapper.put(it.code, it)
            }

            rates.value = mapper
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

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}