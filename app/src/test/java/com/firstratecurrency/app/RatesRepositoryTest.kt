package com.firstratecurrency.app

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.firstratecurrency.app.data.RatesRepository
import com.firstratecurrency.app.data.model.Currency
import com.firstratecurrency.app.data.model.Rates
import com.firstratecurrency.app.data.network.RatesApiService
import com.firstratecurrency.app.di.component.DaggerRatesRepositoryComponent
import com.firstratecurrency.app.viewmodels.RatesListViewModel
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

    private lateinit var ratesRepository: RatesRepository

//    @Before
//    fun setup() {
//        MockitoAnnotations.initMocks(this)
//        FRCApp.Test.running = true
//
//        ratesRepository =
//            RatesRepository()
//            RatesListViewModel(application)
//        DaggerRatesRepositoryComponent.builder()
//            .ratesApiModule(RatesApiModuleTest(ratesApiService))
//            .build()
//            .inject(ratesRepository)
//    }

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
//
//    @Test
//    fun getRatesSuccess() {
//        val currency = Currency(
//            "EUR",
//            1.83
//        )
//        val rates =
//            Rates("", "")
//
//        val testSingle = Single.just(rates)
//        Mockito.`when`(ratesApiService.getRates()).thenReturn(testSingle)
//
//        ratesRepository.refresh()
//
//        Assert.assertEquals(1, ratesRepository.rates.value?.size)
//
//    }
}