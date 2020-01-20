package com.firstratecurrency.app.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.ViewGroup
import androidx.collection.ArrayMap
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

    class RatesListViewHolder(var view: View, var rowListener: (Int) -> Unit, var onTextChanged: (Int, String) -> Unit):
        RecyclerView.ViewHolder(view), View.OnTouchListener, TextWatcher {

        init {
            view.currencyExchangeEntry.setOnTouchListener(this)
            view.currencyExchangeEntry.addTextChangedListener(this)
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

        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val length = p0?.length
            if (length != null) {
                if (length > 0)
                    onTextChanged(adapterPosition, p0.toString())
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
            else -> RatesListViewHolder(inflater.inflate(R.layout.list_item_currency, parent, false), this::makeFirstResponder, this::checkAndConvert)
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
        val currencyItem = ratesList[position] as CurrencyItem
        val currency = currencyItem.currency

        holder.view.countryCurrency.text = currency.extendedCurrency.name
        holder.view.currencyCode.text = currency.code
        Glide.with(holder.view).load(currency.extendedCurrency.flag)
            .apply(Configuration.GLIDE_IMAGE_OPTIONS)
            .into(holder.view.countryFlag)

        holder.view.currencyExchangeEntry.setText(currencyItem.displayedCurrencyRate)
    }

    fun updateList(updatedList: ArrayMap<String, Currency>) {
        // create the list anew if empty, otherwise update the current entries
        if (ratesList.size > 0) {
            ratesList.map { listItem ->
                if (listItem is CurrencyItem) {
                    updatedList[listItem.currency.code]?.apply {
                        listItem.currency.rate = this.rate
                    }
                }
            }
        } else {
            Timber.d("UI (list) updated with new rates")
            ratesList.add(HeaderItem(context.getString(R.string.title_rates)))
            updatedList.map {
                ratesList.add(CurrencyItem(it.value))
            }
        }

        notifyDataSetChanged()
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

    private fun checkAndConvert(position: Int, value: String) {
        // Only run conversion for first responder
        if (position == Configuration.FIRST_RESPONDER_POSITION) {
            val firstItem = ratesList[position] as CurrencyItem

            if (firstItem.displayedCurrencyRate != value) {
                firstItem.displayedCurrencyRate = value
                val baseRate = firstItem.currency.rate
                val valueDouble = value.toDouble()

                for (index in Configuration.FIRST_RESPONDER_POSITION + 1 until ratesList.size) {
                    (ratesList[index] as CurrencyItem).apply {
                        this.displayedCurrencyRate = String.format("%.4f", (this.currency.rate / baseRate) * valueDouble)
                    }
                }

                notifyItemRangeChanged(Configuration.FIRST_RESPONDER_POSITION+1, ratesList.size)
            }
        }
    }
}