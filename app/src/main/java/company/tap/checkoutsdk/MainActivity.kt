package company.tap.checkoutsdk


import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ui.InlineViewCallback
import company.tap.checkout.open.CheckoutTapFragmentt


import company.tap.checkout.open.controller.SDKSession
import company.tap.tapuilibrary.themekit.ThemeManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() ,InlineViewCallback{
    var sdkSession:SDKSession= SDKSession()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        launchSDK.setOnClickListener {

            ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme,"lighttheme")

           // window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            //   SDKSession().startSDK(false)
            println("on click is called in main")
          //  sdkSession.startSDK(supportFragmentManager,this)



            ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme, "lighttheme")
            println("on clicked main")
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            //   SDKSession().startSDK(false)
            // sdkSession.startSDK(supportFragmentManager,this)
            /*  val tapCheckoutFragment = BlankFragment()
            tapCheckoutFragment.show(supportFragmentManager, null)*/
            // BlankFragment.newInstance(this,this)
            // TapCheckoutFragment.newInstance(this)
           if (savedInstanceState == null) { // initial transaction should be wrapped like this
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameContainer, CheckoutTapFragmentt())
                    .commit()
            }
         //  val modalBottomSheet = CheckoutTapFragment()
          //  modalBottomSheet.arguments = getArguments()
         //   modalBottomSheet.show(supportFragmentManager, TapBottomSheetDialog.TAG)
          /*  BlankFragment().apply {
          show(supportFragmentManager, tag)*/
      }
        }


    override fun onScanCardFailed(e: Exception?) {
        TODO("Not yet implemented")
    }

    override fun onScanCardFinished(card: Card?, cardImage: ByteArray?) {
        TODO("Not yet implemented")
    }
}




