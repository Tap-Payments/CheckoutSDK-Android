package company.tap.checkout.internal.viewholders


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.transition.Fade
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.IntRange
import androidx.core.widget.doAfterTextChanged
import company.tap.cardbusinesskit.testmodels.Payment_methods

import company.tap.cardinputwidget.widget.CardInputListener
import company.tap.cardinputwidget.widget.inline.InlineCardInput
import company.tap.checkout.R
import company.tap.checkout.internal.dummygener.TapCardPhoneListDataSource
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.onCardNFCCallListener
import company.tap.checkout.internal.interfaces.onPaymentCardComplete
import company.tap.checkout.internal.viewholders.PaymentInputViewHolder.PaymentType.CARD
import company.tap.checkout.internal.viewholders.PaymentInputViewHolder.PaymentType.MOBILE
import company.tap.tapcardvalidator_android.CardBrand
import company.tap.tapcardvalidator_android.CardValidationState
import company.tap.tapcardvalidator_android.CardValidator
import company.tap.tapuilibrary.themekit.ThemeManager

import company.tap.tapuilibrary.uikit.interfaces.TapSelectionTabLayoutInterface
import company.tap.tapuilibrary.uikit.models.SectionTabItem
import company.tap.tapuilibrary.uikit.views.TapMobilePaymentView
import company.tap.tapuilibrary.uikit.views.TapSelectionTabLayout
import kotlinx.android.synthetic.main.payment_input_layout.view.*
import kotlinx.android.synthetic.main.switch_layout.view.*
import java.net.URL


/**
 *
 * Created by Mario Gamal on 7/28/20
 * Modified by Ahlaam K
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class PaymentInputViewHolder(private val context: Context , private val onPaymentCardComplete: onPaymentCardComplete , private val onCardNFCCallListener: onCardNFCCallListener) : TapBaseViewHolder,
    TapSelectionTabLayoutInterface, CardInputListener {

    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.payment_input_layout, null)

    override val type = SectionType.PAYMENT_INPUT

    private val tabLayout: TapSelectionTabLayout
    private val paymentInputContainer: LinearLayout

    // private val paymentLayoutContainer: LinearLayout
    private val clearText: ImageView

    //  private val scannerOptions: LinearLayout
    var selectedType = CARD
    private var shouldShowScannerOptions = true
    private val cardInputWidget = InlineCardInput(context)
    private val mobilePaymentView = TapMobilePaymentView(context, null)
    private var lastFocusField = CardInputListener.FocusField.FOCUS_CARD
    private var lastCardInput = ""
    private var cardScannerBtn: ImageView? = null
    private var nfcButton: ImageView? = null

    private var linearLayoutPay: LinearLayout? = null
    private var tabPosition: Int? = null

    private var switchViewHolder = SwitchViewHolder(context)
    private var imageURL: String? = null
    private var isadded: Boolean = false
    private lateinit var paymentType: String
    private var itemsTelecom = ArrayList<SectionTabItem>()
    init {
        tabLayout = view.findViewById(R.id.sections_tablayout)
        paymentInputContainer = view.findViewById(R.id.payment_input_layout)
        //  paymentLayoutContainer = view.findViewById(R.id.payment_layout_container)
        //    scannerOptions = view.findViewById(R.id.scanner_options)
        clearText = view.findViewById(R.id.clear_text)
        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)
        // switchViewHolder = SwitchViewHolder(context)
        bindViewComponents()

    }

    fun setCardNumber(cardNumber: String?) {
        cardInputWidget.setCardNumber(cardNumber)
    }

    fun setExpDate(
        @IntRange(from = 1, to = 12) month: Int,
        @IntRange(from = 0, to = 9999) year: Int
    ) {
        cardInputWidget.setExpiryDate(month, year)
    }

    fun setCvc(cvcCode: String?) {
        cardInputWidget.setCvcCode(cvcCode)
    }

    override fun bindViewComponents() {
        // initTabLayout()
        initCardInput()
        initMobileInput()
        initClearText()
        initializeCardForm()
        /**
         * set separator background
         */
        view.separator?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))

    }

    private fun initTabLayout() {
println("sdsadad")
     //   tabLayout.addSection(getCardList())
       // tabLayout.addSection(getMobileList())
        tabLayout.setTabLayoutInterface(this)
    }

    private fun initializeCardForm() {
        cardScannerBtn = view.findViewById(R.id.card_scanner_button)
        nfcButton = view.findViewById(R.id.nfc_button)
        //  mobileNumberEditText = view.findViewById(R.id.mobile_number)
//        alertMessage = view.findViewById(R.id.textview_alert_message)
        nfcButton?.visibility = View.VISIBLE
        cardScannerBtn?.visibility = View.VISIBLE
        linearLayoutPay = view.findViewById(R.id.linear_paylayout)
        cardInputWidget.clearFocus()
        clearText.visibility = View.GONE
        clearText.setOnClickListener {
            tabLayout.resetBehaviour()
            println("is it called")
            mobilePaymentView.clearNumber()
            /* tapCardInputView.setCardNumber("")
             tapCardInputView.setCvcCode("")*/
            cardInputWidget.clear()
            //  alert_text.visibility= View.GONE
            onPaymentCardComplete.onPaycardSwitchAction(false)

            if (tabPosition == 1) {
                nfcButton?.visibility = View.INVISIBLE
                cardScannerBtn?.visibility = View.INVISIBLE

            }
            switchViewHolder.view.cardviewSwitch.visibility= View.INVISIBLE
            switchViewHolder.view.mainSwitch.visibility= View.GONE
            switchViewHolder.view.cardSwitch.visibility= View.GONE
            clearText.visibility = View.GONE

        }

        nfcButton?.setOnClickListener {
            onCardNFCCallListener.onClickNFC()

        }
        cardScannerBtn?.setOnClickListener {

         tabLayout.visibility = View.GONE
            onCardNFCCallListener.onClickCardScanner()
        }
    }

    private fun initClearText() {
        clearText.setOnClickListener {
            when (selectedType) {
                CARD -> cardInputWidget.clear()
                MOBILE -> mobilePaymentView.clearNumber()

            }
            println("is it selectedType")

        }
    }

    private fun initMobileInput() {
        mobilePaymentView.mobileNumber.doAfterTextChanged {
            it?.let {
                if (it.isEmpty())
                    clearText.visibility = View.GONE
                else
                    clearText.visibility = View.VISIBLE
            }
        }
    }

    //Setting on the cardInput with logics
    private fun initCardInput() {
        cardInputWidget.holderNameEnabled = false
         paymentInputContainer.removeView(cardInputWidget)
          paymentInputContainer.addView(cardInputWidget)
        cardInputWidget.clearFocus()
        //Textwatcher for cardNumber
        cardInputWidget.setCardNumberTextWatcher(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    lastCardInput = it.toString()
                    shouldShowScannerOptions = it.isEmpty()
                    controlScannerOptions()
                    cardBrandDetection(s.toString())
                    if (s.trim().length == 19 ) {
                       // onPaymentCardComplete.onPaycardAction(true)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty())
                    clearText.visibility = View.GONE
                else
                    clearText.visibility = View.VISIBLE
            }

        })

        // Textwatcher for CVV
        cardInputWidget.setCvcNumberTextWatcher(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.trim()?.length == 3 || s?.trim()?.length == 4 ) {
                    onPaymentCardComplete.onPaycardSwitchAction(true)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        //Textwatcher for Expiry date
        cardInputWidget.setExpiryDateTextWatcher(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


    }

    // Logic to show the switches when card details are valid
    private fun cardBrandDetection(cardTyped: String) {
        if (cardTyped.isEmpty())
            tabLayout.resetBehaviour()
        val card = CardValidator.validate(cardTyped.toString())
        if (card.cardBrand != null) {
            tabLayout.selectTab(
                card.cardBrand,
                card.validationState == CardValidationState.valid
            )
            println("card brand: ${card.validationState}")
            nfcButton?.visibility = View.GONE
            cardScannerBtn?.visibility = View.GONE

    }

    }

    private fun controlScannerOptions() {
        if (shouldShowScannerOptions) {
            nfcButton?.visibility = View.VISIBLE
            cardScannerBtn?.visibility = View.VISIBLE
        } else {

            nfcButton?.visibility = View.GONE
            cardScannerBtn?.visibility = View.GONE
        }
    }

    // Function to get the card names and images or logos from the API
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getCardList(imageIcon:String?): ArrayList<SectionTabItem> {
        val items = ArrayList<SectionTabItem>()
        if (imageIcon != null) {
            val url = URL(imageIcon)
            if (url != null) {
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                println("bmp id " + bmp)
                val drawable: Drawable = BitmapDrawable(context.resources, bmp)
                println("paymentType values are"+paymentType)
         //   if(paymentType.equals("card")){
               /* items.add(
                    SectionTabItem(
                        drawable, context.resources.getDrawable(R.drawable.ic_visa_black), CardBrand.visa
                    )
                )*/
//}
                drawable.let {
                    SectionTabItem(
                        drawable, context.resources.getDrawable(R.drawable.ic_visa_black),
                        CardBrand.visa
                    )
                }.let {
                    items.add(
                        it
                    )
                }
            }
            println("itemscard value is"+items)
            isadded = true

        }

        return items
    }

    // Function to get the mobile names and images or logos from the API .
    private fun getMobileList(): ArrayList<SectionTabItem> {
        val items1 = ArrayList<SectionTabItem>()

        if (imageURL != null) {
            val url = URL(imageURL)
            if (url != null) {
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                println("bmp id " + bmp)
                val drawableTelecom: Drawable = BitmapDrawable(context.resources, bmp)
                println("paymentType values are telecom "+paymentType)
                if(paymentType.equals("telecom")){
                    items1.add(
                        SectionTabItem(
                            drawableTelecom, context.resources.getDrawable(R.drawable.zain_dark), CardBrand.zain
                        )
                    )
                }
                }
            }
       /* items1.add(
            SectionTabItem(
                context.resources.getDrawable(
                    R.drawable.zain_gray
                ), context.resources.getDrawable(R.drawable.zain_dark), CardBrand.zain
            )
        )
        items1.add(
            SectionTabItem(
                context.resources.getDrawable(
                    R.drawable.ooredoo
                ), context.resources.getDrawable(R.drawable.ooredoo_gray), CardBrand.ooredoo
            )
        )*/
        return items1
    }

    override fun onTabSelected(position: Int?) {
        tabPosition = position
        position?.let { swapInputViews(it) }
    }

    private fun swapInputViews(position: Int) {
        // TransitionManager.beginDelayedTransition(paymentLayoutContainer, Fade())
        TransitionManager.beginDelayedTransition(paymentInputContainer, Fade())
        paymentInputContainer.removeAllViews()
        if (position == 0) {
            selectedType = CARD
            switchViewHolder.setSwitchLocals(selectedType)
            switchViewHolder.view.cardSwitch.switchGoPayCheckout.visibility = View.VISIBLE
            nfcButton?.visibility = View.VISIBLE
            cardScannerBtn?.visibility = View.VISIBLE

            clearText.visibility = View.GONE
            paymentInputContainer.addView(cardInputWidget)
            checkForFocus()
        } else {
            selectedType = MOBILE
            switchViewHolder.setSwitchLocals(selectedType)

            nfcButton?.visibility = View.GONE
            cardScannerBtn?.visibility = View.GONE
            if (mobilePaymentView.mobileNumber.text.isEmpty())
                clearText.visibility = View.INVISIBLE
            else
                clearText.visibility = View.VISIBLE

            paymentInputContainer.addView(mobilePaymentView)
        }
        tabPosition = position
    }

    private fun checkForFocus() {
        shouldShowScannerOptions =
            lastFocusField == CardInputListener.FocusField.FOCUS_CARD
                    && lastCardInput.isEmpty()
        controlScannerOptions()
    }

    enum class PaymentType {
        CARD,
        MOBILE
    }

    override fun onCardComplete() {
    }

    override fun onCvcComplete() {}

    override fun onExpirationComplete() {}

    override fun onFocusChange(focusField: String) {
        lastFocusField = focusField
    }

    /**
     * Sets data from API through LayoutManager
     * @param imageURLApi represents the images of payment methods.
     * */
    fun setDatafromAPI(imageURLApi:  List<TapCardPhoneListDataSource>) {
        println("imageURLApi1 size values are"+imageURLApi)
        for (i in 0 until imageURLApi.size) {
            imageURL = imageURLApi.get(i).icon

        //    println("imageURLApi2 size values are"+imageURLApi.get(i).paymentType)
            paymentType = imageURLApi.get(i).paymentType

            tabLayout.addSection(getCardList(imageURL))


        }
        initTabLayout()
      //  println("imageURL2 values are"+imageURLApi)
    }


}

