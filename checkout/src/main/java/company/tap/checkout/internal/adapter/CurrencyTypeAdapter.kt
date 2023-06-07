package company.tap.checkout.internal.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import company.tap.checkout.R
import company.tap.checkout.internal.api.models.SupportedCurrencies
import company.tap.checkout.internal.enums.ThemeMode
import company.tap.checkout.internal.interfaces.OnCurrencyChangedActionListener
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.themekit.theme.TextViewTheme
import company.tap.tapuilibraryy.uikit.atoms.TapImageView
import company.tap.tapuilibraryy.uikit.atoms.TapTextViewNew
import company.tap.tapuilibraryy.uikit.ktx.setBorderedView
import kotlinx.android.synthetic.main.item_currency_rows.view.*
import java.math.BigDecimal


/**

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/


var selectedPosition = -1
var _context: Context? = null
var currencyRate: BigDecimal = BigDecimal.ZERO
var previousIndex = 0

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
        this.adapterContentCurrencies = adapterContentCurrencies
        notifyDataSetChanged()

    }

    fun updateSelectedPosition(selectedPos: Int) {
        selectedPosition = selectedPos
        notifyDataSetChanged()
    }

    class CurrencyHolders(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v

        fun bindTheme() {
            setTheme()
        }

        private fun setTheme() {
            val tapCardChip = view.findViewById<FrameLayout>(R.id.tapcard_Chip)
            tapCardChip.elevation = 0F
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

        }
    }

    override fun onBindViewHolder(holder: CurrencyHolders, position: Int) {
        holder.bindTheme()
        bindItemData(holder, position)
        if (selectedPosition == position) {
            drawSelectedBorder(holder)
        } else drawUnSelectedBorder(holder)
        onItemClickListener(holder, position)


    }

    private fun bindItemData(holder: CurrencyHolders, position: Int) {
        logicToLoadFlagIconsTheme(holder, position)
        val itemCurrencyText = holder.itemView.findViewById<TapTextViewNew>(R.id.textView_currency)
        itemCurrencyText.text = adapterContentCurrencies[position].currency
        currencyRate = adapterContentCurrencies[position].amount
    }

    private fun logicToLoadFlagIconsTheme(holder: CurrencyHolders, position: Int) {
        val imageViewCard = holder.itemView.findViewById<TapImageView>(R.id.imageView_currency)
        var loadUrlString: String = ""
        if (adapterContentCurrencies[position].logos != null)
            when (CustomUtils.getCurrentTheme()) {
                ThemeMode.dark.name -> {
                    loadUrlString = adapterContentCurrencies[position].logos?.dark?.png.toString()
                    Glide.with(holder.itemView.context)
                        .load(loadUrlString).into(
                            imageViewCard
                        )
                }
                ThemeMode.dark_colored.name -> {
                    loadUrlString =
                        adapterContentCurrencies[position].logos?.dark_colored?.png.toString()
                    Glide.with(holder.itemView.context)
                        .load(loadUrlString).into(
                            imageViewCard
                        )
                }
                ThemeMode.light.name -> {
                    loadUrlString = adapterContentCurrencies[position].logos?.light?.png.toString()
                    Glide.with(holder.itemView.context)
                        .load(loadUrlString).into(
                            imageViewCard
                        )
                }
                ThemeMode.light_mono.name -> {
                    loadUrlString =
                        adapterContentCurrencies[position].logos?.light_mono?.png.toString()
                    Glide.with(holder.itemView.context)
                        .load(loadUrlString).into(
                            imageViewCard
                        )
                }

                else -> {
                    //fallback
                    Glide.with(holder.itemView.context)
                        .load(adapterContentCurrencies[position].flag).into(
                            imageViewCard
                        )
                }
            }


    }

    private fun onItemClickListener(holder: CurrencyHolders, position: Int) {
        holder.itemView.setOnClickListener {

            adapterContentCurrencies[position].rate?.let { it1 ->
                onCurrencyChangedActionListener.onCurrencyClicked(
                    adapterContentCurrencies[position].currency.toString(),
                    it1.toBigDecimal(),
                    adapterContentCurrencies[position].amount,
                    adapterContentCurrencies[previousIndex].currency.toString(),
                    adapterContentCurrencies[position].symbol.toString()
                )
                selectedPosition = position
                notifyItemChanged(selectedPosition)
            }
            CheckoutViewModel().selectedCurrencyPos =
                adapterContentCurrencies[position].currency.toString()
            CheckoutViewModel().selectedAmountPos = adapterContentCurrencies[position].amount

        }


    }


    private fun drawSelectedBorder(holder: CurrencyHolders) {
        /**
         * Method to draw bordered view
         * setBorderedView ( view: View, cornerRadius:Float,strokeWidth: Float, strokeColor: Int,tintColor: Int )
         */
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) holder.itemView.setBackgroundResource(
            // R.drawable.border_currency_black
            R.drawable.border_shadow_white
        )
        else holder.itemView.tapcard_Chip.setBackgroundResource(R.drawable.border_shadow)
    }


    private fun drawUnSelectedBorder(holder: CurrencyHolders) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            holder.itemView.setBackgroundResource(R.drawable.border_unclick_black)
        } else {
            holder.itemView.tapcard_Chip.setBackgroundResource(R.drawable.border_unclick_white)
        }

    }


}





