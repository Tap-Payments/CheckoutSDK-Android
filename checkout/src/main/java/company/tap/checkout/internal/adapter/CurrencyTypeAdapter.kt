package company.tap.checkout.internal.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkout.R
import company.tap.checkout.internal.dummygener.Currencies1
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.TextViewTheme
import company.tap.tapuilibrary.uikit.ktx.setBorderedView
import kotlinx.android.synthetic.main.item_currency_rows.view.*


import java.net.URL

/**

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/


var selectedPosition = 0
var contexts: Context? = null
//var viewType :ViewGroup? = null
//val tapCard_Chip by lazy {  viewType?.findViewById<TapChip>(R.id.tapcard_Chip) }


class CurrencyTypeAdapter(private val arraylistscurencs: ArrayList<Currencies1>) : RecyclerView.Adapter<CurrencyTypeAdapter.CurrencyHolders>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolders {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_currency_rows, parent, false)
        contexts = parent.context
        return CurrencyHolders(
            v
        )
    }

    override fun getItemCount() = arraylistscurencs.size


    class CurrencyHolders(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
       // private var photo: CurrencyModel? = null


        fun bindCurrecnyList(currenciesListe: ArrayList<Currencies1>) {
            //  this.photo = photo
            //  Picasso.with(view.context).load(photo.imageUrl).into(view.imageView_currency)
            for (i in 0 until currenciesListe.size) {
                val url = URL(currenciesListe[i].currencyicon)
                if ( URLUtil.isValidUrl(url.toString()) ) {
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())

                    view.imageView_currency.setImageBitmap(bmp)
                }
                view.textView_currency.text = currenciesListe[i].currencytitle


            }


            setTheme()
        }
        fun setTheme(){
            var tapCard_Chip = view.findViewById<FrameLayout>(R.id.tapcard_Chip)
            tapCard_Chip?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.backgroundColor")))
            val totalQuantityTextViewTheme = TextViewTheme()
            totalQuantityTextViewTheme.textColor = Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.labelTextColor"))
            totalQuantityTextViewTheme.textSize = ThemeManager.getFontSize("horizontalList.chips.currencyChip.labelTextFont")
            totalQuantityTextViewTheme.font = ThemeManager.getFontName("horizontalList.chips.currencyChip.labelTextFont")
            view.textView_currency.setTheme(totalQuantityTextViewTheme)

//            val chipTheme = ChipTheme()
//            chipTheme.backgroundColor= Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.backgroundColor"))
//            chipTheme.outlineSpotShadowColor=  Color.parseColor( ThemeManager.getValue("horizontalList.chips.currencyChip.selected.shadow.color"))
//            view.tapcard_Chip.setTheme(chipTheme)
            view.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
//            tapCard_Chip?.setTheme(chipTheme)

            setBorderedView(itemView.CurrencyChipLinear,
                20.0f,
                0.0f,
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.selected.shadow.color")),
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.backgroundColor")),
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.backgroundColor"))
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: CurrencyHolders, position: Int) {
        holder.bindCurrecnyList(arraylistscurencs)
        if (selectedPosition == position) {
       // holder.bindPhoto(photos[position])
        //if (company.tap.tapuilibrary.uikit.adapters.selectedPosition == position) {
            /**
             * Method to draw bordered view
             * setBorderedView ( view: View, cornerRadius:Float,strokeWidth: Float, strokeColor: Int,tintColor: Int )
             */
//border_currency_black.xml
//            holder.itemView.setBackgroundResource(R.drawable.border_currency)

            if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
                holder.itemView.setBackgroundResource(R.drawable.border_currency_black)
            }else{
                holder.itemView.setBackgroundResource(R.drawable.border_currency)
            }

            setBorderedView(
                holder.itemView.CurrencyChipLinear,
                (ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis
                0.0f,
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.selected.shadow.color")),
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.selected.backgroundColor")),
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.selected.shadow.color"))
            )
//            holder.itemView.outlineSpotShadowColor =
//                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.selected.shadow.color"))


        } else{

            if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
                holder.itemView.setBackgroundResource(R.drawable.border_unclick_black)
            }else{
                holder.itemView.setBackgroundResource(R.drawable.border_unclick)
            }

//            holder.itemView.setBackgroundResource(R.drawable.border_unclick)

            setBorderedView(
                holder.itemView.CurrencyChipLinear,
                (ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis
                0.0f,
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.unSelected.shadow.color")),
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.backgroundColor")),
                Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.unSelected.shadow.color")))
        }

        holder.itemView.setOnClickListener {
           selectedPosition = position


            Toast.makeText(
                contexts,
                "You click ${holder.itemView.textView_currency.text}",
                Toast.LENGTH_SHORT
            ).show()


            notifyDataSetChanged()
        }

    }




}



