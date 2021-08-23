package company.tap.checkout.internal.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import company.tap.checkout.R
import company.tap.checkout.internal.api.models.SupportedCurrencies
import company.tap.checkout.internal.interfaces.OnCurrencyChangedActionListener
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.TextViewTheme
import company.tap.tapuilibrary.uikit.atoms.TapImageView
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.ktx.setBorderedView
import kotlinx.android.synthetic.main.item_currency_rows.view.*
import kotlinx.android.synthetic.main.item_frame_currencies.view.*
import okhttp3.internal.notify
import java.math.BigDecimal


/**

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/


var selectedPosition = -1
var _context: Context? = null
 var currencyRate: BigDecimal= BigDecimal.ZERO

class CurrencyTypeAdapter(private val onCurrencyChangedActionListener: OnCurrencyChangedActionListener) :
    RecyclerView.Adapter<CurrencyTypeAdapter.CurrencyHolders>() {
    private var adapterContentCurrencies: List<SupportedCurrencies> = java.util.ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolders {

        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_currency_rows, parent, false)
        _context = parent.context
        return CurrencyHolders(v)
    }

    override fun getItemCount() = adapterContentCurrencies.size

    fun updateAdapterData(adapterContentCurrencies: List<SupportedCurrencies>) {
        println("list of currencies$adapterContentCurrencies")
        this.adapterContentCurrencies = adapterContentCurrencies
        notifyDataSetChanged()
    }

    fun updateSelectedPosition(selectedPos:Int){
        selectedPosition = selectedPos
    }

    class CurrencyHolders(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v

        fun bindTheme() {
            setTheme()
        }

        private fun setTheme() {
            val tapCardChip = view.findViewById<FrameLayout>(R.id.tapcard_Chip)
            tapCardChip?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.backgroundColor")))
            val totalQuantityTextViewTheme = TextViewTheme()
            totalQuantityTextViewTheme.textColor =
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.labelTextColor"))
            totalQuantityTextViewTheme.textSize =
                ThemeManager.getFontSize("horizontalList.chips.currencyChip.labelTextFont")
            totalQuantityTextViewTheme.font =
                ThemeManager.getFontName("horizontalList.chips.currencyChip.labelTextFont")
            view.textView_currency.setTheme(totalQuantityTextViewTheme)
            view.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
            setBorderedView(
                    itemView.CurrencyChipLinear,
                    25.0f,
                    0.0f,
                    Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.selected.shadow.color")),
                    Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.backgroundColor")),
                    Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.backgroundColor"))
            )
        }
    }

    override fun onBindViewHolder(holder: CurrencyHolders, position: Int) {
        holder.bindTheme()
      //  holder.itemView.isSelected = selectedPosition == position
        for (i in adapterContentCurrencies.indices) {
            bindItemData(holder, position)
        }
        if (selectedPosition == position) drawSelectedBorder(holder)
         else drawUnSelectedBorder(holder)

        onItemClickListener(holder, position)

    }

    private fun bindItemData(holder: CurrencyHolders, position: Int) {
        val imageViewCard = holder.itemView.findViewById<TapImageView>(R.id.imageView_currency)

      /*  Glide.with(holder.itemView.context)
            .load(adapterContentCurrencies[position].flag).into(
                imageViewCard)*/
       // println("adapterContentCurrencies[position].amount" + adapterContentCurrencies[position].amount)
        adapterContentCurrencies[position]?.flag?.let { imageViewCard.loadSvg(it) }
        val itemCurrencyText = holder.itemView.findViewById<TapTextView>(R.id.textView_currency)
        itemCurrencyText.text = adapterContentCurrencies[position].currency
        currencyRate = adapterContentCurrencies[position].amount
    }

    private fun onItemClickListener(holder: CurrencyHolders, position:   Int) {
        holder.itemView.setOnClickListener(null)

        holder.itemView.setOnClickListener {
            notifyItemChanged(selectedPosition)
            selectedPosition = position
            notifyItemChanged(selectedPosition)
           // selectedPosition = position
                onCurrencyChangedActionListener.onCurrencyClicked(
                    adapterContentCurrencies[position].currency.toString(),
                    adapterContentCurrencies[position].amount)
             CheckoutViewModel().selectedCurrencyPos = adapterContentCurrencies[position].currency.toString()
             CheckoutViewModel().selectedAmountPos = adapterContentCurrencies[position].amount



            }



    }


    private fun drawSelectedBorder(holder: CurrencyHolders) {
        /**
         * Method to draw bordered view
         * setBorderedView ( view: View, cornerRadius:Float,strokeWidth: Float, strokeColor: Int,tintColor: Int )
         */
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) holder.itemView.setBackgroundResource(
                R.drawable.border_currency_black
        )
        else holder.itemView.setBackgroundResource(R.drawable.border_currency)
        setBorderedView(
                holder.itemView.CurrencyChipLinear,
                25.0f,// corner raduis
                0.0f,
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.selected.shadow.color")),
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.selected.backgroundColor")),
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.selected.shadow.color"))
        )
    }


    private fun drawUnSelectedBorder(holder: CurrencyHolders) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            holder.itemView.setBackgroundResource(R.drawable.border_unclick_black)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.border_unclick)
        }
        setBorderedView(
                holder.itemView.CurrencyChipLinear,
                25.0f,// corner raduis
                0.0f,
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.unSelected.shadow.color")),
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.backgroundColor")),
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.unSelected.shadow.color"))
        )
    }

    private fun ImageView.loadSvg(url: String) {

        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(this@loadSvg.context)) }
            .build()

        val request = ImageRequest.Builder(this.context)
            .data(url)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    }



}





