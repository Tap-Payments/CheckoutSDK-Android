package company.tap.checkout.open


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ui.InlineViewCallback
import company.tap.checkout.R
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.apiresponse.CardViewState
import company.tap.checkout.internal.apiresponse.Resource
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.controller.SDKSession.sessionDelegate
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.nfcreader.open.reader.TapEmvCard
import company.tap.nfcreader.open.reader.TapNfcCardReader
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapImageView
import company.tap.tapuilibrary.uikit.interfaces.TapBottomDialogInterface
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables


/**
 * A simple [Fragment] subclass.
// * Use the [CheckoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckoutFragment : TapBottomSheetDialog(), TapBottomDialogInterface, InlineViewCallback {

    private var _Context: Context? = null
    private lateinit var viewModel: CheckoutViewModel
    var _Activity: Activity? = null
    private lateinit var tapNfcCardReader: TapNfcCardReader
    private var cardReadDisposable: Disposable = Disposables.empty()

    val closeIcon by lazy { view?.findViewById<TapImageView>( R.id.closeIcon) }



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
        val viewModel: CheckoutViewModel by viewModels()
        val cardViewModel: CardViewModel by viewModels()
        this.viewModel = viewModel
        _Context?.let { cardViewModel.getContext(it) }
        closeIcon?.setOnClickListener {
            bottomSheetDialog.hide()
             }

        val view = inflater.inflate(R.layout.fragment_checkouttaps, container, false)
        backgroundColor = (Color.parseColor(ThemeManager.getValue("GlobalValues.Colors.clear")))


        val checkoutLayout: LinearLayout? = view?.findViewById(R.id.fragment_all)
        val frameLayout: FrameLayout? = view?.findViewById(R.id.fragment_container_nfc_lib)
        val webFrameLayout: FrameLayout? = view?.findViewById(R.id.webFrameLayout)
        val inLineCardLayout: FrameLayout? = view?.findViewById(R.id.inline_container)



        LocalizationManager.loadTapLocale(resources, R.raw.lang)
        tapNfcCardReader = TapNfcCardReader(requireActivity())

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
                                    cardViewModel
                                )
                            }

                        }
                    }
                }
            }
        }
        sessionDelegate?.sessionIsStarting()
         enableSections()
        return view
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun enableSections(): ArrayList<SectionType> {
        val enabledSections = ArrayList<SectionType>()
        enabledSections.add(SectionType.BUSINESS)
        enabledSections.add(SectionType.AMOUNT_ITEMS)
        enabledSections.add(SectionType.FRAGMENT)
       viewModel.displayStartupLayout(enabledSections)
        viewModel.getDatasfromAPIs(PaymentDataSource?.getSDKSettings(),PaymentDataSource?.getPaymentOptionsResponse())
        setBottomSheetInterface(this)
        return enabledSections
    }




    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(context: Context, activity: Activity, intent: Intent) =
            CheckoutFragment().apply {
                arguments = Bundle().apply {}
                _Context = context
                _Activity = activity
            }
    }

    override fun onScanCardFailed(e: Exception?) {
        println("onScanCardFailed")
        viewModel.handleScanFailedResult()
    }

    override fun onScanCardFinished(card: Card?, cardImage: ByteArray?) {
        if (card != null) {
            println("scanned card is$card")
            viewModel.handleScanSuccessResult(card)

        }
    }

    fun handleNFCResult(intent: Intent?) {
        if (tapNfcCardReader.isSuitableIntent(intent)) {
            cardReadDisposable = tapNfcCardReader
                .readCardRx2(intent)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ emvCard: TapEmvCard? ->
                    if (emvCard != null) {
                        viewModel.handleNFCScannedResult(emvCard)
                        println("emvCard$emvCard")
                    }
                },
                    { throwable -> throwable.message?.let { println("error is nfc" + throwable.printStackTrace()) } })
        }

    }

    override fun onPause() {
        cardReadDisposable.dispose()
        tapNfcCardReader.disableDispatch()
        super.onPause()
    }

    private fun consumeResponse(response: Resource<CardViewState>) {
        println("response value is" + response.data?.initResponse)
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


}

