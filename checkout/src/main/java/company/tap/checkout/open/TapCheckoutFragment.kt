package company.tap.checkout.open

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import company.tap.cardbusinesskit.testmodels.DummyResp
import company.tap.checkout.R
import company.tap.checkout.internal.apiresponse.getJsonDataFromAsset
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.viewmodels.TapLayoutManager
import company.tap.checkout.open.controller.SessionManager
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.interfaces.TapBottomDialogInterface
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog


/**
 *
 * Created by Mario Gamal on 6/9/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TapCheckoutFragment : TapBottomSheetDialog(), TapBottomDialogInterface {

    private val layoutManager: TapLayoutManager by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("checkout is onCreateView called")
        return inflater.inflate(R.layout.checkout_sdk_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("checkout is onViewCreated called")

    //     super.onViewCreated(view, savedInstanceState)
        val checkoutLayout: LinearLayout = view.findViewById(R.id.sdkContainer)
        LocalizationManager.loadTapLocale(resources, R.raw.lang)
        backgroundColor= Color.WHITE
        bottomSheetLayout?.let {
            layoutManager.setBottomSheetLayout(it)
        }
        layoutManager.initLayoutManager(requireContext(),childFragmentManager, checkoutLayout)

        val enabledSections = ArrayList<SectionType>()
        enabledSections.add(SectionType.BUSINESS)
        enabledSections.add(SectionType.AMOUNT_ITEMS)
        enabledSections.add(SectionType.FRAGMENT)


        layoutManager.displayStartupLayout(enabledSections)
        //This is for getting response from business engine . Currently dummy response from assets json is used
        getBusinessHeaderData()
        setBottomSheetInterface(this)


    }

    private fun getBusinessHeaderData() {
        val jsonFileString = activity?.applicationContext?.let { getJsonDataFromAsset(it, "dummyapiresponse.json") }
        val gson = Gson()
        val dummyInitApiResponse: DummyResp = gson.fromJson(jsonFileString, DummyResp::class.java)
        // Pass the api response data to LayoutManager
        layoutManager.getDatafromAPI(dummyInitApiResponse)

    }

    override fun onDismiss() {
        println("onDismiss is called")
        SessionManager.setActiveSession(false)
    }

    override fun onShow() {
        println("onShow is called")
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