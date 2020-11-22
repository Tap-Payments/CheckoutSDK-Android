package company.tap.checkoutsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ui.InlineViewCallback
import company.tap.checkout.open.controller.SDKSession
import company.tap.tapuilibrary.themekit.ThemeManager


import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() ,InlineViewCallback{
    var sdkSession:SDKSession= SDKSession()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        launchSDK.setOnClickListener {
            ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme)

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
         //   SDKSession().startSDK(false)
            sdkSession.startSDK(supportFragmentManager,this)

        }
    }

    override fun onScanCardFailed(e: Exception?) {
    }

    override fun onScanCardFinished(card: Card?, cardImage: ByteArray?) {
    }
}
