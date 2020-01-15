package com.firstratecurrency.app.data

data class Rates(
    val base: String,
    val date: String,
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