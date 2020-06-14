package company.tap.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import company.tap.checkout.viewholders.BusinessViewHolder
import company.tap.checkout.viewholders.ViewHolderType
import company.tap.tapuilibrary.views.TapBottomSheetDialog

/**
 *
 * Created by Mario Gamal on 6/9/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TapCheckoutFragment : TapBottomSheetDialog() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.checkout_sdk_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = TapLayoutManager(context)
        layoutManager.initViewHolders()

        val enabledSections = ArrayList<ViewHolderType>()
        enabledSections.add(ViewHolderType.BUSINESS)

        val checkoutLayout = view.findViewById<LinearLayout>(R.id.sdk_container)
        checkoutLayout.addView(layoutManager.getLayout(enabledSections))
    }
}