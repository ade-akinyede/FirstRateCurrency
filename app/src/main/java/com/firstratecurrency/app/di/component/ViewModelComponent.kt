package com.firstratecurrency.app.di.component

import com.firstratecurrency.app.di.module.ApiModule
import com.firstratecurrency.app.ui.RatesListViewModel
import dagger.Component

@Component(modules = [ApiModule::class])
interface ViewModelComponent {

    fun inject(viewModel: RatesListViewModel)
}