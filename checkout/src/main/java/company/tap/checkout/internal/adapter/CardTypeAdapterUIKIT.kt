package company.tap.checkout.internal.adapter


import android.annotation.SuppressLint
import android.app.Activity
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
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentsClient
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.api.models.PaymentOption
import company.tap.checkout.internal.api.models.SavedCard
import company.tap.checkout.internal.interfaces.OnCardSelectedActionListener
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.internal.utils.PaymentsUtil
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.TextViewTheme
import company.tap.tapuilibrary.uikit.ktx.setBorderedView
import kotlinx.android.synthetic.main.item_benefit_pay.view.*
import kotlinx.android.synthetic.main.item_googlepay.view.*
import kotlinx.android.synthetic.main.item_knet.view.*
import kotlinx.android.synthetic.main.item_save_cards.view.*
import mobi.foo.benefitinapp.data.Transaction
import mobi.foo.benefitinapp.listener.BenefitInAppButtonListener
import mobi.foo.benefitinapp.listener.CheckoutListener
import mobi.foo.benefitinapp.utils.BenefitInAppCheckout
import kotlin.collections.ArrayList


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
    private var arrayListSaveCard:ArrayList<List<SavedCard>> = ArrayList()
    private var adapterContent: List<PaymentOption> = java.util.ArrayList()
    private var adapterGooglePay: List<PaymentOption> = java.util.ArrayList()
    private var arrayListGoogle:ArrayList<String> = ArrayList()
    private var isShaking: Boolean = false
    private var goPayOpened: Boolean = false
    private var arrayModified : ArrayList<Any> = ArrayList()
    private var __context: Context? = null
    private var totalArraySize: Int =0

    val appId:String="4530082749"
    val merchantId:String="00000101"
    val seceret:String="3l5e0cstdim11skgwoha8x9vx9zo0kxxi4droryjp4eqd"
    val countrycode:String="1001"
    val mcc:String="4816"
    @DrawableRes
    val deleteIcon: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) R.drawable.icon_dark_closer else R.drawable.icon_light_closer

    // A client for interacting with the Google Pay API.
    private var paymentsClient: PaymentsClient? = null
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
    fun updateAdapterGooglePay(adapterGooglePay:List<PaymentOption>){
        this.adapterGooglePay =adapterGooglePay
        notifyDataSetChanged()

    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapterDataSavedCard(adapterSavedCard: List<SavedCard>) {
        arrayListCards=java.util.ArrayList()
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
            TYPE_GOOGLE_PAY -> {
                view =
                        LayoutInflater.from(parent.context).inflate(R.layout.item_googlepay, parent, false)
                GooglePayViewHolder(view)
            }
            TYPE_3PPG -> {
                view =
                        LayoutInflater.from(parent.context).inflate(R.layout.item_benefit_pay, parent, false)
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
      /*  println("position value are>>>" + position)
        println("totalArraySize value are>>>" + totalArraySize)*/
        if(position < adapterContent.size){
            if(adapterContent[position].paymentType==PaymentType.WEB){
             //   println("adapterContent value are>>>" + adapterContent[position].brand?.name)
                //BenefitPay condition for native removed
               /* if(adapterContent[position].brand?.name?.equals("benefit") == true){
                        return TYPE_3PPG
                    }else {*/
                            if(CustomUtils.getCurrentTheme()!=null && CustomUtils.getCurrentTheme().contains("dark")){
                                (adapterContent[position]).logos?.dark?.png?.let { arrayListRedirect.add(it) }
                            }else  (adapterContent[position]).logos?.light?.png?.let { arrayListRedirect.add(it) }
                      //  (adapterContent[position]).image?.let { arrayListRedirect.add(it) }
                        return TYPE_REDIRECT
                   // }
            }

        }

       // println("gpay paymentType"+adapterGooglePay[0].paymentType)
        if(adapterGooglePay.isNotEmpty() && adapterGooglePay[0].paymentType== PaymentType.GOOGLE_PAY )
        if(position - adapterContent.size < adapterGooglePay.size){
            return TYPE_GOOGLE_PAY
        }

         if(position.minus(adapterContent.size.plus(arrayListCards.size)) < adapterGooglePay.size){
            val index = totalArraySize -adapterContent.size - adapterGooglePay.size
            if(arrayListCards[index-1].`object`.toUpperCase()==PaymentType.CARD.name){
                arrayListSaveCard.clear()
                arrayListSaveCard.add(arrayListCards)
                return  TYPE_SAVED_CARD
            }
        }

        return -1

    }





    override fun getItemCount(): Int {
        if(adapterGooglePay.isNotEmpty()){
            totalArraySize = adapterContent.size.plus(adapterGooglePay.size).plus(arrayListCards.size)
        }else {
            totalArraySize = adapterContent.size.plus(arrayListCards.size)

        }

        return totalArraySize.plus(1)

    }


    private fun setOnClickActions(holder: RecyclerView.ViewHolder, position: Int) {
        if (isShaking) {
            holder.itemView.deleteImageViewSaved?.visibility = View.VISIBLE
            setUnSelectedCardTypeSavedShadowAndBackground(holder)
        }else{holder.itemView.deleteImageViewSaved?.visibility = View.GONE}

        holder.itemView.deleteImageViewSaved?.setOnClickListener {
            println("position>>>"+position)
            println("adapterContent>>>"+adapterContent.size)
            onCardSelectedActionListener.onDeleteIconClicked(true, position.minus(adapterContent.size).minus(1), arrayListCards[position.minus(adapterContent.size).minus(1)].id, maskCardNumber(arrayListCards[position.minus(adapterContent.size).minus(1)].firstSix + arrayListCards[position.minus(adapterContent.size).minus(1)].lastFour),
                arrayListCards as ArrayList<SavedCard>
            )
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

    fun resetSelection (){
        selectedPosition = -1
       // adapterGooglePay =ArrayList()
       //onCardSelectedActionListener.onCardSelectedAction(false, null)
//        goPayOpenedfromMain(goPayOpened)
        notifyDataSetChanged()
    }



    fun removeItems() {
        if(goPayOpened) arrayModified = ArrayList(adapterContent)
        arrayModified.removeAt(0)
       // adapterContent= arrayModified as ArrayList<SavedCards>
        notifyDataSetChanged()
    }



    fun goPayOpenedfromMain(goPayOpened: Boolean){
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
        bindSavedCardData(holder, position)
        setOnSavedCardOnClickAction(holder, position)
      //  deleteSelectedCard(holder, position)
        setOnClickActions(holder, position)
    }


    private fun setOnSavedCardOnClickAction(holder: RecyclerView.ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            if (!isShaking) {
                resetSelection()
                //minus one because of gopaychip
                onCardSelectedActionListener.onCardSelectedAction(true, arrayListCards[position.minus(adapterContent.size).minus(1)])
                selectedPosition = position
                notifyDataSetChanged()
            }
//            tapActionButtonInterface.onSelectPaymentOptionActionListener()
        }

        }
    @SuppressLint("NotifyDataSetChanged")
    fun deleteSelectedCard(position: Int){
        //(arrayListCards as java.util.List<String>).remove(position)
        arrayListCards.toMutableList().removeAt(position)
        this.updateAdapterDataSavedCard(arrayListCards)
        this.notifyDataSetChanged()

    }
    private fun deleteSelectedCard(holder: RecyclerView.ViewHolder, position: Int){
        holder.itemView.deleteImageViewSaved?.setOnClickListener {
            onCardSelectedActionListener.onDeleteIconClicked(
                true,
                position.minus(adapterContent.size),
                arrayListCards[position.minus(adapterContent.size)].id,
                maskCardNumber(arrayListCards[position.minus(adapterContent.size)].firstSix + arrayListCards[position.minus(adapterContent.size)].lastFour),
                arrayListCards as ArrayList<SavedCard>
            )
           // arrayListSaveCard.removeAt(position.minus(adapterContent.size))
            holder.itemView.clearAnimation()
            it.animate().cancel()
            it.clearAnimation()
            holder.itemView.deleteImageViewSaved?.visibility = View.GONE
            notifyDataSetChanged()
        }

    }


    private fun bindSavedCardData(holder: RecyclerView.ViewHolder, position: Int) {
        for (i in arrayListCards.indices) {
            if(adapterGooglePay.isNotEmpty()){
                GlideToVectorYou
                    .init()
                    .with(holder.itemView.context)
                    .load(arrayListCards[position.minus(adapterContent.size).minus(adapterGooglePay.size)].image.toUri(), holder.itemView.imageView_amex)
              holder.itemView.textViewCardDetails.text = maskCardNumber(arrayListCards[position.minus(adapterContent.size).minus(adapterGooglePay.size)].firstSix + arrayListCards[position.minus(adapterContent.size).minus(adapterGooglePay.size)].lastFour)
              //Remove no  holder.itemView.textViewCardDetails.text = "   " +arrayListCards[position.minus(adapterContent.size).minus(adapterGooglePay.size)].lastFour
                //holder.itemView.textViewCardDetails.text = adapterContent[holder.adapterPosition].chip1.title
            }else{
                // arrayListCards [position.minus(adapterContent.size)].image.let { holder.itemView.imageView_amex.loadSvg(it) }

                GlideToVectorYou
                    .init()
                    .with(holder.itemView.context)
                    .load(arrayListCards[position.minus(adapterContent.size)].image.toUri(), holder.itemView.imageView_amex)
              holder.itemView.textViewCardDetails.text = maskCardNumber(arrayListCards[position.minus(adapterContent.size)].firstSix + arrayListCards[position.minus(adapterContent.size)].lastFour)
               //REmove no holder.itemView.textViewCardDetails.text = "   " +arrayListCards[position.minus(adapterContent.size)].lastFour
                //holder.itemView.textViewCardDetails.text = adapterContent[holder.adapterPosition].chip1.title
            }

        }
        val saveCardTextViewTheme = TextViewTheme()
        saveCardTextViewTheme.textColor = Color.parseColor(ThemeManager.getValue("horizontalList.chips.savedCardChip.labelTextColor"))
        saveCardTextViewTheme.textSize =
            ThemeManager.getFontSize("horizontalList.chips.savedCardChip.labelTextFont")
        saveCardTextViewTheme.font = ThemeManager.getFontName("horizontalList.chips.savedCardChip.labelTextFont")
        holder.itemView.textViewCardDetails.setTheme(saveCardTextViewTheme)

    }

    private fun setSavedCardShakingAnimation(holder: RecyclerView.ViewHolder) {

        holder.itemView.deleteImageViewSaved.setImageResource(deleteIcon)
        if (isShaking) {
            Log.d("isShaking", isShaking.toString())
            val animShake: Animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.shake)
            holder.itemView.startAnimation(animShake)
            holder.itemView.deleteImageViewSaved?.visibility = View.VISIBLE
        }else holder.itemView.deleteImageViewSaved?.visibility = View.GONE
    }

    private fun setSelectedCardTypeSavedShadowAndBackground(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            holder.itemView.tapCardChip2.setBackgroundResource(R.drawable.border_shadow_white)
        } else {
            holder.itemView.tapCardChip2.setBackgroundResource(R.drawable.border_shadow)
        }
        setBorderedView(
                holder.itemView.tapCardChip2Constraints,
                //(ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis
               19.0f,// corner raduis
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
               // (ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis
               19.0f,// corner raduis
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
        holder.itemView.setOnClickListener {if (!isShaking) { setOnRedirectCardOnClickAction(holder, position) }}
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
                    this)
                println("values set in benefit checckout are ${"app val : "+ appId +"merchant id :"+ merchantId +"sceret : " + seceret+"countrycode are :"+ countrycode +"mcc val : "+mcc } ")
            }

            override fun onFail(p0: Int) {
                println("failed is value is  ${p0}")
                Toast.makeText(__context, "onFail"+p0, Toast.LENGTH_SHORT).show()


            }

            override fun onTransactionSuccess(p0: Transaction?) {
                Toast.makeText(__context, "onTransactionSuccess"+p0, Toast.LENGTH_SHORT).show()
            }

            override fun onTransactionFail(p0: Transaction?) {
                Toast.makeText(__context, "onTransactionFail"+p0, Toast.LENGTH_SHORT).show()
            }


        })

    }

    private fun typeGooglePay(holder: RecyclerView.ViewHolder, position: Int) {
        if (selectedPosition == position) setSelectedGoogleShadowAndBackground(holder)
        else setUnSelectedCardTypeGoogleShadowAndBackground(holder)
        println("typeGooglePay is called?????"+position)
        (holder as GooglePayViewHolder)
        // Initialize a Google Pay API client for an environment suitable for testing.
        // It's recommended to create the PaymentsClient object inside of the onCreate method.
      //  paymentsClient = PaymentsUtil().createPaymentsClient(holder.itemView.context as Activity)
        possiblyShowGooglePayButton(holder)
       // setUnSelectedCardTypeGoogleShadowAndBackground(holder)
        holder.itemView.googlePayButton.setOnClickListener {
         //   setSelectedGoogleShadowAndBackground(holder)
           // if (!isShaking) {
              //  selectedPosition = position
                println("typeGooglePay is clicked")
            onCardSelectedActionListener.onCardSelectedAction(true, adapterGooglePay[position-1])

           // onCardSelectedActionListener.onGooglePayClicked(true)
              //  goPayOpenedfromMain(goPayOpened)
                notifyDataSetChanged()
           // }
        }
    }
    private fun setOnRedirectCardOnClickAction(holder: RecyclerView.ViewHolder, position: Int) {
        onCardSelectedActionListener.onCardSelectedAction(true, adapterContent[holder.adapterPosition])
        selectedPosition = position
        notifyDataSetChanged()
        //Hide keyboard of any open
        CustomUtils.hideKeyboardFrom(holder.itemView.context, holder.itemView)
    }

    private fun bindRedirectCardImage(holder: RecyclerView.ViewHolder) {
        for (i in 0 until arrayListRedirect.size) {
            val imageViewCard = holder.itemView.findViewById<ImageView>(R.id.imageView_knet)
          //  arrayListRedirect[i].let { imageViewCard.loadSvg(it) }
            Glide.with(holder.itemView.context)
                .load(arrayListRedirect[i].toUri())
                .into(imageViewCard)
           /* GlideToVectorYou
                .init()
                .with(holder.itemView.context)
                .load(arrayListRedirect[i].toUri(), imageViewCard)*/
        }
    }

    private fun setSelectedCardTypeRedirectShadowAndBackground(holder: RecyclerView.ViewHolder) {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) (holder.itemView.setBackgroundResource(
                R.drawable.border_shadow_white
        ))
        else holder.itemView.setBackgroundResource(R.drawable.border_shadow)

        setBorderedView(
                holder.itemView.tapCardChip3Linear,
           // (ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis

            19.0f,// corner raduis
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
                //(ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis
                19.0f,// corner raduis
                0.0f,
                parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.selected.shadow.color")),// stroke color
                parseColor(ThemeManager.getValue("horizontalList.chips.gatewayChip.backgroundColor")),// tint color
                parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.unSelected.shadow.color"))
        )// shadow color
    }
    private fun setUnSelectedCardTypeGoogleShadowAndBackground(holder: RecyclerView.ViewHolder) {
       /* if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) holder.itemView.setBackgroundResource(
            R.drawable.border_unclick_black
        )
        else holder.itemView.setBackgroundResource(R.drawable.border_unclick)*/
      /*  setBorderedView(
            holder.itemView.tapCardChip6Linear,
            // (ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),// corner raduis
            15.0f,// corner raduis
            0.0f,
            parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.selected.shadow.color")),// stroke color
            parseColor(ThemeManager.getValue("horizontalList.chips.gatewayChip.backgroundColor")),// tint color
            parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.unSelected.shadow.color"))
        )// shadow color*/
    }
    private fun setSelectedGoogleShadowAndBackground(holder: RecyclerView.ViewHolder) {
       /* if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) (holder.itemView.setBackgroundResource(
            R.drawable.border_shadow_white
        ))
        else holder.itemView.setBackgroundResource(R.drawable.border_shadow)

        setBorderedView(
            holder.itemView.tapCardChip6Linear,
            15.0f,// corner raduis
            0.0f,
            parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.selected.shadow.color")),// stroke color
            parseColor(ThemeManager.getValue("horizontalList.chips.gatewayChip.backgroundColor")),// tint color
            parseColor(ThemeManager.getValue("horizontalList.chips.goPayChip.unSelected.shadow.color"))
        )// shadow color*/
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
    /**
     * Determine the viewer's ability to pay with a payment method supported by your app and display a
     * Google Pay payment button.
     *
     * @see [](https://developers.google.com/android/reference/com/google/android/gms/wallet/
    PaymentsClient.html.isReadyToPay
    ) */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun possiblyShowGooglePayButton(holder: GooglePayViewHolder) {

        val isReadyToPayJson = PaymentsUtil.isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return
        println("isReadyToPayJson"+request.toJson())
        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        val task = paymentsClient?.isReadyToPay(request)
        task?.addOnCompleteListener { completedTask ->
            try {
              //  completedTask.getResult(ApiException::class.java)?.let(::setGooglePayAvailable)
                setGooglePayAvailable( completedTask.getResult(ApiException::class.java),holder)
            } catch (exception: ApiException) {
                // Process error
                Log.w("isReadyToPay failed", exception)
            }
        }
    }

    /**
     * If isReadyToPay returned `true`, show the button and hide the "checking" text. Otherwise,
     * notify the user that Google Pay is not available. Please adjust to fit in with your current
     * user flow. You are not required to explicitly let the user know if isReadyToPay returns `false`.
     *
     * @param available isReadyToPay API response.
     */
    private fun setGooglePayAvailable(available: Boolean,holder: RecyclerView.ViewHolder) {
        println("available$available")
        if (available) {
            holder.itemView.googlePayButton.setVisibility(View.VISIBLE)
        } else {
            holder.itemView.googlePayButton.setVisibility(View.GONE)
           // Toast.makeText(holder.itemView.getContext(), R.string.googlepay_button_not_supported, Toast.LENGTH_LONG).show()
        }
    }


}

