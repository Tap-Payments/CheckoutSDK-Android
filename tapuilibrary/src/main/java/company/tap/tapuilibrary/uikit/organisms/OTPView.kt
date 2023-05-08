package company.tap.tapuilibrary.uikit.organisms

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Half.trunc
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.fontskit.enums.TapFont
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.TextViewTheme
import company.tap.tapuilibrary.uikit.adapters.context
import company.tap.tapuilibrary.uikit.atoms.TapImageView
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.interfaces.OpenOTPInterface
import company.tap.tapuilibrary.uikit.interfaces.OtpButtonConfirmationInterface
import company.tap.tapuilibrary.uikit.views.TabAnimatedActionButton
import company.tap.tapuilibrary.uikit.views.TapOTPView
import kotlinx.android.synthetic.main.otp_view.view.*
import java.util.*


/**
 *
 * Created by OlaMonir on 21/10/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */

class OTPView : LinearLayout, OpenOTPInterface {

    lateinit var attrs: AttributeSet

    val otpMainView by lazy { findViewById<LinearLayout>(R.id.otpMainView) }
    val otpLinearLayout by lazy { findViewById<LinearLayout>(R.id.otpLinearLayout) }
    val otpViewInput1 by lazy { findViewById<TapOTPView>(R.id.otpViewInput1) }

    //    val otpViewInput2 by lazy { findViewById<TapOTPView>(R.id.otpViewInput2) }
    val otpSentText by lazy { findViewById<TapTextView>(R.id.otpSentText) }
    val mobileNumberText by lazy { findViewById<TapTextView>(R.id.mobileNumberText) }
    val otpHintText by lazy { findViewById<TapTextView>(R.id.otpHintText) }
    val timerText by lazy { findViewById<TapTextView>(R.id.timerText) }
    val changePhone by lazy { findViewById<TapTextView>(R.id.changePhone) }
    val otpViewActionButton by lazy { findViewById<TabAnimatedActionButton>(R.id.otpViewActionButton) }
    val changePhoneCardView by lazy { findViewById<CardView>(R.id.changePhoneCardView) }
    val brandingLayout by lazy { findViewById<LinearLayout>(R.id.brandingLayout) }
    val textViewPowered by lazy { findViewById<TapTextView>(R.id.textViewPowered) }
    val tapLogoImage by lazy { findViewById<TapImageView>(R.id.tapLogoImage) }

    @DrawableRes
    val logoIcon: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            R.drawable.poweredby_dark_mode
        } else if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("light")) {
            R.drawable.poweredby_light_mode
        } else R.drawable.poweredby_light_mode

    /**
     * Attributes for goPay OTP view which diffrent with otp
     * these attributes will show only when user pay with phone number which will be in selection tab of phone number types
     * if this view visibility =  VISIBLE ->   changePhoneCardView visibility = GONE
     *
     */
    val otpSentConstraintGoPay by lazy { findViewById<ConstraintLayout>(R.id.otpSentConstraintNormalPay) }
    val otpSentTextNormalPay by lazy { findViewById<TapTextView>(R.id.otpSentTextNormalPay) }
    val mobileNumberTextNormalPay by lazy { findViewById<TapTextView>(R.id.mobileNumberTextNormalPay) }

    //    private var goPayLoginInput: GoPayLoginInput? = null
    private var openOTPInterface: OpenOTPInterface? = null
    private var otpButtonConfirmationInterface: OtpButtonConfirmationInterface? = null
    var isValidOTP: Boolean = false


    fun setOTPInterface(openOTPInterface: OpenOTPInterface) {
        this.openOTPInterface = openOTPInterface
    }

    fun setOtpButtonConfirmationInterface(otpButtonConfirmationInterface: OtpButtonConfirmationInterface) {
        this.otpButtonConfirmationInterface = otpButtonConfirmationInterface
    }

    /**
     * Simple constructor to use when creating a TapPayCardSwitch from code.
     *  @param context The Context the view is running in, through which it can
     *  access the current theme, resources, etc.
     **/
    constructor(context: Context) : super(context)

    /**
     *  @param context The Context the view is running in, through which it can
     *  access the current theme, resources, etc.
     *  @param attrs The attributes of the XML Button tag being used to inflate the view.
     *
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     *  @param context The Context the view is running in, through which it can
     *  access the current theme, resources, etc.
     *  @param attrs The attributes of the XML Button tag being used to inflate the view.
     * @param defStyleAttr The resource identifier of an attribute in the current theme
     * whose value is the the resource id of a style. The specified style’s
     * attribute values serve as default values for the button. Set this parameter
     * to 0 to avoid use of default values.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    init {
        inflate(context, R.layout.otp_view, this)

        prepareTextViews()
        initOTPConfirmationButton()
        initChange()
        initTheme()
        if (context?.let { LocalizationManager.getLocale(it).language } == "en") setFontsEnglish() else setFontsArabic()

//        showKeyboard()
//        otpViewInput1.addTextChangedListener(GenericTextWatcher(otpViewInput1, otpViewInput2, context))

        /**
         * GenericKeyEvent here works for deleting the element and to switch back to previous EditText
         * first parameter is the current EditText and second parameter is previous EditText
         */
//        otpViewInput2.setOnKeyListener(GenericKeyEvent(otpViewInput2, otpViewInput1, context))

    }

    fun startCounter(){
        startCountdown()
    }

    private fun setFontsArabic() {
        otpSentText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
        otpSentTextNormalPay.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
        changePhone.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )

        otpViewInput1.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
//        otpViewInput2.typeface = Typeface.createFromAsset(
//            context?.assets, TapFont.tapFontType(
//                TapFont.RobotoLight
//            )
//        )

        mobileNumberText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
        mobileNumberTextNormalPay.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )

        timerText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
    }

    private fun setFontsEnglish() {
        otpSentText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
        otpSentTextNormalPay.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
        changePhone.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )

        otpViewInput1.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
//        otpViewInput2.typeface = Typeface.createFromAsset(
//            context?.assets, TapFont.tapFontType(
//                TapFont.RobotoLight
//            )
//        )

        mobileNumberText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
        mobileNumberTextNormalPay.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )

        timerText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
    }

    fun showOnlyButton() {
        changePhoneCardView.visibility = View.GONE
        otpViewInput1.visibility = View.GONE
        timerConstraints.visibility = View.GONE
    }

    private fun initTheme() {
        // changePhoneCardView.setCardBackgroundColor(Color.parseColor(ThemeManager.getValue("TapOtpView.backgroundColor")))
        changePhoneCardView.setCardBackgroundColor(Color.parseColor(ThemeManager.getValue("GlobalValues.Colors.whiteTwo")))
        changePhoneCardView.elevation= 0F
        val timerTextTheme = TextViewTheme()
        timerTextTheme.textColor =
            (Color.parseColor(ThemeManager.getValue("TapOtpView.Timer.textColor")))
        timerTextTheme.textSize = ThemeManager.getFontSize("TapOtpView.Timer.textFont")
        timerText.setTheme(timerTextTheme)


        val mobileNumberTextTextTheme = TextViewTheme()
        mobileNumberTextTextTheme.textColor =
            (Color.parseColor(ThemeManager.getValue("TapOtpView.Ready.Message.title")))
        mobileNumberTextTextTheme.textSize =
            ThemeManager.getFontSize("TapOtpView.Ready.Message.textFont")
        mobileNumberText.setTheme(mobileNumberTextTextTheme)
        mobileNumberTextNormalPay.setTheme(mobileNumberTextTextTheme)

        val otpSentTextTheme = TextViewTheme()
        otpSentTextTheme.textColor =
            (Color.parseColor(ThemeManager.getValue("TapOtpView.Ready.Message.title")))
        otpSentTextTheme.textSize =
            ThemeManager.getFontSize("TapOtpView.Ready.Message.textFont")
        otpSentText.setTheme(otpSentTextTheme)

        otpSentTextNormalPay.setTheme(mobileNumberTextTextTheme)
        otpViewInput1.setTextColor(Color.parseColor(ThemeManager.getValue("TapOtpView.OtpController.textColor")))
//        otpViewInput2.setTextColor(Color.parseColor(ThemeManager.getValue("TapOtpView.OtpController.textColor")))
        setBackground()
        val poweredByTextViewTheme = TextViewTheme()
        poweredByTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("poweredByTap.powerLabel.textColor"))
        poweredByTextViewTheme.textSize =
            ThemeManager.getFontSize("poweredByTap.powerLabel.font")
        poweredByTextViewTheme.font = ThemeManager.getFontName("poweredByTap.powerLabel.font")
        textViewPowered.setTheme(poweredByTextViewTheme)

        tapLogoImage.setImageResource(logoIcon)

    }

    private fun setBackground() {
      /*  if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            otpLinearLayout.setBackgroundResource(R.drawable.blur_background_dark)
        } else {
            otpLinearLayout.setBackgroundResource(R.drawable.blurbackground)
        }*/


            otpLinearLayout.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapOtpView.backgroundColor")))

    }

    private fun setFonts() {
        otpSentText.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )



        if (LocalizationManager.getLocale(context).language == "en") {


        } else {

        }
    }

    fun setHintExpiredTheme() {
        val otpHintTextTheme = TextViewTheme()

        otpHintTextTheme.textColor =
            (Color.parseColor(ThemeManager.getValue("TapOtpView.Expired.Message.title")))
        otpHintTextTheme.textSize =
            ThemeManager.getFontSize("TapOtpView.Expired.Message.textFont")

        otpHintText.setTheme(otpHintTextTheme)
    }

    private fun setHintInValidOtp() {
        val otpHintTextTheme = TextViewTheme()
        otpHintTextTheme.textColor =
            (Color.parseColor(ThemeManager.getValue("TapOtpView.Invalid.Message.title")))
        otpHintTextTheme.textSize =
            ThemeManager.getFontSize("TapOtpView.Invalid.Message.textFont")
        otpHintText.setTheme(otpHintTextTheme)
    }

    private fun initChange() {
        changePhone.setOnClickListener {
            openOTPInterface?.onChangePhoneClicked()
        }
    }

    fun restartTimer() {
        if (timerText.text == LocalizationManager.getValue(
                "resend",
                "ActionButton"
            )
        ) {
            otpViewInput1.text?.clear()
            otpHintText.visibility = View.INVISIBLE
            startCountdown()
        }
    }

    private fun startCountdown() {
        object : CountDownTimer(60 * 1000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished / 1000 % 60
                val minutes = millisUntilFinished / (1000 * 60) % 60
                val secondStr = if (second < 9) "0$second" else "$second"
                val minutesStr = if (minutes < 9) "0$minutes" else "$minutes"
                if (LocalizationManager.getLocale(context).language == "en") timerText.text =
                    "$minutesStr : $secondStr"
                else timerText.text = "$secondStr : $minutesStr"
            }

            override fun onFinish() {
                timerText.text = LocalizationManager.getValue("resend", "ActionButton")
                setHintExpiredTheme()
            }
        }.start()


    }

    private fun prepareTextViews() {
        otpSentText.text = LocalizationManager.getValue("Message", "TapOtpView", "Ready")
        otpSentTextNormalPay.text = LocalizationManager.getValue("Message", "TapOtpView", "Ready")
        changePhone.text = LocalizationManager.getValue("change", "Common")
    }

    @SuppressLint("SetTextI18n")
    override fun getPhoneNumber(phoneNumber: String, countryCode: String, maskedValue: String) {
        mobileNumberText.text = "$countryCode $maskedValue"
        mobileNumberTextNormalPay.text = "$countryCode $maskedValue"
    }

    override fun onChangePhoneClicked() {
        otpMainView.visibility = View.GONE
    }

    private fun initOTPConfirmationButton() {
        otpViewActionButton.setButtonDataSource(
            false, context?.let { LocalizationManager.getLocale(it).language },
            LocalizationManager.getValue(
                "confirm",
                "ActionButton"
            ),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.goLoginBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
        )


        otpViewInput1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length != otpViewInput1.itemCount) {
                    otpViewActionButton.isEnabled = false
                    otpViewActionButton.setButtonDataSource(
                        false, context?.let { LocalizationManager.getLocale(it).language },
                        LocalizationManager.getValue(
                            "confirm",
                            "ActionButton"
                        ),
                        Color.parseColor(ThemeManager.getValue("actionButton.Invalid.goLoginBackgroundColor")),
                        Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
                    )
                } else if (charSequence.length == otpViewInput1.itemCount) {


                    otpViewActionButton.isEnabled = true
                    otpViewActionButton.setButtonDataSource(
                        true, context?.let { LocalizationManager.getLocale(it).language },
                        LocalizationManager.getValue(
                            "confirm",
                            "ActionButton"
                        ),
                        Color.parseColor(ThemeManager.getValue("actionButton.Valid.goLoginBackgroundColor")),
                        Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
                    )


//                    otpViewInput1.addTextChangedListener(
//                        GenericTextWatcher(
//                            otpViewInput1,
//                            otpViewInput2,
//                            context
//                        )
//                    )
                }
            }

            override fun afterTextChanged(editable: Editable) {
//                otpViewInput1.addTextChangedListener(
//                    GenericTextWatcher(
//                        otpViewInput1,
//                        otpViewInput2,
//                        context
//                    )
//                )
            }
        })

//        otpViewInput2.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
//
//            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//
//                if (charSequence.length != otpViewInput2.itemCount) {
//                    otpViewActionButton.isEnabled = false
//                    otpViewActionButton.setButtonDataSource(
//                        false, context?.let { LocalizationManager.getLocale(it).language },
//                        LocalizationManager.getValue(
//                            "confirm",
//                            "ActionButton"
//                        ),
//                        Color.parseColor(ThemeManager.getValue("actionButton.Invalid.goLoginBackgroundColor")),
//                        Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
//                    )
//                } else {
//                    otpViewActionButton.isEnabled = true
//                    otpViewActionButton.setButtonDataSource(
//                        true, context?.let { LocalizationManager.getLocale(it).language },
//                        LocalizationManager.getValue(
//                            "confirm",
//                            "ActionButton"
//                        ),
//                        Color.parseColor(ThemeManager.getValue("actionButton.Valid.goLoginBackgroundColor")),
//                        Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
//                    )
//                }
//            }
//            override fun afterTextChanged(editable: Editable) {
//            }
//        })


        otpViewActionButton.setOnClickListener {
            if (otpViewActionButton.isEnabled) {
//                isValidOTP = otpButtonConfirmationInterface?.onOtpButtonConfirmationClick(otpNumber = otpViewInput1.text.toString() + otpViewInput2.text.toString()) ?: false
                isValidOTP =
                    otpButtonConfirmationInterface?.onOtpButtonConfirmationClick(otpNumber = otpViewInput1.text.toString())
                        ?: false
                if (!isValidOTP) {
                    otpHintText.visibility = View.VISIBLE
                    otpHintText.text = (LocalizationManager.getValue(
                        "Message",
                        "TapOtpView",
                        "Invalid"
                    ))
                    setHintInValidOtp()
                }
                Log.d("isValidOTP", isValidOTP.toString())
            }
        }
    }


    private fun showKeyboard() {
        val inputMethodManager: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

}

class GenericKeyEvent internal constructor(
    private val currentView: EditText,
    private val previousView: EditText?,
    private val context: Context
) : View.OnKeyListener {
    override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.otpViewInput1 && currentView.text.isEmpty()) {
            //If current is empty then previous EditText's number will also be deleted
            previousView?.requestFocus()
            previousView?.text = null
            showKeyboard()
            return true
        }
        return false
    }

    private fun showKeyboard() {
        val inputMethodManager: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun closeKeyboard() {
        val inputMethodManager: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }


}


class GenericTextWatcher internal constructor(
    private val currentView: View,
    private val nextView: View?,
    private val context: Context
) :
    TextWatcher {
    override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
        val text = editable.toString()
        when (currentView.id) {
            R.id.otpViewInput1 -> if (text.length == 3) {
                nextView!!.requestFocus()
                val inputMethodManager: InputMethodManager =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }


            //You can use EditText4 same as above to hide the keyboard
        }
    }

    override fun beforeTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) { // TODO Auto-generated method stub
    }

    override fun onTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) { // TODO Auto-generated method stub
    }


}
