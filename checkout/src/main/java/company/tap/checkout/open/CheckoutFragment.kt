package company.tap.checkout.open

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import company.tap.checkout.R
import company.tap.checkout.internal.apiresponse.getJsonDataFromAsset
import company.tap.checkout.internal.dummygener.JsonResponseDummy1
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.viewmodels.TapLayoutViewModel
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.interfaces.TapBottomDialogInterface
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog


/**
 * A simple [Fragment] subclass.
// * Use the [CheckoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckoutFragment : TapBottomSheetDialog(), TapBottomDialogInterface {

    private var _Context: Context? = null
    private lateinit var viewModel :TapLayoutViewModel
     var _Activity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _Activity = activity?.parent
        this._Context = context
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      val  view = inflater.inflate(R.layout.fragment_checkouttaps, container, false)
        backgroundColor = (Color.parseColor(ThemeManager.getValue("GlobalValues.Colors.clear")))
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        val viewModel: TapLayoutViewModel by viewModels()
        this.viewModel = viewModel

        val checkoutLayout: LinearLayout? = view?.findViewById(R.id.fragment_all)
        val frameLayout: FrameLayout? = view?.findViewById(R.id.fragment_container_nfc_lib)
        val webFrameLayout: FrameLayout? = view?.findViewById(R.id.webFrameLayout)
        LocalizationManager.loadTapLocale(resources, R.raw.lang)

        bottomSheetLayout?.let {
            viewModel.setBottomSheetLayout(it)
        }
        if (checkoutLayout != null) {
            context?.let {
                if (frameLayout != null) {
                    webFrameLayout?.let { it1 ->
                        viewModel.initLayoutManager(bottomSheetDialog,it,childFragmentManager,checkoutLayout,frameLayout,
                            it1
                        )
                    }
                }
            }
        }

        enableSections()
        return view
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun enableSections(){
        val enabledSections = ArrayList<SectionType>()
        enabledSections.add(SectionType.BUSINESS)
        enabledSections.add(SectionType.AMOUNT_ITEMS)
        enabledSections.add(SectionType.FRAGMENT)
        viewModel.displayStartupLayout(enabledSections)
        getBusinessHeaderData(context,viewModel)
        setBottomSheetInterface(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getBusinessHeaderData( context: Context?, viewModel: TapLayoutViewModel) {
        if (context?.let { LocalizationManager.getLocale(it).language } == "en") {
            val jsonFileString = context.let { getJsonDataFromAsset(it, "dummyapiresponsedefault.json") }
            val gson = Gson()
            val dummyInitApiResponse: JsonResponseDummy1 = gson.fromJson(jsonFileString, JsonResponseDummy1::class.java)
            // Pass the api response data to LayoutManager
            viewModel.getDatafromAPI(dummyInitApiResponse)
        }else{
            val jsonFileStringAr = context?.let { getJsonDataFromAsset(it, "dummyapiresponsedefaultar.json") }
            val gson = Gson()
            val dummyInitApiResponse: JsonResponseDummy1 = gson.fromJson(jsonFileStringAr, JsonResponseDummy1::class.java)
            // Pass the api response data to LayoutManager
            viewModel.getDatafromAPI(dummyInitApiResponse)
        }

    }


    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(context: Context, activity: Activity) =
            CheckoutFragment().apply {
                arguments = Bundle().apply {}
                _Context = context
               _Activity=activity
            }
    }


}

