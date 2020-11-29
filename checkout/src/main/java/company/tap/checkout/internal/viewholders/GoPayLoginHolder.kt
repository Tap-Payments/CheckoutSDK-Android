package company.tap.checkout.internal.viewholders

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import company.tap.cardbusinesskit.testmodels.Payment_methods
import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.uikit.adapters.CardTypeAdapter
import company.tap.tapuilibrary.uikit.atoms.TapChipGroup
import company.tap.tapuilibrary.uikit.interfaces.OnCardSelectedActionListener
import kotlinx.android.synthetic.main.item_saved_card.view.*

/**
 *
 * Created by Mario Gamal on 7/26/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class GoPayLoginHolder(
    private val context: Context,
    private val onCardSelectedActionListener: OnCardSelectedActionListener? = null,
    private val baseLayopu: BaseLayoutManager?=null
) : TapBaseViewHolder {

    override val view = TapChipGroup(context, null)

    override val type = SectionType.CARD


    private var paymentsList: List<Payment_methods>? = null


    init {
        bindViewComponents()

    }

    override fun bindViewComponents() {

        view.groupName.text = "GoPay"
        view.groupAction.text = LocalizationManager.getValue(
            "GatewayHeader",
            "HorizontalHeaders",
            "rightTitle"
        )

        view.chipsRecycler.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.HORIZONTAL,
            false
        )
        view.chipsRecycler.adapter =
            paymentsList?.let { CardTypeAdapter(it, onCardSelectedActionListener,false) }
        println("paymentList supported currency ${paymentsList?.get(0)?.supported_currencies}")
        view.groupAction.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this.view.context, R.anim.shake)
            view.chipsRecycler.startAnimation(animation)
            view.deleteImageView?.visibility = View.VISIBLE
            baseLayopu?.displayGoPayLogin()
        }

    }


    /**
     * Sets data from API through LayoutManager
     * @param paymentMethodsApi represents the list of payment methods available from API
     * */
    fun setDatafromAPI(paymentMethodsApi: List<Payment_methods>) {
        paymentsList = paymentMethodsApi
        bindViewComponents()
    }
}