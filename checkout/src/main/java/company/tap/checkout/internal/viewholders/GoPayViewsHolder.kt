package company.tap.checkout.internal.viewholders

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayouttManager
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.uikit.datasource.GoPayLoginDataSource
import company.tap.tapuilibrary.uikit.interfaces.GoPayLoginInterface
import company.tap.tapuilibrary.uikit.interfaces.OpenOTPInterface
import company.tap.tapuilibrary.uikit.interfaces.OtpButtonConfirmationInterface
import company.tap.tapuilibrary.uikit.organisms.GoPayLoginInput
import company.tap.tapuilibrary.uikit.organisms.GoPayPasswordInput
import company.tap.tapuilibrary.uikit.views.TabAnimatedActionButton


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class GoPayViewsHolder(private val context: Context, private val baseLayouttManager: BaseLayouttManager?=null) : TapBaseViewHolder,GoPayLoginInterface,OtpButtonConfirmationInterface,
    OpenOTPInterface {

    override val view: View = LayoutInflater.from(context).inflate(R.layout.gopay_layout, null)

    override val type = SectionType.GOPAY_SIGNIN

     val goPayLoginInput: GoPayLoginInput
     val goPayPasswordInput: GoPayPasswordInput
     val signInButton: TabAnimatedActionButton
     lateinit var mobileNumber: String
  //  private  var otpViewHolder: OTPViewHolder = OTPViewHolder(context)

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
                baseLayouttManager?.displayGoPay()
            }else{
                goPayPasswordInput.textInputLayout.error= LocalizationManager.getValue("GoPayLogin","Hints","password")
            }
        }
    }

    override fun onChangeClicked() {
      goPayLoginInput.visibility = View.VISIBLE
       goPayPasswordInput.visibility = View.GONE
       // otpViewHolder.view.otpView?.visibility = View.GONE
    }

    override fun onEmailValidated() {
        //to be replace dummy check when validating via api
        if(goPayLoginInput.textInput.text.toString()=="a.k@g.com"){
            CustomUtils.showDialog(LocalizationManager.getValue("GoPayAlert", "Hints", "goPayTitle"),LocalizationManager.getValue("GoPayAlert", "Hints", "goPayemailalert"),context)
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
            CustomUtils.showDialog(LocalizationManager.getValue("GoPayAlert", "Hints", "goPayTitle"),LocalizationManager.getValue("GoPayAlert", "Hints", "goPayemailalert"),context)
            return
        }else{

      //  println("viewholder value"+otpViewHolder+"\n"+"view value"+view+"\n"+"otpview is "+otpViewHolder.view.otpView)

       // if(otpViewHolder.view.otpView!=null) {
           // otpViewHolder.view.otpView.visibility = View.VISIBLE
           // otpViewHolder.view.otpView.changePhoneCardView?.visibility = View.VISIBLE
            println(" you clicked for otp!!!")
           /// baseLayouttManager?.displayOTPV(otpViewHolder.view.otpView?.otpViewInput1?.text.toString(), otpViewHolder.otpView.isValidOTP

            baseLayouttManager?.displayOTPView(mobileNumber)


      //  }

        }

    }

    override fun getPhoneNumber(phoneNumber: String, countryCode: String, maskedValue: String) {
        mobileNumber  = "+${countryCode.replace("+", " ")} $maskedValue"
        Log.d("countrycode", countryCode)
        Log.d("countrycode......", countryCode.replace("+", " "))
    }

    override fun onChangePhoneClicked() {
        baseLayouttManager?.displayGoPayLogin()
    }

    override fun onOtpButtonConfirmationClick(otpNumber: String): Boolean {
        Log.d("isValidOTPValid", (otpNumber == "111111").toString())
        return if(otpNumber=="111111"){
            baseLayouttManager?.displayGoPay()
            true
        } else {
            false
        }
    }


}


