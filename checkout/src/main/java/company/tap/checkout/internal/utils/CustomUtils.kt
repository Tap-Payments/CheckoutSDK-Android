package company.tap.checkout.internal.utils

import android.content.Context
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.interfaces.BaseLayouttManager
import company.tap.taplocalizationkit.LocalizationManager


/**
 * Created by AhlaamK on 12/8/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
object CustomUtils {

    fun showDialog(title: String, messageString: String, context: Context, btnType: Int? = null, baseLayouttManager: BaseLayouttManager?,paymentType: PaymentType?=null) {
        val builder = iOSDialogBuilder(context)
        builder
            .setTitle(title)
            .setSubtitle(messageString)
            .setBoldPositiveLabel(true)
            .setCancelable(false)
        if (btnType == 2) {
            builder.setPositiveListener(LocalizationManager.getValue("yes","Common")) { dialog ->
                dialog.dismiss()
                baseLayouttManager?.didDialogueExecute("YES")

            }
            builder.setNegativeListener(LocalizationManager.getValue("no","Common")) { dialog ->
                dialog.dismiss()
                baseLayouttManager?.didDialogueExecute("NO")
            }
        } else if (btnType == 3) {
            builder.setPositiveListener(LocalizationManager.getValue("yes","Common")) { dialog ->
                dialog.dismiss()
                if (paymentType != null) {
                    baseLayouttManager?.dialogueExecuteExtraFees("YES",paymentType)
                }

            }
            builder.setNegativeListener(LocalizationManager.getValue("no","Common")) { dialog ->
                dialog.dismiss()
                if (paymentType != null) {
                    baseLayouttManager?.dialogueExecuteExtraFees("NO",paymentType)
                }
            }
        }
        else {
            builder.setPositiveListener(LocalizationManager.getValue("ok","Common")) { dialog ->
                dialog.dismiss()
                baseLayouttManager?.didDialogueExecute("OK")
            }
        }
            .build().show()

    }
}