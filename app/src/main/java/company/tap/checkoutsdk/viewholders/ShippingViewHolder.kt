package company.tap.checkoutsdk.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkoutsdk.R
import company.tap.checkoutsdk.adapters.ShippingAdapter
import company.tap.checkoutsdk.viewmodels.ShippingViewModel

/**
 * Created by AhlaamK on 9/12/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
class ShippingViewHolder(itemView: View, listener: ShippingAdapter.OnClickListenerInterface?) :
    RecyclerView.ViewHolder(itemView) {
    private val shippingName: TextView = itemView.findViewById(R.id.shipping_name)
    private val shippingDescription: TextView = itemView.findViewById(R.id.shipping_description)
    private val shippingAmount: TextView = itemView.findViewById(R.id.shipping_amount)
    private val right_arrow: ImageView? = null
    private val listenerInterface: ShippingAdapter.OnClickListenerInterface? = listener
    private var viewModel: ShippingViewModel? = null
    fun bindData(viewModel: ShippingViewModel) {
        this.viewModel = viewModel
        shippingName.text = viewModel.getshippingName()
        shippingDescription.text = viewModel.getshippingDecsription()
        shippingAmount.text = viewModel.getshippingAmount()


    }

    init {
        val customer_container: ConstraintLayout = itemView.findViewById(R.id.shipping_container)
        customer_container.setOnClickListener { v: View? ->
            listenerInterface?.onClick(viewModel)
        }

    }
}