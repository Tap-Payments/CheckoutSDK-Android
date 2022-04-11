package company.tap.checkoutsdk.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkoutsdk.R
import company.tap.checkoutsdk.viewholders.TaxesViewHolder
import company.tap.checkoutsdk.viewmodels.TaxesViewModel

/**
 * Created by AhlaamK on 4/11/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
class TaxesAdapter (taxesList: List<TaxesViewModel>, listener: OnClickListenerInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var taxesList: List<TaxesViewModel> = taxesList
    var listener: OnClickListenerInterface = listener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return TaxesViewHolder(view, listener)
    }


    private fun getCustomer(index: Int): TaxesViewModel {
        return taxesList[index]
    }

    override fun getItemCount(): Int {
        return taxesList.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.shipping_layout
    }

    interface OnClickListenerInterface {
        fun onClick(taxesViewModel: TaxesViewModel?)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TaxesViewHolder).bindData(getCustomer(position))
    }

}