package company.tap.tapuilibrary.uikit.interfaces

/**
 * Created by OlaMonir on 7/22/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

interface OnCardSelectedActionListener {
    fun onCardSelectedAction(isSelected:Boolean)
    fun onDeleteIconClicked(stopAnimation:Boolean, itemId : Int)
}