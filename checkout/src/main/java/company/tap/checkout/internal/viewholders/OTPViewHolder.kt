package company.tap.checkout.internal.viewholders

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayouttManager
import company.tap.tapuilibrary.uikit.datasource.GoPayLoginDataSource
import company.tap.tapuilibrary.uikit.enums.GoPayLoginMethod
import company.tap.tapuilibrary.uikit.interfaces.OpenOTPInterface
import company.tap.tapuilibrary.uikit.interfaces.OtpButtonConfirmationInterface
import company.tap.tapuilibrary.uikit.organisms.GoPayLoginInput
import company.tap.tapuilibrary.uikit.organisms.OTPView

/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class OTPViewHolder (context: Context, private val baseLayouttManager: BaseLayouttManager) : TapBaseViewHolder{
    override val view: View = LayoutInflater.from(context).inflate(R.layout.otpview_layout, null)


    override val type = SectionType.OTP_VIEW

    val otpView:OTPView
        init {
        otpView = view.findViewById(R.id.otpView)
        bindViewComponents()


        }

    override fun bindViewComponents() {

    }




}