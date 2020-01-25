package com.firstratecurrency.app.data.network

import com.firstratecurrency.app.data.model.Currency
import com.firstratecurrency.app.data.model.Rates
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
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
        // The data structure should maintain the order of insertion. LinkedHashMap does this.
        val ratesMap: LinkedHashMap<String, Currency> = linkedMapOf()
        // Insert the base currency
        val firstEntry = Currency(baseCurrency, 1.0)
        ratesMap[baseCurrency] = firstEntry

        rates?.apply {
            Timber.d("Transforming rates to list of Currency...")
            this.entrySet().iterator().forEach {
                ratesMap[it.key] = Currency(it.key, it.value.asDouble)
            }
        }

        val response = Rates(baseCurrency, date)
        response.currencies = ratesMap
        return response
    }
}