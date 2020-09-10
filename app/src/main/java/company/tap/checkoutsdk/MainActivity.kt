package company.tap.checkoutsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import company.tap.checkout.TapCheckoutFragment
import company.tap.tapuilibrary.themekit.ThemeManager


import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        launchSDK.setOnClickListener {
            ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme)
            val tapCheckoutFragment = TapCheckoutFragment()
            tapCheckoutFragment.show(supportFragmentManager, null)
        }
    }
}
