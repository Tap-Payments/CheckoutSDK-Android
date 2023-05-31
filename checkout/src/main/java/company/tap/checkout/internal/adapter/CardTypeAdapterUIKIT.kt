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
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.wallet.PaymentsClient
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
class CardTypeAdapterUIKIT(private val onCardSelectedActionListener: OnCardSelectedActionListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedPosition = -1
    private var arrayListRedirect: ArrayList<String> = ArrayList()

    // private var arrayListCards:ArrayList<Chip1> = ArrayList()
    private var arrayListCards: List<SavedCard> = java.util.ArrayList()
    private var arrayListSaveCard: ArrayList<List<SavedCard>> = ArrayList()
    private var adapterContent: List<PaymentOption> = java.util.ArrayList()
    private var adapterGooglePay: List<PaymentOption> = java.util.ArrayList()
    var arrayListCombined: ArrayList<Any> = ArrayList()
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


    // A client for interacting with the Google Pay API.
    private var paymentsClient: PaymentsClient? = null
    var deleteImageView: ImageView? = null

    companion object {
        private const val TYPE_SAVED_CARD = 5
        private const val TYPE_REDIRECT = 2
        private const val TYPE_GO_PAY = 3
        private const val TYPE_GOOGLE_PAY = 4
        private const val TYPE_3PPG = 1
    }

    fun updateAdapterData(adapterContent: List<PaymentOption>) {
        this.adapterContent = adapterContent
        notifyDataSetChanged()
    }

    fun updateAdapterGooglePay(adapterGooglePay: List<PaymentOption>) {
        this.adapterGooglePay = adapterGooglePay
        notifyDataSetChanged()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapterDataSavedCard(adapterSavedCard: List<SavedCard>) {
        this.arrayListCards = adapterSavedCard
        notifyDataSetChanged()

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

        if (position < adapterContent.size) {
            if (adapterContent[position].paymentType == PaymentType.WEB) {

                logicTosetLogoBasedOnTheme(position)
                return TYPE_REDIRECT

            }

        }

        if (adapterGooglePay.isNotEmpty() && adapterGooglePay[0].paymentType == PaymentType.GOOGLE_PAY)
            if (position - adapterContent.size < adapterGooglePay.size) {
                return TYPE_GOOGLE_PAY
            }


        return TYPE_SAVED_CARD

    }

    private fun logicTosetLogoBasedOnTheme(position: Int) {
        if(adapterContent[position].logos!=null)
            when(CustomUtils.getCurrentTheme()){
                ThemeMode.dark.name->{
                    (adapterContent[position]).logos?.dark?.png?.let { arrayListRedirect.add(it) }
                }
                ThemeMode.dark_colored.name->{
                    (adapterContent[position]).logos?.dark_colored?.png?.let { arrayListRedirect.add(it) }
                }
                ThemeMode.light.name->{
                    (adapterContent[position]).logos?.light?.png?.let { arrayListRedirect.add(it) }
                }
                ThemeMode.light_mono.name->{
                    (adapterContent[position]).logos?.light_mono?.png?.let { arrayListRedirect.add(it) }
                }

            }

    }

    override fun getItemCount(): Int {
        if (adapterGooglePay.isNotEmpty()) {
            totalArraySize =
                adapterContent.size.plus(adapterGooglePay.size).plus(arrayListCards.size)
        } else {
            totalArraySize = adapterContent.size.plus(arrayListCards.size)

        }

        //TODO :removed goPAY as requested @osama totalArraySize.plus(1)
        return totalArraySize

    }


    private fun setOnClickActions(holder: RecyclerView.ViewHolder, position: Int) {
        if (isShaking) {
            holder.itemView.deleteImageViewSaved?.visibility = View.VISIBLE
            setUnSelectedCardTypeSavedShadowAndBackground(holder)
        } else {
            holder.itemView.deleteImageViewSaved?.visibility = View.INVISIBLE
        }

        holder.itemView.deleteImageViewSaved?.setOnClickListener {
            println("position>>>" + position)
            println("adapterContent>>>" + adapterContent.size)
            println("arraylistCards>>>" + arrayListCards.size)
            val cardNeeded =
                arrayListCards[position.minus(adapterContent.size).minus(adapterGooglePay.size)]
            onCardSelectedActionListener.onDeleteIconClicked(
                true,
                position.minus(adapterContent.size).minus(adapterGooglePay.size),
                cardNeeded.id,
                cardNeeded.lastFour,
                arrayListCards as ArrayList<SavedCard>,
                holder.itemView.findViewById(R.id.tapCardChip2),
                holder.itemView.findViewById(R.id.tapCardChip2Constraints),
                holder.bindingAdapterPosition
            )
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
        arrayListCombined = ArrayList()
        if (adapterContent.isNotEmpty()) arrayListCombined.addAll(adapterContent)
        if (adapterGooglePay.isNotEmpty()) arrayListCombined.addAll(adapterGooglePay)
        if (arrayListCards.isNotEmpty()) arrayListCombined.addAll(arrayListCards)
        arrayListCombined.add("goPay")
    }

    fun resetSelection() {
        selectedPosition = -1
        // adapterGooglePay =ArrayList()
        //onCardSelectedActionListener.onCardSelectedAction(false, null)
//        goPayOpenedfromMain(goPayOpened)
        notifyDataSetChanged()
    }


    fun removeItems() {
        if (goPayOpened) arrayModified = ArrayList(adapterContent)
        arrayModified.removeAt(0)
        // adapterContent= arrayModified as ArrayList<SavedCards>
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

        (holder as SavedViewHolder)
        bindSavedCardData(holder, position)
        setOnSavedCardOnClickAction(holder, position)
        setOnClickActions(holder, position)
    }


    private fun setOnSavedCardOnClickAction(holder: RecyclerView.ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            if (!isShaking) {
                //resetSelection()
                onCardSelectedActionListener.onCardSelectedAction(true, arrayListCombined[position])
                selectedPosition = position
            }
        }

    }


    private fun bindSavedCardData(holder: RecyclerView.ViewHolder, position: Int) {
        val card = if (adapterGooglePay.isNotEmpty()) arrayListCards[position.minus(adapterContent.size)
                .minus(adapterGooglePay.size)] else arrayListCards[position.minus(adapterContent.size)]
        // arrayListCards [position.minus(adapterContent.size)].image.let { holder.itemView.imageView_amex.loadSvg(it) }
        var loadUrlString : String=""
        when(CustomUtils.getCurrentTheme()){
            ThemeMode.dark.name->{
                loadUrlString = card.logos?.dark?.png.toString()
            }
            ThemeMode.dark_colored.name->{
                loadUrlString = card.logos?.dark_colored?.png.toString()
            }
            ThemeMode.light.name->{
                loadUrlString = card.logos?.light?.png.toString()

            }
            ThemeMode.light_mono.name->{
                loadUrlString = card.logos?.light_mono?.png.toString()
            }

        }
        println("loadUrlString ll"+loadUrlString)
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
        //  possiblyShowGooglePayButton(holder as GooglePayViewHolder)
        holder.itemView.googlePayButton.setOnClickListener {
            println("typeGooglePay is clicked")
            if (position > 0) {
                onCardSelectedActionListener.onCardSelectedAction(
                    true,
                    adapterGooglePay[position - 1]
                )
            } else onCardSelectedActionListener.onCardSelectedAction(
                true,
                adapterGooglePay[position]
            )
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    /** Had kept if product team changes mind to get logos from api for device payments*/
    /* private fun bindGooglePayImageData(holder: GooglePayViewHolder ,position: Int) {
         if(position>0) {
             if (CustomUtils.getCurrentTheme() != null && CustomUtils.getCurrentTheme()
                     .contains("dark")
             ) {
                 Glide.with(holder.itemView.context)
                     .load(adapterGooglePay[position - 1].logos?.dark?.png?.toUri())
                     .into(holder.itemView.googlePayButton)
             } else {
                 Glide.with(holder.itemView.context)
                     .load(adapterGooglePay[position - 1].logos?.light?.png?.toUri())
                     .into(holder.itemView.googlePayButton)
             }
         }else{
             if (CustomUtils.getCurrentTheme() != null && CustomUtils.getCurrentTheme()
                     .contains("dark")
             ) {
                 Glide.with(holder.itemView.context)
                     .load(adapterGooglePay[position].logos?.dark?.png?.toUri())
                     .into(holder.itemView.googlePayButton)
             } else {
                 Glide.with(holder.itemView.context)
                     .load(adapterGooglePay[position].logos?.light?.png?.toUri())
                     .into(holder.itemView.googlePayButton)
             }
         }
     }*/

    private fun setOnRedirectCardOnClickAction(holder: RecyclerView.ViewHolder, position: Int) {
        onCardSelectedActionListener.onCardSelectedAction(
            true,
            adapterContent[holder.adapterPosition]
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

    private fun setUnSelectedCardTypeRedirectShadowAndBackground(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) holder.itemView.tapCardChip3.setBackgroundResource(
            R.drawable.border_unclick_black
        )
        else holder.itemView.tapCardChip3.setBackgroundResource(R.drawable.border_unclick_white)
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

