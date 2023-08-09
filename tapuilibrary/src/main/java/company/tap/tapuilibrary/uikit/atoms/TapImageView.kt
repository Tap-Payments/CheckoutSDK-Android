package company.tap.tapuilibrary.uikit.atoms

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.themekit.theme.ImageViewTheme
import company.tap.tapuilibrary.uikit.interfaces.TapView


/**
 * Created by AhlaamK on 4/29/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

/** Displays image resources, for example Bitmap or Drawable resources. TapImageView is also commonly used to apply
 * tints to an image and handle image scaling.
 * @param context The Context the view is running in, through which it can
 * access the current theme, resources, etc.
 * @param attributeSet The attributes of the XML Button tag being used to inflate the view.
 **/
open class TapImageView(context: Context?, attributeSet: AttributeSet?) : AppCompatImageView(context!!, attributeSet),
    TapView<ImageViewTheme> {


    /**
     * Set the configure the current theme. If null is provided then the default Theme is returned
     * on the next call
     * @param theme Theme to consume in the wrapper, a value of null resets the theme to the default
     **/
    override fun setTheme(theme: ImageViewTheme) {
        theme.imageResource?.let {setImageResource(R.drawable.tap_logo)  }
        theme.contentDescription?.let { contentDescription = "Tap Logo"
        }
    }
}