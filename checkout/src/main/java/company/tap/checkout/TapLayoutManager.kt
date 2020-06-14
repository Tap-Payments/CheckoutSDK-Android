package company.tap.checkout

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import company.tap.checkout.viewholders.BusinessViewHolder
import company.tap.checkout.viewholders.TapBaseViewHolder
import company.tap.checkout.viewholders.ViewHolderType

/**
 *
 * Created by Mario Gamal on 6/14/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TapLayoutManager(private val context: Context?) {

    private val viewHolders = ArrayList<TapBaseViewHolder>()

    fun initViewHolders() {
        val headerView = BusinessViewHolder(context)
        viewHolders.add(headerView)
    }

    fun getLayout(viewHolderTypes: ArrayList<ViewHolderType>): View {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        for (type in viewHolderTypes) {
            getViewByType(type)?.let {
                layout.addView(it)
            }
        }
        return layout
    }

    private fun getViewByType(type: ViewHolderType): View? {
        for (viewHolder in viewHolders) {
            if (viewHolder.type == type)
                return viewHolder.view
        }
        return null
    }
}