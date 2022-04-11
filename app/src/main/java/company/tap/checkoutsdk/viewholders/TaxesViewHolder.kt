package company.tap.checkoutsdk.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkoutsdk.R
import company.tap.checkoutsdk.adapters.ShippingAdapter
import company.tap.checkoutsdk.adapters.TaxesAdapter
import company.tap.checkoutsdk.viewmodels.TaxesViewModel

/**
 * Created by AhlaamK on 4/11/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
class TaxesViewHolder (itemView: View, listener: TaxesAdapter.OnClickListenerInterface?) :
    RecyclerView.ViewHolder(itemView) {
    private val taxesName: TextView = itemView.findViewById(R.id.taxes_name)
    private val taxesDescription: TextView = itemView.findViewById(R.id.taxes_description)
    private val taxesAmount: TextView = itemView.findViewById(R.id.taxes_amount)
    private val right_arrow: ImageView? = null
    private val listenerInterface: TaxesAdapter.OnClickListenerInterface? = listener
    private var viewModel: TaxesViewModel? = null
    fun bindData(viewModel: TaxesViewModel) {
        this.viewModel = viewModel
        taxesName.text = viewModel.getTaxesName()
        taxesDescription.text = viewModel.getTaxesDecsription()
        taxesAmount.text = viewModel.getTaxesAmount()


    }

    init {
        val customer_container: ConstraintLayout = itemView.findViewById(R.id.taxes_container)
        customer_container.setOnClickListener { v: View? ->
            listenerInterface?.onClick(viewModel)
        }

    }
}