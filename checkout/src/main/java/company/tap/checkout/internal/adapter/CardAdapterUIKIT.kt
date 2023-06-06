package company.tap.checkout.internal.adapter


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.PaymentType
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
import mobi.foo.benefitinapp.data.Transaction
import mobi.foo.benefitinapp.listener.BenefitInAppButtonListener
import mobi.foo.benefitinapp.listener.CheckoutListener
import mobi.foo.benefitinapp.utils.BenefitInAppCheckout


/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

@Suppress("PrivatePropertyName")
class CardAdapterUIKIT(private val onCardSelectedActionListener: OnCardSelectedActionListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedPosition = -1
    private var disabledClicked = false

    private var arrayListRedirect: ArrayList<String> = ArrayList()
    private var savedCardsArrayList: List<SavedCard> = arrayListOf()
    private var webArrayListContent: List<PaymentOption> = arrayListOf()
    private var googlePayArrayList: List<PaymentOption> = arrayListOf()
    private var disabledPaymentOptions: List<PaymentOption> = arrayListOf()
    private var isShaking: Boolean = false
    private var goPayOpened: Boolean = false
    private var arrayModified: ArrayList<Any> = ArrayList()
    private var __context: Context? = null
    private var totalArraySize: Int = 0

    val appId: String = "4530082749"
    val merchantId: String = "00000101"
    val seceret: String = "3l5e0cstdim11skgwoha8x9vx9zo0kxxi4droryjp4eqd"
    val countrycode: String = "1001"
    val mcc: String = "4816"

    @DrawableRes
    val deleteIcon: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) R.drawable.icon_dark_closer else R.drawable.icon_light_closer

    @DrawableRes
    val googlePayIcon: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) R.drawable.googlepay_button_content else R.drawable.googlepay_button_content_white

    var deleteImageView: ImageView? = null

    companion object {
        private const val TYPE_SAVED_CARD = 5
        private const val TYPE_REDIRECT = 2
        private const val TYPE_GOOGLE_PAY = 4
        private const val TYPE_3PPG = 1
        private const val TYPE_DISABLED_PAYMENT_OPTIONS = 6

    }

    fun updateAdapterData(adapterContent: List<PaymentOption>) {
        this.webArrayListContent = adapterContent
        notifyDataSetChanged()
    }

    fun updateAdapterGooglePay(adapterGooglePay: List<PaymentOption>) {
        this.googlePayArrayList = adapterGooglePay
        notifyDataSetChanged()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapterDataSavedCard(adapterSavedCard: List<SavedCard>) {
        this.savedCardsArrayList = adapterSavedCard
        notifyDataSetChanged()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDisabledPaymentOptions(paymentOptionsDisable: List<PaymentOption>) {
        this.disabledPaymentOptions = paymentOptionsDisable
        notifyDataSetChanged()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDisabledPaymentOptionsForSpecificItem(paymentOptionsDisable: List<PaymentOption>,position: Int) {
        this.disabledPaymentOptions = paymentOptionsDisable
        selectedPosition = position
        notifyItemChanged(position)

    }



    fun updateShaking(isShaking: Boolean) {
        this.isShaking = isShaking
        notifyDataSetChanged()
    }


    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is SavedViewHolder) {
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

                }) // messageHolder.message being the IncomingTextMessage kept in MyMessageViewHolder
            }
        }
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
            TYPE_GOOGLE_PAY -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_googlepay, parent, false)

                GooglePayViewHolder(view)
            }

            TYPE_DISABLED_PAYMENT_OPTIONS -> {
                view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_disabled, parent, false)
                SingleViewHolder(view)
            }
            TYPE_3PPG -> {
                view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_benefit_pay, parent, false)
                GooglePayViewHolder(view)
            }
            else -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_gopay, parent, false)
                GoPayViewHolder(view)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {

        if (position < webArrayListContent.size) {
            if (webArrayListContent[position].paymentType == PaymentType.WEB) {
                logicTosetLogoBasedOnTheme(position)
                return TYPE_REDIRECT
            }


        }

        if (googlePayArrayList.isNotEmpty() && googlePayArrayList[0].paymentType == PaymentType.GOOGLE_PAY)
            if (position - webArrayListContent.size < googlePayArrayList.size) {
                return TYPE_GOOGLE_PAY
            }

        if (disabledPaymentOptions.isNotEmpty()) {
            if (position.minus(webArrayListContent.size)
                    .minus(googlePayArrayList.size) < disabledPaymentOptions.size
            ) {
                return TYPE_DISABLED_PAYMENT_OPTIONS
            }
        }

        if (position.minus(webArrayListContent.size)
                .minus(googlePayArrayList.size)
                .minus(disabledPaymentOptions.size) < savedCardsArrayList.size
        ) {
            return TYPE_SAVED_CARD
        }


        return TYPE_3PPG

    }

    private fun logicTosetLogoBasedOnTheme(position: Int) {
        if (webArrayListContent[position].logos != null)
            when (CustomUtils.getCurrentTheme()) {
                ThemeMode.dark.name -> {
                    (webArrayListContent[position]).logos?.dark?.png?.let { arrayListRedirect.add(it) }
                }
                ThemeMode.dark_colored.name -> {
                    (webArrayListContent[position]).logos?.dark_colored?.png?.let {
                        arrayListRedirect.add(
                            it
                        )
                    }
                }
                ThemeMode.light.name -> {
                    (webArrayListContent[position]).logos?.light?.png?.let {
                        arrayListRedirect.add(
                            it
                        )
                    }
                }
                ThemeMode.light_mono.name -> {
                    (webArrayListContent[position]).logos?.light_mono?.png?.let {
                        arrayListRedirect.add(
                            it
                        )
                    }
                }

            }

    }

    override fun getItemCount(): Int {
        totalArraySize =
            webArrayListContent.size.plus(googlePayArrayList.size).plus(savedCardsArrayList.size)
                .plus(disabledPaymentOptions.size)

        return totalArraySize

    }


    private fun setOnClickActions(holder: RecyclerView.ViewHolder, position: Int, card: SavedCard) {
        if (isShaking) {
            holder.itemView.deleteImageViewSaved?.visibility = View.VISIBLE
            setUnSelectedCardTypeSavedShadowAndBackground(holder)
        } else {
            holder.itemView.deleteImageViewSaved?.visibility = View.INVISIBLE
        }


        /***
         * Replaced deleteImageViewSaved close button click of chips with
         * setOnLongClickListener as per new requirement
         **/
        holder.itemView.setOnLongClickListener {

            onCardSelectedActionListener.onDeleteIconClicked(
                true,
                position.minus(webArrayListContent.size).minus(googlePayArrayList.size)
                    .minus(disabledPaymentOptions.size),
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


    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        when {

            /**
             * Saved Cards Type
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
            /**
             * 3Rd PartyPG Type
             */
            getItemViewType(position) == TYPE_3PPG -> {
                setAlphaWhenShaking(isShaking, holder)
                type3PPG(holder, position)
            }
            /**
             * Knet Type
             */
            getItemViewType(position) == TYPE_REDIRECT -> {
                setAlphaWhenShaking(isShaking, holder)
                typeRedirect(holder, position)
            }
            /**
             * GooglePay Type
             */
            getItemViewType(position) == TYPE_GOOGLE_PAY -> {
                setAlphaWhenShaking(isShaking, holder)
                typeGooglePay(holder, position)
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
                        println("goPay is clicked" + selectedPosition)
                        onCardSelectedActionListener.onCardSelectedAction(true, null)
                        goPayOpenedfromMain(goPayOpened)
                        notifyDataSetChanged()
                    }
                }
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


    private fun setAlphaWhenShaking(isShaking: Boolean, holder: RecyclerView.ViewHolder) {
        if (isShaking) holder.itemView.alpha = 0.4f
        else holder.itemView.alpha = 1.0f
    }

    private fun typeSavedCard(holder: RecyclerView.ViewHolder, position: Int) {
        setSavedCardShakingAnimation(holder)

        if (selectedPosition == position) setSelectedCardTypeSavedShadowAndBackground(holder)
        else setUnSelectedCardTypeSavedShadowAndBackground(holder)
        val card =
            if (googlePayArrayList.isNotEmpty()) {
                Log.e("disabled", disabledPaymentOptions.size.toString())
                savedCardsArrayList[position.minus(webArrayListContent.size)
                    .minus(googlePayArrayList.size).minus(disabledPaymentOptions.size)]

            } else savedCardsArrayList[position.minus(webArrayListContent.size)
                .minus(disabledPaymentOptions.size)]
        bindSavedCardData(holder, position, card)
        setOnSavedCardOnClickAction(holder, position, card)
        setOnClickActions(holder, position, card)
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

    private fun typeRedirect(holder: RecyclerView.ViewHolder, position: Int) {
        if (selectedPosition == position) setSelectedCardTypeRedirectShadowAndBackground(holder)
        else setUnSelectedCardTypeRedirectShadowAndBackground(holder)

        (holder as SingleViewHolder)
        holder.itemView.setOnClickListener {
            if (!isShaking) {
                setOnRedirectCardOnClickAction(holder, position)
            }
        }
        bindRedirectCardImage(holder)
    }

    private fun typeDisabled(holder: RecyclerView.ViewHolder, position: Int) {
        val typeDisabled = disabledPaymentOptions[position.minus(webArrayListContent.size)
            .minus(googlePayArrayList.size)]



        if (selectedPosition == position) {
            if (typeDisabled.isDisabledClick) {
                setSelectedCardTypeDisabledOne(holder)
            } else
                setSelectedCardTypeDisabledShadowAndBackground(holder)
        } else setUnSelectedCardTypeDisabledShadowAndBackground(holder)


        val imageViewCard = holder.itemView.findViewById<ImageView>(R.id.imageView_disable)
        when (CustomUtils.getCurrentTheme()) {
            ThemeMode.dark.name -> {
                Glide.with(holder.itemView.context)
                    .load(typeDisabled.logos?.dark?.disabled?.png)
                    .into(imageViewCard)
            }
            ThemeMode.dark_colored.name -> {
                Glide.with(holder.itemView.context)
                    .load(typeDisabled.logos?.dark_colored?.disabled?.png)
                    .into(imageViewCard)
            }
            ThemeMode.light.name -> {
                Glide.with(holder.itemView.context)
                    .load(typeDisabled.logos?.light?.disabled?.png)
                    .into(imageViewCard)
            }
            ThemeMode.light_mono.name -> {
                Glide.with(holder.itemView.context)
                    .load(typeDisabled.logos?.light_mono?.disabled?.png)
                    .into(imageViewCard)
            }
        }

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onCardSelectedActionListener.onDisabledChipSelected(typeDisabled, position)
        }


    }

    private fun type3PPG(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.checkout_btn.setListener(object : BenefitInAppButtonListener,
            CheckoutListener {
            override fun onButtonClicked() {
                Toast.makeText(__context, "type3ppg", Toast.LENGTH_SHORT).show()
                BenefitInAppCheckout.newInstance(
                    __context as Activity,
                    appId,
                    "445544",
                    merchantId,
                    seceret,
                    "20.0",
                    "BH",
                    "048",
                    mcc,
                    "Tap",
                    "Manama",
                    this
                )
                println("values set in benefit checckout are ${"app val : " + appId + "merchant id :" + merchantId + "sceret : " + seceret + "countrycode are :" + countrycode + "mcc val : " + mcc} ")
            }

            override fun onFail(p0: Int) {
                println("failed is value is  ${p0}")
                Toast.makeText(__context, "onFail" + p0, Toast.LENGTH_SHORT).show()


            }

            override fun onTransactionSuccess(p0: Transaction?) {
                Toast.makeText(__context, "onTransactionSuccess" + p0, Toast.LENGTH_SHORT).show()
            }

            override fun onTransactionFail(p0: Transaction?) {
                Toast.makeText(__context, "onTransactionFail" + p0, Toast.LENGTH_SHORT).show()
            }


        })

    }

    private fun typeGooglePay(holder: RecyclerView.ViewHolder, position: Int) {
        if (selectedPosition == position) setSelectedGoogleShadowAndBackground(holder)
        else setUnSelectedCardTypeGoogleShadowAndBackground(holder)
        holder.itemView.googlePayButton.setImageResource(googlePayIcon)
        holder.itemView.googlePayButton.setOnClickListener {
            if (position > 0) {
                onCardSelectedActionListener.onCardSelectedAction(
                    true,
                    googlePayArrayList[position - 1]
                )
            } else onCardSelectedActionListener.onCardSelectedAction(
                true,
                googlePayArrayList[position]
            )
            selectedPosition = position
            notifyDataSetChanged()
        }
    }


    private fun setOnRedirectCardOnClickAction(holder: RecyclerView.ViewHolder, position: Int) {
        onCardSelectedActionListener.onCardSelectedAction(
            true,
            webArrayListContent[holder.bindingAdapterPosition]
        )
        selectedPosition = position
        notifyDataSetChanged()
        //Hide keyboard of any open
        CustomUtils.hideKeyboardFrom(holder.itemView.context, holder.itemView)
    }

    private fun bindRedirectCardImage(holder: RecyclerView.ViewHolder) {
        for (i in 0 until arrayListRedirect.size) {
            val imageViewCard = holder.itemView.findViewById<ImageView>(R.id.imageView_knet)
            Glide.with(holder.itemView.context)
                .load(arrayListRedirect[i].toUri())
                .into(imageViewCard)
        }
    }

    private fun setSelectedCardTypeRedirectShadowAndBackground(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) (holder.itemView.tapCardChip3.setBackgroundResource(
            R.drawable.border_shadow_white
        ))
        else holder.itemView.tapCardChip3.setBackgroundResource(R.drawable.border_shadow)


    }

    private fun setSelectedCardTypeDisabledOne(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) (holder.itemView.tapCardChip_disabled.setBackgroundResource(
            R.drawable.border_shadow_white
        ))
        else holder.itemView.tapCardChip_disabled.setBackgroundResource(R.drawable.border_shadow)


    }

    private fun setSelectedCardTypeDisabledShadowAndBackground(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) (holder.itemView.tapCardChip_disabled.setBackgroundResource(
            R.drawable.border_shadow_white
        ))
        else holder.itemView.tapCardChip_disabled.setBackgroundResource(R.drawable.border_shadow_disabled)


    }


    private fun setUnSelectedCardTypeRedirectShadowAndBackground(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) holder.itemView.tapCardChip3.setBackgroundResource(
            R.drawable.border_unclick_black
        )
        else holder.itemView.tapCardChip3.setBackgroundResource(R.drawable.border_unclick_white)
    }

    private fun setUnSelectedCardTypeDisabledShadowAndBackground(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) holder.itemView.tapCardChip_disabled.setBackgroundResource(
            R.drawable.border_unclick_black
        )
        else holder.itemView.findViewById<FrameLayout>(R.id.tapCardChip_disabled)
            .setBackgroundResource(R.drawable.border_unclick_white)
    }


    private fun setSelectedGoogleShadowAndBackground(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            holder.itemView.tapCardChip6.setBackgroundResource(R.drawable.border_shadow_white)
        } else {
            holder.itemView.tapCardChip6.setBackgroundResource(R.drawable.border_shadow)
        }

    }

    internal class SavedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    internal class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    internal class GoPayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    internal class GooglePayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun maskCardNumber(cardInput: String): String {
        val maskLen: Int = cardInput.length - 4
        if (maskLen <= 0) return cardInput // Nothing to mask
        return (cardInput).replaceRange(0, 6, "••• ")
    }


    private fun setUnSelectedCardTypeGoogleShadowAndBackground(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            holder.itemView.tapCardChip6.setBackgroundResource(R.drawable.border_unclick_black)
        } else {
            holder.itemView.tapCardChip6.setBackgroundResource(R.drawable.border_unclick_white)
        }
    }

}

