package com.firstratecurrency.app.data.db

import androidx.room.*
import com.firstratecurrency.app.data.model.Rates
import io.reactivex.Single

@Dao
interface RatesDao {

    @Query("SELECT last_refresh_date FROM rates")
    fun getLastRefreshDate(): Single<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateLastRefreshDate(rates: Rates)
}