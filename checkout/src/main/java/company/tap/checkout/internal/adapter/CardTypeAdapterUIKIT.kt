package company.tap.checkout.internal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Color.parseColor
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.api.models.PaymentOption
import company.tap.checkout.internal.api.models.SavedCard
import company.tap.checkout.internal.dummygener.Chip1
import company.tap.checkout.internal.dummygener.SavedCards
import company.tap.checkout.internal.interfaces.OnCardSelectedActionListener
import company.tap.taplocalizationkit.LocalizationManager


import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.adapters.context
import company.tap.tapuilibrary.uikit.interfaces.TapActionButtonInterface
import company.tap.tapuilibrary.uikit.ktx.setBorderedView
import kotlinx.android.synthetic.main.item_knet.view.*
import kotlinx.android.synthetic.main.item_save_cards.view.*


/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

@Suppress("PrivatePropertyName")
class CardTypeAdapterUIKIT(private val onCardSelectedActionListener: OnCardSelectedActionListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedPosition = -1
    private var arrayListRedirect:ArrayList<String> = ArrayList()
   // private var arrayListCards:ArrayList<Chip1> = ArrayList()
    private var arrayListCards:List<SavedCard> = java.util.ArrayList()
    private var arrayListSaveCard:ArrayList<String> = ArrayList()
    private var adapterContent: List<PaymentOption> = java.util.ArrayList()
    private var isShaking: Boolean = false
    private var goPayOpened: Boolean = false
    private var arrayModified : ArrayList<Any> = ArrayList()
    private var __context: Context? = null
    private var totalArraySize: Int =0


    companion object {
        private const val TYPE_SAVED_CARD = 5
        private const val TYPE_REDIRECT = 2
        private const val TYPE_GO_PAY = 3
    }

    fun updateAdapterData(adapterContent: List<PaymentOption>) {
        this.adapterContent = adapterContent
        notifyDataSetChanged()
    }
    fun updateAdapterDataSavedCard(adapterSavedCard: List<SavedCard>) {
        this.arrayListCards = adapterSavedCard
        notifyDataSetChanged()
    }

    fun updateShaking(isShaking: Boolean) {
        this.isShaking = isShaking
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        __context = parent.context
        return when (viewType) {
            TYPE_SAVED_CARD -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_save_cards, parent, false)
                SavedViewHolder(view)
            }
            TYPE_REDIRECT -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_knet, parent, false)
                SingleViewHolder(view)
            }
            else -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_gopay, parent, false)
                GoPayViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(position < adapterContent.size){
            if(adapterContent[position].paymentType==PaymentType.WEB){
                (adapterContent[position]).image?.let { arrayListRedirect.add(it) }
                return TYPE_REDIRECT
            }

        }

       else if(position.minus(adapterContent.size) < arrayListCards.size){
           println("pos is<><><>"+ arrayListCards[position.minus(adapterContent.size)].`object`.toUpperCase())
            if(arrayListCards[position.minus(adapterContent.size)].`object`.toUpperCase()==PaymentType.CARD.name){
                arrayListSaveCard.add(arrayListCards[position.minus(adapterContent.size)].lastFour)
                arrayListSaveCard.add(arrayListCards[position.minus(adapterContent.size)].image)
              return  TYPE_SAVED_CARD
            }
        }

        return -1

    }





    override fun getItemCount(): Int {
           totalArraySize = adapterContent.size.plus(arrayListCards.size)
           return totalArraySize

    }


    private fun setOnClickActions(holder: RecyclerView.ViewHolder) {
        if (isShaking) holder.itemView.deleteImageViewSaved?.visibility = View.VISIBLE
        else holder.itemView.deleteImageViewSaved?.visibility = View.GONE
        holder.itemView.deleteImageViewSaved?.setOnClickListener {
         //   onCardSelectedActionListener.onDeleteIconClicked(true, holder.itemView.id,arrayListSaveCard[selectedPosition].id)
          //TODO  COMMENTED arrayListCards.removeAt(holder.itemView.id)
            holder.itemView.clearAnimation()
            it.animate().cancel()
            it.clearAnimation()
        }

    }


    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            /**
             * Saved Cards Type
             */
            getItemViewType(position) == TYPE_SAVED_CARD -> {
                typeSavedCard(holder, position)
            }
            /**
             * Knet Type
             */
            getItemViewType(position) == TYPE_REDIRECT -> {
                setAlphaWhenShaking(isShaking, holder)
                typeRedirect(holder, position)
            }
            /**
             * GoPay Type
             */
            else -> {
                setAlphaWhenShaking(isShaking, holder)
                if (selectedPosition == position) holder.itemView.setBackgroundResource(R.drawable.border_gopay)
                else holder.itemView.setBackgroundResource(R.drawable.border_gopay_unclick)

                (holder as GoPayViewHolder)

                    holder.itemView.setOnClickListener {
                        if (!isShaking) {
                        selectedPosition = position
                        println("goPay is clicked")
                        onCardSelectedActionListener.onCardSelectedAction(true, adapterContent[holder.adapterPosition])
                        goPayOpenedfromMain(goPayOpened)
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    fun removeItems() {
        if(goPayOpened) arrayModified = ArrayList(adapterContent)
        arrayModified.removeAt(0)
       // adapterContent= arrayModified as ArrayList<SavedCards>
        notifyDataSetChanged()
    }

    fun deleteSelectedCard(position: Int){
        println("position in deleteSelection"+position)
        val cardDelList = ArrayList(arrayListCards)
        cardDelList.removeAt(position)
        arrayListCards = cardDelList as List<SavedCard>
        println("deleteSelectedCard"+arrayListCards)
        notifyDataSetChanged()
    }

    
    fun goPayOpenedfromMain(goPayOpened:Boolean){
        this.goPayOpened= goPayOpened
        notifyDataSetChanged()
    }


    private fun setAlphaWhenShaking(isShaking: Boolean, holder: RecyclerView.ViewHolder) {
        if (isShaking) holder.itemView.alpha = 0.4f
        else holder.itemView.alpha = 1.0f
    }

    private fun typeSavedCard(holder: RecyclerView.ViewHolder, position: Int) {
        setSavedCardShakingAnimation(holder)
        if (selectedPosition == position) setSelectedCardTypeSavedShadowAndBackground(holder)
        else setUnSelectedCardTypeSavedShadowAndBackground(holder)

        (holder as SavedViewHolder)
        bindSavedCardData(holder,position)
        setOnSavedCardOnClickAction(holder, position)
        deleteSelectedCard(holder,position)
    }


    private fun setOnSavedCardOnClickAction(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            if (!isShaking) {
                onCardSelectedActionListener.onCardSelectedAction(true, arrayListCards[holder.adapterPosition])
                selectedPosition = position
                notifyDataSetChanged()
            }
//            tapActionButtonInterface.onSelectPaymentOptionActionListener()
        }

        }
    private fun deleteSelectedCard(holder: RecyclerView.ViewHolder, position: Int){
        holder.itemView.deleteImageViewSaved?.setOnClickListener {
            println("delete imageview clicked!!"+arrayListCards[position].id)
            onCardSelectedActionListener.onDeleteIconClicked(true, position,arrayListCards[position].id)
            holder.itemView.clearAnimation()
            it.animate().cancel()
            it.clearAnimation()
            holder.itemView.deleteImageViewSaved?.visibility = View.GONE
            notifyDataSetChanged()
        }

    }


    private fun bindSavedCardData(holder: RecyclerView.ViewHolder,position: Int) {
        for (i in arrayListCards.indices) {
            Glide.with(holder.itemView.context)
                .load(arrayListCards[position.minus(adapterContent.size)].image)
                .into(holder.itemView.imageView_amex)
            holder.itemView.textViewCardDetails.text = maskCardNumber(arrayListCards[position.minus(adapterContent.size)].lastFour)
           //holder.itemView.textViewCardDetails.text = adapterContent[holder.adapterPosition].chip1.title
        }
    }

    private fun setSavedCardShakingAnimation(holder: RecyclerView.ViewHolder) {
        if (isShaking) {
            Log.d("isShaking" , isShaking.toString())
            val animShake: Animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.shake)
            holder.itemView.startAnimation(animShake)
            holder.itemView.deleteImageViewSaved?.visibility = View.VISIBLE
        }else holder.itemView.deleteImageViewSaved?.visibility = View.GONE
    }

    private fun setSelectedCardTypeSavedShadowAndBackground(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            holder.itemView.tapCardChip2.setBackgroundResource(R.drawable.border_shadow_black)
        } else {
            holder.itemView.tapCardChip2.setBackgroundResource(R.drawable.border_shadow)
        }
        setBorderedView(
            holder.itemView.tapCardChip2Constraints,
            (ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis
            0.0f,
            parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.selected.shadow.color")),// stroke color
            Color.parseColor(ThemeManager.getValue("horizontalList.chips.savedCardChip.backgroundColor")),// tint color
            parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.unSelected.shadow.color"))
        )// shadow color
    }


    private fun setUnSelectedCardTypeSavedShadowAndBackground(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            holder.itemView.tapCardChip2.setBackgroundResource(R.drawable.border_unclick_black)
        } else {
            holder.itemView.tapCardChip2.setBackgroundResource(R.drawable.border_unclick)
        }

        setBorderedView(
            holder.itemView.tapCardChip2Constraints,
            (ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis
            0.0f,
            parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.selected.shadow.color")),// stroke color
            Color.parseColor(ThemeManager.getValue("horizontalList.chips.savedCardChip.backgroundColor")),// tint color
            parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.unSelected.shadow.color"))
        )// shadow color
    }

    private fun typeRedirect(holder: RecyclerView.ViewHolder, position: Int) {
        if (selectedPosition == position) setSelectedCardTypeRedirectShadowAndBackground(holder)
        else setUnSelectedCardTypeRedirectShadowAndBackground(holder)
        (holder as SingleViewHolder)
        holder.itemView.setOnClickListener {if (!isShaking) { setOnRedirectCardOnClickAction(holder,position) }}
        bindRedirectCardImage(holder)
    }

    private fun setOnRedirectCardOnClickAction( holder: RecyclerView.ViewHolder,position: Int) {
        onCardSelectedActionListener.onCardSelectedAction(true, adapterContent[holder.adapterPosition])
        selectedPosition = position
        notifyDataSetChanged()
    }

    private fun bindRedirectCardImage(holder: RecyclerView.ViewHolder) {
        for (i in 0 until arrayListRedirect.size) {
            val imageViewCard = holder.itemView.findViewById<ImageView>(R.id.imageView_knet)
            Glide.with(holder.itemView.context)
                .load(arrayListRedirect[i])
                .into(imageViewCard)
        }
    }

    private fun setSelectedCardTypeRedirectShadowAndBackground(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) (holder.itemView.setBackgroundResource(
            R.drawable.border_shadow_black
        ))
        else holder.itemView.setBackgroundResource(R.drawable.border_shadow)

        setBorderedView(
            holder.itemView.tapCardChip3Linear,
            (ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis
            0.0f,
            parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.selected.shadow.color")),// stroke color
            parseColor(ThemeManager.getValue("horizontalList.chips.gatewayChip.backgroundColor")),// tint color
            parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.unSelected.shadow.color"))
        )// shadow color
    }

    private fun setUnSelectedCardTypeRedirectShadowAndBackground(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) holder.itemView.setBackgroundResource(
            R.drawable.border_unclick_black
        )
        else holder.itemView.setBackgroundResource(R.drawable.border_unclick)
        setBorderedView(
            holder.itemView.tapCardChip3Linear,
            (ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis
            0.0f,
            parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.selected.shadow.color")),// stroke color
            parseColor(ThemeManager.getValue("horizontalList.chips.gatewayChip.backgroundColor")),// tint color
            parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.unSelected.shadow.color"))
        )// shadow color
    }


    internal class SavedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    internal class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    internal class GoPayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun maskCardNumber(cardInput: String): String {
        val maskLen: Int = cardInput.length - 4
        if (maskLen <= 0) return cardInput // Nothing to mask
        return (cardInput).replaceRange(0, 4, "....")
    }

}

