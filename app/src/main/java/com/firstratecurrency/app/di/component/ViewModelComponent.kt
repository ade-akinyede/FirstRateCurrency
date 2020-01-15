package com.firstratecurrency.app.di.component

import com.firstratecurrency.app.di.module.RatesApiModule
import com.firstratecurrency.app.ui.RatesListViewModel
import dagger.Component

@Component(modules = [RatesApiModule::class])
interface ViewModelComponent {

    fun inject(viewModel: RatesListViewModel)
}