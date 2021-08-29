package company.tap.checkout.internal.utils

import android.content.Context
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.taplocalizationkit.LocalizationManager


/**
 * Created by AhlaamK on 12/8/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
object CustomUtils {

    fun showDialog(title: String, messageString: String, context: Context, btnType: Int? = null, baseLayoutManager: BaseLayoutManager?, paymentType: PaymentType?=null, savedCardsModel: Any?= null, cardTypeDialog:Boolean) {
        val builder = iOSDialogBuilder(context)
        builder
            .setTitle(title)
            .setSubtitle(messageString)
            .setBoldPositiveLabel(true)
            .setCancelable(false)
        if (btnType == 2) {
            builder.setPositiveListener(LocalizationManager.getValue("yes","Common")) { dialog ->
                dialog.dismiss()
                baseLayoutManager?.didDialogueExecute("YES", cardTypeDialog)

            }
            builder.setNegativeListener(LocalizationManager.getValue("no","Common")) { dialog ->
                dialog.dismiss()
                baseLayoutManager?.didDialogueExecute("NO", cardTypeDialog)
            }
                    .build().show()
        } else if (btnType == 3) {
            builder.setPositiveListener(LocalizationManager.getValue("yes","Common")) { dialog ->
                dialog.dismiss()
                if (paymentType != null) {
                    baseLayoutManager?.dialogueExecuteExtraFees("YES",paymentType,savedCardsModel)
                }

            }
            builder.setNegativeListener(LocalizationManager.getValue("no","Common")) { dialog ->
                dialog.dismiss()
                if (paymentType != null) {
                    baseLayoutManager?.dialogueExecuteExtraFees("NO",paymentType,savedCardsModel)
                }
            }
        }
        else {
            builder.setPositiveListener(LocalizationManager.getValue("ok","Common")) { dialog ->
                dialog.dismiss()
                baseLayoutManager?.didDialogueExecute("OK",cardTypeDialog)
            }
        }
       .build().show()

    }
}