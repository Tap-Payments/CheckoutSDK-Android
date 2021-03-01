package company.tap.checkout.internal.utils

import android.content.Context
import company.tap.tapuilibrary.themekit.ThemeManager


object CurrentTheme {
    private var currentTheme: Int? = null


    fun initAppTheme(theme: Int, context: Context) {
        currentTheme = theme
//        ThemeManager.loadTapTheme(this, "https://kar-tempo.s3.ap-south-1.amazonaws.com/theme-tap.json")
        ThemeManager.loadTapTheme(context.resources, theme,"lighttheme")
    }
//    fun swapTheme(view: View?) {
//        if (currentTheme == R.raw.defaultdarktheme) {
//            initAppTheme(R.raw.defaultlighttheme)
//            Toast.makeText(appContext, "Theme switched to defaultlighttheme", Toast.LENGTH_SHORT).show()
//        }
//        else {
//            initAppTheme(R.raw.defaultdarktheme)
//            Toast.makeText(appContext, "Theme switched defaultdarktheme", Toast.LENGTH_SHORT).show()
//
//        }
//    }
}