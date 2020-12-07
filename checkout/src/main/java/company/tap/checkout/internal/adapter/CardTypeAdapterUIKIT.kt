package company.tap.checkout.internal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Color.parseColor
import android.os.Build
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
import company.tap.checkout.internal.dummygener.SavedCards
import company.tap.tapuilibrary.R


import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.interfaces.OnCardSelectedActionListener
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
     val arrayListsSaveCard: List<SavedCards>,
    private val onCardSelectedActionListener: OnCardSelectedActionListener?,
    var isShaking: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_SAVED_CARD = 1
    private val TYPE_REDIRECT = 2
    private val TYPE_GO_PAY = 3
    private var selectedPosition = -1
    private var lastPosition = -1
    var context_: Context? = null
    private var arrayListRedirect:ArrayList<String> = ArrayList()
    private var arrayListCards:ArrayList<String> = ArrayList()
    private var totalArrayList:ArrayList<SavedCards> = ArrayList()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        context_ = parent.context
        totalArrayList.addAll(arrayListsSaveCard)
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
        return if (arrayListsSaveCard[position].chipType == 1) {
            arrayListRedirect.add(arrayListsSaveCard[position].chip1.icon)
            TYPE_REDIRECT
        } else if (arrayListsSaveCard[position].chipType == 5) {
            arrayListCards.add(arrayListsSaveCard[position].chip1.icon)
            TYPE_SAVED_CARD
        } else {
            TYPE_GO_PAY
        }
    }

    override fun getItemCount(): Int {
        return arrayListsSaveCard.size
    }


    private fun setOnClickActions(holder: RecyclerView.ViewHolder) {
       
        holder.itemView.deleteImageViewsaved?.visibility = View.VISIBLE

        holder.itemView.deleteImageViewsaved?.setOnClickListener {
            onCardSelectedActionListener?.onDeleteIconClicked(true, holder.itemView.id)
             arrayListCards.removeAt(holder.itemView.id)
            holder.itemView.clearAnimation()
            it.animate().cancel()
            it.clearAnimation()
        }

    }


    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
      //  println("position printed: $position")

        when {
            /**
             * Saved Cards Type
             */
            getItemViewType(position) === TYPE_SAVED_CARD -> {
                typeSavedCard(holder, position)
            }
            /**
             * Knet Type
             */
            getItemViewType(position) === TYPE_REDIRECT -> {
                if (isShaking) {
                    holder.itemView.alpha = 0.4f
                }
                typeRedirect(holder, position)
            }
            /**
             * GoPay Type
             */
            else -> {
                if (isShaking) {
                    holder.itemView.alpha = 0.4f
                }
                if (selectedPosition == position)
                    holder.itemView.setBackgroundResource(R.drawable.border_gopay)
                else
                    holder.itemView.setBackgroundResource(R.drawable.border_gopay_unclick)
                (holder as GoPayViewHolder)

                if (!isShaking) {
                    holder.itemView.setOnClickListener {
                        selectedPosition = position
                        onCardSelectedActionListener?.onCardSelectedAction(true,TYPE_GO_PAY.toString())
                        notifyDataSetChanged()
                    }
                }


            }
        }

    }

    private fun typeSavedCard(holder: RecyclerView.ViewHolder, position: Int) {
        if (isShaking) {
            val animShake: Animation = AnimationUtils.loadAnimation(context_, R.anim.shake)
            holder.itemView.startAnimation(animShake)
            setOnClickActions(holder)
        }
        if (selectedPosition == position) {
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

        } else {
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
        (holder as SavedViewHolder)
        if (!isShaking) {
            holder.itemView.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
            }
        }
        for (i in 2 until arrayListCards.size-1) {
            val imageViewCard = holder.itemView.findViewById<ImageView>(R.id.imageView_amex)
          //  val url = URL(arrayListCards[i])
           // if ( URLUtil.isValidUrl(url.toString()) ) {
             //   val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())

                Glide.with(holder.itemView.context)
                    .load(arrayListsSaveCard[i])
                    .into(imageViewCard)

               // imageViewCard.setImageBitmap(bmp)
           // }
        }

    }


    private fun typeRedirect(holder: RecyclerView.ViewHolder, position: Int) {
        if (selectedPosition == position) {
//                holder.itemView.tapCardChip3Linear.setBackgroundColor(Color.WHITE)
            if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
                holder.itemView.setBackgroundResource(R.drawable.border_shadow_black)
            } else {
                holder.itemView.setBackgroundResource(R.drawable.border_shadow_)
            }


            setBorderedView(
                holder.itemView.tapCardChip3Linear,
                (ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis
                0.0f,
                parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.selected.shadow.color")),// stroke color
                parseColor(ThemeManager.getValue("horizontalList.chips.gatewayChip.backgroundColor")),// tint color
                parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.unSelected.shadow.color"))
            )// shadow color

        } else {
            if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
                holder.itemView.setBackgroundResource(R.drawable.border_unclick_black)

            } else {
                holder.itemView.setBackgroundResource(R.drawable.border_unclick)

            }

            setBorderedView(
                holder.itemView.tapCardChip3Linear,
                (ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis
                0.0f,
                parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.selected.shadow.color")),// stroke color
                parseColor(ThemeManager.getValue("horizontalList.chips.gatewayChip.backgroundColor")),// tint color
                parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.unSelected.shadow.color"))
            )// shadow color

        }
        (holder as SingleViewHolder)
        if (!isShaking) {
            holder.itemView.setOnClickListener {
                onCardSelectedActionListener?.onCardSelectedAction(true, arrayListRedirect.toString())
                selectedPosition = position
                notifyDataSetChanged()
            }
        }
        for (i in 2 until arrayListRedirect.size-1) {
            val imageViewCard = holder.itemView.findViewById<ImageView>(R.id.imageView_knet)
           /* val url = URL(arrayListRedirect[i])
            if ( URLUtil.isValidUrl(url.toString()) ) {
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                imageViewCard.setImageBitmap(bmp)
            }*/
            Glide.with(holder.itemView.context)
                .load(arrayListRedirect[i])
                .into(imageViewCard)
        }


    }


    internal class SavedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    internal class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    internal class GoPayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}

