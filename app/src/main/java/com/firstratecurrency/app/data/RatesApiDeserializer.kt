package com.firstratecurrency.app.data

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.mynameismidori.currencypicker.ExtendedCurrency
import timber.log.Timber
import java.lang.reflect.Type

class RatesApiDeserializer: JsonDeserializer<Rates> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Rates {
        Timber.d("Deserializing Json: %s", json?.toString())
        val jsonObject = json?.asJsonObject
        val baseCurrency: String = jsonObject?.get("base")?.asString ?: ""
        val date: String = jsonObject?.get("date")?.asString ?: ""

        Timber.d("Extracting rates for transformation...")
        val rates = jsonObject?.get("rates")?.asJsonObject
        val ratesList: MutableList<Currency> = mutableListOf()
        rates?.apply {
            Timber.d("Transforming rates to list of Currency...")
            this.entrySet().iterator().forEach {
                val extendedCurrency = ExtendedCurrency.getCurrencyByISO(it.key)
                val ratesExtendedCurrency = ExtendedCurrency(extendedCurrency.name, extendedCurrency.flag)
                ratesList.add(Currency(it.key, it.value.asDouble, ratesExtendedCurrency))
            }
        }

        return Rates(baseCurrency, date, ratesList)
    }
}