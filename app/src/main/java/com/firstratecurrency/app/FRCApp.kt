package com.firstratecurrency.app

import android.app.Application
import com.firstratecurrency.app.di.component.AppComponent
import com.firstratecurrency.app.di.component.DaggerAppComponent
import com.firstratecurrency.app.di.module.AppModule

class FRCApp: Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = initDagger(this)
    }

    private fun initDagger(app: FRCApp): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()

}