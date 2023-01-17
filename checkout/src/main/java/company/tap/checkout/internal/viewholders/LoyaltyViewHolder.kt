package company.tap.checkout.internal.viewholders

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import company.tap.checkout.R
import company.tap.checkout.internal.api.models.LoyaltySupportedCurrency
import company.tap.checkout.internal.api.models.TapLoyaltyModel
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.PaymentCardComplete
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapSwitch
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.atoms.TextInputEditText
import company.tap.tapuilibrary.uikit.datasource.LoyaltyHeaderDataSource
import company.tap.tapuilibrary.uikit.ktx.makeLinks
import company.tap.tapuilibrary.uikit.organisms.TapLoyaltyView
import java.math.BigDecimal

class LoyaltyViewHolder(private val context: Context, checkoutViewModel: CheckoutViewModel,
                        private val onPaymentCardComplete: PaymentCardComplete,) : TapBaseViewHolder  {

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
    lateinit var textViewTouchPoints :TapTextView
    lateinit var constraintt :LinearLayout

    private var bankName: String? = null
    private var bankLogo: String? = null
    private var tapLoyaltyModel: TapLoyaltyModel?=null
    private var arrayListLoyal:List<LoyaltySupportedCurrency> = java.util.ArrayList()
    private var  newBalance:BigDecimal?= BigDecimal.ZERO
    private var  newTouchPoints:Int?=0
    private var  initialBalance:Int?=0
    private var  initialTouchPoints:Int?=0
    private var remainingAmountText:String ?=null
    private var remainingPointsText:String ?=null
    private var alertMessage:String ?=null
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
        textViewTouchPoints= view.findViewById(R.id.textViewTouchPoints)
        constraintt= view.findViewById(R.id.constraintt)
        loyaltyView.setLoyaltyHeaderDataSource(LoyaltyHeaderDataSource(bankName,bankLogo))
        constraintt.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))

        configureSwitch()
        amountEditTextWatcher()

    }

    private fun setData() {
        val redeemText :String =LocalizationManager.getValue("headerView","TapLoyaltySection","redeem")
        val balanceText :String =LocalizationManager.getValue("headerView","TapLoyaltySection","balance")
        val termsConditionsText :String =LocalizationManager.getValue("headerView","TapLoyaltySection","tc")
        textViewTitle.setText(redeemText+" "+tapLoyaltyModel?.loyaltyProgramName)
        initialBalance = arrayListLoyal[1].balance
        initialTouchPoints = "6400".toInt()
        textViewClickable.text = balanceText+" "+arrayListLoyal?.get(1).currency+ " " +initialBalance+ "  "+ "("+tapLoyaltyModel?.transactionsCount+")"+ " "+
                tapLoyaltyModel?.loyaltyPointsName+" "+termsConditionsText
        textViewTouchPoints.text = "="+initialTouchPoints +tapLoyaltyModel?.loyaltyPointsName
        textViewSubTitle.text = LocalizationManager.getValue("amountView","TapLoyaltySection","title")
        loyaltyView.textViewClickable?.makeLinks(
            Pair(termsConditionsText, View.OnClickListener {
                val url = "https://www.adcb.com/en/tools-resources/adcb-privacy-policy/"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context.startActivity(intent)
            }))
        editTextAmount.setText("320")
       alertMessage =LocalizationManager.getValue("hintView","TapLoyaltySection","minimumWarning")
        loyaltyView.loyaltyAlertView?.alertMessage?.text = alertMessage+" "+arrayListLoyal?.get(1).currency+"20.00"
        loyaltyView.loyaltyAlertView?.visibility =View.GONE

         remainingPointsText  =LocalizationManager.getValue("footerView","TapLoyaltySection","points")
        textViewRemainPoints.text = remainingPointsText+" "+tapLoyaltyModel?.loyaltyPointsName+" : "+arrayListLoyal?.get(1).balance
        remainingAmountText  =LocalizationManager.getValue("footerView","TapLoyaltySection","amount")

        reCalculateAmount(initialBalance?.toBigDecimal(),editTextAmount.text.toString().toBigDecimal())


    }

    private fun configureSwitch() {
        /**
         * Logic for switchLoyalty switch
         * **/
        loyaltyView.switchLoyalty?.setOnCheckedChangeListener { buttonView, isChecked ->
            loyaltyView.switchTheme()

            if(isChecked){
                loyaltyView.linearLayout2?.visibility = View.VISIBLE
                loyaltyView.linearLayout3?.visibility = View.VISIBLE

            }else {

                loyaltyView.linearLayout2?.visibility = View.GONE
                loyaltyView.linearLayout3?.visibility = View.GONE

            }
        }
    }


    /**
     * Sets data from API through LayoutManager
     * @param bankLogo represents the images of payment methods.
     * @param bankName represents the Name of payment methods.
     * */
    fun setDataFromAPI(
        bankLogo: String,
        bankName: String,
        tapLoyaltyModel: TapLoyaltyModel,
        supportedLoyalCards: List<LoyaltySupportedCurrency>
    ){
        this.bankLogo = bankLogo
        this.bankName = bankName
        this.tapLoyaltyModel = tapLoyaltyModel
        this.arrayListLoyal = supportedLoyalCards
        bindViewComponents()
        setData()
    }

    private fun amountEditTextWatcher() {
        editTextAmount.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                if (s != "") {
                    //do your work here
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {

            }

            override fun afterTextChanged(s: Editable) {
                if(s.toString().isNullOrEmpty()){
                    loyaltyView.loyaltyAlertView?.visibility =View.VISIBLE
                    loyaltyView.loyaltyAlertView?.alertMessage?.text = alertMessage+" "+arrayListLoyal?.get(1).currency+"20.00"

                    textViewTouchPoints.text = "= "+"0 "+tapLoyaltyModel?.loyaltyPointsName
                }else{
                    val touchPoints :BigDecimal= s.toString().toBigDecimal().times(BigDecimal.valueOf(20))
                    textViewTouchPoints.text = "= "+touchPoints+" "+tapLoyaltyModel?.loyaltyPointsName
                    loyaltyView.loyaltyAlertView?.visibility =View.GONE
                    reCalculateTouchPoints(initialTouchPoints,touchPoints.toInt())
                }
                if(!s.isNullOrEmpty()){
                    if(s.contains(".")){
                        reCalculateAmount(initialBalance?.toBigDecimal(),s.toString().replace(".","").toBigDecimal())
                    }

                    onPaymentCardComplete.onPaymentCardIsLoyaltyCard(true)
                }
                }
        })
    }

    private fun reCalculateAmount(previousBalance:BigDecimal?,amountTypedInText:BigDecimal){
        println("previousBalance>>"+previousBalance)
        println("newBalance>>"+newBalance)
        if (previousBalance != null) {
            newBalance = previousBalance.minus(amountTypedInText)


                if (newBalance!!.toInt() >= 0 && newBalance!!.toInt() <= previousBalance.toInt()) {
                    textViewRemainAmount.text =
                        remainingAmountText + " : " + arrayListLoyal?.get(1).currency + " " + newBalance
                } else {
                    loyaltyView.loyaltyAlertView?.visibility = View.VISIBLE
                    loyaltyView.loyaltyAlertView?.alertMessage?.text = alertMessage+" "+arrayListLoyal?.get(1).currency+"20.00"

                    textViewRemainAmount.text =
                        remainingAmountText + " : " + arrayListLoyal?.get(1).currency + "  " + initialBalance
                }


        }

    }

    private fun reCalculateTouchPoints(previousTouchPoints:Int?,reflectedTouchPoints:Int){
      //  println("previousTouchPoints>>"+previousTouchPoints)
     //   println("newTouchPoints>>"+newTouchPoints)
        if (previousTouchPoints != null) {
            newTouchPoints = previousTouchPoints-reflectedTouchPoints

            if(newTouchPoints!! < 0 && newTouchPoints!!  < tapLoyaltyModel?.transactionsCount?.replace(",","")?.toInt()!!){
                loyaltyView.loyaltyAlertView?.visibility = View.VISIBLE
                loyaltyView.loyaltyAlertView?.alertMessage?.text = "Insufficent Touchpoints"

            }

            if (newBalance!!.toInt() >= 0 && newBalance!!.toInt() <= newTouchPoints!!) {
                textViewRemainPoints.text =
                    remainingPointsText + " " + tapLoyaltyModel?.loyaltyPointsName+" : " +  newTouchPoints
            } else {
              //  loyaltyView.loyaltyAlertView?.visibility = View.VISIBLE
                  textViewRemainAmount.text =
                    remainingPointsText + " " + tapLoyaltyModel?.loyaltyPointsName +" : " + initialTouchPoints
            }


        }

    }
}

