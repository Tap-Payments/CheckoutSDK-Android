package company.tap.tapuilibraryy.uikit.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.R
import company.tap.tapuilibraryy.fontskit.enums.TapFont
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.themekit.theme.SeparatorViewTheme
import company.tap.tapuilibraryy.themekit.theme.TextViewTheme
import company.tap.tapuilibraryy.uikit.atoms.TapSeparatorView
import company.tap.tapuilibraryy.uikit.atoms.TapTextViewNew
import company.tap.tapuilibraryy.uikit.datasource.ItemViewDataSource
import company.tap.tapuilibraryy.uikit.ktx.setBorderedView
import company.tap.tapuilibraryy.uikit.views.TapListItemView


/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class ItemAdapter(private val itemList: ArrayList<Int>) :
    RecyclerView.Adapter<ItemAdapter.ItemHolder>() {
    private var previousExpandedPosition = -1
    private var mExpandedPosition = -1
    private lateinit var itemViewAdapter: TapListItemView


    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ItemHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_view_adapter, parent, false)
        context = parent.context
        itemViewAdapter = v.findViewById(R.id.amount_item_view)
        return ItemHolder(v)
    }

    override fun getItemCount() = itemList.size


    class ItemHolder(v: View) : RecyclerView.ViewHolder(v)

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        initView(holder, position)
    }

    private fun initView(holder: ItemHolder, position: Int) {
        val descriptionTextView = holder.itemView.findViewById<TapTextViewNew>(R.id.description_textView)
        val descText = holder.itemView.findViewById<TapTextViewNew>(R.id.show_description)
        val itemSeparator = holder.itemView.findViewById<TapSeparatorView>(R.id.itemseparator)
        val totalQuantity = holder.itemView.findViewById<TapTextViewNew>(R.id.total_quantity)
        val discount = holder.itemView.findViewById<TapTextViewNew>(R.id.discount_text)
        val quantityRelative = holder.itemView.findViewById<RelativeLayout>(R.id.quantityRelative)
        val totalAmount = holder.itemView.findViewById<TapTextViewNew>(R.id.total_amount)
        val mainViewLinear = holder.itemView.findViewById<LinearLayout>(R.id.mainViewLinear)
        val itemName = holder.itemView.findViewById<TapTextViewNew>(R.id.item_title)
        val itemAmount = holder.itemView.findViewById<TapTextViewNew>(R.id.item_amount)
        val isExpanded = position == mExpandedPosition

        descriptionTextView.text = "Lorem ipsum dolor sit amet, ex exercitation ullamco laboris."
        descriptionTextView.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.itemView.isActivated = isExpanded
        holder.itemView.setBackgroundColor( Color.parseColor(ThemeManager.getValue("itemsList.item.backgroundColor")))

        onItemClickAction(holder, position, isExpanded)
        showHideDescText(isExpanded, position, descText)
        setTheme(descriptionTextView,discount,descText,totalQuantity,totalAmount,itemName,itemSeparator,mainViewLinear,quantityRelative,itemAmount)
        setFonts(itemName,totalAmount,discount,descText,descriptionTextView,totalQuantity,itemAmount)
        checkItemListPosition(position,discount, totalAmount, itemName)
    }

    private fun showHideDescText(isExpanded: Boolean, position: Int, descText:TapTextViewNew?) {
        if (isExpanded) {
            previousExpandedPosition = position
            descText?.text = LocalizationManager.getValue("hideDesc", "ItemList")
        } else {
            descText?.text = LocalizationManager.getValue("showDesc", "ItemList")
        }
    }

    private fun onItemClickAction(holder: ItemHolder, position: Int, isExpanded: Boolean) {
        holder.itemView.setOnClickListener {
            itemViewAdapter.visibility = View.VISIBLE
            mExpandedPosition = if (isExpanded) -1 else position
            notifyItemChanged(previousExpandedPosition)
            notifyItemChanged(position)
        }
    }


    private fun checkItemListPosition(position: Int, discount:TapTextViewNew?, totalAmount:TapTextViewNew?, itemName: TapTextViewNew? ) {
        if (itemList[position] % 2 == 0) {
            discount?.visibility = View.VISIBLE
            discount?.text = LocalizationManager.getValue("Discount", "ItemList")
            totalAmount?.paintFlags = totalAmount?.paintFlags?.or(Paint.STRIKE_THRU_TEXT_FLAG)!!
            itemName?.text = "ITEM TITLE " + itemList[position]
        } else {
            discount?.visibility = View.INVISIBLE
            totalAmount?.paintFlags = totalAmount?.paintFlags?.and(Paint.STRIKE_THRU_TEXT_FLAG.inv())!!
            itemName?.text =
                "VERY LOOOONNGGGG ITEM TITLE ITEM ITEM TITLETITLE ITEM TITLETITLE ITEM TITLETITLE ITEM TITLETITLE ITEM TITLETITLE ITEM TITLE " + itemList[position]
        }
    }


    fun setTheme(descriptionTextView:TapTextViewNew?, discount:TapTextViewNew?, descText:TapTextViewNew?, totalQuantity:TapTextViewNew?,
                 totalAmount:TapTextViewNew?, itemName:TapTextViewNew?, itemSeparator: TapSeparatorView?, mainViewLinear :LinearLayout?, quantityRelative:RelativeLayout?, itemAmount:TapTextViewNew?) {

        itemViewAdapter.setBackgroundColor(Color.parseColor(ThemeManager.getValue("itemsList.item.backgroundColor")))
        val descriptionTextViewTheme = TextViewTheme()
        descriptionTextViewTheme.textColor = Color.parseColor(ThemeManager.getValue("itemsList.item.descLabelColor"))
        descriptionTextViewTheme.backgroundColor = Color.parseColor(ThemeManager.getValue("itemsList.item.backgroundColor"))
        descriptionTextViewTheme.textSize = ThemeManager.getFontSize("itemsList.item.descLabelFont")
        descriptionTextViewTheme.font = ThemeManager.getFontName("itemsList.item.descLabelFont")
        descriptionTextView?.setTheme(descriptionTextViewTheme)
        discount?.setTheme(descriptionTextViewTheme)
        descText?.setTheme(descriptionTextViewTheme)

        mainViewLinear?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("itemsList.item.backgroundColor")))

        val totalQuantityTextViewTheme = TextViewTheme()
        totalQuantityTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("itemsList.item.count.countLabelColor"))
        totalQuantityTextViewTheme.textSize =
            ThemeManager.getFontSize("itemsList.item.count.countLabelFont")
        totalQuantityTextViewTheme.font =
            ThemeManager.getFontName("itemsList.item.count.countLabelFont")
        totalQuantity?.setTheme(totalQuantityTextViewTheme)
        itemAmount?.setTheme(totalQuantityTextViewTheme)


        val totalAmountTextViewTheme = TextViewTheme()
        totalAmountTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("itemsList.item.calculatedPriceLabelColor"))
        totalAmountTextViewTheme.textSize =
            ThemeManager.getFontSize("itemsList.item.calculatedPriceLabelFont")
        totalAmountTextViewTheme.font =
            ThemeManager.getFontName("itemsList.item.calculatedPriceLabelFont")
        totalAmount?.setTheme(totalAmountTextViewTheme)

        quantityRelative?.let {
            setBorderedView(
                it,
                50f,
                0.0f,
                Color.parseColor(ThemeManager.getValue("itemsList.separatorColor")),
                Color.parseColor(ThemeManager.getValue("itemsList.separatorColor")),
                Color.parseColor(ThemeManager.getValue("itemsList.separatorColor"))
            )
        }


        val itemTitleTextViewTheme = TextViewTheme()
        itemTitleTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("itemsList.item.titleLabelColor"))
        itemTitleTextViewTheme.textSize = ThemeManager.getFontSize("itemsList.item.titleLabelFont")
        itemTitleTextViewTheme.font = ThemeManager.getFontName("itemsList.item.titleLabelFont")
        itemName?.setTheme(itemTitleTextViewTheme)


        val separatorViewTheme = SeparatorViewTheme()
        separatorViewTheme.strokeColor = Color.parseColor(ThemeManager.getValue("itemsList.separatorColor"))
        itemSeparator?.setTheme(separatorViewTheme)

    }


    private fun setFonts(itemName:TapTextViewNew?, totalAmount:TapTextViewNew?,
                         discount:TapTextViewNew?, descText:TapTextViewNew?,
                         descriptionTextView:TapTextViewNew?, totalQuantity:TapTextViewNew?, item_amount:TapTextViewNew?) {
        itemName?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
        totalAmount?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
        discount?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
        descText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )

        descriptionTextView?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
        item_amount?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )

        itemViewAdapter.setItemViewDataSource(getItemViewDataSource())
        totalQuantity?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
    }

    private fun getItemViewDataSource(): ItemViewDataSource {
        return ItemViewDataSource(
            itemAmount = "KD000,000",
            totalAmount = "KD000,000.000",
            totalQuantity = "2"
        )
    }


}
