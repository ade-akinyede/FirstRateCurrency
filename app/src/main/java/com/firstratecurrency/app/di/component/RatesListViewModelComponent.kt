package com.firstratecurrency.app.di.component

import com.firstratecurrency.app.di.module.AppDatabaseModule
import com.firstratecurrency.app.di.module.RatesApiModule
import com.firstratecurrency.app.di.module.RatesRepositoryModule
import com.firstratecurrency.app.viewmodels.RatesListViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppDatabaseModule::class, RatesRepositoryModule::class, RatesApiModule::class])
interface RatesListViewModelComponent {

    fun inject(viewModel: RatesListViewModel)
}