package com.firstratecurrency.app.di.component

import android.app.Application
import android.content.Context
import com.firstratecurrency.app.di.module.AppModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun getContext(): Context

    fun getApplication(): Application
}