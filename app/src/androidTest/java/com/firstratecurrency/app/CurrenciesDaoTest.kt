package com.firstratecurrency.app

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.firstratecurrency.app.data.db.AppDatabase
import com.firstratecurrency.app.data.db.CurrenciesDao
import com.firstratecurrency.app.data.model.Currency
import io.reactivex.internal.util.NotificationLite.getValue
import io.reactivex.observers.TestObserver
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CurrenciesDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var currenciesDao: CurrenciesDao

    private val aud = Currency("AUD", 1.6207)
    private val bgn = Currency("BGN", 1.961)
    private val brl = Currency("BRL", 4.8046)
    private val cad = Currency("CAD", 1.5379)
    private val chf = Currency("CHF", 1.1305)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before fun createDb() {
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
            currenciesDao = database.currenciesDao()
        }
    }

    @After fun closeDb() {
        database.close()
    }

    @Test fun testInsertCurrenciesAndValidateInsertionOrder() {
        val currencies = listOf(aud, bgn, brl, cad, chf)
        currenciesDao.insertCurrencies(currencies).blockingAwait()
        val result = currenciesDao.getCurrencies().blockingGet()
        assertThat(result.size, equalTo(currencies.size))

        // Ensure plant list is sorted by insertion order
        assertThat(result[0], equalTo(aud))
        assertThat(result[1], equalTo(bgn))
        assertThat(result[2], equalTo(brl))
        assertThat(result[3], equalTo(cad))
        assertThat(result[4], equalTo(chf))
    }

//    @Test fun testInsertCurrencies() {
//        val plantList = getValue(currenciesDao.getPlantsWithGrowZoneNumber(1))
//        assertThat(plantList.size, equalTo(2))
//        assertThat(getValue(currenciesDao.getPlantsWithGrowZoneNumber(2)).size, equalTo(1))
//        assertThat(getValue(currenciesDao.getPlantsWithGrowZoneNumber(3)).size, equalTo(0))
//
//        // Ensure plant list is sorted by name
//        assertThat(plantList[0], equalTo(plantA))
//        assertThat(plantList[1], equalTo(plantB))
//    }

//    @Test fun testInsertCurrencies() {
//        assertThat(getValue(currenciesDao.getPlant(plantA.plantId)), equalTo(plantA))
//    }

}
