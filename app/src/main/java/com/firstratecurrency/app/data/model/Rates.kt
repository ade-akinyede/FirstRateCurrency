package com.firstratecurrency.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "rates")
data class Rates(
    @PrimaryKey
    val base: String,

    @ColumnInfo(name = "release_date")
    val date: String,
    /**
     * Indicates when the [Rates] was last fetched. Used for showing notification when it's time
     * to refresh.
     */
    @ColumnInfo(name = "last_refresh_date")
    val refreshedAt: Long = System.currentTimeMillis()
) {
    @Ignore
    var currencies: LinkedHashMap<String, Currency> = linkedMapOf()
}

