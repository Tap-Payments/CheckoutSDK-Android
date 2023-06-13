package company.tap.checkout.internal.viewholders

import SupportedCurrencies
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
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.OnCurrencyChangedActionListener
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.open.models.ItemsModel
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.uikit.atoms.TapChipGroup
import company.tap.tapuilibraryy.uikit.atoms.TapSeparatorView
import company.tap.tapuilibraryy.uikit.utils.MetricsUtil
import kotlinx.android.synthetic.main.cardviewholder_layout1.view.*
import kotlinx.android.synthetic.main.itemviewholder_layout.view.*

/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
@SuppressLint("ClickableViewAccessibility")
class ItemsViewHolder(private val context: Context, private val onCurrencyChangedActionListener: OnCurrencyChangedActionListener) :
    TapBaseViewHolder {
    override val view = LayoutInflater.from(context).inflate(
        R.layout.itemviewholder_layout,
        null
    )

    override val type = SectionType.SELECT

    var itemsdisplayed: Boolean = false
    private lateinit var supportedCurrecnyList: ArrayList<SupportedCurrencies>
    private  var supportedItemList: List<ItemsModel>?=null
     var mainCurrencyChip: TapChipGroup
     var itemsRecyclerView:RecyclerView
     var currencyRecyclerView:RecyclerView
     var headerview:ConstraintLayout
     var itemSeparatorView: TapSeparatorView
    init {
        mainCurrencyChip = view.findViewById(R.id.mainCurrencyChip)
        mainCurrencyChip.groupAction.visibility = View.GONE

        itemsRecyclerView = view.findViewById(R.id.itemRecylerView)
        currencyRecyclerView = mainCurrencyChip.findViewById<View>(R.id.chip_recycler) as RecyclerView
        headerview = view.findViewById<View>(R.id.header_view) as ConstraintLayout
        itemSeparatorView = view.findViewById<View>(R.id.item_Separator) as TapSeparatorView
        mainCurrencyChip.groupName.text = LocalizationManager.getValue("currencyAlert","Common")
        mainCurrencyChip.groupName.visibility = View.VISIBLE

       /**
        * Added for spacing alignment in ar**/
//        if(CustomUtils.getCurrentLocale(context).contains("ar")) mainCurrencyChip.setPaddingRelative( MetricsUtil.convertDpToPixel(-8f,context).toInt(),0, MetricsUtil.convertDpToPixel(-8f,context).toInt(),0)
//        else mainCurrencyChip.left =( MetricsUtil.convertDpToPixel(20f,context).toInt())
        mainCurrencyChip.groupName.setPaddingRelative( MetricsUtil.convertDpToPixel(5f,context).toInt(),0, MetricsUtil.convertDpToPixel(5f,context).toInt(),0)
        if(CustomUtils.getCurrentLocale(context).contains("en")) mainCurrencyChip.chipsRecycler.setPaddingRelative( MetricsUtil.convertDpToPixel(12f,context).toInt(),0, 0,0)
          else mainCurrencyChip.chipsRecycler.setPaddingRelative( MetricsUtil.convertDpToPixel(-2f,context).toInt(),0,0,0)
        view.mainCurrencyChip.chipsRecycler.elevation = 0f
        itemsRecyclerViewAction(itemsRecyclerView)
        setRecyclerViewDivider(currencyRecyclerView)
        currencyRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        itemsRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        // currencyRecyclerView.adapter = adapterCurrency
        mainCurrencyChip.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        itemsRecyclerView.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        itemSeparatorView.setBackgroundColor(Color.parseColor(ThemeManager.getValue("itemsList.separatorColor")))
        itemSeparatorView.visibility = View.VISIBLE


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
    fun setDataFromAPI(
        supportedCurrencyApi: ArrayList<SupportedCurrencies>,
        supportItemListApi: List<ItemsModel>?
    ) {
        supportedCurrecnyList = supportedCurrencyApi
        if (supportItemListApi != null) {
            supportedItemList = supportItemListApi
        }

    }

}