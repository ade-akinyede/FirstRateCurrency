package com.firstratecurrency.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.firstratecurrency.app.data.Rates

class RatesListViewModel(app: Application): AndroidViewModel(app) {

    val rates by lazy { MutableLiveData<List<Rates>>() }
    val loading by lazy { MutableLiveData<Boolean>() }
    val loadError by lazy { MutableLiveData<Boolean>() }

    fun refresh() {
        getRates()
    }

    private fun getRates() {
        rates.value = arrayListOf(
            Rates("AUD",1.6207),
            Rates("BGN", 1.961),
            Rates("AUD",1.6207),
            Rates("BRL", 4.8046),
            Rates("CAD", 1.5379),
            Rates("CHF", 1.1305),
            Rates("CNY", 7.9663),
            Rates("CZK", 25.784),
            Rates("DKK", 7.4766),
            Rates("GBP", 0.90064),
            Rates("HKD", 9.1568),
            Rates("HRK", 7.4539),
            Rates("HUF", 327.36),
            Rates("IDR", 17370.0),
            Rates("ILS", 4.1817),
            Rates("INR", 83.941),
            Rates("ISK", 128.14),
            Rates("JPY", 129.9),
            Rates("KRW", 1308.2),
            Rates("MXN", 22.425),
            Rates("MYR", 4.8248),
            Rates("NOK", 9.8021),
            Rates("NZD", 1.768),
            Rates("PHP", 62.759),
            Rates("PLN", 4.3298),
            Rates("RON", 4.6509),
            Rates("RUB", 79.787),
            Rates("SEK", 10.619),
            Rates("SGD", 1.6043),
            Rates("THB", 38.232),
            Rates("TRY", 7.6486),
            Rates("USD", 1.1665),
            Rates("ZAR", 17.871)
        )

        loading.value = false
        loadError.value = false
    }
}