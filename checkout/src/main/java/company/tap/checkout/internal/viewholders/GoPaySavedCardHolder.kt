package company.tap.checkout.internal.viewholders

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkout.R
import company.tap.checkout.internal.adapter.CardTypeAdapterUIKIT
import company.tap.checkout.internal.adapter.GoPayCardAdapterUIKIT
import company.tap.checkout.internal.dummygener.GoPaySavedCards
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayouttManager
import company.tap.checkout.internal.interfaces.OnCardSelectedActionListener
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import kotlinx.android.synthetic.main.cardviewholder_layout.view.*
import kotlinx.android.synthetic.main.gopayloginview_layout.view.*
import kotlinx.android.synthetic.main.gopayloginview_layout.view.tapSeparatorViewLinear

/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class GoPaySavedCardHolder(
    private val context: Context,
    private val onCardSelectedActionListener: OnCardSelectedActionListener,
    private val baseLayouttManager: BaseLayouttManager?=null
) : TapBaseViewHolder {

  //  override val view = TapChipGroup(context, null)
    override val view: View = LayoutInflater.from(context).inflate(R.layout.gopaysavedcard_layout, null)
    override val type = SectionType.CARD
    private var goPaySavedCardsList: List<GoPaySavedCards>? = null
    private var chipRecyclerView: RecyclerView
    private var groupAction: TapTextView


    init {
        bindViewComponents()
        chipRecyclerView = view.findViewById(R.id.chip_recycler)
        groupAction = view.findViewById(R.id.group_action)
    }

    override fun bindViewComponents() {
        view.goPayLoginView.groupName.text = LocalizationManager.getValue("GoPayAlert", "Hints", "goPayTitle")
        view.goPayLoginView.groupAction.text = LocalizationManager.getValue("GatewayHeader", "HorizontalHeaders", "rightTitle")
        view.goPayLoginView.chipsRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

//        groupAction.setOnClickListener {
//            onCardSelectedActionListener.onEditClicked(true)
//            //val adapter = CardTypeAdapterUIKIT(onCardSelectedActionListener)
//            if (groupAction.text == "Close") {
//                onCardSelectedActionListener.onEditClicked(false)
//                groupAction.text =  LocalizationManager.getValue("edit", "Common")
//            } else {
//                onCardSelectedActionListener.onEditClicked(true)
//                groupAction.text = LocalizationManager.getValue("close", "Common")
//            }
//        }

        /**
         * set separator background
         */
        view.tapSeparatorViewLinear?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
    }


    /**
     * Sets data from API through LayoutManager
     * @param goPaySavedCardsApi represents the list of payment methods available from API
     * */
    fun setDatafromAPI(goPaySavedCardsApi: List<GoPaySavedCards>) {
        goPaySavedCardsList = goPaySavedCardsApi
        bindViewComponents()
        val adapter = GoPayCardAdapterUIKIT(onCardSelectedActionListener)
        chipRecyclerView.adapter = adapter
        adapter.updateShaking(false)
        goPaySavedCardsList?.let { adapter.updateAdapterData(it) }
    }

}