package com.firstratecurrency.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.firstratecurrency.app.BuildConfig
import com.firstratecurrency.app.FRCApp
import com.firstratecurrency.app.data.Currency
import com.firstratecurrency.app.data.Rates
import com.firstratecurrency.app.data.RatesApiService
import com.firstratecurrency.app.di.component.DaggerViewModelComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RatesListViewModel(app: Application): AndroidViewModel(app) {

    val rates by lazy { MutableLiveData<List<Currency>>() }
    val loading by lazy { MutableLiveData<Boolean>() }
    val loadError by lazy { MutableLiveData<Boolean>() }

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

    private fun getRates() {
        disposable.add(
            ratesApiService.getRates()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<Rates>() {
                    override fun onSuccess(ratesList: Rates) {
                        if (ratesList.currencies.isNotEmpty()) {
                            rates.value = ratesList.currencies
                            loadError.value = false
                            loading.value = false
                        } else {
                            loadError.value = true
                            loading.value = false
                            rates.value = null
                        }
                    }

                    override fun onError(error: Throwable) {
                        error.printStackTrace()
                        loading.value = false
                        rates.value = null
                        loadError.value = true
                    }

                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}