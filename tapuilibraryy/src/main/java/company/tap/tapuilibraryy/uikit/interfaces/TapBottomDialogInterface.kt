package company.tap.tapuilibraryy.uikit.interfaces

/**
 * Created by AhlaamK on 6/3/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
interface TapBottomDialogInterface {
    fun onShow() {}
    fun onSlide(slideOffset: Float) {}
    fun onStateChanged(newState: Int) {}
    fun onDismiss() {}
}