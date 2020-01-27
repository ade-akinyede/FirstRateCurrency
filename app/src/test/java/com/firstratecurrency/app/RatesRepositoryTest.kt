package com.firstratecurrency.app

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.firstratecurrency.app.data.RatesRepository
import com.firstratecurrency.app.data.db.CurrenciesDao
import com.firstratecurrency.app.data.db.RatesDao
import com.firstratecurrency.app.data.model.Currency
import com.firstratecurrency.app.data.model.Rates
import com.firstratecurrency.app.data.network.RatesApiService
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor

class RatesRepositoryTest {

    // Execute a task instantly and receive a response
    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Mock
    lateinit var ratesApiService: RatesApiService

    private val application: Application = Mockito.mock(Application::class.java)
    private val currenciesDao: CurrenciesDao = Mockito.mock(CurrenciesDao::class.java)
    private val ratesDao: RatesDao = Mockito.mock(RatesDao::class.java)

    private lateinit var ratesRepository: RatesRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        FRCApp.Test.running = true

        ratesRepository = RatesRepository(ratesDao, currenciesDao, ratesApiService)
    }

    @Before
    fun setupRxSchedulers() {
        // The object to execute immediately
        val immediate = object: Scheduler() {
            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run() }, true)
            }
        }

        // Background and Main thread schedulers
        RxJavaPlugins.setInitNewThreadSchedulerHandler { immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }
    }

    @Test
    fun getRatesSuccess() {
        val rates = Rates("EUR", "2020-01")
        val currenciesMap = LinkedHashMap<String, Currency>()
        val aud = Currency("AUD",1.6207)
        val bgn = Currency("BGN",1.961)
        val brl = Currency("BRL",4.8046)
        val cad = Currency("CAD",1.5379)
        val chf = Currency("CHF",1.1305)

        currenciesMap[aud.code] = aud
        currenciesMap[bgn.code] = bgn
        currenciesMap[brl.code] = brl
        currenciesMap[cad.code] = cad
        currenciesMap[chf.code] = chf

        rates.currencies = currenciesMap

        val testSingle = Single.just(rates)
        Mockito.`when`(ratesDao.updateRates(rates)).then { Completable.complete() }
        Mockito.`when`(currenciesDao.insertCurrencies(rates.currencies.values.toList())).then { Completable.complete() }
        Mockito.`when`(ratesApiService.getRates()).thenReturn(testSingle)

        ratesRepository.fetchRatesFromApiService()

        Assert.assertEquals(currenciesMap.size, ratesRepository.getRatesLiveData().value?.size)

    }
}