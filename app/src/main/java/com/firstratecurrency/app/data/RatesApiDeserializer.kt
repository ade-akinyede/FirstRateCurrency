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

        Timber.d("Extracting rates for transformation...")
        val rates: JsonElement? = jsonObject?.get("rates")
        val ratesList: MutableList<Currency> = mutableListOf()
        rates?.apply {
            Timber.d("Transforming rates to list of Currency...")
            this.asJsonObject.entrySet().iterator().forEach {
                ratesList.add(Currency(it.key, it.value.asDouble, Country(ExtendedCurrency.getCurrencyByISO(it.key).name, -1)))
            }
        }

        return Rates("", "", ratesList)
    }
}