package company.tap.checkoutsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import company.tap.checkout.TapCheckoutFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        launchSDK.setOnClickListener {
            val tapCheckoutFragment = TapCheckoutFragment()
            tapCheckoutFragment.show(supportFragmentManager, null)
        }
    }
}
