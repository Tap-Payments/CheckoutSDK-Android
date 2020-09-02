package company.tap.checkout.viewholders

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkout.adapters.CardAdapter
import company.tap.checkout.enums.SectionType
import company.tap.checkout.interfaces.OnCardSelectedActionListener
import company.tap.tapuilibrary.atoms.TapChipGroup

import company.tap.thememanager.manager.ThemeManager
import company.tap.thememanager.theme.ButtonTheme
import company.tap.thememanager.theme.TextViewTheme

/**
 *
 * Created by Mario Gamal on 7/26/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class CardViewHolder(private val context: Context,private val onCardSelectedActionListener: OnCardSelectedActionListener? = null ) : TapBaseViewHolder {

    override val view = TapChipGroup(context, null)

    override val type = SectionType.CARD

    private val paymentsList: ArrayList<Int> = arrayListOf(1, 2, 3, 4, 5, 6)

    init {
        bindViewComponents()
        setThemeToLeftButton()
        setThemeToRightButton()
    }

    override fun bindViewComponents() {
        view.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.headers.gatewayHeader.backgroundColor")))
        view.groupName.text = "Select"
        view.groupAction.text = "Edit"
        view.chipsRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        view.chipsRecycler.adapter = CardAdapter(paymentsList, onCardSelectedActionListener)
    }



    fun setThemeToLeftButton(){
        val buttonTheme = TextViewTheme()
        buttonTheme.textColor = Color.parseColor(ThemeManager.getValue("horizontalList.headers.gatewayHeader.leftButton.labelTextColor"))
//        buttonTheme.textSize = ThemeManager.getFontSize("horizontalList.headers.gatewayHeader.leftButton.labelTextFont").toFloat()
        buttonTheme.font = ThemeManager.getFontName("horizontalList.headers.gatewayHeader.leftButton.labelTextFont")
//        view.groupName.setTheme(buttonTheme)
    }

    fun setThemeToRightButton(){
        val buttonTheme = TextViewTheme()
        buttonTheme.textColor = Color.parseColor(ThemeManager.getValue("horizontalList.headers.gatewayHeader.rightButton.labelTextColor"))
 //       buttonTheme.textSize = ThemeManager.getFontSize("horizontalList.headers.gatewayHeader.rightButton.labelTextFont").toFloat()
        buttonTheme.font = ThemeManager.getFontName("horizontalList.headers.gatewayHeader.rightButton.labelTextFont")
//        view.groupAction.setTheme(buttonTheme)
    }

    /*
    horizontalList
     "headers": {
            "gatewayHeader": {
                "backgroundColor" : "whiteTwo",
                "leftButton": {
                    "labelTextFont": "Roboto-Regular,10",
                    "labelTextColor": "greyishBrown"
                },
                "rightButton": {
                    "labelTextFont": "Roboto-Regular,10",
                    "labelTextColor": "greyishBrown"
                }
            }
        },
     */

}