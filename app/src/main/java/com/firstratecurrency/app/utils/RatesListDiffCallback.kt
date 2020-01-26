package com.firstratecurrency.app.utils

import androidx.recyclerview.widget.DiffUtil
import com.firstratecurrency.app.data.model.Currency

class RatesListDiffCallback(private val oldList: ArrayList<Currency>, private val newList: ArrayList<Currency>):
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].code == newList[newItemPosition].code

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return oldItem.code == newItem.code &&
                oldItem.getCurrencyValue() == newItem.getCurrencyValue()
    }
}