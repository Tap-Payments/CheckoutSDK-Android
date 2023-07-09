package company.tap.checkout.internal.viewholders

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.internal.enums.PaymentTypeEnum.*
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.uikit.datasource.GoPayLoginDataSource
import company.tap.tapuilibraryy.uikit.interfaces.GoPayLoginInterface
import company.tap.tapuilibraryy.uikit.interfaces.OpenOTPInterface
import company.tap.tapuilibraryy.uikit.interfaces.OtpButtonConfirmationInterface
import company.tap.tapuilibraryy.uikit.organisms.GoPayLoginInput
import company.tap.tapuilibraryy.uikit.organisms.GoPayPasswordInput
import company.tap.tapuilibraryy.uikit.views.TabAnimatedActionButton


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class GoPayViewsHolder(private val context: Context, private val baseLayoutManager: BaseLayoutManager?=null, private val otpViewHolder: OTPViewHolder?=null) : TapBaseViewHolder,GoPayLoginInterface,OtpButtonConfirmationInterface,
    OpenOTPInterface {

    override val view: View = LayoutInflater.from(context).inflate(R.layout.gopay_layout, null)

    override val type = SectionType.GOPAY_SIGN_IN

     val goPayLoginInput: GoPayLoginInput
     private val goPayPasswordInput: GoPayPasswordInput
     private val signInButton: TabAnimatedActionButton
     private lateinit var mobileNumber: String
      var goPayopened: Boolean= false

    init {
        goPayLoginInput = view.findViewById(R.id.gopay_login_input)
        goPayPasswordInput = view.findViewById(R.id.goPay_password)
        signInButton = view.findViewById(R.id.sigin_button)

        bindViewComponents()
    }

    override fun bindViewComponents() {
        goPayLoginInput.changeDataSource(GoPayLoginDataSource())
        goPayLoginInput.setLoginInterface(this)
        goPayLoginInput.setOpenOTPInterface(this)
      //  otpViewHolder.view.otpView?.setOtpButtonConfirmationInterface(this)
        goPayPasswordInput.setLoginInterface(this, goPayLoginInput.textInput.text.toString())
        signInButton.setOnClickListener {
            if(goPayPasswordInput.passwordTextInput.text.toString()=="12345678"){
                baseLayoutManager?.displayGoPay()
            }else{
                goPayPasswordInput.textInputLayout.error= LocalizationManager.getValue("GoPayLogin","Hints","password")
            }
        }
    }

    override fun onChangeClicked() {
      goPayLoginInput.visibility = View.VISIBLE
       goPayPasswordInput.visibility = View.GONE

    }

    override fun onEmailValidated() {
        //to be replace dummy check when validating via api
        if(goPayLoginInput.textInput.text.toString()=="a.k@g.com"){
            CustomUtils.showDialog(LocalizationManager.getValue("GoPayAlert", "Hints", "goPayTitle"),LocalizationManager.getValue("GoPayAlert", "Hints", "goPayemailalert"),context,null,baseLayoutManager,null,null,false)
        return
        }else {
            goPayLoginInput.visibility = View.GONE
            goPayPasswordInput.visibility = View.VISIBLE
         //   otpViewHolder.view.otpView?.visibility = View.GONE
            goPayPasswordInput.setLoginInterface(this, goPayLoginInput.textInput?.text.toString())
        }

    }

    override fun onPhoneValidated() {
        goPayPasswordInput.visibility = View.GONE
        goPayLoginInput.visibility = View.GONE
        //to be replace dummy check when validating via api
        if(goPayLoginInput.textInput.text.toString()=="69045932"){
            CustomUtils.showDialog(LocalizationManager.getValue("GoPayAlert", "Hints", "goPayTitle"),LocalizationManager.getValue("GoPayAlert", "Hints", "goPayphonealert"),context,null,baseLayoutManager,null,null,false)
            return
        }else{
          //  println(" you clicked to display otp!!!")
            baseLayoutManager?.displayOTPView(PaymentDataSource.getCustomer().getPhone(), GOPAY.name)
        }

    }

    override fun getPhoneNumber(phoneNumber: String, countryCode: String, maskedValue: String) {
        mobileNumber  = "+${countryCode.replace("+", " ")} $maskedValue"
        Log.d("countrycode", countryCode)
        Log.d("countrycode......", countryCode.replace("+", " "))
    }

    override fun onChangePhoneClicked() {
        baseLayoutManager?.displayGoPayLogin()
        goPayopened = true

    }

    override fun onOtpButtonConfirmationClick(otpNumber: String): Boolean {
        otpViewHolder?.otpView?.otpHintText?.visibility= View.GONE
        Log.d("isValidOTPValid", (otpNumber == "111111").toString())
        return if(otpNumber=="111111"){
            baseLayoutManager?.displayGoPay()
            true
        } else {
            otpViewHolder?.otpView?.otpHintText?.text= (LocalizationManager.getValue("Message", "TapOtpView", "Invalid"))
            otpViewHolder?.otpView?.otpHintText?.visibility= View.VISIBLE
            false
        }
    }


}


