package company.tap.checkout

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import company.tap.cardbusinesskit.testmodels.DummyResp
import company.tap.checkout.internal.apiresponse.getJsonDataFromAsset
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.viewmodels.TapLayoutManager
import company.tap.checkout.open.controller.SessionManager
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.uikit.interfaces.TapBottomDialogInterface
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TapCheckoutFragment1.newInstance] factory method to
 * create an instance of this fragment.
 */
class TapCheckoutFragment1 : TapBottomSheetDialog(), TapBottomDialogInterface {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val layoutManager: TapLayoutManager by viewModels()
    private var view1:View?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view1 = inflater.inflate(R.layout.fragment_tap_checkout1, container, false)
        val checkoutLayout: LinearLayout? = view1?.findViewById(R.id.sdkContainer)
        LocalizationManager.loadTapLocale(resources, R.raw.lang)
        backgroundColor= Color.WHITE
        if(bottomSheetDialog!=null){
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        bottomSheetLayout?.let {
            layoutManager.setBottomSheetLayout(it)
        }

        if (checkoutLayout != null) {
            layoutManager.initLayoutManager(requireContext(),childFragmentManager,checkoutLayout)
        }


        val enabledSections = ArrayList<SectionType>()
        enabledSections.add(SectionType.BUSINESS)
        enabledSections.add(SectionType.AMOUNT_ITEMS)
        enabledSections.add(SectionType.FRAGMENT)


        layoutManager.displayStartupLayout(enabledSections)
        //This is for getting response from business engine . Currently dummy response from assets json is used
        getBusinessHeaderData()
        setBottomSheetInterface(this)
        return view1
    }


    private fun getBusinessHeaderData() {
        val jsonFileString = activity?.applicationContext?.let { getJsonDataFromAsset(it, "dummyapiresponse.json") }
        val gson = Gson()
        val dummyInitApiResponse: DummyResp = gson.fromJson(jsonFileString, DummyResp::class.java)
        // Pass the api response data to LayoutManager
        layoutManager.getDatafromAPI(dummyInitApiResponse)

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TapCheckoutFragment1.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TapCheckoutFragment1().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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