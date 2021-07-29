package company.tap.checkout.internal.interfaces
/**
 * Created by OlaMonir on 9/12/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

interface OnCardSelectedActionListener {
    fun onCardSelectedAction(isSelected:Boolean , savedCardsModel : Any)
    fun onDeleteIconClicked(stopAnimation:Boolean, itemId : Int,cardId : String)
    fun onGoPayLogoutClicked(isClicked:Boolean)
    fun onEditClicked(isClicked:Boolean)
}