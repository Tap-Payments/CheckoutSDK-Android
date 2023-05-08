package company.tap.tapuilibrary.uikit.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import android.widget.LinearLayout
import kotlin.math.roundToInt


/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
object  BlurBuilder {
    private val BITMAP_SCALE = 0.4f
    private val BLUR_RADIUS = 7.5f

    fun blur(v: View): Bitmap? {
        return getScreenshot(v)?.let { blur(v.context, it) }
    }

    fun blur(ctx: Context?, image: Bitmap): Bitmap? {
        val width = (image.width * BITMAP_SCALE).roundToInt()
        val height = (image.height * BITMAP_SCALE).roundToInt()
        val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)
        val rs = RenderScript.create(ctx)
        val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
        theIntrinsic.setRadius(BLUR_RADIUS)
        theIntrinsic.setInput(tmpIn)
        theIntrinsic.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)
        return outputBitmap
    }
    private fun getScreenshot(v: View): Bitmap? {
        val b = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.draw(c)
        return b
    }


}