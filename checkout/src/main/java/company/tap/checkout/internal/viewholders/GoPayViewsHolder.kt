package company.tap.checkout.internal.viewholders

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayouttManager
import company.tap.tapuilibrary.uikit.datasource.GoPayLoginDataSource
import company.tap.tapuilibrary.uikit.enums.GoPayLoginMethod
import company.tap.tapuilibrary.uikit.interfaces.GoPayLoginInterface
import company.tap.tapuilibrary.uikit.interfaces.OnCardSelectedActionListener
import company.tap.tapuilibrary.uikit.interfaces.OpenOTPInterface
import company.tap.tapuilibrary.uikit.interfaces.OtpButtonConfirmationInterface
import company.tap.tapuilibrary.uikit.organisms.GoPayLoginInput
import company.tap.tapuilibrary.uikit.organisms.GoPayPasswordInput
import kotlinx.android.synthetic.main.otpview_layout.view.*


/**
 *
 * Created by Mario Gamal on 7/29/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class GoPayViewsHolder(private val context: Context, private val baseLayouttManager: BaseLayouttManager?=null) : TapBaseViewHolder, OpenOTPInterface,GoPayLoginInterface,
    OtpButtonConfirmationInterface, OnCardSelectedActionListener {

    override val view: View = LayoutInflater.from(context).inflate(R.layout.gopay_layout, null)

    override val type = SectionType.GOPAY_SIGNIN

     val goPayLoginInput: GoPayLoginInput
     val goPayPasswordInput: GoPayPasswordInput
    private  var otpViewHolder: OTPViewHolder = OTPViewHolder(context)

    init {
        goPayLoginInput = view.findViewById(R.id.gopay_login_input)
        goPayPasswordInput = view.findViewById(R.id.goPay_password)

        bindViewComponents()
    }

    override fun bindViewComponents() {
        goPayLoginInput.changeDataSource(GoPayLoginDataSource())
        goPayLoginInput.setLoginInterface(this)
        goPayLoginInput.setOpenOTPInterface(this)
        otpViewHolder.view.otpView?.setOtpButtonConfirmationInterface(this)
        goPayPasswordInput.setLoginInterface(this, goPayLoginInput.textInput.text.toString())
    }

    override fun onChangeClicked() {
      goPayLoginInput.visibility = View.VISIBLE
       goPayPasswordInput.visibility = View.GONE
        otpViewHolder.view.otpView?.visibility = View.GONE
    }

    override fun onEmailValidated() {
        goPayLoginInput.visibility = View.GONE
        goPayPasswordInput.visibility = View.VISIBLE
        otpViewHolder.view.otpView?.visibility = View.GONE
        goPayPasswordInput.setLoginInterface(this, goPayLoginInput.textInput?.text.toString())


    }

    override fun onPhoneValidated() {
        goPayPasswordInput.visibility = View.GONE
        goPayLoginInput.visibility = View.GONE
        println("viewhodler value"+otpViewHolder+"\n"+"view value"+view+"\n"+"otpview is "+otpViewHolder.view.otpView)

        if(otpViewHolder.view.otpView!=null){
            otpViewHolder.view.otpView.visibility = View.VISIBLE
            otpViewHolder.view.otpView.changePhoneCardView?.visibility = View.VISIBLE
            println(" you clicked for otp!!!")
            baseLayouttManager?.displayOTPV( otpViewHolder.view.otpView?.otpViewInput1?.text.toString(),otpViewHolder.otpView.isValidOTP)
           // baseLayoutManager?.displayGoPay()

        }

        /*  goPayLoginInput.actionButton.setOnClickListener {
          otpViewHolder.view.otpView.visibility = View.VISIBLE
            println(" you clicled for otp")
        }*/
    }

    override fun getPhoneNumber(phoneNumber: String, countryCode: String, maskedValue: String) {
        println("get phn number$phoneNumber\ncountryCode ia$countryCode\nmasked value$maskedValue")
        if(otpViewHolder.view.otpView!=null)
        otpViewHolder.view.otpView?.mobileNumberText?.text = "+${countryCode} $maskedValue"
    }

    override fun onChangePhoneClicked() {
        println("onChangePhoneClicked is ()")
        goPayLoginInput.visibility = View.VISIBLE
        goPayLoginInput.changeDataSource(GoPayLoginDataSource())
        goPayLoginInput.inputType = GoPayLoginMethod.EMAIL
        println("otpViewHolder.view.otpView gopay is ()"+otpViewHolder.view.otpView)
        if(otpViewHolder.view.otpView!=null){
            otpViewHolder.view.otpView?.visibility = View.GONE
            otpViewHolder.view.otpView?.changePhoneCardView?.visibility = View.GONE
            println("otpViewHolder.inside called")

        }

    }



  /*  override fun onCardSelectedAction(isSelected: Boolean, typeCardView: String) {

    }
*/
    override fun onCardSelectedAction(isSelected: Boolean) {

    }


    override fun onDeleteIconClicked(stopAnimation: Boolean, itemId: Int) {

    }

    override fun onOtpButtonConfirmationClick(otpNumber: String): Boolean {
        println("otpNumber is $otpNumber")
        Log.d("isValidOTP1" ,(otpNumber == "111111").toString() )
        // GoPayLoginHolder1(context,this,baseLayoutManager).view
        if(otpNumber== "111111"){
            GoPaySavedCardHolder(context,this,baseLayouttManager).view

        }
        return otpNumber == "111111"
    }

}