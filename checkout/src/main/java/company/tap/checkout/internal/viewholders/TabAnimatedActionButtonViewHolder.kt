package company.tap.checkout.internal.viewholders


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.uikit.views.TabAnimatedActionButton

/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class TabAnimatedActionButtonViewHolder(context: Context) : TapBaseViewHolder {

    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.action_button_animation, null)
    override val type = SectionType.ActionButton

    val actionButton by lazy { view.findViewById<TabAnimatedActionButton>(R.id.actionButton) }


    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {
        activateButton()
    }



    fun activateButton() {
        actionButton.setButtonDataSource(
            true,
            view.context.let { it?.let { it1 -> LocalizationManager.getLocale(it1).language } },
            LocalizationManager.getValue("pay", "ActionButton"),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
        )
    }


}