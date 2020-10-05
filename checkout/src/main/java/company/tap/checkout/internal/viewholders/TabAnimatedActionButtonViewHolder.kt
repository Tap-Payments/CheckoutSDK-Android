package company.tap.checkout.internal.viewholders


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.uikit.adapters.context
import company.tap.tapuilibrary.uikit.datasource.ActionButtonDataSource
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
import kotlinx.android.synthetic.main.action_button_animation.view.*

/**
 * Created by OlaMonir on 8/25/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class TabAnimatedActionButtonViewHolder(context: Context) : TapBaseViewHolder {

    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.action_button_animation, null)
    override val type = SectionType.ActionButton


    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {
         // view.actionButton.setButtonDataSource(getSuccessDataSource(Color.GRAY, "Pay", Color.WHITE ))
        view.actionButton.setButtonDataSource(false,
            context?.let { LocalizationManager.getLocale(it).language },LocalizationManager.getValue("pay","ActionButton"))
    }

    fun activateButton(context: Context) {
        view.actionButton.setButtonDataSource(true,
            LocalizationManager.getLocale(context).language,LocalizationManager.getValue("pay","ActionButton"))
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

    fun setOnClickAction() {
         /*view.actionButton.addChildView(
             view.actionButton.getImageView(
                 R.drawable.loader,
                 1
             ) {  })*/
          view.actionButton.changeButtonState(ActionButtonState.LOADING)
    }

}