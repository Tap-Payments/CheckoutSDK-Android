package company.tap.checkoutsdk.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkoutsdk.R
import company.tap.checkoutsdk.adapters.CustomerAdapter
import company.tap.checkoutsdk.viewmodels.CustomerViewModel

/**
 * Created by AhlaamK on 9/6/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
class CustomerViewHolder(itemView: View, listener: CustomerAdapter.OnClickListenerInterface?) :
    RecyclerView.ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.customer_name)
    private val email: TextView = itemView.findViewById(R.id.customer_email)
    private val mobile: TextView = itemView.findViewById(R.id.customer_mobile)
    private val tick_mark: ImageView?= itemView.findViewById(R.id.tick_mark)
    private val listenerInterface: CustomerAdapter.OnClickListenerInterface? = listener
    private var viewModel: CustomerViewModel? = null
    fun bindData(viewModel: CustomerViewModel) {
        this.viewModel = viewModel
        name.text = viewModel.getName()
        email.text = viewModel.getEmail()
        mobile.text = viewModel.getMobile()
    }

    init {
        val customer_container: ConstraintLayout = itemView.findViewById(R.id.customer_container)
        customer_container.setOnClickListener { v: View? ->
            listenerInterface?.onClick(viewModel)


        }
    }



}