package company.tap.checkout.internal.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.AmountInterface
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.checkout.internal.utils.CurrencyFormatter
import company.tap.taplocalizationkit.LocalizationManager
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
    private var readyToScanText: TapTextView = view.findViewById(R.id.cardscan_ready)
    var scannerClicked: Boolean? = false

    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {
        view.amount_section.setAmountViewDataSource(getAmountDataSourceFromAPIs())

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
        } else
            return AmountViewDataSource(
                selectedCurr = originalAmount,
                selectedCurrText = transactionCurrency,
                itemCount = itemCountt + "  " + LocalizationManager.getValue("items", "Common")
            )
    }

    private fun changeDataSource(amountViewDataSource: AmountViewDataSource) {
        view.amount_section.setAmountViewDataSource(amountViewDataSource)
    }

    override fun changeGroupAction(isOpen: Boolean) {
        isOpenedList = isOpen
        if (isOpen) {
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

        } else {
//            if (::originalAmount?.isInitialized)
            changeDataSource(
                AmountViewDataSource(
                    selectedCurr = originalAmount,
                    selectedCurrText = transactionCurrency,
                    itemCount = LocalizationManager.getValue("close", "Common")
                )
            )
            view.amount_section.itemCountButton.text =
                LocalizationManager.getValue("close", "Common")

        }
    }


    fun setOnItemsClickListener(onItemsClickListener: () -> Unit) {
        view.amount_section.itemAmountText.setOnClickListener {
            onItemsClickListener()
            baseLayoutManager?.controlCurrency(isOpenedList)
        }
    }

    fun updateSelectedCurrency(
        isOpen: Boolean,
        selectedAmount: String,
        selectedCurrency: String,
        currentAmount: String,
        currentCurrency: String,
        selectedCurrencySymbol: String? = null
    ) {
        isOpenedList = isOpen
        if (selectedAmount == currentAmount && selectedCurrency == currentCurrency) {
            view.amount_section.mainKDAmountValue.visibility = View.GONE
        } else {
            view.amount_section.mainKDAmountValue.visibility = View.VISIBLE
        }
        if (isOpen) {
            if (selectedCurrencySymbol?.isNotBlank() == true || selectedCurrencySymbol?.isNotEmpty() == true) {
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
                        itemCount = LocalizationManager.getValue("close", "Common")
                    )
                )
            } else
                changeDataSource(
                    AmountViewDataSource(
                        selectedCurr = selectedAmount,
                        selectedCurrText = selectedCurrency,
                        currentCurr = currentAmount,
                        currentCurrText = currentCurrency,
                        itemCount = LocalizationManager.getValue("close", "Common")
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
        println("transactionCurrencyApi" + transactionCurrencyApi)
        itemCountt = itemCountApi
        originalAmount = CurrencyFormatter.currencyFormat(originalAmountApi)
        transactionCurrency = transactionCurrencyApi
        bindViewComponents()
    }
}