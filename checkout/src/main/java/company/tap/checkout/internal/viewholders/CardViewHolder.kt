package company.tap.checkout.internal.viewholders

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkout.R
import company.tap.checkout.internal.adapter.CardTypeAdapterUIKIT
import company.tap.checkout.internal.api.models.PaymentOption
import company.tap.checkout.internal.api.models.SavedCard


import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.OnCardSelectedActionListener
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.TextViewTheme
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import kotlinx.android.synthetic.main.cardviewholder_layout1.view.*


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class CardViewHolder(private val context: Context, private val onCardSelectedActionListener: OnCardSelectedActionListener) : TapBaseViewHolder {

    override val view: View = LayoutInflater.from(context).inflate(R.layout.cardviewholder_layout1, null)
    override val type = SectionType.CARD
    private var paymentCardsList: MutableList<PaymentOption>? = null
    private var saveCardsList: MutableList<SavedCard>? = null
    lateinit var cardInfoHeaderText: TapTextView

    init { bindViewComponents() }

    override fun bindViewComponents() {
        view.mainChipgroup.groupName.text = LocalizationManager.getValue(
            "GatewayHeader",
            "HorizontalHeaders",
            "leftTitle"
        )
        view.mainChipgroup.groupAction.text = LocalizationManager.getValue(
            "GatewayHeader",
            "HorizontalHeaders",
            "rightTitle"
        )
        view.mainChipgroup.chipsRecycler.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.HORIZONTAL,
            false
        )
        val adapter = CardTypeAdapterUIKIT(onCardSelectedActionListener )
        view.mainChipgroup.chipsRecycler.adapter = adapter
        view.mainChipgroup.chipsRecycler.elevation = 0F
        paymentCardsList?.let { adapter.updateAdapterData(it) }
        println("saveCardsList in adapter"+saveCardsList)
        println("paymentCardsList in adapter"+paymentCardsList)
        saveCardsList?.let { adapter.updateAdapterDataSavedCard(it) }
        /**
         * set separator background
         */
        view.tapSeparatorViewLinear1?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        cardInfoHeaderText = view.findViewById(R.id.cardInfoHeaderText)
        if(paymentCardsList?.isEmpty() == true){
            cardInfoHeaderText.text =  LocalizationManager.getValue("cardSectionTitle", "TapCardInputKit")

        }else{
       // cardInfoHeaderText.text =  LocalizationManager.getValue("cardSectionTitle", "TapCardInputKit")
        cardInfoHeaderText.text =  "OR ENTER CARD"
        }
       /* var cardInfoTextTheme = TextViewTheme()
        cardInfoTextTheme.textColor =
            Color.parseColor(ThemeManager.getValue("horizontalList.headers.labelTextColor"))
        cardInfoTextTheme.textSize =
            ThemeManager.getFontSize("horizontalList.headers.labelTextFont")
        cardInfoTextTheme.font = ThemeManager.getFontName("horizontalList.headers.labelTextFont")
        cardInfoHeaderText.setTheme(cardInfoTextTheme)*/
    }

    /**
     * Sets data from API through LayoutManager
     * @param savedCardMethodsApi represents the list of payment methods available from API
     * */
    fun setDatafromAPI(paymentCardMethodsApi: MutableList<PaymentOption>) {
        paymentCardsList = paymentCardMethodsApi
        bindViewComponents()
    }
}