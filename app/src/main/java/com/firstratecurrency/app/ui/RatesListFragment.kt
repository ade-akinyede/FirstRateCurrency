package com.firstratecurrency.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.firstratecurrency.app.R
import com.firstratecurrency.app.data.Currency
import kotlinx.android.synthetic.main.fragment_rates_list.*
import timber.log.Timber

class RatesListFragment: Fragment(), RatesListAdapter.RatesChangeListener {

    private lateinit var ratesViewModel: RatesListViewModel
    private lateinit var ratesListAdapter: RatesListAdapter

    private val ratesListDataObserver = Observer<ArrayList<Currency>> { list ->
        list?.let {
            ratesList.visibility = View.VISIBLE
            listLoading.visibility = View.GONE
            listError.visibility = View.GONE
            // Pass a copy of the live data rather than reference for proper change notification and handling.
            ratesListAdapter.updateList(it.toList().map { entry -> entry.copy() } as ArrayList<Currency>)
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

        ratesListAdapter = RatesListAdapter(requireContext(), this)
        ratesViewModel = ViewModelProviders.of(this).get(RatesListViewModel::class.java)

        ratesViewModel.getRatesLiveData().observe(viewLifecycleOwner, ratesListDataObserver)
        ratesViewModel.getRatesLoadingState().observe(viewLifecycleOwner, ratesListLoadingObserver)
        ratesViewModel.getLoadErrorState().observe(viewLifecycleOwner, ratesListErrorObserver)

        ratesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ratesListAdapter
        }
    }

    override fun onFirstResponderChange(position: Int) {
        ratesViewModel.movePositionToTop(position)
    }

    override fun onRateValueChange(value: Double) {
        ratesViewModel.onRateValueChanged(value)
    }
}