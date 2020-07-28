package company.tap.checkout.viewholders

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import company.tap.cardinputwidget.widget.CardInputListener
import company.tap.cardinputwidget.widget.inline.InlineCardInput
import company.tap.checkout.R
import company.tap.checkout.enums.SectionType
import company.tap.tapcardvalidator_android.CardBrand
import company.tap.tapuilibrary.models.SectionTabItem
import company.tap.tapuilibrary.views.TapSelectionTabLayout

/**
 *
 * Created by Mario Gamal on 7/28/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class PaymentInputViewHolder(private val context: Context) : TapBaseViewHolder {

    override val view = LayoutInflater.from(context).inflate(R.layout.payment_input_layout, null)

    override val type = SectionType.PAYMENT_INPUT

    private val tabLayout: TapSelectionTabLayout
    private val paymentInputContainer: LinearLayout
    private val clearText: ImageView
    private val scannerOptions: LinearLayout
    private val cardInputWidget = InlineCardInput(context)

    init {
        tabLayout = view.findViewById(R.id.sections_tablayout)
        paymentInputContainer = view.findViewById(R.id.payment_input_layout)
        scannerOptions = view.findViewById(R.id.scanner_options)
        clearText = view.findViewById(R.id.clear_text)
        bindViewComponents()
    }

    override fun bindViewComponents() {
        initTabLayout()
        initCardInput()
        initClearText()
    }

    private fun initTabLayout() {
        tabLayout.addSection(getCardList())
        tabLayout.addSection(getMobileList())
    }

    private fun initClearText() {
        clearText.setOnClickListener {
            cardInputWidget.clear()
        }
    }

    private fun initCardInput() {
        cardInputWidget.holderNameEnabled = false
        paymentInputContainer.addView(cardInputWidget)
        cardInputWidget.setCardNumberTextWatcher(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (s.isEmpty())
                        scannerOptions.visibility = View.VISIBLE
                    else
                        scannerOptions.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun getCardList(): ArrayList<SectionTabItem>{
        val items = ArrayList<SectionTabItem>()
        items.add(
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
        )
        return items
    }

    private fun getMobileList(): ArrayList<SectionTabItem>{
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
}