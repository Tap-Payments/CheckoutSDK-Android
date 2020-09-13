package company.tap.checkout.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import company.tap.checkout.R
import company.tap.checkout.enums.SectionType
import company.tap.checkout.utils.AnimationEngine
import company.tap.checkout.utils.AnimationEngine.Type.SLIDE
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


    fun setThemeToGoPayLoginInput() {
//        goPayLoginInput.textInput.
    }

    fun setThemeToGoPayPasswordInput() {

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

//    fun setThemeToBusinessName(){
//        val textViewTheme = TextViewTheme()
//        textViewTheme.textColor = Color.parseColor(ThemeManager.getValue("merchantHeaderView.titleLabelColor"))
//        textViewTheme.textSize = ThemeManager.getFontSize("merchantHeaderView.titleLabelFont").toFloat()
//        textViewTheme.font = ThemeManager.getFontName("merchantHeaderView.titleLabelFont")
//        view.businessName.setTheme(textViewTheme)
//    }

}