package company.tap.checkout.internal.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import company.tap.checkout.internal.viewholders.BusinessViewHolder

class BusinessViewHolderViewModel(context: Context) : ViewModel() {



    private var businessViewHolder: BusinessViewHolder = BusinessViewHolder(context)


    fun getBusinessViewHolder() = businessViewHolder
}