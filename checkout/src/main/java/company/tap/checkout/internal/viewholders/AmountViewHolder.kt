package company.tap.checkout.internal.viewholders

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import company.tap.cardinputwidget.CardInputUIStatus
import company.tap.checkout.R
import company.tap.checkout.internal.cache.SharedPrefManager
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.AmountInterface
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.checkout.internal.utils.*
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.TextViewTheme
import company.tap.tapuilibrary.uikit.adapters.context
import company.tap.tapuilibrary.uikit.atoms.TapChip
import company.tap.tapuilibrary.uikit.atoms.TapSeparatorView
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.datasource.AmountViewDataSource
import kotlinx.android.synthetic.main.amountview_layout.view.*


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class AmountViewHolder(context: Context, private val baseLayoutManager: BaseLayoutManager? = null) :
    TapBaseViewHolder,
    AmountInterface {


    override val view: View = LayoutInflater.from(context).inflate(R.layout.amountview_layout, null)

    override val type = SectionType.AMOUNT_ITEMS

    private var itemCountt: String? = null
    var originalAmount: String? = null
    private var isOpenedList: Boolean = true
    private var transactionCurrency: String? = null
    private var scannerLinearView: LinearLayout = view.findViewById(R.id.scannerLinearView)
    private var CustomCardViewStyle: TapChip = view.findViewById(R.id.CustomCardViewStyle)
    private var readyToScanText: TapTextView = view.findViewById(R.id.cardscan_ready)
    var scannerClicked: Boolean? = false

    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {
        view.amount_section.setAmountViewDataSource(getAmountDataSourceFromAPIs())
        scanTextTheme()
    }

    private fun scanTextTheme() {
        val readyToScanTextViewTheme = TextViewTheme()
        readyToScanTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("Hints.Default.textColor"))
        readyToScanTextViewTheme.textSize =
            ThemeManager.getFontSize("Hints.Default.textFont")
        readyToScanTextViewTheme.font =
            ThemeManager.getFontName("Hints.Default.textFont")
        readyToScanText.setTheme(readyToScanTextViewTheme)
        scannerLinearView.setBackgroundColor(Color.parseColor(ThemeManager.getValue("Hints.Default.backgroundColor")))
        CustomCardViewStyle.setBackgroundColor(Color.parseColor(ThemeManager.getValue("Hints.Default.backgroundColor")))
    }

    fun readyToScanVisibility(scannerClicked: Boolean) {
        if (scannerClicked) {
            scannerLinearView.visibility = View.VISIBLE
            readyToScanText.text = LocalizationManager.getValue("Default", "Hints", "scan")
        } else {
            scannerLinearView.visibility = View.GONE
        }
    }

    private fun getAmountDataSourceFromAPIs(): AmountViewDataSource {
        if (itemCountt?.equals("1") == true) {
            return AmountViewDataSource(
                selectedCurr = originalAmount,
                selectedCurrText = transactionCurrency,
                itemCount = itemCountt + "  " + LocalizationManager.getValue("item", "Common")
            )
        } else {
            var items: String = ""
            if (LocalizationManager.currentLocalized.length() != 0) {
                items = LocalizationManager.getValue("items", "Common")
            }
            return AmountViewDataSource(
                selectedCurr = originalAmount,
                selectedCurrText = transactionCurrency,
                itemCount = itemCountt + "  " + items
            )
        }
    }

    private fun changeDataSource(amountViewDataSource: AmountViewDataSource) {
        view.amount_section.setAmountViewDataSource(amountViewDataSource)
    }

    override fun changeGroupAction(isOpen: Boolean) {
        isOpenedList = isOpen
        if (isOpen) {
            /**
             * Second time opening The Items Currencies after choosing one Currency
             */
            if (itemCountt?.contentEquals("1") == true) {
                changeDataSource(
                    AmountViewDataSource(
                        selectedCurr = originalAmount,
                        selectedCurrText = transactionCurrency,
                        itemCount = itemCountt + "  " + LocalizationManager.getValue(
                            "item",
                            "Common"
                        )
                    )
                )
            } else changeDataSource(
                AmountViewDataSource(
                    selectedCurr = originalAmount,
                    selectedCurrText = transactionCurrency,
                    itemCount = itemCountt + "  " + LocalizationManager.getValue("items", "Common")
                )
            )
            view.amount_section.itemAmountText.visibility = View.VISIBLE
            view.amount_section.viewSeparator.visibility = View.VISIBLE
            view.amount_section.amountImageView.visibility = View.VISIBLE
            /**
             * if same currency selected as Currency of user , not show local prompt
             */
            if (CheckoutViewModel.currencySelectedForCheck != SharedPrefManager.getUserSupportedLocaleForTransactions(view.amount_section.context)?.currency
            )
                doAfterSpecificTime(500) {
                    view.amount_section?.tapChipPopup?.addFadeInAnimation()
                }
        } else {
            /**
             * opening The Items Currencies to choose from
             */

            changeDataSource(
                AmountViewDataSource(
                    selectedCurr = originalAmount,
                    selectedCurrText = transactionCurrency,
                    itemCount = LocalizationManager.getValue<String?>("close", "Common")
                )
            )
            view.amount_section.itemCountButton.text =
                LocalizationManager.getValue("close", "Common")

            view.amount_section.itemAmountText.visibility = View.GONE
            view.amount_section.viewSeparator.visibility = View.GONE
            view.amount_section.amountImageView.visibility = View.GONE
            view.amount_section?.tapChipPopup?.addFadeOutAnimation()


        }
    }


    @SuppressLint("ClickableViewAccessibility")
    fun setOnItemsClickListener() {
        /* view.amount_section.itemAmountLayout.setOnClickListener {
             //onItemsClickListener()
             baseLayoutManager?.controlCurrency(isOpenedList)
         }*/

        /*view.amount_section.itemAmountLayout.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                baseLayoutManager?.controlCurrency(isOpenedList)
                return false
            }
        })*/
        view.amount_section.tapChipAmount.setOnTouchListener { v, event ->
            baseLayoutManager?.controlCurrency(isOpenedList)


            false
        }

        /**Adding to get back to sdk when clicked closed ***/
//        view.amount_section.itemPopupLayout.setOnTouchListener(object : View.OnTouchListener {
//            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//                baseLayoutManager?.reOpenSDKState()
//                return false
//            }
//        })
    }

    fun updateSelectedCurrency(
        isOpen: Boolean,
        selectedAmount: String,
        selectedCurrency: String,
        currentAmount: String,
        currentCurrency: String,
        selectedCurrencySymbol: String? = null,
        isChangingCurrencyFromOutside: Boolean? = false
    ) {


        isOpenedList = isOpen
        //  if (selectedCurrency == get)
        if (selectedAmount == currentAmount && selectedCurrency == currentCurrency) {
            view.amount_section.mainKDAmountValue.visibility = View.GONE
        } else {
            view.amount_section.mainKDAmountValue.visibility = View.VISIBLE
        }
        if (isOpen) {
            if (selectedCurrencySymbol?.isNotBlank() == true || selectedCurrencySymbol?.isNotEmpty() == true) {
                /**
                 * here changed all symbols  to needed symbol currency
                 */
                if (itemCountt?.equals("1") == true) {
                    changeDataSource(
                        AmountViewDataSource(
                            selectedCurr = selectedAmount,
                            selectedCurrText = selectedCurrencySymbol,
                            currentCurr = currentAmount,
                            currentCurrText = currentCurrency,
                            itemCount = itemCountt + "  " + LocalizationManager.getValue(
                                "item",
                                "Common"
                            )
                        )
                    )
                } else {
                    changeDataSource(
                        AmountViewDataSource(
                            selectedCurr = selectedAmount,
                            selectedCurrText = selectedCurrencySymbol,
                            currentCurr = currentAmount,
                            currentCurrText = currentCurrency,
                            itemCount = itemCountt + "  " + LocalizationManager.getValue(
                                "items",
                                "Common"
                            )
                        )
                    )
                }

            } else {
                if (itemCountt?.equals("1") == true) {
                    changeDataSource(
                        AmountViewDataSource(
                            selectedCurr = selectedAmount,
                            selectedCurrText = selectedCurrency,
                            currentCurr = currentAmount,
                            currentCurrText = currentCurrency,
                            itemCount = itemCountt + "  " + LocalizationManager.getValue(
                                "item",
                                "Common"
                            )
                        )
                    )
                } else {
                    changeDataSource(
                        AmountViewDataSource(
                            selectedCurr = selectedAmount,
                            selectedCurrText = selectedCurrency,
                            currentCurr = currentAmount,
                            currentCurrText = currentCurrency,
                            itemCount = itemCountt + "  " + LocalizationManager.getValue(
                                "items",
                                "Common"
                            )
                        )
                    )
                }
            }


        } else {
            if (selectedCurrencySymbol?.isNotBlank() == true || selectedCurrencySymbol?.isNotEmpty() == true) {
                changeDataSource(
                    AmountViewDataSource(
                        selectedCurr = selectedAmount,
                        selectedCurrText = selectedCurrencySymbol,
                        currentCurr = currentAmount,
                        currentCurrText = currentCurrency,
                        itemCount = if (isChangingCurrencyFromOutside == true) itemCountt + "  " + LocalizationManager.getValue(
                            "items",
                            "Common"
                        ) else LocalizationManager.getValue("confirm", "Common")
                    )
                )
            } else
                changeDataSource(
                    AmountViewDataSource(
                        selectedCurr = selectedAmount,
                        selectedCurrText = selectedCurrency,
                        currentCurr = currentAmount,
                        currentCurrText = currentCurrency,
                        itemCount = LocalizationManager.getValue("confirm", "Common")
                    )
                )
        }

    }


    /**
     * Sets data from API through LayoutManager
     * @param transactionCurrencyApi represents the currency which by default.
     * @param itemCountApi represents the itemsCount for the Merchant.
     * @param originalAmountApi represents the default amount from API.
     * */
    fun setDataFromAPI(
        originalAmountApi: String,
        transactionCurrencyApi: String,
        itemCountApi: String
    ) {
        // println("transactionCurrencyApi" + transactionCurrencyApi)
        itemCountt = itemCountApi
        originalAmount = CurrencyFormatter.currencyFormat(originalAmountApi)
        transactionCurrency = transactionCurrencyApi
        bindViewComponents()
    }
}