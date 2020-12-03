package company.tap.checkoutsdk


import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ui.InlineViewCallback
<<<<<<< Updated upstream
import company.tap.checkout.open.TapCheckoutFragment
=======
import com.google.android.material.bottomsheet.BottomSheetDialog
import company.tap.checkout.internal.dummygener.JsonResponseDummy1
import company.tap.checkout.internal.interfaces.getDataInterface
import company.tap.checkout.open.CheckoutTapFragment
>>>>>>> Stashed changes
import company.tap.checkout.open.controller.SDKSession
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog
import kotlinx.android.synthetic.main.activity_main.*

<<<<<<< Updated upstream
class MainActivity : AppCompatActivity() ,InlineViewCallback{
=======
class MainActivity : AppCompatActivity() ,InlineViewCallback,getDataInterface{
>>>>>>> Stashed changes
    var sdkSession:SDKSession= SDKSession()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        launchSDK.setOnClickListener {
<<<<<<< Updated upstream
            ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme,"lighttheme")

           // window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            //   SDKSession().startSDK(false)
            println("on click is called in main")
          //  sdkSession.startSDK(supportFragmentManager,this)
            TapCheckoutFragment().apply {
                show(supportFragmentManager, tag)
            }

=======
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
                    .replace(R.id.frameContainer, CheckoutTapFragment())
                    .commit()
            }
         //  val modalBottomSheet = CheckoutTapFragment()
          //  modalBottomSheet.arguments = getArguments()
         //   modalBottomSheet.show(supportFragmentManager, TapBottomSheetDialog.TAG)
          /*  BlankFragment().apply {
          show(supportFragmentManager, tag)*/
      }
>>>>>>> Stashed changes
        }


    override fun onScanCardFailed(e: Exception?) {
        TODO("Not yet implemented")
    }

    override fun onScanCardFinished(card: Card?, cardImage: ByteArray?) {
        TODO("Not yet implemented")
    }
<<<<<<< Updated upstream
}
=======


    override fun getDatafromAPIS(dummyResponse1: JsonResponseDummy1) {
      println("dummyResponse1 in main" + dummyResponse1)
    }

}





>>>>>>> Stashed changes
