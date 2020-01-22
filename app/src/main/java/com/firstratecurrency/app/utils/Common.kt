package com.firstratecurrency.app.utils

import com.firstratecurrency.app.data.Currency

fun calculateCurrencyValue(currency: Currency): Double = (currency.rate / currency.refRate) * currency.refValue