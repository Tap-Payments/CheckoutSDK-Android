package company.tap.tapuilibrary.uikit.fragment

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
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.adapters.CurrencyAdapter
import company.tap.tapuilibrary.uikit.adapters.ItemAdapter
import company.tap.tapuilibrary.uikit.atoms.TapChipGroup
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.model.CurrencyModel
import kotlinx.android.synthetic.main.item_frame_currency.*


/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
///  set on currency item click action and send request to api to get items list with converted amount and make interface to notify amount section with new converted currency amount
open class CurrencyViewFragment : Fragment() {
    private lateinit var chipRecycler: RecyclerView
    lateinit var currencyList: ArrayList<CurrencyModel>

    private lateinit var itemsRecycler: RecyclerView
    private val itemList: ArrayList<Int> = arrayListOf(1, 2, 3, 4, 5, 6,7,8,9,10,11,12,13,14,15,16,17,18,20,21,22)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.item_frame_currency, container, false)
        fillData()

        val currencyGroup = view.findViewById<TapChipGroup>(R.id.currencyLayout1)
        val headerView = view.findViewById<ConstraintLayout>(R.id.header_view)
//        headerView.visibility=View.GONE
        currencyGroup.orientation = LinearLayout.HORIZONTAL
        val groupName = currencyGroup.findViewById<TapTextView>(R.id.group_name)
        groupName.visibility=View.GONE
        val groupAction = currencyGroup.findViewById<TapTextView>(R.id.group_action)
        groupAction.visibility=View.GONE
        chipRecycler = currencyGroup.findViewById<View>(R.id.chip_recycler) as RecyclerView
        chipRecycler.setHasFixedSize(true)
        chipRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        chipRecycler.adapter = CurrencyAdapter(currencyList)
        itemsRecycler = view.findViewById<View>(R.id.items_recylerview) as RecyclerView
//        itemsRecycler.setHasFixedSize(false)
        headerView.visibility = View.GONE

        itemsRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
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

    //Filling dummy data for currency chips
    private fun fillData() {
        currencyList = ArrayList()

        //adding some dummy data to the list
        currencyList.add(
            CurrencyModel(
                "KWD",
                "https://www.countryflags.io/kw/flat/24.png"
            )
        )
        currencyList.add(
            CurrencyModel(
                "SAR",
                "https://www.countryflags.io/sa/flat/24.png"
            )
        )
        currencyList.add(
            CurrencyModel(
                "BHD",
                "https://www.countryflags.io/bh/flat/24.png"
            )
        )
        currencyList.add(
            CurrencyModel(
                "QAR",
                "https://www.countryflags.io/qa/flat/24.png"
            )
        )
        currencyList.add(
            CurrencyModel(
                "KWD",
                "https://www.countryflags.io/kw/flat/24.png"
            )
        )
        currencyList.add(
            CurrencyModel(
                "SAR",
                "https://www.countryflags.io/sa/flat/24.png"
            )
        )

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
    fun setCurrencyList(ApiCurrencyList: ArrayList<String>) {
//        currencyList = ApiCurrencyList
    }
}