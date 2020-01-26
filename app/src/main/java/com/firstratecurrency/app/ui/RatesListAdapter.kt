package com.firstratecurrency.app.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.firstratecurrency.app.R
import com.firstratecurrency.app.data.model.Currency
import com.firstratecurrency.app.utils.RatesListDiffCallback
import com.mynameismidori.currencypicker.ExtendedCurrency
import kotlinx.android.synthetic.main.list_header.view.*
import kotlinx.android.synthetic.main.list_item_currency.view.*
import timber.log.Timber

class RatesListAdapter(context: Context, private val ratesChangeListener: RatesChangeListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface RatesChangeListener {
        fun onFirstResponderChange(position: Int)
        fun onRateValueChange(value: Double)
    }

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
        const val FIRST_RESPONDER_POSITION = 0
        const val TYPE_HEADER = 100
        const val TYPE_CURRENCY = 200
        val GLIDE_IMAGE_OPTIONS: RequestOptions =
            RequestOptions().placeholder(R.drawable.vd_flag)
                .error(R.drawable.vd_flag)
                .fitCenter()
                .transform(CircleCrop())
    }

    private val currenciesList: ArrayList<Currency> = arrayListOf()
    private val HEADER_TITLE = context.getString(R.string.title_rates)
    private var firstResponderRefValCache: Currency? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Configuration.TYPE_HEADER -> RatesHeaderViewHolder(inflater.inflate(R.layout.list_header, parent, false))
            else -> RatesListViewHolder(inflater.inflate(R.layout.list_item_currency, parent, false), this::makeFirstResponder, this::checkAndConvert)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return Configuration.TYPE_CURRENCY
//        return when (position) {
//            0 -> Configuration.TYPE_HEADER
//            else -> Configuration.TYPE_CURRENCY
//        }
    }

    override fun getItemCount(): Int = currenciesList.size//if (ratesList.isEmpty()) 0 else ratesList.size + 1 // Header included

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RatesHeaderViewHolder -> displayHeader(holder)
            is RatesListViewHolder -> displayCurrencyItem(holder, position) // Header excluded
        }
    }

    private fun displayHeader(holder: RatesHeaderViewHolder) {
        holder.view.listHeaderText.text = HEADER_TITLE
    }

    private fun displayCurrencyItem(holder: RatesListViewHolder, position: Int) {
        val currency = currenciesList[position]

        val extendedCurrency = ExtendedCurrency.getCurrencyByISO(currency.code)
        holder.view.countryCurrency.text = extendedCurrency.name
        holder.view.currencyCode.text = currency.code
        Glide.with(holder.view).load(extendedCurrency.flag)
            .apply(Configuration.GLIDE_IMAGE_OPTIONS)
            .into(holder.view.countryFlag)

        val displayValue = String.format("%.4f", currency.getCurrencyValue())
        holder.view.currencyExchangeEntry.setText(displayValue)
    }

    fun updateList(updatedList: ArrayList<Currency>) {
        // if list is empty, simply update rather than run a diff
        if (currenciesList.isEmpty()) {
            currenciesList.addAll(updatedList)
            notifyDataSetChanged()
        } else {
            val diffCallback = RatesListDiffCallback(currenciesList, updatedList)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            firstResponderRefValCache = currenciesList[0]
            currenciesList.clear()
            currenciesList.addAll(updatedList)
            diffResult.dispatchUpdatesTo(this)
        }
    }

    private fun makeFirstResponder(position: Int) {
        // Move to top of row if not already
        if (position > Configuration.FIRST_RESPONDER_POSITION) {
            ratesChangeListener.onFirstResponderChange(position)
        }
    }

    private fun checkAndConvert(position: Int, value: String) {
        // Only run conversion for first responder
        if (position == 0) {
            val firstResponder = currenciesList[0]
            val currentValue = firstResponder.getCurrencyValue()
            val enteredValue = value.toDouble()
            if (currentValue != enteredValue) {
                // update the changed position data in the list so that the diffUtil
                // doesn't register a change
                firstResponder.refValue = enteredValue
                ratesChangeListener.onRateValueChange(enteredValue)
            }
        }
    }
}