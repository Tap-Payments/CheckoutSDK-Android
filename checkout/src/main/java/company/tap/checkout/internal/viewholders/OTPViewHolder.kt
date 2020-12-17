package company.tap.checkout.internal.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType
import company.tap.tapuilibrary.uikit.organisms.OTPView

/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class OTPViewHolder(context: Context) : TapBaseViewHolder {
    override val view: View = LayoutInflater.from(context).inflate(R.layout.otpview_layout, null)


    override val type = SectionType.OTP_VIEW

    val otpView: OTPView

    init {
        otpView = view.findViewById(R.id.otpView)
        bindViewComponents()


    }

    override fun bindViewComponents() {

    }

    fun setMobileOtpView() {
        otpView.visibility = View.VISIBLE
        otpView.changePhoneCardView.visibility = View.GONE
        otpView.otpSentConstraintGoPay.visibility = View.VISIBLE
        otpView.otpSentTextNormalPay.visibility = View.VISIBLE
        otpView.mobileNumberTextNormalPay.visibility = View.VISIBLE

    }


}