package com.firstratecurrency.app.data

import com.google.gson.annotations.SerializedName

/**
 * Sample API response from endpoint https://revolut.duckdns.org/latest?base=EUR
 * {
    "base":"EUR",
    "date":"2018-09-06",
    "rates": {
        "AUD":1.6207,
        "BGN":1.961,
        "BRL":4.8046,
        "CAD":1.5379,
        "CHF":1.1305,
        "CNY":7.9663,
        "CZK":25.784,
        "DKK":7.4766,
        "GBP":0.90064,
        "HKD":9.1568,
        "HRK":7.4539,
        "HUF":327.36,
        "IDR":17370.0,
        "ILS":4.1817,
        "INR":83.941,
        "ISK":128.14,
        "JPY":129.9,
        "KRW":1308.2,
        "MXN":22.425,
        "MYR":4.8248,
        "NOK":9.8021,
        "NZD":1.768,
        "PHP":62.759,
        "PLN":4.3298,
        "RON":4.6509,
        "RUB":79.787,
        "SEK":10.619,
        "SGD":1.6043,
        "THB":38.232,
        "TRY":7.6486,
        "USD":1.1665,
        "ZAR":17.871
    }
 */
data class Rates(
    val base: String,
    val date: String,

    @SerializedName("rates")
    val currencies: List<Currency>
)

data class Currency(
    val code: String,
    val rate: Double,
    val country: Country
)

data class Country (
    val name: String,
    val flag: Int
)