package company.tap.checkout.internal.viewholders

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkout.R
import company.tap.checkout.internal.adapter.CurrencyTypeAdapter
import company.tap.checkout.internal.adapter.ItemAdapter
import company.tap.checkout.internal.api.models.AmountedCurrency

import company.tap.checkout.internal.dummygener.Items1
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.OnCurrencyChangedActionListener
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapChipGroup

/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
@SuppressLint("ClickableViewAccessibility")
class ItemsViewHolder1(private val context: Context, private val onCurrencyChangedActionListener: OnCurrencyChangedActionListener) :
    TapBaseViewHolder {
    override val view = LayoutInflater.from(context).inflate(
        R.layout.itemviewholder_layout,
        null
    )

    override val type = SectionType.SELECT

    var itemsdisplayed: Boolean = false
    private lateinit var supportedCurrecnyList: ArrayList<AmountedCurrency>
    private lateinit var supportedItemList: List<Items1>
     var mainCurrencyChip: TapChipGroup
     var itemsRecyclerView:RecyclerView
     var currencyRecyclerView:RecyclerView
     var headerview:ConstraintLayout
    private val adapterItems by lazy { ItemAdapter() }
    private val adapterCurrency by lazy { CurrencyTypeAdapter(onCurrencyChangedActionListener) }

    init {
        mainCurrencyChip = view.findViewById(R.id.mainCurrencyChip)
        mainCurrencyChip.groupAction.visibility = View.GONE
        mainCurrencyChip.groupName.visibility = View.GONE

        itemsRecyclerView = view.findViewById(R.id.itemRecylerView)
        currencyRecyclerView = mainCurrencyChip.findViewById<View>(R.id.chip_recycler) as RecyclerView
        headerview = view.findViewById<View>(R.id.header_view) as ConstraintLayout
        headerview.visibility = View.GONE
        itemsRecyclerViewAction(itemsRecyclerView)
        setRecyclerViewDivider(currencyRecyclerView)
        mainCurrencyChip.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        itemsRecyclerView.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
    }

    private fun itemsRecyclerViewAction(itemsRecyclerView: RecyclerView) {
        itemsRecyclerView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN ->                         // Disallow NestedScrollView to intercept touch events.
                    v.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP ->                         // Allow NestedScrollView to intercept touch events.
                    v.parent.requestDisallowInterceptTouchEvent(false)
            }
            // Handle RecyclerView touch events.
            v.onTouchEvent(event)
            true
        }
    }

    private fun setRecyclerViewDivider(currencyRecyclerView: RecyclerView) {
        val divider = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
        divider.setDrawable(ShapeDrawable().apply {
            intrinsicWidth = 25
            paint.color = Color.TRANSPARENT
        }) // note: currently (support version 28.0.0), we can not use tranparent color here, if we use transparent, we still see a small divider line. So if we want to display transparent space, we can set color = background color or we can create a custom ItemDecoration instead of DividerItemDecoration.
        currencyRecyclerView.addItemDecoration(divider)
    }

    override fun bindViewComponents() {

    }

    /**
     * Sets data from API through LayoutManager
     * @param supportedCurrencyApi represents the supported currency for the Merchant.
     * @param supportItemListApi represents the supported currency for the Merchant.
     * */
    fun setDatafromAPI(
        supportedCurrencyApi: ArrayList<AmountedCurrency>,
        supportItemListApi: List<Items1>?
    ) {
        supportedCurrecnyList = supportedCurrencyApi
        if (supportItemListApi != null) {
            supportedItemList = supportItemListApi
        }
//        println("supportedItemList curr list:$supportedItemList")

    }


    fun setCurrencyRecylerView(){
        currencyRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        currencyRecyclerView.adapter = adapterCurrency
        adapterCurrency.updateAdapterData(supportedCurrecnyList)
    }
    fun setItemsRecylerView(){
        itemsRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        itemsRecyclerView.adapter = adapterItems
       // if(supportedItemList)
        adapterItems.updateAdapterData(supportedItemList)

    }

    fun setResetItemsRecylerView(itemsListUpdated:List<Items1>){
        itemsRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        itemsRecyclerView.adapter = adapterItems
        adapterItems.updateAdapterData(itemsListUpdated)

    }

}