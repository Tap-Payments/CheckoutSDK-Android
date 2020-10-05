package company.tap.checkout.viewholders

import android.content.Context
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import company.tap.checkout.R
import company.tap.checkout.enums.SectionType
import company.tap.checkout.utils.AnimationEngine
import company.tap.checkout.utils.AnimationEngine.Type.SLIDE
import company.tap.tapuilibrary.uikit.adapters.context
import company.tap.tapuilibrary.uikit.datasource.GoPayLoginDataSource
import company.tap.tapuilibrary.uikit.interfaces.GoPayLoginInterface
import company.tap.tapuilibrary.uikit.organisms.GoPayLoginInput
import company.tap.tapuilibrary.uikit.organisms.GoPayPasswordInput


/**
 *
 * Created by Mario Gamal on 7/29/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class GoPayViewHolder(context: Context, private val bottomSheet: FrameLayout) : TapBaseViewHolder,
    GoPayLoginInterface {
    override val view: View = LayoutInflater.from(context).inflate(R.layout.gopay_layout, null)

    override val type = SectionType.GOPAY_SIGNIN

    private val goPayLoginInput: GoPayLoginInput
    private val goPayPasswordInput: GoPayPasswordInput

    init {
        goPayLoginInput = view.findViewById(R.id.gopay_login_input)
        goPayPasswordInput = view.findViewById(R.id.goPay_password)
        bindViewComponents()
    }

    override fun bindViewComponents() {
        goPayLoginInput.changeDataSource(GoPayLoginDataSource())
        goPayLoginInput.setLoginInterface(this)

        goPayPasswordInput.setLoginInterface(this, goPayLoginInput.textInput.text.toString())

    }

    override fun onChangeClicked() {
        AnimationEngine.applyTransition(bottomSheet, SLIDE)
        goPayLoginInput.visibility = View.VISIBLE
        goPayPasswordInput.visibility = View.GONE
    }

    override fun onEmailValidated() {
        AnimationEngine.applyTransition(bottomSheet, SLIDE)
        goPayLoginInput.visibility = View.GONE
        goPayPasswordInput.visibility = View.VISIBLE
       goPayPasswordInput.setLoginInterface(this,goPayLoginInput.textInput.text.toString())


    }

    override fun onPhoneValidated() {
        //Todo open otp view here
        AnimationEngine.applyTransition(bottomSheet, SLIDE)
        Toast.makeText(context,"OTP view to slide up",Toast.LENGTH_SHORT).show()
    }


}