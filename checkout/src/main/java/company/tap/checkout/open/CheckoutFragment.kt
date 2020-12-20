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
import company.tap.checkout.internal.viewmodels.TapLayoutViewModell
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.uikit.interfaces.TapBottomDialogInterface
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog


/**
 * A simple [Fragment] subclass.
// * Use the [CheckoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckoutFragment : TapBottomSheetDialog(), TapBottomDialogInterface {

    private var view1: View? = null
    private var _Context: Context? = null
    private lateinit var viewModell :TapLayoutViewModell
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
        view1 = inflater.inflate(R.layout.fragment_checkouttaps, container, false)
        backgroundColor = (Color.parseColor("#00000000"))
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        val viewModell: TapLayoutViewModell by viewModels()
        this.viewModell = viewModell
        val checkoutLayout: LinearLayout? = view1?.findViewById(R.id.fragment_all)
        val frameLayout: FrameLayout? = view1?.findViewById(R.id.fragment_container_nfc_lib)
        val webFrameLayout: FrameLayout? = view1?.findViewById(R.id.webFrameLayout)
        LocalizationManager.loadTapLocale(resources, R.raw.lang)
        bottomSheetLayout?.let {
            viewModell.setBottomSheetLayout(it)
        }
        if (checkoutLayout != null) {
            context?.let {
                if (frameLayout != null) {
                    webFrameLayout?.let { it1 ->
                        viewModell.initLayoutManager(bottomSheetDialog,it,childFragmentManager,checkoutLayout,frameLayout,
                            it1
                        )
                    }
                }
            }
        }
        val enabledSections = ArrayList<SectionType>()
        enabledSections.add(SectionType.BUSINESS)
        enabledSections.add(SectionType.AMOUNT_ITEMS)
        enabledSections.add(SectionType.FRAGMENT)
        viewModell.displayStartupLayout(enabledSections)
        getBusinessHeaderData(context,viewModell)
        setBottomSheetInterface(this)
        return view1
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getBusinessHeaderData( context: Context?, viewModell22: TapLayoutViewModell) {
        if (context?.let { LocalizationManager.getLocale(it).language } == "en") {
            val jsonFileString = context?.let { getJsonDataFromAsset(it, "dummyapiresponsedefault.json") }
            val gson = Gson()
            val dummyInitApiResponse: JsonResponseDummy1 = gson.fromJson(jsonFileString, JsonResponseDummy1::class.java)
            // Pass the api response data to LayoutManager
            viewModell22.getDatafromAPI(dummyInitApiResponse)
        }else{
            val jsonFileStringar = context?.let { getJsonDataFromAsset(it, "dummyapiresponsedefaultar.json") }
            val gson = Gson()
            val dummyInitApiResponse: JsonResponseDummy1 = gson.fromJson(jsonFileStringar, JsonResponseDummy1::class.java)
            // Pass the api response data to LayoutManager
            viewModell22.getDatafromAPI(dummyInitApiResponse)
        }

    }


    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(context: Context, activity: Activity) =
            CheckoutFragment().apply {
                arguments = Bundle().apply {

                }
                _Context = context
               _Activity=activity
            }
    }


}

