package company.tap.checkout.utils

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import company.tap.checkout.R
import company.tap.thememanager.manager.ThemeManager

object CurrentTheme  {
    private  var currentTheme :Int?= null


     fun initAppTheme(theme: Int, context: Context) {
        currentTheme = theme
//        ThemeManager.loadTapTheme(this, "https://kar-tempo.s3.ap-south-1.amazonaws.com/theme-tap.json")
        ThemeManager.loadTapTheme(context.resources, theme)
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