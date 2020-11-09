package company.tap.checkout.internal.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType
import company.tap.tapuilibrary.uikit.interfaces.OpenOTPInterface
import company.tap.tapuilibrary.uikit.interfaces.OtpButtonConfirmationInterface
import company.tap.tapuilibrary.uikit.organisms.GoPayLoginInput
import company.tap.tapuilibrary.uikit.organisms.OTPView

/**
 * Created by AhlaamK on 11/8/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class OTPViewHolder (context: Context) : TapBaseViewHolder
     {
    override val view: View = LayoutInflater.from(context).inflate(R.layout.otpview_layout, null)

    override val type = SectionType.OTP_VIEW
    val otpView: OTPView

        init {
        otpView = view.findViewById(R.id.otpView)
       // bindViewComponents()
    }

    override fun bindViewComponents() {

    }


}