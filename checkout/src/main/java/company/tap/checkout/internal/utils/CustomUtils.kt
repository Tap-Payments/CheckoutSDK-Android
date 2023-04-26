package company.tap.checkout.internal.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager


/**
 * Created by AhlaamK on 12/8/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
object CustomUtils {

    @JvmField
    val tagEvent: String = "EVENTS"

    @JvmField
    val tagAPI: String = "API"
    fun showDialog(
        title: String,
        messageString: String,
        context: Context,
        btnType: Int? = null,
        baseLayoutManager: BaseLayoutManager?,
        paymentType: PaymentType? = null,
        savedCardsModel: Any? = null,
        cardTypeDialog: Boolean
    ) {
        val builder = iOSDialogBuilder(context)

        builder
            .setTitle(title)
            .setSubtitle(messageString)
            .setBoldPositiveLabel(false)
            .setCancelable(false)


        if (btnType == 2) {
            builder.setPositiveListener(LocalizationManager.getValue("yes", "Common")) { dialog ->
                dialog.dismiss()
                baseLayoutManager?.didDialogueExecute("YES", cardTypeDialog)

            }
            builder.setNegativeListener(LocalizationManager.getValue("no", "Common")) { dialog ->
                dialog.dismiss()
                baseLayoutManager?.didDialogueExecute("NO", cardTypeDialog)
            }
                .build().show()
        } else if (btnType == 3) {
            builder.setPositiveListener(LocalizationManager.getValue("yes", "Common")) { dialog ->
                dialog.dismiss()
                if (paymentType != null) {
                    baseLayoutManager?.dialogueExecuteExtraFees("YES", paymentType, savedCardsModel)
                }

            }
            builder.setNegativeListener(LocalizationManager.getValue("no", "Common")) { dialog ->
                dialog.dismiss()
                if (paymentType != null) {
                    baseLayoutManager?.dialogueExecuteExtraFees("NO", paymentType, savedCardsModel)
                }
            }
        } else if (btnType == 4) {
            builder.setPositiveListener(
                LocalizationManager.getValue(
                    "confirm",
                    "DeleteCard"
                )
            ) { dialog ->
                dialog.dismiss()
                baseLayoutManager?.didDialogueExecute("YES", cardTypeDialog)

            }
            builder.setNegativeListener(
                LocalizationManager.getValue(
                    "cancel",
                    "DeleteCard"
                )
            ) { dialog ->
                dialog.dismiss()
                baseLayoutManager?.didDialogueExecute("NO", cardTypeDialog)
            }
                .build().show()
        } else {
            builder.setPositiveListener(LocalizationManager.getValue("ok", "Common")) { dialog ->
                dialog.dismiss()
                baseLayoutManager?.didDialogueExecute("OK", cardTypeDialog)
            }
                .build().show()
        }


    }

    /**
     * Hide keyboard from.
     *
     * @param context the context
     * @param view    the view
     */
    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()

    }

    /**
     * Show keyboard from.
     *
     * @param context the context
     *
     */
    fun showKeyboard(context: Context) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun getDeviceDisplayMetrics(activity: Activity): Int {
        // Determine density
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val density = metrics.densityDpi
        return density
    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus?.windowToken,
                0
            )
        }
    }

    fun getCurrentTheme(): String {

        return ThemeManager.currentTheme
    }


}