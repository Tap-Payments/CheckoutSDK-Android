package company.tap.checkout.viewholders

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.graphics.toColor
import androidx.core.view.size
import company.tap.checkout.R
import company.tap.checkout.enums.SectionType
import company.tap.checkout.utils.AnimationEngine
import company.tap.checkout.utils.AnimationEngine.Type.*
import company.tap.tapuilibrary.adapters.context
import company.tap.tapuilibrary.atoms.TapTextView
import company.tap.tapuilibrary.datasource.GoPayLoginDataSource
import company.tap.tapuilibrary.interfaces.GoPayLoginInterface
import company.tap.tapuilibrary.organisms.GoPayLoginInput
import company.tap.tapuilibrary.organisms.GoPayPasswordInput
import company.tap.tapuilibrary.utils.FakeThemeManager
import company.tap.thememanager.manager.ThemeManager
import company.tap.thememanager.theme.EditTextTheme
import company.tap.thememanager.theme.TabSelectTheme
import company.tap.thememanager.theme.TextViewTheme


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
        setThemeToGoPayLoginInput()
        setThemeToGoPayPasswordInput()
    }

    override fun bindViewComponents() {
        goPayLoginInput.changeDataSource(GoPayLoginDataSource())
        goPayLoginInput.setLoginInterface(this)
        goPayPasswordInput.setLoginInterface(this)
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
    }

    override fun onPhoneValidated() {

    }


    fun setThemeToGoPayLoginInput(){
//
        val tabSelectTheme = TabSelectTheme()
        tabSelectTheme.backgroundColor = Color.parseColor(ThemeManager.getValue("goPay.loginBar.backgroundColor"))
        tabSelectTheme.selectedBackgroundColor = Color.parseColor(ThemeManager.getValue("goPay.loginBar.underline.selected.backgroundColor"))
        tabSelectTheme.unselectedBackgroundColor = Color.parseColor(ThemeManager.getValue("goPay.loginBar.underline.unselected.backgroundColor"))
        goPayLoginInput.loginTabLayout.setSelectedTabIndicatorColor(tabSelectTheme.selectedBackgroundColor!!)
        goPayLoginInput.textInputLayout.boxBackgroundColor = tabSelectTheme.unselectedBackgroundColor!!

        val textViewTheme = TextViewTheme()
        textViewTheme.textColor = Color.parseColor(ThemeManager.getValue("goPay.loginBar.title.selected.textColor"))
        textViewTheme.font = ThemeManager.getFontName("goPay.loginBar.title.selected.textFont")


        val textViewTheme1 = TextViewTheme()
        textViewTheme1.textColor = Color.parseColor(ThemeManager.getValue("goPay.loginBar.title.otherSegmentSelected.textColor"))
        textViewTheme1.font = ThemeManager.getFontName("goPay.loginBar.title.otherSegmentSelected.textFont")
        goPayLoginInput.loginTabLayout.tabTextColors = ColorStateList.valueOf(textViewTheme1.textColor!!)



        goPayLoginInput.textInputLayout.hintTextColor = ColorStateList.valueOf(Color.parseColor(ThemeManager.getValue("goPay.loginBar.hintLabel.textColor")))
        //TODO     java.lang.RuntimeException: Font asset not found /Roboto-Light        goPayLoginInput.textInputLayout.typeface = Typeface.createFromFile(ThemeManager.getFontName("goPay.loginBar.hintLabel.textFont"))
    }

    fun setThemeToGoPayPasswordInput(){

        val editTextTheme = EditTextTheme()
        editTextTheme.backgroundColor = Color.parseColor(ThemeManager.getValue("goPay.passwordField.backgroundColor"))
     //TODO  editTextTheme.textSize =  ThemeManager.getFontName("goPay.passwordField.textFont").toDouble()  (java.lang.NumberFormatException: For input string:
        editTextTheme.textColor =Color.parseColor(ThemeManager.getValue("goPay.passwordField.textColor"))
        editTextTheme.placeHolderColor = Color.parseColor(ThemeManager.getValue("goPay.passwordField.placeHolderColor"))
        editTextTheme.showPasswordIcon = ThemeManager.getValue("goPay.passwordField.showPasswordIcon")
        editTextTheme.hidePasswordIcon = ThemeManager.getValue("goPay.passwordField.hidePasswordIcon")
        editTextTheme.underlineSelectemptyBackground = Color.parseColor(ThemeManager.getValue("goPay.passwordField.underline.empty.backgroundColor"))
        editTextTheme.underlineSelectfillBackground = Color.parseColor(ThemeManager.getValue("goPay.passwordField.underline.filled.backgroundColor"))
        goPayPasswordInput.passwordTextInput.setTheme(editTextTheme)

        goPayPasswordInput.textInputLayout.boxBackgroundColor = Color.parseColor(ThemeManager.getValue("goPay.passwordView.backgroundColor"))



    }

    /*
        "goPay": {
        "loginBar" :{
            "backgroundColor" : "whiteTwo",
            "underline": {
                "selected": {
                    "backgroundColor" : "deepSkyBlue"
                },
                "unselected": {
                    "backgroundColor" : "brownGrey"
                }
            },
            "title": {
                "selected": {
                    "textFont": "Roboto-Regular,12",
                    "textColor": "greyishBrown"
                },
                "otherSegmentSelected" : {
                    "textFont": "Roboto-Regular,12",
                    "textColor": "brownGreyFive"
                }
            },
            "hintLabel": {
                "textFont": "Roboto-Light,10",
                "textColor": "brownGreyThree"
            }
        },
   ///     "passwordView" :{
            "backgroundColor": "clear"
        },
 ///       "passwordField": {
            "backgroundColor" : "clear",
            "textFont": "Roboto-Light,17",
            "textColor": "greyishBrown",
            "placeHolderColor" : "brownGreyThree",
            "showPasswordIcon" : "passwordEye",
            "hidePasswordIcon" : "passwordEye",
            "underline":{
                "empty": {
                    "backgroundColor" : "brownGrey"
                },
                "filled": {
                    "backgroundColor" : "deepSkyBlue"
                }
            }
        }
    },
     */



}