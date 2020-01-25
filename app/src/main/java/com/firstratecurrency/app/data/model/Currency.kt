package com.firstratecurrency.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class Currency(
    @PrimaryKey
    val code: String,

    var rate: Double,

    @ColumnInfo(name = "reference_rate")
    var refRate: Double = 1.0,

    @ColumnInfo(name = "reference_value")
    var refValue: Double = 1.0,

    @ColumnInfo(name = "created_at")
    val createdTime: Long = System.currentTimeMillis()
) {
    /**
     * Calculates the currency value based on reference rate and conversion.
     */
    fun getCurrencyValue(): Double = (this.rate / this.refRate) * this.refValue
}