package com.firstratecurrency.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.firstratecurrency.app.FRCApp
import com.firstratecurrency.app.R
import com.firstratecurrency.app.data.Currency
import kotlinx.android.synthetic.main.fragment_rates_list.*
import timber.log.Timber

class RatesListFragment: Fragment() {

    private lateinit var ratesViewModel: RatesListViewModel
    private lateinit var ratesListAdapter: RatesListAdapter

    private val ratesListDataObserver = Observer<List<Currency>> { list ->
        list?.let {
            Timber.i("Rates update received successfully")
            ratesListAdapter.updateList(it)
        } ?: Timber.e("Rates update received but is empty")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rates_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ratesListAdapter = RatesListAdapter(arrayListOf(), requireContext())
        ratesViewModel = ViewModelProviders.of(this).get(RatesListViewModel::class.java)
        ratesViewModel.rates.observe(viewLifecycleOwner, ratesListDataObserver)

        ratesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ratesListAdapter
        }
    }
}