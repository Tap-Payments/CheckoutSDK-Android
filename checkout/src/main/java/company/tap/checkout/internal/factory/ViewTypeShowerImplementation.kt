package company.tap.checkout.internal.factory

import android.content.Context
import android.view.View
import android.view.ViewGroup
import company.tap.checkout.internal.api.models.PaymentOption
import company.tap.checkout.internal.api.models.SavedCard
import company.tap.checkout.internal.enums.ViewsType
import company.tap.checkout.internal.enums.ViewsType.*
import company.tap.checkout.internal.interfaces.OnCardSelectedActionListener
import company.tap.checkout.internal.viewholders.*
import company.tap.checkout.internal.viewmodels.CheckoutViewModel

class ViewTypeShowerImplementation(context: Context) : ViewTypeShowerFactory,
    OnCardSelectedActionListener {
    val viewModel: CheckoutViewModel = CheckoutViewModel()

    var businessViewHolderViewModel: BusinessViewHolder = BusinessViewHolder(context)
    var amountViewHolder = AmountViewHolder(context)
    var cardViewHolder = CardViewHolder(context, this)
    var saveCardSwitchHolder = SwitchViewHolder(context)
    var loyaltyViewHolder = LoyaltyViewHolder(context)
    var paymentInlineViewHolder = PaymentInlineViewHolder(context)
    var goPayViewsHolder = GoPayViewsHolder(context)
    var otpViewHolder = OTPViewHolder(context)
    var goPaySavedCardHolder = GoPaySavedCardHolder(context, this)
    var itemsViewHolder = ItemsViewHolder(context)


    override fun createViewsFromType(viewType: ViewsType) {
        when (viewType) {
            LOYALITY -> {
                addViews(
                    businessViewHolderViewModel,
                    amountViewHolder,
                    cardViewHolder,
                    paymentInlineViewHolder,
                    loyaltyViewHolder,
                    saveCardSwitchHolder as TapBaseViewHolder
                )
            }
            TOKENIZE_CARD -> {
                addViews(
                    businessViewHolderViewModel,
                    paymentInlineViewHolder,
                    saveCardSwitchHolder as TapBaseViewHolder
                )
            }
            SAVED_CARD -> {
                addViews(
                    businessViewHolderViewModel,
                    paymentInlineViewHolder,
                    saveCardSwitchHolder as TapBaseViewHolder
                )
            }
            WEB_PAYMENT_DATA -> {
                addViews(
                    businessViewHolderViewModel,
                    amountViewHolder,
                    cardViewHolder,
                    saveCardSwitchHolder as TapBaseViewHolder
                )
                cardViewHolder.cardInfoHeaderText.visibility = View.GONE
            }
            CARD_PAYMENT_DATA -> {
                addViews(
                    businessViewHolderViewModel,
                    amountViewHolder,
                    cardViewHolder,
                    paymentInlineViewHolder, saveCardSwitchHolder as TapBaseViewHolder
                )
                cardViewHolder.cardInfoHeaderText.visibility = View.VISIBLE
            }
            DisplayGoPayLogin -> {
                removeViews(
                    businessViewHolderViewModel, amountViewHolder,
                    cardViewHolder, paymentInlineViewHolder,
                    saveCardSwitchHolder, otpViewHolder,
                    goPayViewsHolder
                )

                addViews(
                    businessViewHolderViewModel,
                    amountViewHolder,
                    goPayViewsHolder
                )
            }
            DisplayGoPay -> {

                removeViews(
                    businessViewHolderViewModel,
                    amountViewHolder,
                    goPayViewsHolder,
                    cardViewHolder,
                    paymentInlineViewHolder,
                    saveCardSwitchHolder,
                    otpViewHolder
                )

                addViews(
                    businessViewHolderViewModel,
                    amountViewHolder,
                    goPaySavedCardHolder,
                    cardViewHolder,
                    paymentInlineViewHolder,
                    saveCardSwitchHolder
                )
            }
            CurrenciesWithItems -> {

            }
            SetActionGoPayOpenedItemsDisplayed -> {
                removeViews(
                    cardViewHolder,
                    paymentInlineViewHolder,
                    goPayViewsHolder,
                    otpViewHolder,
                    itemsViewHolder
                )
//                if (::webViewHolder.isInitialized) {
//                    removeViews(webViewHolder)
//                }
                addViews(
                    cardViewHolder,
                    paymentInlineViewHolder,
                    saveCardSwitchHolder as TapBaseViewHolder
                )

            }
            DisplayOtpGoPay -> {
                removeViews(
                    cardViewHolder,
                    paymentInlineViewHolder, saveCardSwitchHolder as TapBaseViewHolder, amountViewHolder
                )
                addViews(amountViewHolder, otpViewHolder)
            }
            DisplayOtpCharge -> {
                removeViews(cardViewHolder)
                addViews(otpViewHolder)
            }
            DisplayOtpTelecoms -> {
                removeViews(
                    businessViewHolderViewModel,
                    amountViewHolder,
                    paymentInlineViewHolder,
                    cardViewHolder,
                    saveCardSwitchHolder as TapBaseViewHolder,
                    otpViewHolder
                )
                addViews(
                    businessViewHolderViewModel,
                    amountViewHolder,
                    cardViewHolder,
                    paymentInlineViewHolder,
                    saveCardSwitchHolder as TapBaseViewHolder,
                    otpViewHolder
                )
            }
        }
    }

    private fun removeViews(vararg viewHolders: TapBaseViewHolder, onRemoveEnd: () -> Unit = {}) {
        viewModel.removeViewHolders.value = viewHolders.toMutableList()
    }


    fun addViews(
        vararg viewHolders: TapBaseViewHolder,
        afterAddingViews: () -> Unit = {}
    ) {
        viewModel.addViewHolders.value = viewHolders.toMutableList()
    }

    override fun onCardSelectedAction(
        isSelected: Boolean,
        savedCardsModel: Any?,
        isChangeBrandIcon: Boolean?
    ) {
    }

    override fun onDisabledChipSelected(
        paymentOption: PaymentOption,
        itemView: Int?,
        isDisabledClicked: Boolean?
    ) {
    }

    override fun onDeselectionOfItem(clearInput: Boolean) {
    }

    override fun onDeleteIconClicked(
        stopAnimation: Boolean,
        itemId: Int,
        cardId: String,
        maskedCardNumber: String,
        arrayListSavedCardSize: ArrayList<SavedCard>,
        selectedViewToBeDeleted: ViewGroup,
        viewToBeBlurr: View,
        position: Int
    ) {
    }

    override fun onGoPayLogoutClicked(isClicked: Boolean) {
    }

    override fun removePaymentInlineShrinkageAndDimmed() {
    }
}

