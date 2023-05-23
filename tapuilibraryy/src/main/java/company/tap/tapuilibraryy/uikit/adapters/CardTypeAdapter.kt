package company.tap.tapuilibraryy.uikit.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Color.parseColor
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.uikit.interfaces.OnCardSelectedActionListener
import company.tap.tapuilibraryy.uikit.ktx.setBorderedView
import company.tap.tapuilibraryy.R
import kotlinx.android.synthetic.main.item_saved_card.view.*

/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

@Suppress("PrivatePropertyName")
class CardTypeAdapter(
    private val arrayList: ArrayList<Int>,
    private val onCardSelectedActionListener: OnCardSelectedActionListener,
    var isShaking: Boolean = false
) : RecyclerView.Adapter<ViewHolder>() {
    private val TYPE_SAVED_CARD = 1
    private val TYPE_REDIRECT = 2
    private val TYPE_GO_PAY = 3
    private var selectedPosition = -1
    private var lastPosition = -1
    var context_: Context? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View
        context_ = parent.context
        return when (viewType) {
            TYPE_SAVED_CARD -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_saved_card, parent, false)
                SavedViewHolder(view)
            }
            TYPE_REDIRECT -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_gopay, parent, false)
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
        return if (arrayList[position] == 1 || arrayList[position] == 5) {
            TYPE_REDIRECT
        } else if (arrayList[position] == 2) {
            TYPE_GO_PAY
        } else {
            TYPE_SAVED_CARD
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


    private fun setOnClickActions(holder: ViewHolder) {
        holder.itemView.deleteImageView2?.visibility = View.VISIBLE

        holder.itemView.deleteImageView2?.setOnClickListener {
            onCardSelectedActionListener.onDeleteIconClicked(true, holder.itemView.id)
            arrayList.remove(holder.itemView.id)
            holder.itemView.clearAnimation()
            it.animate().cancel()
            it.clearAnimation()
        }

    }


    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        println("position printed: $position")

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
                        onCardSelectedActionListener?.onCardSelectedAction(false)
                        notifyDataSetChanged()
                    }
                }


            }
        }

    }

    private fun typeSavedCard(holder: ViewHolder, position: Int) {
        holder.itemView.textViewCardDetails.setTextColor(Color.parseColor(ThemeManager.getValue("horizontalList.chips.savedCardChip.labelTextColor")))
        holder.itemView.textViewCardDetails.textSize = ThemeManager.getFontSize("horizontalList.chips.savedCardChip.labelTextFont").toFloat()
        if (isShaking) {
            val animShake: Animation = AnimationUtils.loadAnimation(context_, R.anim.shake)
            holder.itemView.startAnimation(animShake)
            setOnClickActions(holder)
        }
        if (selectedPosition == position) {
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
    }


    private fun typeRedirect(holder: ViewHolder, position: Int) {
        if (selectedPosition == position) {
//                holder.itemView.tapCardChip3Linear.setBackgroundColor(Color.WHITE)
            if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
                holder.itemView.setBackgroundResource(R.drawable.border_shadow_black)
            } else {
                holder.itemView.setBackgroundResource(R.drawable.border_shadow)
            }


//            setBorderedView(
//                holder.itemView.tapCardChip3Linear,
//                (ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis
//                0.0f,
//                parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.selected.shadow.color")),// stroke color
//                parseColor(ThemeManager.getValue("horizontalList.chips.gatewayChip.backgroundColor")),// tint color
//                parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.unSelected.shadow.color"))
//            )// shadow color

        } else {
            if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
                holder.itemView.setBackgroundResource(R.drawable.border_unclick_black)
            } else {
                holder.itemView.setBackgroundResource(R.drawable.border_unclick)
            }

//            setBorderedView(
//                holder.itemView.tapCardChip3Linear,
//                (ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis
//                0.0f,
//                parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.selected.shadow.color")),// stroke color
//                parseColor(ThemeManager.getValue("horizontalList.chips.gatewayChip.backgroundColor")),// tint color
//                parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.unSelected.shadow.color"))
//            )// shadow color

        }
        (holder as SingleViewHolder)
        if (!isShaking) {
            holder.itemView.setOnClickListener {
                onCardSelectedActionListener?.onCardSelectedAction(true)
                selectedPosition = position
                notifyDataSetChanged()
            }
        }

    }


    internal class SavedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    internal class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    internal class GoPayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}




