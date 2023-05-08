package company.tap.tapuilibrary.uikit.views

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.provider.Settings.Global.getString
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.fontskit.enums.TapFont
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.TextViewTheme
import company.tap.tapuilibrary.uikit.atoms.TapImageView
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.datasource.ItemViewDataSource

/**
 * Created by AhlaamK on 6/15/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

/**
 * TapItemsView  is a molecule for setting Amount, Description and Quantity.
 */
class TapItemListView : LinearLayout {
    val itemTitle by lazy { findViewById<TapTextView>(R.id.item_title) }
    val totalAmount by lazy { findViewById<TapTextView>(R.id.total_amount) }
   // val totalQuantity by lazy { findViewById<TapTextView>(R.id.total_quantity) }
   // val itemAmount by lazy { findViewById<TapTextView>(R.id.item_amount) }
    val expandImageView by lazy { findViewById<TapImageView>(R.id.add_image) }
    val collapseImageView by lazy { findViewById<TapImageView>(R.id.sub_image) }
    val quantityRelative by lazy { findViewById<RelativeLayout>(R.id.quantityRelative) }
    val descriptionText by lazy { findViewById<TapTextView>(R.id.brief_description) }
    private var itemViewDataSource: ItemViewDataSource? = null
    @DrawableRes
    val addIcon: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) R.drawable.add_items_dark else R.drawable.add_items_light

    @DrawableRes
    val subIcon: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) R.drawable.sub_items_dark else R.drawable.sub_items_light

    /**
     * Simple constructor to use when creating a TapItemsView from code.
     *  @param context The Context the view is running in, through which it can
     *  access the current theme, resources, etc.
     **/
    constructor(context: Context) : super(context)

    /**
     *  @param context The Context the view is running in, through which it can
     *  access the current theme, resources, etc.
     *  @param attrs The attributes of the XML Button tag being used to inflate the view.
     *
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     *  @param context The Context the view is running in, through which it can
     *  access the current theme, resources, etc.
     *  @param attrs The attributes of the XML Button tag being used to inflate the view.
     * @param defStyleAttr The resource identifier of an attribute in the current theme
     * whose value is the the resource id of a style. The specified style's
     * attribute values serve as default values for the button. Set this parameter
     * to 0 to avoid use of default values.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        inflate(context, R.layout.tap_items_view_2, this)
        if (context?.let { LocalizationManager.getLocale(it).language } == "en") setFontsEnglish() else setFontsArabic()

        setTheme()
    }

    private fun setTheme() {

        val itemTitleTextViewTheme = TextViewTheme()
        itemTitleTextViewTheme.textColor = Color.parseColor(ThemeManager.getValue("itemsList.item.titleLabelColor"))
        itemTitleTextViewTheme.textSize =
            ThemeManager.getFontSize("itemsList.item.titleLabelFont")
        itemTitleTextViewTheme.font = ThemeManager.getFontName("itemsList.item.titleLabelFont")
        itemTitle.setTheme(itemTitleTextViewTheme)


     /*   val totalQuantityTextViewTheme = TextViewTheme()
        totalQuantityTextViewTheme.textColor = Color.parseColor(ThemeManager.getValue("itemsList.item.count.countLabelColor"))
        totalQuantityTextViewTheme.textSize =
            ThemeManager.getFontSize("itemsList.item.count.countLabelFont")
        totalQuantityTextViewTheme.font = ThemeManager.getFontName("itemsList.item.count.countLabelFont")
        totalQuantity.setTheme(totalQuantityTextViewTheme)*/

        val descrptionTextViewTheme = TextViewTheme()
        descrptionTextViewTheme.textColor = Color.parseColor(ThemeManager.getValue("itemsList.item.descLabelColor"))
        descrptionTextViewTheme.textSize =
            ThemeManager.getFontSize("itemsList.item.descLabelFont")
        descrptionTextViewTheme.font = ThemeManager.getFontName("itemsList.item.descLabelFont")
        descriptionText.setTheme(descrptionTextViewTheme)

        val totalAmountTextViewTheme = TextViewTheme()
        totalAmountTextViewTheme.textColor = Color.parseColor(ThemeManager.getValue("itemsList.item.calculatedPriceLabelColor"))
        totalAmountTextViewTheme.textSize =
            ThemeManager.getFontSize("itemsList.item.calculatedPriceLabelFont")
        totalAmountTextViewTheme.font = ThemeManager.getFontName("itemsList.item.calculatedPriceLabelFont")
        totalAmount.setTheme(totalAmountTextViewTheme)
        expandImageView.setImageResource(addIcon)
        collapseImageView.setImageResource(subIcon)
    }

    /**
     * @param itemViewDataSource is set via the consumer app for itemTitle,
     * itemAmount , totalAmount and totalQuantity .
     **/
    fun setItemViewDataSource(itemViewDataSource: ItemViewDataSource) {
        this.itemViewDataSource = itemViewDataSource
        itemViewDataSource.itemTitle?.let {
            itemTitle.text = it
        }
        //itemAmount.text = String.format(context.getString(R.string.item_price),itemViewDataSource.itemAmountCurr, itemViewDataSource.itemAmount )
       // totalAmount.text = String.format(context.getString(R.string.item_price),itemViewDataSource.totalAmountCurr, itemViewDataSource.totalAmount)
        totalAmount.text = itemViewDataSource.totalAmountCurr+itemViewDataSource.itemAmount+"X"+itemViewDataSource.totalQuantity

        /*  itemViewDataSource.totalQuantity?.let {
              totalQuantity.text = it
          }*/
    }


    fun setFontsEnglish() {
        itemTitle?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )

        totalAmount?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )






    }

    fun setFontsArabic() {
        itemTitle?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalRegular
            )
        )

        totalAmount?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalRegular
            )
        )

    }


}