package com.firstratecurrency.app.data.db

import androidx.room.*
import com.firstratecurrency.app.data.model.Rates
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface RatesDao {

    @Query("SELECT last_refresh_date FROM rates")
    fun getLastRefreshDate(): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateRates(rates: Rates): Completable
}