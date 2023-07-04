package company.tap.checkout.internal.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import company.tap.checkout.internal.viewholders.BusinessViewHolder

class BusinessViewHolderViewModel() : ViewModel() {
    lateinit var contextBusiness: Context

    fun getContext(): Context {
        return this.contextBusiness
    }

    fun setContext(context: Context) {
        this.contextBusiness = context
    }


    private var businessViewHolder: BusinessViewHolder = BusinessViewHolder(getContext())


    fun getBusinessViewHolder() = businessViewHolder
}