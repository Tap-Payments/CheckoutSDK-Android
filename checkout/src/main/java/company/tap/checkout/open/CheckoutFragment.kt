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
import kotlinx.android.synthetic.main.fragment_checkouttaps.view.*


/**
 * A simple [Fragment] subclass.
// * Use the [CheckoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckoutFragment : TapBottomSheetDialog(), TapBottomDialogInterface {

    private var view1: View? = null
    private var contextt: Context? = null
    private lateinit var viewModell :TapLayoutViewModell
     var _Activity: Activity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _Activity = activity?.parent
        this.contextt = context
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    fun refreshFragment (){
        dismiss()
        dialog?.dismiss()
        onShow()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view1 = inflater.inflate(R.layout.fragment_checkouttaps, container, false)

        /**
         * set bottom sheet background
         */
//        backgroundColor = (Color.parseColor(ThemeManager.getValue("GlobalValues.Colors.main_switch_background")))
        backgroundColor = (Color.parseColor("#00000000"))

        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        val viewModell: TapLayoutViewModell by viewModels()
        this.viewModell = viewModell

        //BlankFragment().retainInstance = true

        val checkoutLayout: LinearLayout? = view1?.findViewById(R.id.fragment_all)
        val frameLayout: FrameLayout? = view1?.findViewById(R.id.fragment_container_nfc_lib)
        val webFrameLayout: FrameLayout? = view1?.findViewById(R.id.webFrameLayout)
        LocalizationManager.loadTapLocale(resources, R.raw.lang)

        bottomSheetLayout?.let {
            viewModell.setBottomSheetLayout(it)
        }
        print("contextt before business"+contextt)
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

//        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        viewModell.displayStartupLayout(enabledSections)
        //This is for getting response from business engine . Currently dummy response from assets json is used
        getBusinessHeaderData(context,viewModell)
        setBottomSheetInterface(this)
        return view1
    }

    private fun getBusinessHeaderData(context: Context?, viewModell22: TapLayoutViewModell) {
        print("contexct22"+context)
        val jsonFileString = context?.let { getJsonDataFromAsset(it, "dummyapiresponsedefault.json") }
        val gson = Gson()
        val dummyInitApiResponse: JsonResponseDummy1 = gson.fromJson(jsonFileString, JsonResponseDummy1::class.java)
        // Pass the api response data to LayoutManager
        viewModell22.getDatafromAPI(dummyInitApiResponse)
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
            CheckoutFragment().apply {
                arguments = Bundle().apply {

                }
                contextt = context
                println("context" + contextt)
               _Activity=activity
            }
    }


}

