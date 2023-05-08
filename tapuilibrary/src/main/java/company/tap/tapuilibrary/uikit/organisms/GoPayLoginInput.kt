package company.tap.tapuilibrary.uikit.organisms

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hbb20.CountryCodePicker
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.fontskit.enums.TapFont
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.EditTextTheme
import company.tap.tapuilibrary.themekit.theme.SeparatorViewTheme
import company.tap.tapuilibrary.themekit.theme.TextViewTheme
import company.tap.tapuilibrary.uikit.atoms.TapSeparatorView
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.datasource.ActionButtonDataSource
import company.tap.tapuilibrary.uikit.datasource.GoPayLoginDataSource
import company.tap.tapuilibrary.uikit.enums.GoPayLoginMethod.EMAIL
import company.tap.tapuilibrary.uikit.enums.GoPayLoginMethod.PHONE
import company.tap.tapuilibrary.uikit.interfaces.GoPayLoginInterface
import company.tap.tapuilibrary.uikit.interfaces.OpenOTPInterface
import company.tap.tapuilibrary.uikit.interfaces.TapView
import company.tap.tapuilibrary.uikit.utils.FakeThemeManager
import company.tap.tapuilibrary.uikit.views.TabAnimatedActionButton
import kotlin.math.abs

/**
 *
 * Created on 7/14/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class GoPayLoginInput(context: Context?, attrs: AttributeSet?) :
    LinearLayout(context, attrs), CountryCodePicker.OnCountryChangeListener,
    TapView<EditTextTheme> {

    val textInput by lazy { findViewById<TextInputEditText>(R.id.gopay_text_input) }
    val loginTabLayout by lazy { findViewById<TabLayout>(R.id.login_type) }
    val textInputLayout by lazy { findViewById<TextInputLayout>(R.id.text_input_layout) }
    val loginMethodImage by lazy { findViewById<ImageView>(R.id.login_method_icon) }
    val actionButton by lazy { findViewById<TabAnimatedActionButton>(R.id.gopay_button) }
    val goPayHint by lazy { findViewById<TapTextView>(R.id.gopay_hint) }
    val countryCodePicker by lazy { findViewById<CountryCodePicker>(R.id.countryCodePicker) }
    val goPayLinear by lazy { findViewById<LinearLayout>(R.id.goPayLinear) }
    val goPayTabSeparator by lazy { findViewById<TapSeparatorView>(R.id.goPayTabSeparator) }
    val goPayTabSeparator_ by lazy { findViewById<TapSeparatorView>(R.id.goPayTabSeparator_) }
    val loginInputLayout by lazy { findViewById<LinearLayout>(R.id.login_input_layout) }
    val container by lazy { findViewById<ScrollView>(R.id.container) }

    private var loginInterface: GoPayLoginInterface? = null
    private var openOTPInterface: OpenOTPInterface? = null
    private var countryCode: String? = null

    var dataSource: GoPayLoginDataSource? = null
    var inputType = EMAIL


    init {
        inflate(context, R.layout.gopay_login_input, this)
        initTheme()
        setFonts()
        initGoPayHint()
        initCountryCodePicker()
        setupKeyboardListener(container) // call in OnCreate or similar
    }

    private fun initGoPayHint() {
        goPayHint.text = LocalizationManager.getValue("HintLabel", "GoPay")

    }

    private fun setFonts() {
        if (context?.let { LocalizationManager.getLocale(it).language } == "en") setFontsEnglish() else setFontsArabic()
        countryCodePicker.textView_selectedCountry?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
        textInput?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
    }


    private fun setupKeyboardListener(view: View) {
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            if (abs(view.rootView.height - (r.bottom - r.top)) > 100) { // if more than 100 pixels, its probably a keyboard...
                onKeyboardShow()
            }
        }
    }

    private fun onKeyboardShow() {
        container.scrollToBottomWithoutFocusChange()
    }

    private fun ScrollView.scrollToBottomWithoutFocusChange() { // Kotlin extension to scrollView
        val lastChild = getChildAt(childCount - 1)
        val bottom = lastChild.bottom + paddingBottom
        val delta = bottom - (scrollY + height)
        smoothScrollBy(0, delta)
    }

    private fun initTheme() {
        setSeparatorTheme()
        val textThem = TextViewTheme()
        textThem.textColor =
            Color.parseColor(ThemeManager.getValue("goPay.loginBar.hintLabel.textColor"))
        textThem.textSize = ThemeManager.getFontSize("goPay.loginBar.hintLabel.textFont")
        goPayHint.setTheme(textThem)
        goPayLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("goPay.loginBar.backgroundColor")))
        loginTabLayout.setSelectedTabIndicatorColor(Color.parseColor(ThemeManager.getValue("goPay.loginBar.underline.selected.backgroundColor")))
        loginTabLayout.setBackgroundColor(Color.parseColor(ThemeManager.getValue("goPay.loginBar.backgroundColor")))
        loginTabLayout.tabTextColors =
            ColorStateList.valueOf(Color.parseColor(ThemeManager.getValue("goPay.loginBar.title.selected.textColor")))
        goPayHint.setBackgroundColor(Color.parseColor(ThemeManager.getValue("goPay.loginBar.backgroundColor")))
        textInput.setHintTextColor(Color.parseColor(ThemeManager.getValue("phoneCard.textFields.placeHolderColor")))
        textInput.setTextColor(Color.parseColor(ThemeManager.getValue("emailCard.textFields.textColor")))
        textInput.textSize = ThemeManager.getFontSize("emailCard.textFields.font").toFloat()
        loginInputLayout.setBackgroundColor(Color.parseColor(ThemeManager.getValue("goPay.passwordField.backgroundColor")))
        countryCodePicker.setDialogBackgroundColor(Color.parseColor(ThemeManager.getValue("goPay.loginBar.backgroundColor")))
    }

    private fun setSeparatorTheme() {
        val separatorViewTheme = SeparatorViewTheme()
        separatorViewTheme.strokeColor =
            Color.parseColor(ThemeManager.getValue("tapSeparationLine.backgroundColor"))
        separatorViewTheme.strokeHeight = ThemeManager.getValue("tapSeparationLine.height")
        goPayTabSeparator.setTheme(separatorViewTheme)
        goPayTabSeparator_.setTheme(separatorViewTheme)
    }

    private fun initCountryCodePicker() {
        countryCodePicker.visibility = View.VISIBLE
        countryCodePicker.setDefaultCountryUsingNameCode("KW")
        countryCode = countryCodePicker.defaultCountryCodeAsInt.toString()
        countryCodePicker.ccpDialogShowFlag = false
        countryCodePicker.showArrow(false)
        countryCodePicker.contentColor =
            Color.parseColor(ThemeManager.getValue("phoneCard.textFields.placeHolderColor"))
        countryCodePicker.setDialogBackgroundColor(Color.parseColor(ThemeManager.getValue("goPay.loginBar.backgroundColor")))
    }


    fun changeDataSource(dataSource: GoPayLoginDataSource) {
        this.dataSource = dataSource
        initTabLayout()
        initTextInput()
        changeInputType()
        initPayButton()
    }

    fun setLoginInterface(loginInterface: GoPayLoginInterface) {
        this.loginInterface = loginInterface
    }

    fun setOpenOTPInterface(openOTPInterface: OpenOTPInterface) {
        this.openOTPInterface = openOTPInterface
    }

    private fun initPayButton() {
        actionButton.isEnabled = false
        actionButton.setButtonDataSource(
            false,
            context?.let { LocalizationManager.getLocale(it).language },
            LocalizationManager.getValue("next", "Common"),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
        )
        setPayButtonAction()
    }

    private fun setPayButtonAction() {
        actionButton.setOnClickListener {
            when (inputType) {
                EMAIL -> loginInterface?.onEmailValidated()
                PHONE -> loginInterface?.onPhoneValidated()
            }
        }
    }

    private fun initTextInput() {
        textInput.doAfterTextChanged {
            if (isValidInput(it.toString())) {
                enableNext()
                sendPhoneNumber()
            } else
                disableNext()
        }
    }

    private fun sendPhoneNumber() {
        var replaced = ""
        var countryCodeReplaced = ""
        if (textInput.text.toString().length > 7)
            replaced = (textInput.text.toString()).replaceRange(1, 6, "....")

        countryCodeReplaced = (countryCodePicker.selectedCountryCode).replace("+", " ")
        countryCodeReplaced.let {
            openOTPInterface?.getPhoneNumber(
                textInput.text.toString(),
                it,
                replaced
            )
        }
    }

    private fun isValidInput(text: String): Boolean {
        return when (inputType) {
            EMAIL -> isValidEmail(text)
            PHONE -> isValidPhone(text)
        }
    }

    private fun disableNext() {
        actionButton.isEnabled = false
        actionButton.setButtonDataSource(
            false,
            context?.let { LocalizationManager.getLocale(it).language },
            LocalizationManager.getValue("next", "Common"),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
        )
        loginTabLayout.setSelectedTabIndicatorColor(FakeThemeManager.getGoPayUnValidatedColor())
    }

    private fun enableNext() {
        loginTabLayout.setSelectedTabIndicatorColor(FakeThemeManager.getGoPayValidatedColor())
        actionButton.isEnabled = true
        actionButton.setButtonDataSource(
            true,
            context?.let { LocalizationManager.getLocale(it).language },
            LocalizationManager.getValue("next", "Common"),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.goLoginBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS
            .matcher(email)
            .matches()
    }

    private fun isValidPhone(phone: String): Boolean {
        return phone.length > 7
    }

    private fun initTabLayout() {
        loginTabLayout.removeAllTabs()
        loginTabLayout.addTab(
            loginTabLayout.newTab().setCustomView(
                getThemedTabText(
                    dataSource?.emailTabText ?: LocalizationManager.getValue(
                        "email",
                        "Common"
                    ), true
                )
            )
        )

        loginTabLayout.addTab(
            loginTabLayout.newTab().setCustomView(
                getThemedTabText(
                    dataSource?.phoneTabText ?: LocalizationManager.getValue(
                        "phone",
                        "Common"
                    ), false
                )
            )
        )
        onTabSelectionListener()
    }

    private fun onTabSelectionListener() {
        loginTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                setOnTabUnselected(tab)
            }
            override fun onTabSelected(tab: TabLayout.Tab?) {
                setOnTabSelected(tab)
            }
        })
    }

    private fun setOnTabUnselected(tab: TabLayout.Tab?) {
        val tabText = tab?.customView as TapTextView
        setTabUnSelectedTextFont(tabText)
        tabText.setTextColor(Color.parseColor(ThemeManager.getValue("goPay.loginBar.title.otherSegmentSelected.textColor")))
    }

    private fun setTabUnSelectedTextFont(tabText :TapTextView){
        if (LocalizationManager.getLocale(context).language == "en") {
            tabText.typeface = Typeface.createFromAsset(
                context?.assets, TapFont.tapFontType(
                    TapFont.RobotoRegular
                )
            )
        } else {
            tabText.typeface = Typeface.createFromAsset(
                context?.assets, TapFont.tapFontType(
                    TapFont.TajawalMedium
                )
            )
        }
    }

    private fun setOnTabSelected(tab: TabLayout.Tab?) {
        val tabText = tab?.customView as TapTextView
        setTabSelectedTextFont(tabText)
        tabText.setTextColor(Color.parseColor(ThemeManager.getValue("goPay.loginBar.title.selected.textColor")))
        inputType = if (tab.position == 0) EMAIL else PHONE
        changeInputType()
    }

    private  fun setTabSelectedTextFont(tabText :TapTextView){
        if (LocalizationManager.getLocale(context).language == "en") {
            tabText.typeface = Typeface.createFromAsset(
                context?.assets, TapFont.tapFontType(
                    TapFont.RobotoRegular
                )
            )
        } else {
            tabText.typeface = Typeface.createFromAsset(
                context?.assets, TapFont.tapFontType(
                    TapFont.TajawalMedium
                )
            )
        }
    }

    private fun changeInputType() {
        textInput.text = null
        loginMethodImage.visibility = View.VISIBLE
        textInputLayout.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
        when (inputType) {
            EMAIL -> {
                textInput.hint = dataSource?.emailInputHint ?: "mail@mail.com"
                textInput.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                textInput.setTextColor(Color.parseColor(ThemeManager.getValue("emailCard.textFields.textColor")))
                loginMethodImage.setImageResource(R.drawable.ic_mail)
                countryCodePicker.visibility = View.GONE
            }
            PHONE -> {
                initCountryCodePicker()
                textInput.hint = dataSource?.phoneInputHint ?: "00000000"
                textInput.inputType = InputType.TYPE_CLASS_PHONE
                textInput.setTextColor(Color.parseColor(ThemeManager.getValue("phoneCard.textFields.textColor")))
                loginMethodImage.setImageResource(R.drawable.ic_mobile)
                loginMethodImage.setOnClickListener { countryCodePicker.launchCountrySelectionDialog() }
                setListenerForPhone()

            }
        }
    }

    private fun setListenerForPhone() {
        textInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                countryCodePicker.contentColor =
                    Color.parseColor(ThemeManager.getValue("phoneCard.textFields.textColor"))
            }
            override fun afterTextChanged(mobileText: Editable) {
                phoneInputAfterTextChange(mobileText)
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        })
    }

    private  fun phoneInputAfterTextChange(mobileText: Editable){
        if (mobileText.length > 1) {
            countryCodePicker.contentColor =
                Color.parseColor(ThemeManager.getValue("phoneCard.textFields.textColor"))
        } else {
            countryCodePicker.contentColor =
                Color.parseColor(ThemeManager.getValue("phoneCard.textFields.placeHolderColor"))
        }
    }

    private fun getThemedTabText(text: String, isSelected: Boolean): TapTextView {
        val tabText = TapTextView(context, null)
        tabText.setTheme(FakeThemeManager.getGoPayTabLayoutTextTheme(isSelected))
        tabText.text = text
        if (LocalizationManager.getLocale(context).language == "en") {
            tabText.typeface = Typeface.createFromAsset(
                context?.assets, TapFont.tapFontType(
                    TapFont.RobotoRegular
                )
            )
            countryCodePicker.visibility = View.VISIBLE

        } else {
            tabText.typeface = Typeface.createFromAsset(
                context?.assets, TapFont.tapFontType(
                    TapFont.TajawalMedium
                )
            )
            countryCodePicker.visibility = View.VISIBLE

        }

        return tabText
    }

    override fun setTheme(theme: EditTextTheme) {
        theme.maxLines?.let { it }
        theme.textColor?.let { it }
        theme.textSize?.let { it }
        theme.letterSpacing?.let { it }
        theme.textColorHint?.let { }
        theme.backgroundTint?.let { backgroundTintList = ColorStateList.valueOf(it) }
    }

    fun setFontsEnglish() {
        textInput?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )

        goPayHint?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
    }

    fun setFontsArabic() {
        textInput?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
        goPayHint?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )

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

    override fun onCountrySelected() {
//        countryCode = countryCodePicker.selectedCountryCode
    }


}