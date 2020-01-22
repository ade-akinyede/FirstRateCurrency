package com.firstratecurrency.app.data

data class Rates(
    val base: String,
    val date: String,
    val currencies: LinkedHashMap<String, Currency>
)

data class Currency(
    val code: String,
    var rate: Double,
    val extendedCurrency: ExtendedCurrency,

    var refRate: Double = 1.0,
    var refValue: Double = 1.0
)

data class ExtendedCurrency (
    val name: String,
    val flag: Int
)