package com.firstratecurrency.app.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.firstratecurrency.app.R
import com.firstratecurrency.app.data.Currency
import kotlinx.android.synthetic.main.list_header.view.*
import kotlinx.android.synthetic.main.list_item_currency.view.*
import timber.log.Timber
import kotlin.collections.ArrayList

class RatesListAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class RatesHeaderViewHolder(var view: View): RecyclerView.ViewHolder(view)

    class RatesListViewHolder(var view: View, var rowListener: (Int) -> Unit):
        RecyclerView.ViewHolder(view), View.OnTouchListener {

        init {
            view.currencyExchangeEntry.setOnTouchListener(this)
        }

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {
            return when (event?.action) {
                ACTION_UP -> {
                    rowListener(adapterPosition)
                    return view?.performClick() ?: false
                }
                else -> false
            }
        }
    }

    object Configuration {
        const val FIRST_RESPONDER_POSITION = 1
        const val TYPE_HEADER = 100
        const val TYPE_CURRENCY = 200
        val GLIDE_IMAGE_OPTIONS: RequestOptions =
            RequestOptions().placeholder(R.drawable.vd_flag)
                .error(R.drawable.vd_flag)
                .fitCenter()
                .transform(CircleCrop())
    }

    private val ratesList: ArrayList<RatesListItem> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Configuration.TYPE_HEADER -> RatesHeaderViewHolder(inflater.inflate(R.layout.list_header, parent, false))
            else -> RatesListViewHolder(inflater.inflate(R.layout.list_item_currency, parent, false), this::makeFirstResponder)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HeaderItem -> Configuration.TYPE_HEADER
            else -> Configuration.TYPE_CURRENCY
        }
    }

    override fun getItemCount(): Int = ratesList.size

    private fun getItem(position: Int): RatesListItem? = ratesList[position]

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RatesHeaderViewHolder -> displayHeader(holder, position)
            is RatesListViewHolder -> displayCurrencyItem(holder, position)
        }
    }

    private fun displayHeader(holder: RatesHeaderViewHolder, position: Int) {
        holder.view.listHeaderText.text = (ratesList[position] as HeaderItem).title
    }

    private fun displayCurrencyItem(holder: RatesListViewHolder, position: Int) {
        val entry = (ratesList[position] as CurrencyItem).currency

        holder.view.countryCurrency.text = entry.extendedCurrency.name
        holder.view.currencyCode.text = entry.code
        Glide.with(holder.view).load(entry.extendedCurrency.flag)
            .apply(Configuration.GLIDE_IMAGE_OPTIONS)
            .into(holder.view.countryFlag)

        holder.view.currencyExchangeEntry.setText(entry.rate.toString())
    }

    fun updateList(updatedList: List<Currency>) {
        ratesList.clear()
        ratesList.add(HeaderItem(context.getString(R.string.title_rates)))
        updatedList.map {
            ratesList.add(CurrencyItem(it))
        }
        notifyDataSetChanged()
        Timber.d("UI (list) updated with new rates")
    }

    private fun makeFirstResponder(position: Int) {
        // Move to top of row if not already
        if (position > Configuration.FIRST_RESPONDER_POSITION) {
            ratesList[position].apply {
                ratesList.removeAt(position)
                ratesList.add(Configuration.FIRST_RESPONDER_POSITION, this)
                notifyItemMoved(position, Configuration.FIRST_RESPONDER_POSITION)
            }
        }
    }
}