package company.tap.checkoutsdk


import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ui.InlineViewCallback
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.open.CheckoutFragment


import company.tap.checkout.open.controller.SDKSession
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.models.DialogConfigurations
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog.Companion.TAG
import java.util.*


class MainActivity : AppCompatActivity() ,InlineViewCallback{
    var sdkSession:SDKSession= SDKSession()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ThemeManager.currentTheme.isEmpty()) ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme, "lighttheme")
      //  ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme, "lighttheme")
        ThemeManager.loadTapTheme(resources, R.raw.defaultdarktheme, "darktheme")
      //  setTheme(R.style.AppThemeBlack)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setLocale(this, LocalizationManager.getLocale(this).language)
        LocalizationManager.loadTapLocale(resources, R.raw.lang)

        }
    fun openBottomSheet(view: View) {
        val modalBottomSheet = CheckoutFragment()
        modalBottomSheet.arguments = getArguments()
        modalBottomSheet.show(supportFragmentManager, TAG)
    }
    private fun getArguments(): Bundle {
        val arguments = Bundle()
        arguments.putFloatArray(DialogConfigurations.Corners, floatArrayOf(25f, 25f, 0f, 0f))
        arguments.putInt(DialogConfigurations.Color, Color.TRANSPARENT)
        arguments.putBoolean(DialogConfigurations.Cancelable, false)
        arguments.putFloat(DialogConfigurations.Dim, 0.75f)
        return arguments
    }

    override fun onScanCardFailed(e: Exception?) {

    }

    override fun onScanCardFinished(card: Card?, cardImage: ByteArray?) {

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menumain, menu)
        return true
    }

    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_dark -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            if (ThemeManager.currentTheme.isNotEmpty() && !(ThemeManager.currentTheme.contains("dark")))
                ThemeManager.loadTapTheme(resources, R.raw.defaultdarktheme, "defaultdarktheme")

            recreate()
            true
        }
        R.id.action_light -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark"))
            ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme, "defaultlighttheme")
            recreate()
            true
        }
        R.id.change_language -> {
            if (LocalizationManager.getLocale(this).language == "en") {
                LocalizationManager.setLocale(this, Locale("ar"))
                setLocale(this, "ar")
            } else if (LocalizationManager.getLocale(this).language == "ar") {
                LocalizationManager.setLocale(this, Locale("en"))
                setLocale(this, "en")
            }
            recreate()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    fun setLocale(activity: Activity, languageCode: String?) {
        println("languageCode is$languageCode")
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}




