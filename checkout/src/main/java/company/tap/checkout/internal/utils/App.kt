package company.tap.checkout.internal.utils

import android.app.Application
import com.bugfender.sdk.Bugfender

import com.bugfender.android.BuildConfig


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Bugfender.init(this, "hAXrruJ5j83CyBAKl3nGsGnnoxGs9nbc", BuildConfig.DEBUG)
        Bugfender.enableCrashReporting()
        Bugfender.enableUIEventLogging(this)
        //Bugfender.enableLogcatLogging() // optional, if you want logs automatically collected from logcat
    }
}