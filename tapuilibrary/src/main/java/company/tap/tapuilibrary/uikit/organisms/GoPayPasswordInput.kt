package company.tap.tapuilibrary.uikit.organisms

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.fontskit.enums.TapFont
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.EditTextTheme
import company.tap.tapuilibrary.themekit.theme.TextViewTheme
import company.tap.tapuilibrary.uikit.atoms.TapTextViewNew
import company.tap.tapuilibrary.uikit.atoms.TextInputEditText
import company.tap.tapuilibrary.uikit.datasource.ActionButtonDataSource
import company.tap.tapuilibrary.uikit.interfaces.GoPayLoginInterface
import company.tap.tapuilibrary.uikit.interfaces.PasswordConfirmationInterface
import company.tap.tapuilibrary.uikit.interfaces.TapView
import company.tap.tapuilibrary.uikit.views.TabAnimatedActionButton
import kotlinx.android.synthetic.main.gopay_password_input.view.*


/**
 *
 * Created  on 7/16/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class GoPayPasswordInput(context: Context?, attrs: AttributeSet?) :
    LinearLayout(context, attrs),
    TapView<EditTextTheme> {

    val passwordTextInput by lazy { findViewById<TextInputEditText>(R.id.gopay_password_input_text) }
    val changeEmail by lazy { findViewById<TapTextViewNew>(R.id.change_email) }
    val textInputLayout by lazy { findViewById<TextInputLayout>(R.id.text_input_layout) }
    val signInButton by lazy { findViewById<TabAnimatedActionButton>(R.id.sigin_button) }
    val passwordEmailText by lazy { findViewById<TapTextViewNew>(R.id.passwordEmailText) }
    val rootView by lazy { findViewById<LinearLayout>(R.id.root_view) }
    val changeEmailCardView by lazy { findViewById<CardView>(R.id.changeEmailCardView) }
    private var passwordConfirmationInterface: PasswordConfirmationInterface? = null
    var isValidPassword: Boolean = false



    private var loginInterface: GoPayLoginInterface? = null

    init {
        inflate(context, R.layout.gopay_password_input, this)
        initButton()
        initChange()
        initPasswordInput()
        setPasswordValidation()
        initTheme()
//        rootView.setBackgroundColor(Color.parseColor(ThemeManager.getValue("goPay.passwordField.backgroundColor")))
        if (context?.let { LocalizationManager.getLocale(it).language } == "en") setFontsEnglish() else setFontsArabic()

        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            passwordLinearLayout.setBackgroundResource(R.drawable.blur_background_dark)
        } else {
            passwordLinearLayout.setBackgroundResource(R.drawable.blurbackground)
        }
    }

    fun setOtpButtonConfirmationInterface(passwordConfirmationInterface: PasswordConfirmationInterface) {
        this.passwordConfirmationInterface = passwordConfirmationInterface
    }

    private fun setPasswordValidation() {
        initTheme()
        if (passwordTextInput.text?.length?:0 > 7) {
            changeButtonStatus(true)
        } else {
            changeButtonStatus(false)
        }
        signInButton.isEnabled = passwordTextInput.text?.length?:0 > 7
    }


    fun setLoginInterface(loginInterface: GoPayLoginInterface, emailText: String) {
        this.loginInterface = loginInterface
        passwordEmailText.text = emailText
        val passwordEmailTextTextTheme = TextViewTheme()
        passwordEmailTextTextTheme.textColor = Color.parseColor(ThemeManager.getValue("goPay.passwordField.textColor"))
        passwordEmailTextTextTheme.textSize = ThemeManager.getFontSize("goPay.passwordField.textFont")
        passwordEmailText.setTheme(passwordEmailTextTextTheme)
        passwordEmailText.setTextColor(Color.parseColor(ThemeManager.getValue("goPay.passwordField.textColor")))

    }

    private fun initButton() {
        changeButtonStatus(false)
        signInButton.setOnClickListener {
            if (isEnabled) {
                isValidPassword =  passwordConfirmationInterface?.onOtpButtonConfirmationClick(passwordTextInput.text.toString()) ?: false
                if (!isValidPassword) textInputLayout.error = (LocalizationManager.getValue(
                    "GoPayLogin",
                    "Hints",
                    "password"
                ))
            }
        }
    }

    private fun initChange() {
        changeEmail.text = LocalizationManager.getValue(
            "change",
            "Common"
        )
        changeEmail.setOnClickListener {
            loginInterface?.onChangeClicked()
        }
    }

    private fun initPasswordInput() {
        passwordTextInput.hint = LocalizationManager.getValue(
            "password",
            "Common"
        )
        passwordTextInput.doAfterTextChanged {
            changeButtonStatus(it.toString().length > 7)
            initTheme()
        }
    }

    private fun changeButtonStatus(isEnabled: Boolean) {
        if (isEnabled) {
            signInButton.isEnabled = isEnabled
            signInButton.setButtonDataSource(
                true,
                context?.let { LocalizationManager.getLocale(it).language },
                LocalizationManager.getValue("signin", "ActionButton"),
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.goLoginBackgroundColor")),
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor")))
        } else {
            signInButton.setButtonDataSource(
                false,
                context?.let { LocalizationManager.getLocale(it).language },
                LocalizationManager.getValue("signin", "ActionButton"),
                Color.parseColor(ThemeManager.getValue("actionButton.Invalid.goLoginBackgroundColor")),
                Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")))
        }
    }


    /**
     * Set the configure the current theme. If null is provided then the default Theme is returned
     * on the next call
     * @param theme Theme to consume in the wrapper, a value of null resets the theme to the default
     **/
    override fun setTheme(theme: EditTextTheme) {
        theme.maxLines?.let { it }
        theme.textColor?.let { it }
        theme.textSize?.let { it }
        theme.letterSpacing?.let { it }
        theme.textColorHint?.let { }
        theme.backgroundTint?.let { backgroundTintList = ColorStateList.valueOf(it) }
    }


    fun setFontsEnglish() {

        passwordTextInput?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
        changeEmail?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
    }

    fun setFontsArabic() {
        passwordTextInput?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
        changeEmail?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )
    }


    private fun initTheme() {
        passwordTextInput.setErrorColor(Color.parseColor(ThemeManager.getValue("goPay.passwordField.underline.filled.backgroundColor")))
        passwordTextInput.setTextColor(Color.parseColor(ThemeManager.getValue("goPay.passwordField.textColor")))
        passwordTextInput.setHintTextColor(Color.parseColor(ThemeManager.getValue("goPay.passwordField.placeHolderColor")))
        passwordTextInput.textSize = ThemeManager.getFontSize("goPay.passwordField.textFont").toFloat()
        passwordTextInput.backgroundTintList = ColorStateList.valueOf(Color.parseColor(ThemeManager.getValue("goPay.passwordField.underline.empty.backgroundColor")))
        textInputLayout.backgroundTintList = ColorStateList.valueOf(Color.parseColor(ThemeManager.getValue("goPay.passwordField.underline.empty.backgroundColor")))
        changeEmailCardView.setCardBackgroundColor(Color.parseColor(ThemeManager.getValue("GlobalValues.Colors.whiteTwo")))
        val passwordEmailTextTextTheme = TextViewTheme()
        passwordEmailTextTextTheme.textColor = Color.parseColor(ThemeManager.getValue("goPay.passwordField.textColor"))
        passwordEmailTextTextTheme.textSize = ThemeManager.getFontSize("goPay.passwordField.textFont")
        passwordEmailText.setTheme(passwordEmailTextTextTheme)
        passwordEmailText.setTextColor(Color.parseColor(ThemeManager.getValue("goPay.passwordField.textColor")))
        val changeEmailTextTextTheme = TextViewTheme()
        changeEmailTextTextTheme.textColor = (Color.parseColor(ThemeManager.getValue("goPay.passwordField.textColor")))
        changeEmailTextTextTheme.textSize = ThemeManager.getFontSize("goPay.passwordField.textFont")
        changeEmail.setTheme(changeEmailTextTextTheme)
    }

    fun getSuccessDataSource(
        backgroundColor: Int,
        text: String,
        textColor: Int
    ): ActionButtonDataSource {
        return ActionButtonDataSource(
            text = text,
            textSize = 18f,
            textColor = textColor,
            cornerRadius = 100f,
            successImageResources = R.drawable.checkmark,
            backgroundColor = backgroundColor
        )
    }


}