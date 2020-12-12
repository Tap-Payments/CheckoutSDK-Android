package company.tap.checkoutsdk


import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ui.InlineViewCallback
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.open.CheckoutFragment


import company.tap.checkout.open.controller.SDKSession
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.models.DialogConfigurations
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog.Companion.TAG


class MainActivity : AppCompatActivity() ,InlineViewCallback{
    var sdkSession:SDKSession= SDKSession()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme, "lighttheme")
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

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
        TODO("Not yet implemented")
    }

    override fun onScanCardFinished(card: Card?, cardImage: ByteArray?) {
        TODO("Not yet implemented")
    }
}




