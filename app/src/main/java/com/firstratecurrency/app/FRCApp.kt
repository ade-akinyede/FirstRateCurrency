package com.firstratecurrency.app

import android.app.Application
import com.firstratecurrency.app.di.component.AppComponent
import com.firstratecurrency.app.di.component.DaggerAppComponent
import com.firstratecurrency.app.di.module.AppModule
import com.firstratecurrency.app.utils.LoggingReleaseTree
import timber.log.Timber
import java.util.*

class FRCApp: Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = initDagger(this)

        // Debug and verbose levels are not logged for release builds
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(LoggingReleaseTree())
        }

        Timber.i("App started")
    }

    private fun initDagger(app: FRCApp): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()

}