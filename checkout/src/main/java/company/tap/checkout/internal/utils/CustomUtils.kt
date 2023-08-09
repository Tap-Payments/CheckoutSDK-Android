package company.tap.checkout.internal.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.AppColorTheme
import company.tap.tapuilibrary.uikit.ktx.loadAppThemManagerFromPath


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
        /**
         * passed if used library modification of iosDialog
         */
//            .setBackgroundColor(loadAppThemManagerFromPath(AppColorTheme.GlobalValuesColor))
//            .setTextColor(getColor())



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
            }.build().show()

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
            builder.setPositiveListener(LocalizationManager.getValue("done", "Common")) { dialog ->
                dialog.dismiss()
                baseLayoutManager?.didDialogueExecute("DONE", cardTypeDialog)
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


    fun getColor(): Int {
        if (getCurrentTheme().contains("dark")) {
            return Color.parseColor("#FFFFFF")
        } else return Color.parseColor("#000000")
    }

    fun getDeviceDisplayMetrics(activity: Activity): Int {
        // Determine density
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val density = metrics.densityDpi
        return density
    }

    fun hideSoftKeyboard(context: Context) {
        val inputMethodManager = context.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(
                (context as Activity).currentFocus?.windowToken,
                0
            )
        }
    }

    fun getCurrentTheme(): String {

        return ThemeManager.currentThemeName
    }

    fun getCurrentLocale(context: Context): String {

        return LocalizationManager.getLocale(context).language
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun showAlertDialog(title: String, messageString: String, context: Context) {
        MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_rounded)
            .setTitle(title)
            .setMessage(messageString)
            .setPositiveButtonIcon(context.resources.getDrawable(R.drawable.baseline_delete_24))
            .setPositiveButton("Delete", null)
            .setNegativeButton("Cancel", null)

            .create().show()
    }
}