package com.firstratecurrency.app

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.runner.AndroidJUnit4
import com.firstratecurrency.app.data.db.AppDatabase
import com.firstratecurrency.app.data.db.RatesDao
import com.firstratecurrency.app.data.model.Rates
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {

    private lateinit var ratesDao: RatesDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().context
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        ratesDao = db.ratesDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        val refreshedAt = System.currentTimeMillis()
        val rates: Rates = Rates("EUR", "2020-02", refreshedAt)
        ratesDao.updateRates(rates)

        val lastRefreshDate = ratesDao.getLastRefreshDate()
        assert(lastRefreshDate == refreshedAt)
    }
}