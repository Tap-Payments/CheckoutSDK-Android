package company.tap.tapuilibraryy.uikit.views

import android.graphics.*
import android.graphics.Paint.Align
import android.graphics.drawable.Drawable


/**
 * Created by AhlaamK on 6/29/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
open class TextDrawable(text:String): Drawable() {
    private var text: String
    private var paint: Paint
    init {
        this.text = text
        paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 22f
        paint.isAntiAlias = true
        paint.isFakeBoldText = true
        paint.style = Paint.Style.FILL
        paint.textAlign = Align.CENTER
    }


    override fun draw(canvas: Canvas) {
        canvas.drawText(text, 0F, 0F, paint)

    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

}


