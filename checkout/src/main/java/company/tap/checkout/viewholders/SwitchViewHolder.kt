package company.tap.checkout.viewholders

import android.content.Context
import android.os.StrictMode
import com.google.gson.Gson
import company.tap.cardbusinesskit.testmodels.DummyResp
import company.tap.checkout.apiresponse.getJsonDataFromAsset
import company.tap.checkout.enums.SectionType
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.uikit.datasource.TapSwitchDataSource
import company.tap.tapuilibrary.uikit.views.TapCardSwitch

/**
 *
 * Created by Mario Gamal on 7/29/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class SwitchViewHolder(private val context: Context) : TapBaseViewHolder {

    override val view = TapCardSwitch(context)

    override val type = SectionType.SAVE_CARD
    private var dummyInitApiResponse: DummyResp? = null
    init {
        val jsonFileString = this.let {
            getJsonDataFromAsset(
                it.context,
                "dummyapiresponse.json"
            )
        }
        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val gson = Gson()
        dummyInitApiResponse = gson.fromJson(
            jsonFileString,
            DummyResp::class.java
        )
        println("api response in payment input ${dummyInitApiResponse?.payment_methods} ")

        bindViewComponents()
    }

    override fun bindViewComponents() {
        //ToDO data will be set based on API response
        view.setSwitchDataSource(
            TapSwitchDataSource(
                switchSave = LocalizationManager.getValue("cardSaveLabel", "TapCardInputKit"),
                switchSaveMerchantCheckout = "Save for "+ dummyInitApiResponse?.merchant?.name +" Checkouts",
                switchSavegoPayCheckout = "By enabling goPay, your mobile number will be saved with Tap Payments to get faster and more secure checkouts in multiple apps and websites.",
                savegoPayText = "Save for goPay Checkouts",
                alertgoPaySignup = "Please check your email or SMS’s in order to complete the goPay Checkout signup process."
            )
        )
    }


}

