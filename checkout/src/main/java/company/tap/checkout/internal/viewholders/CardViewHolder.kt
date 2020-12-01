package company.tap.checkout.internal.viewholders

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import company.tap.cardbusinesskit.testmodels.Payment_methods
import company.tap.checkout.R
import company.tap.checkout.internal.dummygener.SavedCards
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.adapters.CardTypeAdapter
import company.tap.tapuilibrary.uikit.atoms.TapChipGroup
import company.tap.tapuilibrary.uikit.interfaces.OnCardSelectedActionListener
import kotlinx.android.synthetic.main.businessview_layout.view.*
import kotlinx.android.synthetic.main.cardviewholder_layout.view.*
import kotlinx.android.synthetic.main.item_saved_card.view.*

/**
 *
 * Created by Mario Gamal on 7/26/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class CardViewHolder(
    private val context: Context,
    private val onCardSelectedActionListener: OnCardSelectedActionListener,
    private val baseLayopu: BaseLayoutManager?=null
) : TapBaseViewHolder {

  //  override val view = TapChipGroup(context, null)
  override val view: View = LayoutInflater.from(context).inflate(R.layout.cardviewholder_layout, null)


    override val type = SectionType.CARD


    private var paymentsList: List<SavedCards>? = null


    init {
        bindViewComponents()

    }

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
        view.mainChipgroup.chipsRecycler.adapter =
            paymentsList?.let { CardTypeAdapter(it as ArrayList<SavedCards>, onCardSelectedActionListener,false) }
        println("paymentList supported currency ${paymentsList}")
        view.mainChipgroup.groupAction.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this.view.context, R.anim.shake)
            view.mainChipgroup.chipsRecycler.startAnimation(animation)
            view.deleteImageView2?.visibility = View.VISIBLE
            baseLayopu?.displayGoPayLogin()
        }

        /**
         * set separator background
         */
        view.tapSeparatorViewLinear?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))


    }


    /**
     * Sets data from API through LayoutManager
     * @param paymentMethodsApi represents the list of payment methods available from API
     * */
    fun setDatafromAPI(paymentMethodsApi: List<SavedCards>) {
        paymentsList = paymentMethodsApi
        bindViewComponents()
    }
}