package com.firstratecurrency.app.data.db

import androidx.room.*
import com.firstratecurrency.app.data.model.Currency

@Dao
interface CurrenciesDao {

    @Query("SELECT * FROM currencies")
    fun getCurrencies(): List<Currency>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrencies(list: List<Currency>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateCurrencies(list: List<Currency>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun changeReferenceCurrency(currency: Currency)
}