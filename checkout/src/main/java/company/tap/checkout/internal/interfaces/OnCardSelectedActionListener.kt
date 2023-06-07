package company.tap.checkout.internal.interfaces

import android.view.View
import android.view.ViewGroup
import company.tap.checkout.internal.api.models.PaymentOption
import company.tap.checkout.internal.api.models.SavedCard

/**
 * Created by OlaMonir on 9/12/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

interface OnCardSelectedActionListener {
    fun onCardSelectedAction(isSelected:Boolean , savedCardsModel : Any?)

    fun onDisabledChipSelected(paymentOption: PaymentOption, itemView: Int)
    fun onDeleteIconClicked(
        stopAnimation: Boolean,
        itemId: Int,
        cardId: String,
        maskedCardNumber: String,
        arrayListSavedCardSize: ArrayList<SavedCard>,
        selectedViewToBeDeleted: ViewGroup,
        viewToBeBlurr: View,
        position: Int
    )
    fun onGoPayLogoutClicked(isClicked:Boolean)

    fun removePaymentInlineShrinkageAndDimmed()
}