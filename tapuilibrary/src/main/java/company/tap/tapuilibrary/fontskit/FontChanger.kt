package company.tap.tapuilibrary.fontskit;
import android.content.res.AssetManager
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


/**

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
 open class FontChanger(assets: AssetManager?, assetsFontFileName: String?) {
    private  var typeface: Typeface = Typeface.createFromAsset(assets, assetsFontFileName)


    fun replaceFonts(viewTree: ViewGroup) {
        var child: View?
        for (i in 0 until viewTree.childCount) {
            child = viewTree.getChildAt(i)
            if (child is ViewGroup) {
                // recursive call
                replaceFonts(child)
            } else if (child is TextView) {
                // base case
                child.typeface = typeface
            }
        }
    }

}