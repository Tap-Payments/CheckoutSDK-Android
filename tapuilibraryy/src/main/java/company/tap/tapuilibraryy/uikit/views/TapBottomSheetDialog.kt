package company.tap.tapuilibraryy.uikit.views

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import company.tap.tapuilibraryy.R
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.themekit.theme.SeparatorViewTheme
import company.tap.tapuilibraryy.uikit.interfaces.TapBottomDialogInterface
import kotlinx.android.synthetic.main.modal_bottom_sheet.*


/**
 *
 * Created on 6/3/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
open class TapBottomSheetDialog : BottomSheetDialogFragment() {

    private var topLeftCorner = 16f
    private var topRightCorner = 16f
    private var bottomRightCorner = 0f
    private var bottomLeftCorner = 0f
    var backgroundColor = Color.TRANSPARENT
    var windowRatio = 0.5f


    var bottomSheetLayout: FrameLayout? = null
    lateinit var bottomSheetDialog: BottomSheetDialog
    private var tapBottomDialogInterface: TapBottomDialogInterface? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.modal_bottom_sheet, container, false)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetDialog.setCancelable(false)
        }
        return bottomSheetDialog
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (view?.parent as View).setBackgroundColor(Color.TRANSPARENT)
    }


    fun setBottomSheetInterface(tapBottomDialogInterface: TapBottomDialogInterface) {
        this.tapBottomDialogInterface = tapBottomDialogInterface
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    open fun onNewIntent(intent: Intent?) {}


    fun setSeparatorTheme() {
        topLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")))
        val separatorViewTheme = SeparatorViewTheme()
        separatorViewTheme.strokeColor =
            Color.parseColor(ThemeManager.getValue("tapSeparationLine.backgroundColor"))
        separatorViewTheme.strokeHeight = ThemeManager.getValue("tapSeparationLine.height")
        indicatorSeparator.setTheme(separatorViewTheme)
    }
}