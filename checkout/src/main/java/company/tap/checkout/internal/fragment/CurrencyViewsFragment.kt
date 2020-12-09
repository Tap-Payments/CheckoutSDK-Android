package company.tap.checkout.internal.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkout.R
import company.tap.checkout.internal.adapter.CurrencyTypeAdapter
import company.tap.checkout.internal.adapter.ItemAdapter
import company.tap.checkout.internal.dummygener.Currencies1

import company.tap.checkout.internal.dummygener.Items1
import company.tap.checkout.internal.interfaces.OnCurrencyChangedActionListener
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapChipGroup
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import kotlinx.android.synthetic.main.item_frame_currencies.*


/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
open class CurrencyViewsFragment(private var currencyLists1: ArrayList<Currencies1>, private var itemLists: List<Items1>?,private var onCurrencyChangedActionListener:OnCurrencyChangedActionListener) : Fragment(){
    private lateinit var chipRecycler: RecyclerView


    lateinit var currenciesList: ArrayList<Currencies1>


    private lateinit var itemsRecycler: RecyclerView

    private lateinit var itemList: ArrayList<Items1>
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.item_frame_currencies, container, false)

        currenciesList = currencyLists1
        itemList = itemLists as ArrayList<Items1>
        val currencyGroup = view.findViewById<TapChipGroup>(R.id.currencyLayout1)
        val mainView = view.findViewById<LinearLayout>(R.id.mainView)
        val headerView = view.findViewById<ConstraintLayout>(R.id.header_view)
//        headerView.visibility=View.GONE
        currencyGroup.orientation = LinearLayout.HORIZONTAL
        val groupName = currencyGroup.findViewById<TapTextView>(R.id.group_name)
        groupName.visibility = View.GONE
        val groupAction = currencyGroup.findViewById<TapTextView>(R.id.group_action)
        groupAction.visibility = View.GONE
        chipRecycler = currencyGroup.findViewById<View>(R.id.chip_recycler) as RecyclerView
       // chipRecycler.setHasFixedSize(true)
        chipRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        // chipRecycler.adapter = CurrencyAdapter(currencyList)
        if(currenciesList==null||currenciesList.isEmpty()){return null}
        if (this::currenciesList.isInitialized)
            chipRecycler.adapter = CurrencyTypeAdapter(currenciesList,onCurrencyChangedActionListener)
        itemsRecycler = view.findViewById<View>(R.id.items_recylerview) as RecyclerView

//        itemsRecycler.setHasFixedSize(false)
        headerView.visibility = View.GONE

        itemsRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        if (this::itemList.isInitialized)
            itemsRecycler.adapter = ItemAdapter(itemList)


        val divider = DividerItemDecoration(
            context,
            DividerItemDecoration.HORIZONTAL
        )
        divider.setDrawable(ShapeDrawable().apply {
            intrinsicWidth = 25
            paint.color = Color.TRANSPARENT
        }) // note: currently (support version 28.0.0), we can not use tranparent color here, if we use transparent, we still see a small divider line. So if we want to display transparent space, we can set color = background color or we can create a custom ItemDecoration instead of DividerItemDecoration.
        chipRecycler.addItemDecoration(divider)


//        chipRecycler.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        currencyGroup.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        itemsRecycler.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        headerView.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))

        itemsRecycler.setOnTouchListener { v, event ->
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

        return view
    }



    fun setTheme() {
        chipRecycler.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        currencyLayout1.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))

        chipRecycler.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))

        mainView.setBackgroundColor(Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")))
        itemsRecycler.setBackgroundColor(Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")))


        //currencyLayout1
    }

    @JvmName("setCurrencyList1")
    fun setCurrencyList(ApiCurrencyList: ArrayList<Currencies1>) {
        currenciesList = ApiCurrencyList
    }


}