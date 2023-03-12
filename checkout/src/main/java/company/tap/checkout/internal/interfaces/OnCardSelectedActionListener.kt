package company.tap.checkout.internal.interfaces

import android.view.View
import com.google.android.gms.wallet.PaymentsClient
import company.tap.checkout.internal.api.models.SavedCard

/**
 * Created by OlaMonir on 9/12/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

interface OnCardSelectedActionListener {
    fun onCardSelectedAction(isSelected:Boolean , savedCardsModel : Any?)
    fun onDeleteIconClicked(
        stopAnimation: Boolean,
        itemId: Int,
        cardId: String,
        maskedCardNumber: String,
        arrayListSavedCardSize: ArrayList<SavedCard>
    )
    fun onGoPayLogoutClicked(isClicked:Boolean)
    fun onEditClicked(isClicked:Boolean)
    fun onGooglePayClicked(isClicked: Boolean)
}