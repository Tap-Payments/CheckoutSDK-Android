package company.tap.checkout.internal.factory

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import company.tap.checkout.internal.api.models.PaymentOption
import company.tap.checkout.internal.api.models.SavedCard
import company.tap.checkout.internal.enums.ViewsType
import company.tap.checkout.internal.enums.ViewsType.*
import company.tap.checkout.internal.interfaces.OnCardSelectedActionListener
import company.tap.checkout.internal.viewholders.*
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.CheckoutFragment

class ViewTypeShowerImplementation(context: Context) : ViewTypeShowerFactory,
    OnCardSelectedActionListener {
    val viewModel: CheckoutViewModel = CheckoutViewModel()

    var businessViewHolderViewModel: BusinessViewHolder = BusinessViewHolder(context)
    var amountViewHolder = AmountViewHolder(context)
    var cardViewHolder = CardViewHolder(context,this )
    var saveCardSwitchHolder = SwitchViewHolder(context)
    var loyaltyViewHolder = LoyaltyViewHolder(context)
    var paymentInlineViewHolder = PaymentInlineViewHolder(context)

    override fun createViewsFromType(viewType: ViewsType) {
        when (viewType) {
            LOYALITY -> {
                addViews(
                    businessViewHolderViewModel,
                    amountViewHolder,
                    cardViewHolder,
                    paymentInlineViewHolder,
                    loyaltyViewHolder,
                    saveCardSwitchHolder  as TapBaseViewHolder
                )
            }
            TOKENIZE_CARD -> {
                addViews(
                    businessViewHolderViewModel,
                    paymentInlineViewHolder,
                    saveCardSwitchHolder  as TapBaseViewHolder
                )
            }
            SAVED_CARD -> {
                addViews(
                    businessViewHolderViewModel,
                    paymentInlineViewHolder,
                    saveCardSwitchHolder  as TapBaseViewHolder
                )
            }
            WEB_PAYMENT_DATA -> {
                addViews(
                    businessViewHolderViewModel,
                    amountViewHolder,
                    cardViewHolder,
                    saveCardSwitchHolder  as TapBaseViewHolder
                )
                cardViewHolder.cardInfoHeaderText.visibility = View.GONE
            }
            CARD_PAYMENT_DATA -> {
                addViews(
                    businessViewHolderViewModel,
                    amountViewHolder,
                    cardViewHolder,
                    paymentInlineViewHolder, saveCardSwitchHolder  as TapBaseViewHolder
                )
                cardViewHolder.cardInfoHeaderText.visibility = View.VISIBLE
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
        TODO("Not yet implemented")
    }

    override fun onDisabledChipSelected(
        paymentOption: PaymentOption,
        itemView: Int?,
        isDisabledClicked: Boolean?
    ) {
        TODO("Not yet implemented")
    }

    override fun onDeselectionOfItem(clearInput: Boolean) {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun onGoPayLogoutClicked(isClicked: Boolean) {
        TODO("Not yet implemented")
    }

    override fun removePaymentInlineShrinkageAndDimmed() {
        TODO("Not yet implemented")
    }
}

