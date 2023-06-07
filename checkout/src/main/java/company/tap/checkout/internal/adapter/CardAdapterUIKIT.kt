package company.tap.checkout.internal.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import company.tap.checkout.R
import company.tap.checkout.internal.api.models.PaymentOption
import company.tap.checkout.internal.api.models.SavedCard
import company.tap.checkout.internal.enums.ThemeMode
import company.tap.checkout.internal.interfaces.OnCardSelectedActionListener
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.themekit.theme.TextViewTheme
import kotlinx.android.synthetic.main.item_benefit_pay.view.*
import kotlinx.android.synthetic.main.item_disabled.view.*
import kotlinx.android.synthetic.main.item_googlepay.view.*
import kotlinx.android.synthetic.main.item_knet.view.*
import kotlinx.android.synthetic.main.item_save_cards.view.*


/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

const val strokeWidth = 1.5

@Suppress("PrivatePropertyName")
class CardAdapterUIKIT(private val onCardSelectedActionListener: OnCardSelectedActionListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedPosition = -1
    private var savedCardsArrayList: List<SavedCard> = arrayListOf()
    private var webArrayListContent: List<PaymentOption> = arrayListOf()
    private var disabledPaymentOptions: List<PaymentOption> = arrayListOf()
    private var isShaking: Boolean = false
    private var goPayOpened: Boolean = false
    private var arrayModified: ArrayList<Any> = ArrayList()
    private var __context: Context? = null

    @DrawableRes
    val deleteIcon: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) R.drawable.icon_dark_closer else R.drawable.icon_light_closer

    var deleteImageView: ImageView? = null

    companion object {
        private const val TYPE_SAVED_CARD = 2
        private const val TYPE_DISABLED_PAYMENT_OPTIONS = 1

    }

    fun updateAdapterData(adapterContent: List<PaymentOption>) {
        this.webArrayListContent = adapterContent
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapterDataSavedCard(adapterSavedCard: List<SavedCard>) {
        this.savedCardsArrayList = adapterSavedCard
        notifyDataSetChanged()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDisabledPaymentOptions(
        paymentOptionsDisable: List<PaymentOption>,
        position: Int? = null
    ) {
        this.disabledPaymentOptions = paymentOptionsDisable
        notifyDataSetChanged()
        position?.let {
            selectedPosition = position
            notifyItemChanged(it)
        }
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
            TYPE_DISABLED_PAYMENT_OPTIONS -> {
                view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_disabled, parent, false)
                SingleViewHolder(view)
            }
            else -> {
                view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_disabled, parent, false)
                SingleViewHolder(view)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {

        if (disabledPaymentOptions.isNotEmpty()) {
            if (position < disabledPaymentOptions.size
            ) {
                return TYPE_DISABLED_PAYMENT_OPTIONS
            }
        }
        if (position.minus(disabledPaymentOptions.size) < savedCardsArrayList.size) {
            return TYPE_SAVED_CARD
        }
        return TYPE_SAVED_CARD

    }

    override fun getItemCount(): Int {
        return savedCardsArrayList.size.plus(disabledPaymentOptions.size)
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        when {

            /**
             * PaymentOptions All Type
             */
            getItemViewType(position) == TYPE_DISABLED_PAYMENT_OPTIONS -> {
                typeDisabled(holder, position)
            }
            /**
             * Saved Cards Type
             */
            getItemViewType(position) == TYPE_SAVED_CARD -> {
                typeSavedCard(holder, position)
            }

        }
    }

    fun resetSelection() {
        selectedPosition = -1
        notifyDataSetChanged()
    }


    fun removeItems() {
        if (goPayOpened) arrayModified = ArrayList(webArrayListContent)
        arrayModified.removeAt(0)
        notifyDataSetChanged()
    }


    fun goPayOpenedfromMain(goPayOpened: Boolean) {
        this.goPayOpened = goPayOpened
        notifyDataSetChanged()
    }


    private fun typeSavedCard(holder: RecyclerView.ViewHolder, position: Int) {
        setSavedCardShakingAnimation(holder)

        if (selectedPosition == position) setSelectedCardTypeSavedShadowAndBackground(holder)
        else setUnSelectedCardTypeSavedShadowAndBackground(holder)
        val card = savedCardsArrayList[position.minus(disabledPaymentOptions.size)]
        bindSavedCardData(holder, position, card)
        setOnSavedCardOnClickAction(holder, position, card)

        holder.itemView.setOnLongClickListener {
            onCardSelectedActionListener.onDeleteIconClicked(
                true,
                position.minus(disabledPaymentOptions.size),
                card.id,
                card.lastFour,
                savedCardsArrayList as ArrayList<SavedCard>,
                holder.itemView.findViewById(R.id.tapCardChip2),
                holder.itemView.findViewById(R.id.tapCardChip2Constraints),
                holder.bindingAdapterPosition
            )

            return@setOnLongClickListener true
        }
    }


    private fun setOnSavedCardOnClickAction(
        holder: RecyclerView.ViewHolder,
        position: Int,
        card: SavedCard
    ) {

        holder.itemView.setOnClickListener {
            if (!isShaking) {
                onCardSelectedActionListener.onCardSelectedAction(true, card)
                selectedPosition = position
                onCardSelectedActionListener.removePaymentInlineShrinkageAndDimmed()
            }
        }

    }


    private fun bindSavedCardData(holder: RecyclerView.ViewHolder, position: Int, card: SavedCard) {
        var loadUrlString: String = ""
        when (CustomUtils.getCurrentTheme()) {
            ThemeMode.dark.name -> {
                loadUrlString = card.logos?.dark?.png.toString()
            }
            ThemeMode.dark_colored.name -> {
                loadUrlString = card.logos?.dark_colored?.png.toString()
            }
            ThemeMode.light.name -> {
                loadUrlString = card.logos?.light?.png.toString()

            }
            ThemeMode.light_mono.name -> {
                loadUrlString = card.logos?.light_mono?.png.toString()
            }

        }
        Glide.with(holder.itemView.context)
            .load(loadUrlString).into(
                holder.itemView.imageView_amex as ImageView
            )
        holder.itemView.textViewCardDetails.text = maskCardNumber(
            card.firstSix + card.lastFour
        )
        if (isShaking) {
            Log.d("isShaking", isShaking.toString())
            val animShake: Animation =
                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.shake)
            holder.itemView.startAnimation(animShake)
            animShake.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    holder.itemView.setHasTransientState(false)
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }

            })
            holder.itemView.deleteImageViewSaved?.visibility = View.VISIBLE
        } else holder.itemView.deleteImageViewSaved?.visibility = View.INVISIBLE

        val saveCardTextViewTheme = TextViewTheme()
        saveCardTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("horizontalList.chips.savedCardChip.labelTextColor"))
        saveCardTextViewTheme.textSize =
            ThemeManager.getFontSize("horizontalList.chips.savedCardChip.labelTextFont")
        saveCardTextViewTheme.font =
            ThemeManager.getFontName("horizontalList.chips.savedCardChip.labelTextFont")
        holder.itemView.textViewCardDetails.setTheme(saveCardTextViewTheme)

    }


    private fun setSavedCardShakingAnimation(holder: RecyclerView.ViewHolder) {
        deleteImageView = holder.itemView.deleteImageViewSaved
        holder.itemView.deleteImageViewSaved.setImageResource(deleteIcon)

    }

    private fun setSelectedCardTypeSavedShadowAndBackground(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            holder.itemView.tapCardChip2.setBackgroundResource(R.drawable.border_shadow_white)
        } else {
            holder.itemView.tapCardChip2.setBackgroundResource(R.drawable.border_shadow)
        }
    }


    private fun setUnSelectedCardTypeSavedShadowAndBackground(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            holder.itemView.tapCardChip2.setBackgroundResource(R.drawable.border_unclick_black)
        } else {
            holder.itemView.tapCardChip2.setBackgroundResource(R.drawable.border_unclick_white)
        }
    }


    private fun typeDisabled(holder: RecyclerView.ViewHolder, position: Int) {
        val typeDisabled = disabledPaymentOptions[position]
        if (selectedPosition == position) {
            if (typeDisabled.isPaymentOptionEnabled) {
                setSelectedCardTypeDisabledShadowAndBackground(holder)
            } else
                setSelectedCardTypeDisabledShadowAndBackground(holder, isBackgroundDimmed = true)
        } else {
            if (typeDisabled.isPaymentOptionEnabled) {
                setUnSelectedCardTypeDisabledShadowAndBackground(holder)
            } else
                setUnSelectedCardTypeDisabledShadowAndBackground(holder, isBackgroundDimmed = true)
        }


        val imageViewCard = holder.itemView.findViewById<ImageView>(R.id.imageView_disable)
        when (CustomUtils.getCurrentTheme()) {
            ThemeMode.dark.name -> {
                if (typeDisabled.isPaymentOptionEnabled) {
                    Glide.with(holder.itemView.context)
                        .load(typeDisabled.logos?.dark?.svg)
                        .into(imageViewCard)
                } else
                    Glide.with(holder.itemView.context)
                        .load(typeDisabled.logos?.dark?.disabled?.svg)
                        .into(imageViewCard)
            }
            ThemeMode.dark_colored.name -> {
                if (typeDisabled.isPaymentOptionEnabled) {
                    Glide.with(holder.itemView.context)
                        .load(typeDisabled.logos?.dark_colored?.svg)
                        .into(imageViewCard)
                } else
                    Glide.with(holder.itemView.context)
                        .load(typeDisabled.logos?.dark_colored?.disabled?.svg)
                        .into(imageViewCard)
            }
            ThemeMode.light.name -> {
                if (typeDisabled.isPaymentOptionEnabled) {
                    Glide.with(holder.itemView.context)
                        .load(typeDisabled.logos?.light?.svg)
                        .into(imageViewCard)
                } else
                    Glide.with(holder.itemView.context)
                        .load(typeDisabled.logos?.light?.disabled?.svg)
                        .into(imageViewCard)
            }
            ThemeMode.light_mono.name -> {
                if (typeDisabled.isPaymentOptionEnabled) {
                    Glide.with(holder.itemView.context)
                        .load(typeDisabled.logos?.light_mono?.svg)
                        .into(imageViewCard)
                } else
                    Glide.with(holder.itemView.context)
                        .load(typeDisabled.logos?.light_mono?.disabled?.svg)
                        .into(imageViewCard)
            }
        }

        holder.itemView.setOnClickListener {
            selectedPosition = position
            onCardSelectedActionListener.onDisabledChipSelected(typeDisabled, position)
            notifyDataSetChanged()
        }


    }

    private fun setSelectedCardTypeDisabledShadowAndBackground(
        holder: RecyclerView.ViewHolder,
        isBackgroundDimmed: Boolean = false
    ) {

        with(holder.itemView.tapCardChip_disabled) {
            strokeWidth = strokeWidth
            if (isBackgroundDimmed) {
                alpha = 0.9f
            }
            if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
                setStrokeColor(ColorStateList.valueOf(resources.getColor(R.color.white)))
                setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.black_color)))

            } else {
                setStrokeColor(ColorStateList.valueOf(resources.getColor(R.color.greencolor)))
                setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.white)))

            }

        }


    }


    private fun setUnSelectedCardTypeDisabledShadowAndBackground(
        holder: RecyclerView.ViewHolder,
        isBackgroundDimmed: Boolean = false
    ) {

        with(holder.itemView.tapCardChip_disabled) {
            strokeWidth = 0
            if (isBackgroundDimmed) {
                alpha = 0.9f
            }
            if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
                setCardBackgroundColor(
                    ColorStateList.valueOf(
                        resources.getColor(
                            R.color.black_color
                        )
                    )
                )
            } else holder.itemView.tapCardChip_disabled.apply {
                setCardBackgroundColor(
                    ColorStateList.valueOf(
                        resources.getColor(
                            R.color.white
                        )
                    )
                )
            }
        }

    }


    internal class SavedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    internal class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun maskCardNumber(cardInput: String): String {
        val maskLen: Int = cardInput.length - 4
        if (maskLen <= 0) return cardInput // Nothing to mask
        return (cardInput).replaceRange(0, 6, "••• ")
    }


}

