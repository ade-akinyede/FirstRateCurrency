package com.firstratecurrency.app.utils

import android.util.Log
import timber.log.Timber

class LoggingReleaseTree: Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // Logging control for production release
        if (priority == Log.DEBUG || priority == Log.VERBOSE) {
            return
        }
    }
}