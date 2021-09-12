package company.tap.checkoutsdk.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkoutsdk.viewholders.CustomerViewHolder
import company.tap.checkoutsdk.R
import company.tap.checkoutsdk.viewholders.ShippingViewHolder
import company.tap.checkoutsdk.viewmodels.CustomerViewModel
import company.tap.checkoutsdk.viewmodels.ShippingViewModel

/**
 * Created by AhlaamK on 9/12/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
class ShippingAdapter (shippings: List<ShippingViewModel>, listener: OnClickListenerInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var shipping: List<ShippingViewModel> = shippings
    var listener: OnClickListenerInterface = listener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ShippingViewHolder(view, listener)
    }


    private fun getCustomer(index: Int): ShippingViewModel {
        return shipping[index]
    }

    override fun getItemCount(): Int {
        return shipping.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.shipping_layout
    }

    interface OnClickListenerInterface {
        fun onClick(shippingViewModel: ShippingViewModel?)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ShippingViewHolder).bindData(getCustomer(position))
    }

}