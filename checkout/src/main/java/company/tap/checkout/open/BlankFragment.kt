package company.tap.checkout.open

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import company.tap.checkout.R
import company.tap.checkout.internal.apiresponse.getJsonDataFromAsset
import company.tap.checkout.internal.dummygener.JsonResponseDummy1
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.checkout.internal.viewmodels.TapLayoutManager
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
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment : TapBottomSheetDialog(), TapBottomDialogInterface {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var layoutManager: TapLayoutManager?=null
    private var view1: View? = null
    private var contextt: Context? = null
    private lateinit var baseLayoutManager: BaseLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        layoutManager = ViewModelProvider(requireActivity()).get(TapLayoutManager::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view1 = inflater.inflate(R.layout.fragment_blank, container, false)
        // Inflate the layout for this fragment
        val checkoutLayout: LinearLayout? = view1?.findViewById(R.id.sdkContainer)
        LocalizationManager.loadTapLocale(resources, R.raw.lang)
        /**
         * set bottom sheet background
         */
        backgroundColor =
            (Color.parseColor(ThemeManager.getValue("GlobalValues.Colors.main_switch_background")))
        backgroundColor = (Color.parseColor("#00000000"))
        //  if(bottomSheetDialog!=null){
        //      bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        //  }
        bottomSheetLayout?.let {
            layoutManager?.setBottomSheetLayout(it)
        }

        if (checkoutLayout != null) {
            context?.let {
                layoutManager?.initLayoutManager(
                    it,
                    childFragmentManager,
                    checkoutLayout
                )
            }
        }
        // dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val enabledSections = ArrayList<SectionType>()
        enabledSections.add(SectionType.BUSINESS)
        enabledSections.add(SectionType.AMOUNT_ITEMS)
        enabledSections.add(SectionType.FRAGMENT)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        layoutManager?.displayStartupLayout(enabledSections)
        //This is for getting response from business engine . Currently dummy response from assets json is used
        println("onview created")

        setBottomSheetInterface(this)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED


        return view1
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(context: Context,activity: Activity) =
            BlankFragment().apply {
                arguments = Bundle().apply {
                    // putString(ARG_PARAM1, param1)
                    // putString(ARG_PARAM2, param2)
                }
                contextt = context
                println("context" + contextt)
                getBusinessHeaderData(context,activity)

            }
    }

    private fun getBusinessHeaderData(context: Context,activity: Activity) {
        println("jaonstrinb" + activity?.applicationContext)
        val jsonFileString =
            context?.let { getJsonDataFromAsset(it, "dummyapiresponsedefault.json") }
        println("jsonFileString" + jsonFileString)
        val gson = Gson()
        val dummyInitApiResponse1: JsonResponseDummy1 = gson.fromJson(
            jsonFileString,
            JsonResponseDummy1::class.java
        )
        // Pass the api response data to LayoutManager
         // println("layoutManager" + layoutManager)
        println("isAdded" + isAdded)
        if(isAdded){
         layoutManager?.getDatafromAPI(dummyInitApiResponse1)
        } else{
           // layoutManager = ViewModelProvider(requireActivity()).get(TapLayoutManager::class.java)
           // println("activity ifs " + activity)
          //  layoutManager = activity?.let { ViewModelProviders.of(this).get(TapLayoutManager::class.java) }
          //  println("isAdded if" + isAdded)
          //  layoutManager?.getDatafromAPI(dummyInitApiResponse1)

            childFragmentManager
                .beginTransaction()
                .detach(this)
                .attach(this)
                .commit()
            layoutManager?.getDatafromAPI(dummyInitApiResponse1)
        }

    }



 override fun onAttach(context: Context) {
        super.onAttach(context)
     context?.let { activity?.let { it1 -> getBusinessHeaderData(it, it1) } }
     try {
         println(" context inner" + context)
         baseLayoutManager = context as TapLayoutManager
         println("baseLayoutManager inner" + baseLayoutManager)
     } catch (e: ClassCastException) {
         throw ClassCastException("$context must implement baseLayoutManager")
     }
        val jsonFileString =
            context?.let { getJsonDataFromAsset(it, "dummyapiresponsedefault.json") }
        println("jsonFileString" + jsonFileString)

        val gson = Gson()
        val dummyInitApiResponse1: JsonResponseDummy1 = gson.fromJson(
            jsonFileString,
            JsonResponseDummy1::class.java
        )
        // Pass the api response data to LayoutManager
        println("dummyInitApiResponse1" + baseLayoutManager)
        try {
            baseLayoutManager.getDatafromAPI(dummyInitApiResponse1)

        } catch (e: Exception) {
            println("Exception" + e.printStackTrace())
        }
        println("onAttach")
    }
}