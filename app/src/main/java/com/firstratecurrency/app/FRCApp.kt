package com.firstratecurrency.app

import android.app.Application
import com.firstratecurrency.app.utils.LoggingReleaseTree
import timber.log.Timber

class FRCApp: Application() {

    object Test {
        var running = false
    }

    override fun onCreate() {
        super.onCreate()
        // Debug and verbose levels are not logged for release builds
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(LoggingReleaseTree())
        }

        Timber.i("App started")
    }

}