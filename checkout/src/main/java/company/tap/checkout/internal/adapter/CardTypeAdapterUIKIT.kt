package company.tap.checkout.internal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import company.tap.checkout.R
import company.tap.checkout.internal.dummygener.SavedCards
import company.tap.checkout.internal.interfaces.OnCardSelectedActionListener


import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.ktx.setBorderedView
import kotlinx.android.synthetic.main.item_knet.view.*
import kotlinx.android.synthetic.main.item_save_cards.view.*
import java.net.URL


/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

@Suppress("PrivatePropertyName")
class CardTypeAdapterUIKIT(
    private val onCardSelectedActionListener: OnCardSelectedActionListener

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedPosition = -1
    private var lastPosition = -1
    private var arrayListRedirect:ArrayList<String> = ArrayList()
    private var arrayListCards:ArrayList<String> = ArrayList()
    private var totalArrayList:ArrayList<SavedCards> = ArrayList()
    private var adapterContent: List<Any> = java.util.ArrayList()
    var isShaking: Boolean = false

    companion object {
        private const val TYPE_SAVED_CARD = 1
        private const val TYPE_REDIRECT = 2
        private const val TYPE_GO_PAY = 3
    }

    fun updateAdapterData(adapterContent: List<Any>) {
        this.adapterContent = adapterContent
        notifyDataSetChanged()
    }

    fun updateShaking(isShaking: Boolean) {
        this.isShaking = isShaking
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
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
        /**
         * here we will cast the list of any depending on card type
         */

        return when ((adapterContent[position]  as SavedCards).chipType ) {
            1 -> {
                arrayListRedirect.add((adapterContent[position]  as SavedCards).chip1.icon)
                TYPE_REDIRECT
            }
            5 -> {
                arrayListCards.add((adapterContent[position]  as SavedCards).chip1.icon)
                TYPE_SAVED_CARD
            }
            else -> {
                TYPE_GO_PAY
            }
        }
    }

    override fun getItemCount(): Int {
        return adapterContent.size
    }


    private fun setOnClickActions(holder: RecyclerView.ViewHolder) {
        holder.itemView.deleteImageViewsaved?.visibility = View.VISIBLE
        holder.itemView.deleteImageViewsaved?.setOnClickListener {
            onCardSelectedActionListener.onDeleteIconClicked(true, holder.itemView.id)
            arrayListCards.removeAt(holder.itemView.id)
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
                if ( !isShaking) {
                    holder.itemView.setOnClickListener {
                        selectedPosition = position
                        onCardSelectedActionListener.onCardSelectedAction(true, adapterContent[holder.adapterPosition])
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }


    private fun setAlphaWhenShaking(isShaking: Boolean, holder: RecyclerView.ViewHolder) {
        if (isShaking) holder.itemView.alpha = 0.4f
    }

    private fun typeSavedCard(holder: RecyclerView.ViewHolder, position: Int) {
        setSavedCardShakingAnimation(holder)
        if (selectedPosition == position) setSelectedCardTypeSavedShadowAndBackground(holder)
        else setUnSelectedCardTypeSavedShadowAndBackground(holder)

        (holder as SavedViewHolder)
        bindSavedCardData(holder)
        setOnSavedCardOnClickAction(holder, position)
    }


    private fun setOnSavedCardOnClickAction(holder: RecyclerView.ViewHolder, position: Int) {
        if (!isShaking) {
            holder.itemView.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
            }
        }
    }

    private fun bindSavedCardData(holder: RecyclerView.ViewHolder) {
        for (i in 0 until arrayListCards.size) {
            val imageViewCard = holder.itemView.findViewById<ImageView>(R.id.imageView_amex)
            Glide.with(holder.itemView.context)
                .load(arrayListCards[i])
                .into(imageViewCard)

            holder.itemView.textViewCardDetails.text = (adapterContent[holder.adapterPosition] as SavedCards).chip1.title
        }
    }

    private fun setSavedCardShakingAnimation(holder: RecyclerView.ViewHolder) {
        if (isShaking) {
            Log.d("isShaking" , isShaking.toString())
            val animShake: Animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.shake)
            holder.itemView.startAnimation(animShake)
            setOnClickActions(holder)
        }
    }

    private fun setSelectedCardTypeSavedShadowAndBackground(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            holder.itemView.tapCardChip2.setBackgroundResource(R.drawable.border_shadow_black)
        } else {
            holder.itemView.tapCardChip2.setBackgroundResource(R.drawable.border_shadow_)
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
        if (!isShaking) holder.itemView.setOnClickListener { setOnRedirectCardOnClickAction(holder,position) }
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
        else holder.itemView.setBackgroundResource(R.drawable.border_shadow_)

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


}

