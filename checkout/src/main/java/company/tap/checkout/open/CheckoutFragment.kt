package company.tap.checkout.open


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ui.InlineViewCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.apiresponse.CardViewState
import company.tap.checkout.internal.apiresponse.Resource
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.controller.SDKSession.sessionDelegate
import company.tap.checkout.open.controller.SDKSession.tabAnimatedActionButton
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
import company.tap.tapuilibrary.uikit.interfaces.TapBottomDialogInterface
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog


/**
 * A simple [Fragment] subclass.
// * Use the [CheckoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckoutFragment : TapBottomSheetDialog(),TapBottomDialogInterface, InlineViewCallback {

    private var _Context: Context? = null
    @JvmField
     var _viewModel: CheckoutViewModel?=null
    private lateinit var cardViewModel: CardViewModel
    var _activity: Activity? = null
    var checkOutActivity: CheckOutActivity? = null

    var hideAllView =false
    lateinit var status :ChargeStatus
    private  var _resetFragment :Boolean = true
    @JvmField
    var isNfcOpened:Boolean=false

    private var isFullscreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activity = activity?.parent
        this._Context = context
       // setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)


    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDestroyView() {
        println("onDestroyView>>>")
        if (view?.parent != null) {
            (view?.parent as ViewGroup).removeView(view)
        }
        resetTabAnimatedButton()
        super.onDestroyView()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_checkouttaps, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: CheckoutViewModel by viewModels()
        val cardViewModel: CardViewModel by viewModels()
        this._viewModel = viewModel
        _Context?.let { cardViewModel.getContext(it) }
        backgroundColor = (Color.parseColor(ThemeManager.getValue("GlobalValues.Colors.clear")))

        bottomSheetDialog.behavior.peekHeight
        bottomSheetDialog.behavior.isDraggable
        val checkoutLayout: LinearLayout? = view.findViewById(R.id.fragment_all)
        val frameLayout: FrameLayout? = view.findViewById(R.id.fragment_container_nfc_lib)
        val webFrameLayout: FrameLayout? = view.findViewById(R.id.webFrameLayout)
        val inLineCardLayout: FrameLayout? = view.findViewById(R.id.inline_container)
        val closeText: TapTextView? = view.findViewById(R.id.closeText)
        closeText?.text = LocalizationManager.getValue("close", "Common")

        if(!hideAllView) {
        Handler().postDelayed({
            closeText?.visibility = View.VISIBLE

        }, 4000)
        }

        closeText?.setOnClickListener {
            bottomSheetDialog.dismissWithAnimation
            bottomSheetDialog.hide()
            bottomSheetDialog.dismiss()
            resetTabAnimatedButton()

        }

        LocalizationManager.loadTapLocale(resources, R.raw.lang)

        bottomSheetLayout?.let {
            viewModel.setBottomSheetLayout(it)
        }
        if (checkoutLayout != null) {
            context?.let {
                if (frameLayout != null) {
                    webFrameLayout?.let { it1 ->
                        if (inLineCardLayout != null) {
                            activity?.intent?.let { it2 ->
                                viewModel.initLayoutManager(
                                    bottomSheetDialog,
                                    it,
                                    childFragmentManager,
                                    checkoutLayout,
                                    frameLayout,
                                    it1,
                                    inLineCardLayout,
                                    this,
                                    it2,
                                    cardViewModel, this
                                )
                            }

                        }
                    }
                }
            }
        }
         enableSections()
        sessionDelegate?.sessionIsStarting()
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation

        bottomSheetDialog.setOnShowListener {
            //Handler().postDelayed({
                bottomSheetDialog.behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
          //  }, 0)
        }


        bottomSheetDialog.behavior.setBottomSheetCallback(object:BottomSheetBehavior.BottomSheetCallback(){

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                   resetTabAnimatedButton()
                    dismiss()
                }
            }

            override fun onSlide(p0: View, p1: Float) {

            }
        })


    }



    @RequiresApi(Build.VERSION_CODES.N)
    fun enableSections(): ArrayList<SectionType> {
        val enabledSections = ArrayList<SectionType>()
        enabledSections.add(SectionType.BUSINESS)
        enabledSections.add(SectionType.AMOUNT_ITEMS)
        enabledSections.add(SectionType.FRAGMENT)
        enabledSections.add(SectionType.ActionButton)

        println("_resetFragment>>" + _resetFragment)
        println("hideAllView>>" + hideAllView)
if(_resetFragment) {
    if (hideAllView) {
        if (::status.isInitialized)
            _viewModel?.showOnlyButtonView(status, checkOutActivity as CheckOutActivity?, this)

    } else {
        _viewModel?.displayStartupLayout(enabledSections)
        _viewModel?.getDatasfromAPIs(
            PaymentDataSource.getMerchantData(),
            PaymentDataSource.getPaymentOptionsResponse()
        )

    }
}else{
    if (::status.isInitialized)
        _viewModel?.showOnlyButtonView(status, checkOutActivity as CheckOutActivity?, this)
}

        setBottomSheetInterface(this)

        return enabledSections
    }





    companion object {
        // TODO: Rename and change types and number of parameters
        const val RESET_FRAG = "resetFragment"
        @JvmStatic
        fun newInstance(context: Context, activity: Activity?, resetFragment: Boolean) =
            CheckoutFragment().apply {
                arguments = Bundle().apply {}
                _Context = context
                _activity = activity
                _resetFragment =resetFragment
requireArguments().putBoolean(RESET_FRAG, resetFragment)
            }
    }

    override fun onScanCardFailed(e: Exception?) {
        println("onScanCardFailed")
        _viewModel?.handleScanFailedResult()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onScanCardFinished(card: Card?, cardImage: ByteArray?) {
        if (card != null) {
            println("scanned card is$card")
            _viewModel?.handleScanSuccessResult(card)

        }
    }




    private fun consumeResponse(response: Resource<CardViewState>) {
        println("response value is" + response.data?.configResponseModel)
        when (response) {
            is Resource.Loading -> concatText("Loading")
            is Resource.Finished -> renderView(response.data)
            is Error -> response.message?.let { concatText(it) }
            is Resource.Success -> renderView(response.data)

        }
    }

    private fun renderView(data: CardViewState?) {

    }
    @SuppressLint("SetTextI18n")
    private fun concatText(newText: String) {
        println("newText respoonse$newText")

    }



    override fun onDetach() {
        super.onDetach()
     if (view == null) {
            return
        }
        if(isNfcOpened){
        }else {
            checkOutActivity?.onBackPressed()
        }


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun resetTabAnimatedButton(){
        SDKSession.sessionActive = false
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.RESET)
if(checkOutActivity?.isGooglePayClicked == false){
    checkOutActivity?.finish()
}
      //  checkOutActivity?.finish()
        tabAnimatedActionButton?.setButtonDataSource(
            true,
            context.let {
                if (it != null) {
                    LocalizationManager.getLocale(it).language
                }
            }.toString(),
            LocalizationManager.getValue("pay", "ActionButton"),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
        )

        tabAnimatedActionButton?.isClickable=true
    }





}

