package company.tap.checkoutsdk.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkoutsdk.R
import company.tap.checkoutsdk.adapters.CustomerAdapter
import company.tap.checkoutsdk.adapters.ItemsAdapter
import company.tap.checkoutsdk.viewmodels.CustomerViewModel
import company.tap.checkoutsdk.viewmodels.PaymentItemViewModel

/**
 * Created by AhlaamK on 9/6/21.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
class ItemsViewHolder(itemView: View, listener: ItemsAdapter.OnClickListenerInterface?) :
    RecyclerView.ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.item_name)
    private val description: TextView = itemView.findViewById(R.id.item_description)
    private val quantity: TextView = itemView.findViewById(R.id.item_quantity)
    private val amountPerunit: TextView = itemView.findViewById(R.id.item_quantity)
    private val totalAmount: TextView = itemView.findViewById(R.id.item_quantity)
    private val tick_mark: ImageView?= itemView.findViewById(R.id.tick_mark)
    private val listenerInterface: ItemsAdapter.OnClickListenerInterface? = listener
    private var viewModel: PaymentItemViewModel? = null
    fun bindData(viewModel: PaymentItemViewModel) {
        this.viewModel = viewModel
        name.text = viewModel.getItemsName()
        description.text = viewModel.getItemDescription()
        quantity.text = viewModel.getitemQuantity().toString()
        amountPerunit.text = viewModel.getPricePUnit().toString()
        totalAmount.text = viewModel.getitemTotalPrice().toString()
    }

    init {
        val customer_container: ConstraintLayout = itemView.findViewById(R.id.customer_container)
        customer_container.setOnClickListener { v: View? ->
            listenerInterface?.onClick(viewModel)


        }
    }



}