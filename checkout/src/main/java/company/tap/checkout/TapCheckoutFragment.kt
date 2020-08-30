package company.tap.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import company.tap.checkout.enums.SectionType
import company.tap.checkout.interfaces.OnCardSelectedActionListener
import company.tap.checkout.viewmodels.TapLayoutManager
import company.tap.taplocalizationkit.LocalizationManager

import company.tap.tapuilibrary.interfaces.TapBottomDialogInterface
import company.tap.tapuilibrary.views.TapBottomSheetDialog
import kotlinx.android.synthetic.main.checkout_sdk_layout.*
import kotlin.collections.ArrayList

/**
 *
 * Created by Mario Gamal on 6/9/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TapCheckoutFragment : TapBottomSheetDialog(), TapBottomDialogInterface{

    private val layoutManager: TapLayoutManager by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.checkout_sdk_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val checkoutLayout: LinearLayout = view.findViewById(R.id.sdkContainer)
        LocalizationManager.loadTapLocale(resources, R.raw.lang)
        layoutManager.initLayoutManager(requireContext(), childFragmentManager, checkoutLayout)

        val enabledSections = ArrayList<SectionType>()
        enabledSections.add(SectionType.BUSINESS)
        enabledSections.add(SectionType.AMOUNT_ITEMS)

        layoutManager.displayStartupLayout(enabledSections)
        setBottomSheetInterface(this)
    }

    override fun onDismiss() {
    }

    override fun onShow() {
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.behavior.skipCollapsed = true
        bottomSheetLayout?.let {
            layoutManager.setBottomSheetLayout(it)
        }
        
    }

    override fun onSlide(slideOffset: Float) {
    }

    override fun onStateChanged(newState: Int) {
    }


}