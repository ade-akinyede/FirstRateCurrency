package com.firstratecurrency.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
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
            ratesList.visibility = View.VISIBLE
            listLoading.visibility = View.GONE
            listError.visibility = View.GONE
            Timber.i("Rates update received successfully")
            ratesListAdapter.updateList(it)
        } ?: Timber.e("Rates update received but is empty")
    }

    private val ratesListLoadingObserver = Observer<Boolean> { isLoading ->
        // Only show loading when the list is empty
        if (ratesListAdapter.itemCount == 0) {
            if (isLoading) {
                listLoading.visibility = View.VISIBLE
                ratesList.visibility = View.GONE
                listError.visibility = View.GONE
            } else {
                listLoading.visibility = View.GONE
            }
        }
    }

    private val ratesListErrorObserver = Observer<Boolean> { isError ->
        // Only show error when the list is empty
        if (ratesListAdapter.itemCount == 0 && isError) {
            listError.visibility = View.VISIBLE
            listLoading.visibility = View.GONE
            ratesList.visibility = View.GONE
        }

        // TODO show error in a non-intrusive way
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

        ratesListAdapter = RatesListAdapter(requireContext())
        ratesViewModel = ViewModelProviders.of(this).get(RatesListViewModel::class.java)

        ratesViewModel.rates.observe(viewLifecycleOwner, ratesListDataObserver)
        ratesViewModel.loading.observe(viewLifecycleOwner, ratesListLoadingObserver)
        ratesViewModel.loadError.observe(viewLifecycleOwner, ratesListErrorObserver)

        ratesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ratesListAdapter
        }
    }
}