package company.tap.checkout.internal.viewholders


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.adapters.context
import company.tap.tapuilibrary.uikit.datasource.ActionButtonDataSource
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
import kotlinx.android.synthetic.main.action_button_animation.view.*

/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class TabAnimatedActionButtonViewHolder11(context: Context) : TapBaseViewHolder {

    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.action_button_animation, null)
    override val type = SectionType.ActionButton

    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {
         // view.actionButton.setButtonDataSource(getSuccessDataSource(Color.GRAY, "Pay", Color.WHITE ))
//        view.actionButton.setButtonDataSource(false,
//            view.context?.let { LocalizationManager.getLocale(it).language },LocalizationManager.getValue("pay","ActionButton"),R. color.gray)

        activateBlueConfirmButton(view.context)
    }



    fun activateButton(context: Context) {
        view.actionButton.setButtonDataSource(true,
            LocalizationManager.getLocale(context).language,LocalizationManager.getValue("pay","ActionButton"),R.color.gray)
    }

    fun activateBlueConfirmButton(context: Context) {
        view.actionButton.setButtonDataSource(
            true, context.let { LocalizationManager.getLocale(it).language },
            LocalizationManager.getValue(
                "confirm",
                "ActionButton"
            ),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.goLoginBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
        )}

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