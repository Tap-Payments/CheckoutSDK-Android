package company.tap.checkout.internal.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Color.parseColor
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import company.tap.checkout.R
import company.tap.checkout.internal.dummygener.Chip1
import company.tap.checkout.internal.dummygener.GoPaySavedCards
import company.tap.checkout.internal.interfaces.OnCardSelectedActionListener
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.ktx.setBorderedView
import kotlinx.android.synthetic.main.item_gopay_signout.view.*
import kotlinx.android.synthetic.main.item_knet.view.*
import kotlinx.android.synthetic.main.item_save_cards.view.*

/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

@Suppress("PrivatePropertyName")
class GoPayCardAdapterUIKIT(
    private val onCardSelectedActionListener: OnCardSelectedActionListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedPosition = -1
    private var lastPosition = -1
    private var arrayListRedirect:ArrayList<String> = ArrayList()
    private var arrayListCards:ArrayList<Chip1> = ArrayList()
    private var adapterContent: List<GoPaySavedCards> = java.util.ArrayList()
    private var isShaking: Boolean = false
    private var isLogout: Boolean = false
    lateinit var tapCardFrameLayout: FrameLayout
    lateinit var holderLogout: RecyclerView.ViewHolder

    companion object {
        private const val TYPE_SAVED_CARD = 1
        private const val TYPE_GO_PAY_SIGNOUT = 2
    }

    fun updateAdapterData(adapterContent: List<GoPaySavedCards>) {
        this.adapterContent = adapterContent
        notifyDataSetChanged()
    }
    fun updateSignOut(adapterContent: List<GoPaySavedCards>,isLogout:Boolean) {
        this.adapterContent = adapterContent
        this.isLogout = isLogout
        notifyDataSetChanged()
        Log.d("isLogout", isLogout.toString())
    }
    fun goPayOpenedfromMain(goPayOpened: Boolean){

    }

    fun updateShaking(isShaking: Boolean) {
        this.isShaking = isShaking
        notifyDataSetChanged()
        Log.d("isShakingupdate", isShaking.toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            TYPE_SAVED_CARD -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_save_cards,
                    parent,
                    false
                )
                SavedViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_gopay_signout,
                    parent,
                    false
                )
                GoPayViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        /**
         * here we will cast the list of any depending on card type
         */

        return when ((adapterContent[position]).chipType ) {
            5 -> {
                arrayListCards.add((adapterContent[position].chip1))

                TYPE_SAVED_CARD
            }
            else -> TYPE_GO_PAY_SIGNOUT
        }
    }

    override fun getItemCount(): Int {
        return adapterContent.size
    }


    private fun setOnClickActions(holder: RecyclerView.ViewHolder) {}

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isShaking) {
            holder.itemView.alpha = 0.4f
        }else{
            holder.itemView.alpha = 1.0f
        }
        when {
            /**
             * Saved Cards Type
             */
            getItemViewType(position) == TYPE_SAVED_CARD -> {
                typeSavedCard(holder, position)
            }

            /**
             * GoPay Type
             */
            else -> {
                if (isShaking) {
                    holder.itemView.alpha = 0.4f
                }
                if (selectedPosition == position) {
                    if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains(
                            "dark"
                        )) {
                        holder.itemView.tapCardChip4.setBackgroundResource(R.drawable.border_shadow_black)
                    } else {
                        holder.itemView.tapCardChip4.setBackgroundResource(R.drawable.border_shadow)
                    }
                    setBorderedView(
                        holder.itemView.tapCardChip4Linear,
                        (ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis
                        0.0f,
                        parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.selected.shadow.color")),// stroke color
                        Color.parseColor(ThemeManager.getValue("horizontalList.chips.savedCardChip.backgroundColor")),// tint color
                        parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.unSelected.shadow.color"))
                    )// shadow color

                } else {
                    if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains(
                            "dark"
                        )) {
                        holder.itemView.tapCardChip4.setBackgroundResource(R.drawable.border_unclick_black)
                    } else {
                        holder.itemView.tapCardChip4.setBackgroundResource(R.drawable.border_unclick)
                    }


                    setBorderedView(
                        holder.itemView.tapCardChip4Linear,
                        (ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis
                        0.0f,
                        parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.selected.shadow.color")),// stroke color
                        Color.parseColor(ThemeManager.getValue("horizontalList.chips.savedCardChip.backgroundColor")),// tint color
                        parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.unSelected.shadow.color"))
                    )// shadow color

                }
                (holder as GoPayViewHolder)
                holderLogout = holder
                //holder.itemView.tapCardChip4.visibility =View.GONE
                if(isLogout) {
                    typeGoPaySavedCard(holder, position)
                }else holder.itemView.tapCardChip4.visibility =View.GONE

            }
        }
    }

    private fun typeSavedCard(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("isShaking", isShaking.toString())
        if (isShaking) {
            holder.itemView.alpha = 0.4f
            val animShake: Animation = AnimationUtils.loadAnimation(
                holder.itemView.context,
                R.anim.shake
            )
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
            holder.itemView.setOnClickListener {
                if (!isShaking) {
                selectedPosition = position
                notifyDataSetChanged()
            }
        }
        for (i in 0 until arrayListCards.size) {
            if (isShaking) {
                holder.itemView.alpha = 0.4f
            }
            val imageViewCard = holder.itemView.findViewById<ImageView>(R.id.imageView_amex)
            val textViewCardDetails = holder.itemView.findViewById<TapTextView>(R.id.textViewCardDetails)
            Glide.with(holder.itemView.context)
                    .load(arrayListCards[i].icon)
                    .into(imageViewCard)

            textViewCardDetails.text= maskCardNumber(arrayListCards[i].title)
        }

    }

    private fun typeRedirect(holder: RecyclerView.ViewHolder, position: Int) {
        if (selectedPosition == position) {
            if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
                holder.itemView.setBackgroundResource(R.drawable.border_shadow_black)
            } else {
                holder.itemView.setBackgroundResource(R.drawable.border_shadow)
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
            holder.itemView.setOnClickListener {
                if (!isShaking) {
                onCardSelectedActionListener.onCardSelectedAction(
                    true,
                    adapterContent[holder.adapterPosition]
                )
                selectedPosition = position
                notifyDataSetChanged()
            }
        }
        for (i in 0 until arrayListRedirect.size) {
            Glide.with(holder.itemView.context)
                    .load(arrayListRedirect[i])
                    .into(holder.itemView.imageView_knet)
        }
    }

    private fun typeGoPaySavedCard(holder: RecyclerView.ViewHolder, position: Int){
        tapCardFrameLayout = holder.itemView.findViewById(R.id.tapCardChip4)
        tapCardFrameLayout.visibility = View.VISIBLE

        holder.itemView.setOnClickListener {
            if (!isShaking) {
                selectedPosition = position
                onCardSelectedActionListener.onGoPayLogoutClicked(true)
                notifyDataSetChanged()
            }
        }

    }

    internal class SavedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    internal class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    internal class GoPayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
       // itemtapCardChip4


    }

    private fun maskCardNumber(cardinput: String): String {
        val maskLen: Int = cardinput.length - 4
            if (maskLen <= 0) return cardinput // Nothing to mask

        return (cardinput).replaceRange(0, 4, "....")

    }
}

