package company.tap.checkout.open


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import company.tap.checkout.internal.apiresponse.*
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.viewmodels.TapLayoutViewModel
import company.tap.nfcreader.open.reader.TapEmvCard
import company.tap.nfcreader.open.reader.TapNfcCardReader
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
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
    private lateinit var viewModel: TapLayoutViewModel
    var _Activity: Activity? = null
    private lateinit var tapNfcCardReader: TapNfcCardReader
    private var cardReadDisposable: Disposable = Disposables.empty()


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
        val view = inflater.inflate(R.layout.fragment_checkouttaps, container, false)
        backgroundColor = (Color.parseColor(ThemeManager.getValue("GlobalValues.Colors.clear")))

        val viewModel: TapLayoutViewModel by viewModels()
        this.viewModel = viewModel
        val cardViewModel: CardViewModel by viewModels()
        _Context?.let { cardViewModel.getContext(it) }
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

        enableSections()
        return view
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun enableSections() {
        val enabledSections = ArrayList<SectionType>()
        enabledSections.add(SectionType.BUSINESS)
        enabledSections.add(SectionType.AMOUNT_ITEMS)
        enabledSections.add(SectionType.FRAGMENT)
        viewModel.displayStartupLayout(enabledSections)
        getBusinessHeaderData(context, viewModel)
        setBottomSheetInterface(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getBusinessHeaderData(context: Context?, viewModel: TapLayoutViewModel) {
        if (context?.let { isNetworkAvailable(it) } == true) {
            val cardViewModel: CardViewModel by viewModels()
            if (context != null) {
                cardViewModel.getContext(context)
            }
          //  cardViewModel.liveData.observe(this, { consumeResponse(it) })
            cardViewModel.processEvent(CardViewEvent.InitEvent, viewModel, null)

        }
        //else loadDatafromAssets(context, viewModel) //Incase API not working use local

    }

    @RequiresApi(Build.VERSION_CODES.N)
     fun createChargeReq(context: Context?, viewModel: TapLayoutViewModel){
        if (context?.let { isNetworkAvailable(it) } == true) {
            val cardViewModel: CardViewModel by viewModels()
            if (context != null) {
                cardViewModel.getContext(context)
            }
            //  cardViewModel.liveData.observe(this, { consumeResponse(it) })
            cardViewModel.processEvent(CardViewEvent.ChargeEvent, viewModel, null)

        }
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
        viewModel?.handleScanFailedResult()
    }

    override fun onScanCardFinished(card: Card?, cardImage: ByteArray?) {
        if (card != null) {
            println("scanned card is$card")
            viewModel?.handleScanSuccessResult(card)

        }
    }

    fun handleNFCResult(intent: Intent?) {
        if (tapNfcCardReader?.isSuitableIntent(intent)) {
            cardReadDisposable = tapNfcCardReader
                .readCardRx2(intent)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ emvCard: TapEmvCard? ->
                    if (emvCard != null) {
                        viewModel?.handleNFCScannedResult(emvCard)
                        println("emvCard$emvCard")
                    }
                },
                    { throwable -> throwable.message?.let { println("error is nfc" + throwable.printStackTrace()) } })
        }

    }

    override fun onPause() {
        cardReadDisposable.dispose()
        tapNfcCardReader?.disableDispatch()
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
        println("renderView respoonse" + data)
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data?.initResponse?.let { viewModel.getDatafromAPI(it) }
            data?.paymentOptionsResponse.let {
                if (it != null) {
                    viewModel.getDataPaymentOptionsResponse(it)
                }
            }

        }*/
    }

    @SuppressLint("SetTextI18n")
    private fun concatText(newText: String) {
        println("newText respoonse$newText")

    }

  /*  private fun loadDatafromAssets(context: Context?, viewModel: TapLayoutViewModel) {
        if (context?.let { LocalizationManager.getLocale(it).language } == "en") {
            val jsonFileString = context.let {
                getJsonDataFromAsset(
                    it,
                    "dummyapiresponsedefault.json"
                )
            }
            val dummyInitApiResponse: JsonResponseDummy1 = Gson().fromJson(
                jsonFileString,
                JsonResponseDummy1::class.java
            )
            // Pass the api response data to LayoutManager
            viewModel.getDatafromAPI(dummyInitApiResponse)
        } else {
            val jsonFileStringAr = context?.let {
                getJsonDataFromAsset(
                    it,
                    "dummyapiresponsedefaultar.json"
                )
            }
            val gson = Gson()
            val dummyInitApiResponse: JsonResponseDummy1 = gson.fromJson(
                jsonFileStringAr,
                JsonResponseDummy1::class.java
            )
            // Pass the api response data to LayoutManager
            viewModel.getDatafromAPI(dummyInitApiResponse)
        }

    }*/

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetworkInfo: NetworkInfo? = null
        activeNetworkInfo = cm.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

}

