package company.tap.checkout.open

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import company.tap.checkout.R
import company.tap.checkout.internal.apiresponse.getJsonDataFromAsset
import company.tap.checkout.internal.dummygener.JsonResponseDummy1
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.viewmodels.TapLayoutManager
import company.tap.checkout.open.controller.SessionManager
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.interfaces.TapBottomDialogInterface
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TapCheckoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TapCheckoutFragment : TapBottomSheetDialog(), TapBottomDialogInterface
     {
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
       // activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
      setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view1 = inflater.inflate(R.layout.fragment_tap_checkout, container, false)
       // dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val checkoutLayout: LinearLayout? = view1?.findViewById(R.id.sdkContainer)
        LocalizationManager.loadTapLocale(resources, R.raw.lang)
        /**
         * set bottom sheet background
         */
        backgroundColor = (Color.parseColor(ThemeManager.getValue("GlobalValues.Colors.main_switch_background")))
        backgroundColor = (Color.parseColor("#00000000"))
      //  if(bottomSheetDialog!=null){
      //      bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
      //  }
        bottomSheetLayout?.let {
            layoutManager.setBottomSheetLayout(it)
        }

        if (checkoutLayout != null) {
            context?.let { layoutManager.initLayoutManager(it,childFragmentManager,checkoutLayout) }
        }
       // dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val enabledSections = ArrayList<SectionType>()
        enabledSections.add(SectionType.BUSINESS)
        enabledSections.add(SectionType.AMOUNT_ITEMS)
        enabledSections.add(SectionType.FRAGMENT)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        layoutManager.displayStartupLayout(enabledSections)
        //This is for getting response from business engine . Currently dummy response from assets json is used
        getBusinessHeaderData()
        setBottomSheetInterface(this)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        return view1
    }


    private fun getBusinessHeaderData() {
        val jsonFileString = activity?.applicationContext?.let { getJsonDataFromAsset(it, "dummyapiresponsedefault.json") }
        val gson = Gson()
        val dummyInitApiResponse1: JsonResponseDummy1 = gson.fromJson(jsonFileString, JsonResponseDummy1::class.java)
        // Pass the api response data to LayoutManager
        layoutManager.getDatafromAPI(dummyInitApiResponse1)

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TapCheckoutFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TapCheckoutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
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