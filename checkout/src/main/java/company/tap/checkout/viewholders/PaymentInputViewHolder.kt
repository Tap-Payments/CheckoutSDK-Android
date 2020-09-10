package company.tap.checkout.viewholders


import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.transition.Fade
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import company.tap.cardbusinesskit.testmodels.DummyResp
import company.tap.cardinputwidget.widget.CardInputListener
import company.tap.cardinputwidget.widget.inline.InlineCardInput
import company.tap.checkout.R
import company.tap.checkout.apiresponse.getJsonDataFromAsset
import company.tap.checkout.enums.SectionType
import company.tap.checkout.viewholders.PaymentInputViewHolder.PaymentType.CARD
import company.tap.checkout.viewholders.PaymentInputViewHolder.PaymentType.MOBILE
import company.tap.tapcardvalidator_android.CardBrand
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.fragment.NFCFragment
import company.tap.tapuilibrary.uikit.interfaces.TapSelectionTabLayoutInterface
import company.tap.tapuilibrary.uikit.models.SectionTabItem
import company.tap.tapuilibrary.uikit.views.TapMobilePaymentView
import company.tap.tapuilibrary.uikit.views.TapSelectionTabLayout
import java.net.URL


/**
 *
 * Created by Mario Gamal on 7/28/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class PaymentInputViewHolder(private val context: Context) : TapBaseViewHolder,
    TapSelectionTabLayoutInterface, CardInputListener {

    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.payment_input_layout, null)

    override val type = SectionType.PAYMENT_INPUT

    private val tabLayout: TapSelectionTabLayout
    private val paymentInputContainer: LinearLayout

    // private val paymentLayoutContainer: LinearLayout
    private val clearText: ImageView

    //  private val scannerOptions: LinearLayout
    private var selectedType = CARD
    private var shouldShowScannerOptions = true
   private val cardInputWidget = InlineCardInput(context)
    private val mobilePaymentView = TapMobilePaymentView(context, null)
    private var lastFocusField = CardInputListener.FocusField.FOCUS_CARD
    private var lastCardInput = ""
    private var cardScannerBtn: ImageView? = null
    private var nfcButton: ImageView? = null
    private var mobileNumberEditText: EditText? = null
    private var alertMessage: TapTextView? = null
    private var linearLayoutPay: LinearLayout? = null
    private var tabPosition: Int? = null
    private var dummyInitApiResponse: DummyResp? = null

    init {
        tabLayout = view.findViewById(R.id.sections_tablayout)
        paymentInputContainer = view.findViewById(R.id.payment_input_layout)
        //  paymentLayoutContainer = view.findViewById(R.id.payment_layout_container)
        //    scannerOptions = view.findViewById(R.id.scanner_options)
        clearText = view.findViewById(R.id.clear_text)
        val jsonFileString = this.let {
            getJsonDataFromAsset(
                    it.context,
                    "dummyapiresponse.json"
            )
        }
        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val gson = Gson()
        dummyInitApiResponse = gson.fromJson(
                jsonFileString,
                DummyResp::class.java
        )
        println("api response in payment input ${dummyInitApiResponse?.payment_methods} ")

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
        initTabLayout()
        initCardInput()
        initMobileInput()
        initClearText()
        initializeCardForm()
    }

    private fun initTabLayout() {
        tabLayout.addSection(getCardList())
        tabLayout.addSection(getMobileList())
        tabLayout.setTabLayoutInterface(this)
    }

    private fun initializeCardForm() {
        cardScannerBtn = view.findViewById(R.id.card_scanner_button)
        nfcButton = view.findViewById(R.id.nfc_button)
        mobileNumberEditText = view.findViewById(R.id.mobile_number)
        alertMessage = view.findViewById(R.id.textview_alert_message)

        linearLayoutPay = view.findViewById(R.id.linear_paylayout)
        cardInputWidget.clearFocus()
        clearText.visibility = View.GONE
        clearText.setOnClickListener {
            tabLayout.resetBehaviour()

            mobilePaymentView.clearNumber()
            /* tapCardInputView.setCardNumber("")
             tapCardInputView.setCvcCode("")*/
            cardInputWidget.clear()
            //  alert_text.visibility= View.GONE

            if (tabPosition == 1) {
                nfcButton?.visibility = View.INVISIBLE
                cardScannerBtn?.visibility = View.INVISIBLE

            }

            clearText.visibility = View.GONE
        }
        // alertMessage?.visibility = View.GONE
        nfcButton?.setOnClickListener {
            val nfcFragment = NFCFragment()

            val appCompatActivity = it.context as AppCompatActivity

            val fm: FragmentManager = appCompatActivity.getSupportFragmentManager()
            val newFrame: Fragment = NFCFragment()
            fm.beginTransaction().replace(R.id.fragment_container_nfc, newFrame).commit()
            Toast.makeText(context, "u clicked nfc", Toast.LENGTH_SHORT).show()

        }
        cardScannerBtn?.setOnClickListener {
            // val cardFragment = CardScannerFragment()
            tabLayout.visibility = View.GONE
            Toast.makeText(context, "u clicked card scanner", Toast.LENGTH_SHORT).show()

        }
    }

    private fun initClearText() {
        clearText.setOnClickListener {
            when (selectedType) {
                CARD -> cardInputWidget.clear()
                MOBILE -> mobilePaymentView.clearNumber()
            }
        }
    }

    private fun initMobileInput() {
        mobilePaymentView.mobileInputEditText.doAfterTextChanged {
            it?.let {
                if (it.isEmpty())
                    clearText.visibility = View.GONE
                else
                    clearText.visibility = View.VISIBLE
            }
        }
    }

    private fun initCardInput() {
        cardInputWidget.holderNameEnabled = false
        paymentInputContainer.addView(cardInputWidget)
        cardInputWidget.setCardNumberTextWatcher(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    lastCardInput = it.toString()
                    shouldShowScannerOptions = it.isEmpty()
                    controlScannerOptions()
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

    private fun getCardList(): ArrayList<SectionTabItem> {
        val items = ArrayList<SectionTabItem>()
        val url = URL(
                dummyInitApiResponse?.payment_methods?.get(
                        0
                )?.image
        )
        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())

        println("bmp id " + bmp)
        val drawable: Drawable = BitmapDrawable(context.resources, bmp)

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

        /*  items.add(
              SectionTabItem(
                  context.resources.getDrawable(
                      R.drawable.ic_visa
                  ), context.resources.getDrawable(R.drawable.ic_visa_black), CardBrand.visa
              )
          )
          items.add(
              SectionTabItem(
                  context.resources.getDrawable(
                      R.drawable.mastercard
                  ), context.resources.getDrawable(R.drawable.mastercard_gray), CardBrand.masterCard
              )
          )
          items.add(
              SectionTabItem(
                  context.resources.getDrawable(
                      R.drawable.amex
                  ), context.resources.getDrawable(R.drawable.amex_gray), CardBrand.americanExpress
              )
          )*/
        return items
    }

    private fun getMobileList(): ArrayList<SectionTabItem> {
        val items = ArrayList<SectionTabItem>()
        items.add(
                SectionTabItem(
                        context.resources.getDrawable(
                                R.drawable.zain_gray
                        ), context.resources.getDrawable(R.drawable.zain_dark), CardBrand.zain
                )
        )
        items.add(
                SectionTabItem(
                        context.resources.getDrawable(
                                R.drawable.ooredoo
                        ), context.resources.getDrawable(R.drawable.ooredoo_gray), CardBrand.ooredoo
                )
        )
        return items
    }

    override fun onTabSelected(position: Int?) {
        position?.let { swapInputViews(it) }
    }

    private fun swapInputViews(position: Int) {
        // TransitionManager.beginDelayedTransition(paymentLayoutContainer, Fade())
        TransitionManager.beginDelayedTransition(paymentInputContainer, Fade())
        paymentInputContainer.removeAllViews()
        if (position == 0) {
            selectedType = CARD
            nfcButton?.visibility = View.VISIBLE
            cardScannerBtn?.visibility = View.VISIBLE

            clearText.visibility = View.GONE
            paymentInputContainer.addView(cardInputWidget)
            checkForFocus()
        } else {
            selectedType = MOBILE
            nfcButton?.visibility = View.GONE
            cardScannerBtn?.visibility = View.GONE
            if (mobilePaymentView.mobileInputEditText.text.isEmpty())
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

    override fun onCardComplete() {}

    override fun onCvcComplete() {}

    override fun onExpirationComplete() {}

    override fun onFocusChange(focusField: String) {
        lastFocusField = focusField
    }


}

