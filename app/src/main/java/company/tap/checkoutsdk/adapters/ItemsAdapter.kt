package company.tap.checkoutsdk.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkoutsdk.R
import company.tap.checkoutsdk.manager.SettingsManager
import company.tap.checkoutsdk.viewholders.ItemsViewHolder
import company.tap.checkoutsdk.viewmodels.PaymentItemViewModel

/**
 * Created by AhlaamK on 08/02/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
class ItemsAdapter(paymentList: List<PaymentItemViewModel>, listener: OnClickListenerInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<PaymentItemViewModel>
    var listener: OnClickListenerInterface
    private var item_shippable: TextView? = null
    private var item_currency: TextView? = null
    private var item_discount: TextView? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        item_currency = view.findViewById(R.id.item_currency)
        item_shippable = view.findViewById(R.id.item_shippable)
        item_discount = view.findViewById(R.id.item_discount)

        return ItemsViewHolder(view, listener)
    }


    private fun getPayitems(index: Int): PaymentItemViewModel {
        return items[index]
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.items_layout
    }

    interface OnClickListenerInterface {
        fun onClick(paymentItemViewModel: PaymentItemViewModel?)
    }

    init {
        this.items = paymentList
        this.listener = listener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemsViewHolder).bindData(getPayitems(position))
        item_currency?.setText("Items Currency is: "+ SettingsManager.getString("key_sdk_transaction_currency", "KWD"))
        item_shippable?.setText("Item is Shippable: "+(getPayitems(position).getItemIsRequireShip()))
        item_discount?.setText("Item Discount: "+(getPayitems(position).getAmountType()?.getNormalizedValue()))

    }
}