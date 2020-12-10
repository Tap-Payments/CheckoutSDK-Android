package company.tap.checkout.internal.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import company.tap.checkout.R
import company.tap.checkout.internal.dummygener.Currencies1
import company.tap.checkout.internal.dummygener.SavedCards
import company.tap.checkout.internal.enums.Currencies
import company.tap.checkout.internal.interfaces.OnCurrencyChangedActionListener
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.TextViewTheme
import company.tap.tapuilibrary.uikit.atoms.TapImageView
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.ktx.setBorderedView
import kotlinx.android.synthetic.main.item_currency_rows.view.*


/**

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/


var selectedPosition = 0
var contexts: Context? = null
var currencyRate:Double=0.0
//var viewType :ViewGroup? = null
//val tapCard_Chip by lazy {  viewType?.findViewById<TapChip>(R.id.tapcard_Chip) }


class CurrencyTypeAdapter(private val onCurrencyChangedActionListener: OnCurrencyChangedActionListener) :
    RecyclerView.Adapter<CurrencyTypeAdapter.CurrencyHolders>() {
    private var adapterContentCurrencies: List<Currencies1> = java.util.ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolders {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_currency_rows, parent, false)
        contexts = parent.context
        return CurrencyHolders(
            v
        )
    }

    override fun getItemCount() = adapterContentCurrencies.size
    fun updateAdapterData(adapterContentCurrencies: List<Currencies1>) {
        this.adapterContentCurrencies = adapterContentCurrencies
        notifyDataSetChanged()
    }

    class CurrencyHolders(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v

        fun bindTheme() {
            setTheme()
        }

        fun setTheme() {
            var tapCard_Chip = view.findViewById<FrameLayout>(R.id.tapcard_Chip)
            tapCard_Chip?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.backgroundColor")))
            val totalQuantityTextViewTheme = TextViewTheme()
            totalQuantityTextViewTheme.textColor =
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.labelTextColor"))
            totalQuantityTextViewTheme.textSize =
                ThemeManager.getFontSize("horizontalList.chips.currencyChip.labelTextFont")
            totalQuantityTextViewTheme.font =
                ThemeManager.getFontName("horizontalList.chips.currencyChip.labelTextFont")
            view.textView_currency.setTheme(totalQuantityTextViewTheme)

//            val chipTheme = ChipTheme()
//            chipTheme.backgroundColor= Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.backgroundColor"))
//            chipTheme.outlineSpotShadowColor=  Color.parseColor( ThemeManager.getValue("horizontalList.chips.currencyChip.selected.shadow.color"))
//            view.tapcard_Chip.setTheme(chipTheme)
            view.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
//            tapCard_Chip?.setTheme(chipTheme)

            setBorderedView(
                itemView.CurrencyChipLinear,
                40.0f,
                0.0f,
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.selected.shadow.color")),
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.backgroundColor")),
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.backgroundColor"))
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: CurrencyHolders, position: Int) {
        holder.bindTheme()
        for (i in 0 until adapterContentCurrencies.size) {
            val imageViewCard = holder.itemView.findViewById<TapImageView>(R.id.imageView_currency)
            Glide.with(holder.itemView.context).load(adapterContentCurrencies[position].currencyicon).into(
                imageViewCard
            )

            val itemCurrencyText = holder.itemView.findViewById<TapTextView>(R.id.textView_currency)
            itemCurrencyText.text = adapterContentCurrencies[position].currencytitle
            currencyRate = adapterContentCurrencies[position].conversionrate
        }



        if (selectedPosition == position) {

            //if (company.tap.tapuilibrary.uikit.adapters.selectedPosition == position) {
            /**
             * Method to draw bordered view
             * setBorderedView ( view: View, cornerRadius:Float,strokeWidth: Float, strokeColor: Int,tintColor: Int )
             */
//border_currency_black.xml
//            holder.itemView.setBackgroundResource(R.drawable.border_currency)

            if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
                holder.itemView.setBackgroundResource(R.drawable.border_currency_black)
            } else {
                holder.itemView.setBackgroundResource(R.drawable.border_currency)
            }

            setBorderedView(
                holder.itemView.CurrencyChipLinear,
                40.0f,// corner raduis
                0.0f,
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.selected.shadow.color")),
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.selected.backgroundColor")),
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.selected.shadow.color"))
            )
//            holder.itemView.outlineSpotShadowColor =
//                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.selected.shadow.color"))


        } else {

            if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
                holder.itemView.setBackgroundResource(R.drawable.border_unclick_black)
            } else {
                holder.itemView.setBackgroundResource(R.drawable.border_unclick)
            }

//            holder.itemView.setBackgroundResource(R.drawable.border_unclick)

            setBorderedView(
                holder.itemView.CurrencyChipLinear,
                40.0f,// corner raduis
                0.0f,
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.unSelected.shadow.color")),
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.backgroundColor")),
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.unSelected.shadow.color"))
            )
        }

        holder.itemView.setOnClickListener {
            selectedPosition = position

            Toast.makeText(
                holder.itemView.context,
                "You click ${holder.itemView.textView_currency.text}",
                Toast.LENGTH_SHORT
            ).show()

            onCurrencyChangedActionListener?.onCurrencyClicked(holder.itemView.textView_currency.text.toString(),
                adapterContentCurrencies[position].conversionrate)
                notifyItemChanged(position)
        }

    }
    fun updateItemposition() {
       notifyItemChanged(selectedPosition)
        notifyDataSetChanged()
    }


}



