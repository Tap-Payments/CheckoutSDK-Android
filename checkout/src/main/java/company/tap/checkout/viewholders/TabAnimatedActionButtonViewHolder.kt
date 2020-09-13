package company.tap.checkout.viewholders


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.enums.SectionType
import company.tap.tapuilibrary.uikit.datasource.ActionButtonDataSource

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
        //  view.actionButton.setButtonDataSource(getSuccessDataSource(Color.GRAY, "Pay", Color.WHITE ))
    }

    fun activateButton() {
        //  view.actionButton.(getSuccessDataSource(Color.GREEN,"", Color.WHITE))
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
        /* view.actionButton.addChildView(
             view.actionButton.getImageView(
                 R.drawable.loader,
                 1
             ) {  })*/
        //  view.actionButton.changeButtonState(ActionButtonState.LOADING)
    }

    fun setActionButtonTheme() {

    }
}