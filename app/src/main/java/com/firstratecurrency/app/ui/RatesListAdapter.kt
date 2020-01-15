package com.firstratecurrency.app.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.firstratecurrency.app.R
import com.firstratecurrency.app.data.Currency
import kotlinx.android.synthetic.main.list_header.view.*
import kotlinx.android.synthetic.main.list_item_currency.view.*
import timber.log.Timber

class RatesListAdapter(private val ratesList: ArrayList<Currency>, context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class RatesHeaderViewHolder(var view: View): RecyclerView.ViewHolder(view)
    class RatesListViewHolder(var view: View): RecyclerView.ViewHolder(view)

    object Configuration {
        const val TYPE_HEADER = 100
        const val TYPE_LIST_ITEM = 200
        val GLIDE_IMAGE_OPTIONS: RequestOptions =
            RequestOptions().placeholder(R.drawable.vd_flag)
                .error(R.drawable.vd_flag)
                .fitCenter()
                .transform(CircleCrop())
    }

    private val headerTextTitle = context.getString(R.string.title_rates)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Configuration.TYPE_HEADER -> RatesHeaderViewHolder(inflater.inflate(R.layout.list_header, parent, false))
            else -> RatesListViewHolder(inflater.inflate(R.layout.list_item_currency, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> Configuration.TYPE_HEADER
            else -> Configuration.TYPE_LIST_ITEM
        }
    }

    override fun getItemCount(): Int = ratesList.count() // this accounts for the header item as well

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RatesHeaderViewHolder -> populateHeader(holder)
            is RatesListViewHolder -> populateListItem(holder, position)
        }
    }

    private fun populateHeader(holder: RatesHeaderViewHolder) {
        holder.view.listHeaderText.text = headerTextTitle
    }

    private fun populateListItem(holder: RatesListViewHolder, position: Int) {
        val entry: Currency = ratesList[position]
        holder.view.countryCurrency.text = entry.country.name
        holder.view.currencyCode.text = entry.code
//        holder.view.countryFlag.apply {
//            this.setImageResource(
//                if (entry.country.flag == -1) R.drawable.ic_country_flag_placeholder
//                else entry.country.flag
//            )
//        }
        Glide.with(holder.view).load(entry.country.flag)
            .apply(Configuration.GLIDE_IMAGE_OPTIONS)
            .into(holder.view.countryFlag)

        holder.view.currencyExchangeEntry.setText(entry.rate.toString())
    }

    fun updateList(updatedList: List<Currency>) {
        ratesList.clear()
        ratesList.addAll(updatedList)
        notifyDataSetChanged()
        Timber.d("UI (list) updated with new rates")
    }
}