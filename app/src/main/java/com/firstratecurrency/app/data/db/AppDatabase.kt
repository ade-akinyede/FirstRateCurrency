package com.firstratecurrency.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.firstratecurrency.app.data.model.Currency
import com.firstratecurrency.app.data.model.Rates

@Database(entities = [Rates::class, Currency::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun ratesDao(): RatesDao
    abstract fun currenciesDao(): CurrenciesDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}