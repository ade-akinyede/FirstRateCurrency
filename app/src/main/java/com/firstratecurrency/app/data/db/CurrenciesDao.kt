package com.firstratecurrency.app.data.db

import androidx.room.*
import com.firstratecurrency.app.data.model.Currency
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface CurrenciesDao {

    @Query("SELECT * FROM currencies")
    fun getCurrencies(): Single<List<Currency>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrencies(list: List<Currency>): Completable

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateCurrencies(list: List<Currency>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun changeReferenceCurrency(currency: Currency): Completable
}