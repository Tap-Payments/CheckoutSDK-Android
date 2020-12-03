package company.tap.checkout.open

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.*
import com.google.gson.Gson
import company.tap.checkout.R
import company.tap.checkout.internal.apiresponse.getJsonDataFromAsset
import company.tap.checkout.internal.dummygener.JsonResponseDummy1
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.checkout.internal.interfaces.getDataInterface
import company.tap.checkout.internal.viewmodels.TapLayoutViewModel
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.interfaces.TapBottomDialogInterface
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog




/**
 * A simple [Fragment] subclass.
 * Use the [CheckoutTapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckoutTapFragment : TapBottomSheetDialog(), TapBottomDialogInterface {



    private var view1: View? = null
    private var contextt: Context? = null
    private lateinit var baseLayoutManager: BaseLayoutManager
    private lateinit var getDataInterface: getDataInterface

    private lateinit var viewModel1: TapLayoutViewModel

     var _Activity: Activity? = null

    lateinit var dummyInitApiResponse1: JsonResponseDummy1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  val viewModel: TapLayoutViewModel by viewModels()
      //  this.viewModel1 = viewModel
        _Activity = activity?.parent
        this.contextt = context
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        backgroundColor =
            (Color.parseColor(ThemeManager.getValue("GlobalValues.Colors.main_switch_background")))
        backgroundColor = (Color.parseColor("#00000000"))
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view1 = inflater.inflate(R.layout.fragment_checkouttap, container, false)

       // bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        val viewModel: TapLayoutViewModel by viewModels()
        this.viewModel1 = viewModel

        //BlankFragment().retainInstance = true
        println("onAttach is calleds")

        /*try {
            baseLayoutManager = context as BaseLayoutManager
            //getDataInterface = activity2 as getDataInterface
        } catch (e: ClassCastException) {
            //throw ClassCastException("$activity2 must implement MyInterface ")
        }
        val jsonFileString =
            context?.let { getJsonDataFromAsset(it, "dummyapiresponsedefault.json") }*/

        // Inflate the layout for this fragment
        val checkoutLayout: LinearLayout? = view1?.findViewById(R.id.sdkContainer)
        LocalizationManager.loadTapLocale(resources, R.raw.lang)
      //  println("jsonFileString in create is calleds"+jsonFileString)
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
            viewModel1?.setBottomSheetLayout(it)
        }


     /*   println("jsonFileString in create is calleds"+jsonFileString)
        val gson11 = Gson()



            val dummyInitApiResponse11 :JsonResponseDummy1= gson11.fromJson(jsonFileString, JsonResponseDummy1::class.java)
            println("dummyInit are"+dummyInitApiResponse11 +"\n"+"context2"+context)
            println("context are"+context +"\n"+"childFragmentManager"+childFragmentManager+"viewmode"+viewModel1)
            dummyInitApiResponse1= dummyInitApiResponse11*/
           // baseLayoutManager.getDatafromAPI(dummyInitApiResponse11)
        if (checkoutLayout != null) {
            context?.let { viewModel1.initLayoutManager(it,childFragmentManager,checkoutLayout) }
        }
        // dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val enabledSections = ArrayList<SectionType>()
        enabledSections.add(SectionType.BUSINESS)
        enabledSections.add(SectionType.AMOUNT_ITEMS)
        enabledSections.add(SectionType.FRAGMENT)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        viewModel1.displayStartupLayout(enabledSections)
        //This is for getting response from business engine . Currently dummy response from assets json is used
        getBusinessHeaderData(context,viewModel1)
           /* context?.let { checkoutLayout?.let { it1 ->
                viewModel1.initLayoutManager(it,childFragmentManager,
                    it1
                )
            } }

        println("base viewModel1"+viewModel1)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
         viewModel1.displayStartupLayout(enabledSections)*/
        //This is for getting response from business engine . Currently dummy response from assets json is used
        println("onview created")

        setBottomSheetInterface(this)


        return view1
    }

    private fun getBusinessHeaderData(context: Context?,viewModel2: TapLayoutViewModel) {
        val jsonFileString = context?.let { getJsonDataFromAsset(it, "dummyapiresponsedefault.json") }
        val gson = Gson()
        val dummyInitApiResponse: JsonResponseDummy1 = gson.fromJson(jsonFileString, JsonResponseDummy1::class.java)
        // Pass the api response data to LayoutManager
        viewModel2.getDatafromAPI(dummyInitApiResponse)
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
        fun newInstance(context: Context, activity: Activity) =
            CheckoutTapFragment().apply {
                arguments = Bundle().apply {
                    // putString(ARG_PARAM1, param1)
                    // putString(ARG_PARAM2, param2)
                }
                contextt = context
                println("context" + contextt)
               _Activity=activity
               /// getBusinessHeaderData(context, activity)


            }
    }





  /*  override fun onAttach(context2: Context) {
        super.onAttach(context2)

     //   getDataInterface.getDatafromAPIS(dummyInitApiResponse1)
    }*/



}

