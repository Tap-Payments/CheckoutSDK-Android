package company.tap.checkout.internal.viewholders

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import company.tap.checkout.R
import company.tap.checkout.internal.api.models.Charge
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.organisms.FawryPaymentView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by AhlaamK on 8/19/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
class AsynchronousPaymentViewHolder(private val context: Context,private  val viewModel: CheckoutViewModel) : TapBaseViewHolder {

    private lateinit var fawryView :FawryPaymentView
    private lateinit var linearAsynch :LinearLayout
    private lateinit var dateTimestr :String
    private  var smsEnabled :Boolean = false
    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.asynchronous_layout, null)

    override val type = SectionType.BUSINESS




    init {
        bindViewComponents()
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewComponents() {
       fawryView = view.findViewById(R.id.fawry_payment_view)

        val firstString:String = LocalizationManager.getValue("paymentProgressLabel","TapAsyncSection")
        val secondString:String = LocalizationManager.getValue("paymentRecieptLabel","TapAsyncSection")
        val emailString:String = LocalizationManager.getValue("paymentRecieptEmail","TapAsyncSection")
        val smsString:String = LocalizationManager.getValue("paymentRecieptSms","TapAsyncSection")
        fawryView.titleText?.text = firstString+"\n"+secondString.replace("%@",smsString)
        fawryView.descText?.text = LocalizationManager.getValue("paymentReferenceTitleLabel","TapAsyncSection")
        fawryView.orderCodeText?.text = LocalizationManager.getValue("paymentCodeTitleLabel","TapAsyncSection")
        fawryView.codeExpireText?.text = LocalizationManager.getValue("paymentExpiryTitleLabel","TapAsyncSection")
        fawryView.linkDescText?.text = LocalizationManager.getValue<String?>("paymentVisitBranchesLabel","TapAsyncSection")?.replace("%@","Fawry")
        fawryView.payButton?.setButtonDataSource(
            true, context?.let { LocalizationManager.getLocale(it).language },
            LocalizationManager.getValue(
                "close",
                "Common"
            ),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.goLoginBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
        )

        fawryView?.payButton?.setOnClickListener {
           viewModel.closeAsynchView()
        }




    }

    fun setDataFromAPI(chargeResponse: Charge){
        //    phonNumber = chargeResponse?.customer?.getPhone()?.number
        //    emailId = chargeResponse.customer.email

        val created: Long =
            chargeResponse.transaction.created
        val period: Double? =
            chargeResponse.transaction.expiry?.period

        val experiod = period?.toLong()
        val totalTime = experiod?.plus(created)
        if (totalTime != null) {
            getDateTimeStamp(totalTime)
        }

        fawryView.initViews(chargeResponse.transaction.order?.reference.toString(),
            dateTimestr,
            chargeResponse.transaction.order?.store_url.toString()
        )
        Linkify.addLinks(fawryView.linkValue, Linkify.ALL)

    }

    /**
     * Converting the epoch time period and created time to datetime string.
     *
     */
    private fun getDateTimeStamp(mdateTime: Long): String? {
        val format = SimpleDateFormat("MM/dd/yyyy' 'HH:mm a ")
        val dateParser = SimpleDateFormat("MM/dd/yyyy' 'HH:mm a ")
        var dateTime: Date? = null
        return try {
            dateTime = dateParser.parse(format.format(mdateTime))
            Log.e("dateTime", "dateTime" + format.format(dateTime))
            dateTimestr = format.format(dateTime)
            dateTimestr
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }


}
