package company.tap.checkout.internal.interfaces

import company.tap.checkout.internal.dummygener.SavedCards

/**
 * Created by OlaMonir on 9/12/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

interface OnCardSelectedActionListener {
    fun onCardSelectedAction(isSelected:Boolean , savedCardsModel : Any)
    fun onDeleteIconClicked(stopAnimation:Boolean, itemId : Int)
    fun ongoPayLogoutClicked(isClicked:Boolean)
}