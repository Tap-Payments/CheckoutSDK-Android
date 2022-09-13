package company.tap.checkout.internal.viewholders

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.enums.PaymentTypeEnum
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.nfcreader.open.utils.TapNfcUtils
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapChipGroup
import company.tap.tapuilibrary.uikit.atoms.TapSwitch
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.atoms.TextInputEditText
import company.tap.tapuilibrary.uikit.datasource.LoyaltyHeaderDataSource
import company.tap.tapuilibrary.uikit.datasource.TapSwitchDataSource
import company.tap.tapuilibrary.uikit.ktx.setBorderedView
import company.tap.tapuilibrary.uikit.ktx.setBottomBorders
import company.tap.tapuilibrary.uikit.organisms.TapLoyaltyView
import company.tap.tapuilibrary.uikit.views.TapSelectionTabLayout
import kotlinx.android.synthetic.main.loyalty_view_layout.view.*
import kotlinx.android.synthetic.main.switch_layout.view.*

class LoyaltyViewHolder(private val context: Context, checkoutViewModel: CheckoutViewModel) : TapBaseViewHolder  {

    override val view: View = LayoutInflater.from(context).inflate(R.layout.loyalty_view_layout, null)

    override val type = SectionType.LOYALTY
     var loyaltyView: TapLoyaltyView= view.findViewById(R.id.loyaltyView)

    lateinit var textViewTitle :TapTextView
    lateinit var textViewClickable :TapTextView
    lateinit var switchLoyalty: TapSwitch
    lateinit var textViewSubTitle :TapTextView
    lateinit var editTextAmount :TextInputEditText
    lateinit var textViewRemainPoints:TapTextView
    lateinit var textViewRemainAmount :TapTextView

    private var bankName: String? = null
    private var bankLogo: String? = null

    init {
        bindViewComponents()

    }

    override fun bindViewComponents() {

        textViewTitle = view.findViewById(R.id.textViewTitle)
         textViewClickable= view.findViewById(R.id.textViewClickable)
         switchLoyalty= view.findViewById( R.id.switchLoyalty)
         textViewSubTitle = view.findViewById(R.id.textViewSubTitle)
         editTextAmount = view.findViewById(R.id.editTextAmount)
         textViewRemainPoints= view.findViewById(R.id.textViewRemainPoints)
         textViewRemainAmount= view.findViewById(R.id.textViewRemainAmount)
        loyaltyView.setLoyaltyHeaderDataSource(LoyaltyHeaderDataSource(bankName,bankLogo))

        configureSwitch()
    }

    private fun configureSwitch() {
        /**
         * Logic for switchLoyalty switch
         * **/
        view.loyaltyView.switchLoyalty?.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                textViewSubTitle.visibility =View.VISIBLE
                editTextAmount .visibility =View.VISIBLE
                textViewRemainPoints.visibility =View.VISIBLE
                textViewRemainAmount.visibility =View.VISIBLE
            }
            else{
                textViewSubTitle.visibility =View.GONE
                editTextAmount .visibility =View.GONE
                textViewRemainPoints.visibility =View.GONE
                textViewRemainAmount.visibility =View.GONE

            }            }
    }


    /**
     * Sets data from API through LayoutManager
     * @param bankLogo represents the images of payment methods.
     * @param bankName represents the Name of payment methods.
     * */
    fun setDataFromAPI(bankLogo : String , bankName:String){
        this.bankLogo = bankLogo
        this.bankName = bankName
        bindViewComponents()

    }
}

