package company.tap.tapuilibrary.uikit.organisms

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import company.tap.tapuilibrary.R

/**
 * Created by AhlaamK on 8/24/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class GoPayLayout (context: Context?, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {
    init {
        inflate(context, R.layout.gopay_layout, this)
    }
}