package com.firstratecurrency.app.ui

import com.firstratecurrency.app.data.Currency

open class RatesListItem

class CurrencyItem(var currency: Currency): RatesListItem() {

    var displayedCurrencyRate: String = currency.rate.toString()
}

class HeaderItem(val title: String): RatesListItem()