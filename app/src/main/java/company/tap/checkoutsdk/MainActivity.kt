package company.tap.checkoutsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ui.InlineViewCallback
import company.tap.checkout.internal.dummygener.JsonResponseDummy1
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.checkout.open.BlankFragment
import company.tap.checkout.open.TapCheckoutFragment
import company.tap.checkout.open.controller.SDKSession
import company.tap.tapuilibrary.themekit.ThemeManager


import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() ,InlineViewCallback,BaseLayoutManager{
    var sdkSession:SDKSession= SDKSession()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        launchSDK.setOnClickListener {
            ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme,"lighttheme")
            println("on clicked main")
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            //   SDKSession().startSDK(false)
           // sdkSession.startSDK(supportFragmentManager,this)
          /*  val tapCheckoutFragment = BlankFragment()
            tapCheckoutFragment.show(supportFragmentManager, null)*/
            BlankFragment.newInstance(this,this)
           // TapCheckoutFragment.newInstance(this)

        }
    }

    override fun onScanCardFailed(e: Exception?) {
    }

    override fun onScanCardFinished(card: Card?, cardImage: ByteArray?) {
    }

    override fun displayStartupLayout(enabledSections: ArrayList<SectionType>) {

    }

    override fun displayGoPayLogin() {

    }

    override fun displayGoPay() {

    }

    override fun controlCurrency(display: Boolean) {

    }

    override fun displayOTP(otpMobile: String) {

    }

    override fun displayRedirect(url: String) {

    }

    override fun displaySaveCardOptions() {

    }

    override fun getDatafromAPI(dummyResponse1: JsonResponseDummy1) {
        println("dummyResponse1 in main"+dummyResponse1)
    }
}
