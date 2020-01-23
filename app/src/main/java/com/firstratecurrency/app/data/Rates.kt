package com.firstratecurrency.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "rates"
)
data class Rates(
    val base: String,
    val date: String,
    /**
     * Indicates when the [Rates] was last fetched. Used for showing notification when it's time
     * to refresh.
     */
    @ColumnInfo(name = "last_refresh_date") val refreshedAt: Calendar = Calendar.getInstance(),

    val currencies: LinkedHashMap<String, Currency>
)

@Entity(tableName = "currencies")
data class Currency(
    @PrimaryKey val code: String,
    var rate: Double,
    val extendedCurrency: ExtendedCurrency,

    var refRate: Double = 1.0,
    var refValue: Double = 1.0
) {
    /**
     * Calculates the currency value based on reference rate and conversion.
     */
    fun getCurrencyValue(): Double = (this.rate / this.refRate) * this.refValue
}

@Entity(tableName = "extend_currencies")
data class ExtendedCurrency (
    val name: String,
    val flag: Int
)