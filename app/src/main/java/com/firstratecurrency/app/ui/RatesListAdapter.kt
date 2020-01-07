package com.firstratecurrency.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firstratecurrency.app.R
import com.firstratecurrency.app.data.Rates
import kotlinx.android.synthetic.main.list_header.view.*
import kotlinx.android.synthetic.main.list_item_currency.view.*

class RatesListAdapter(private val ratesList: ArrayList<Rates>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class RatesHeaderViewHolder(var view: View): RecyclerView.ViewHolder(view)
    class RatesListViewHolder(var view: View): RecyclerView.ViewHolder(view)

    object Configuration {
        const val HEADER_COUNT: Int = 1
        const val TYPE_HEADER = 100
        const val TYPE_LIST_ITEM = 200
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = when (viewType) {
            0 -> inflater.inflate(R.layout.list_header, parent, false)
            else -> inflater.inflate(R.layout.list_item_currency, parent, false)
        }
        return RatesListViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> Configuration.TYPE_HEADER
            else -> Configuration.TYPE_LIST_ITEM
        }
    }

    override fun getItemCount(): Int = Configuration.HEADER_COUNT + ratesList.count()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RatesHeaderViewHolder -> populateHeader(holder)
            is RatesListViewHolder -> populateListItem(holder, position)
        }
    }

    private fun populateHeader(holder: RatesHeaderViewHolder) = holder.view.listHeaderText

    private fun populateListItem(holder: RatesListViewHolder, position: Int) {
//        holder.view.countryCode
        holder.view.countryCurrency.text = ratesList[position].currency
//        holder.view.countryFlag
        holder.view.currencyExchangeEntry.setText(ratesList[position].rate.toString())
    }

    open fun updateList(updatedList: ArrayList<Rates>) {
        ratesList.clear()
        ratesList.addAll(updatedList)
        notifyDataSetChanged()
    }
}