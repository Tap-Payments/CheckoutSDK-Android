package company.tap.tapuilibraryy.uikit.datasource

import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt

/**
 *
 * Created by Mario Gamal on 6/25/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
data class AnimationDataSource(
    val fromHeight: Int?= null,
    val toHeight: Int?= null,

    val fromWidth: Int?= null,
    val toWidth: Int?= null,

    val fromCorners: Float? = null,
    val toCorners: Float? = null,
    val elevation: Float? = null,

    @ColorInt
    val fromColor: Int?= null,

    @ColorInt
    val toColor: Int?= null,

    val duration: Int? = null,
    val background: GradientDrawable? = null

)