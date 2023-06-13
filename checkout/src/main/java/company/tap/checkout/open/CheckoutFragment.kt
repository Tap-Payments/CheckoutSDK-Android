package company.tap.checkout.open


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ui.InlineViewCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.apiresponse.CardViewEvent
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.apiresponse.UserRepository
import company.tap.checkout.internal.cache.SharedPrefManager
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.utils.*
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.internal.webview.WebFragment.Companion.isWebViewOpened
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.controller.SDKSession.sessionDelegate
import company.tap.checkout.open.controller.SDKSession.tabAnimatedActionButton
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.uikit.enums.ActionButtonState
import company.tap.tapuilibraryy.uikit.interfaces.TapBottomDialogInterface
import company.tap.tapuilibraryy.uikit.views.TapBottomSheetDialog
import company.tap.tapuilibraryy.uikit.views.TapBrandView
import kotlinx.android.synthetic.main.fragment_checkouttaps.*
import org.json.JSONObject
import java.util.*


class CheckoutFragment : TapBottomSheetDialog(), TapBottomDialogInterface, InlineViewCallback {
    val viewModel: CheckoutViewModel by viewModels()
    val cardViewModel: CardViewModel by viewModels()
    lateinit var userRepository: UserRepository
    var checkOutActivity: CheckOutActivity? = null
    var hideAllView = false
    lateinit var status: ChargeStatus
    private var _resetFragment: Boolean = true

    @JvmField
    var isNfcOpened: Boolean = false

    @JvmField
    var isScannerOpened: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation

        return inflater.inflate(R.layout.fragment_checkouttaps, container, false)
    }

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardViewModel.getContext(requireContext())
           //  userRepository = UserRepository(requireContext(), viewModel)
        //userRepository.getUserIpAddress()
        cardViewModel.processEvent(event = CardViewEvent.IpAddressEvent,viewModel= viewModel, context = context)
        initViews(view)
        isWebViewOpened = false
        sessionDelegate?.sessionHasStarted()


        viewModel.localCurrencyReturned.observe(this, androidx.lifecycle.Observer {
            with(viewModel) {
                /**
                 * check if data cached and different currency present
                 * should put : @for check !isUserCurrencySameToMainCurrency()
                 */
                if (cacheUserLocalCurrency() && !requireActivity().isUserCurrencySameToMainCurrency(
                        viewModel.isUserCurrencySameAsCurrencyOfApplication
                    )
                ) {
                    viewModel.powerdByTapAnimationFinished.observe(this@CheckoutFragment) {
                        if (it == true) {
                            doAfterSpecificTime {
                                viewModel.addTitlePaymentAndFlag()
                                viewModel.isUserCurrencySameAsCurrencyOfApplication.observe(
                                    this@CheckoutFragment
                                ) {
                                    if (it) viewModel.removevisibiltyCurrency()
                                    else viewModel.showVisibiltyOfCurrency()
                                }
                            }
                        } else {
                            viewModel.removevisibiltyCurrency()
                        }
                    }
                } else {
                    viewModel.removevisibiltyCurrency()
                }
            }

        })



        enableSections()

    }


    /**
     * Logic to obtain ISO country code **/
    fun getSimIsoCountryCurrency(): String? {
        return SharedPrefManager.getUserSupportedLocaleForTransactions(requireContext())?.symbol

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initViews(view: View) {
        bottomSheetLayout = bottomSheetDialog.findViewById(R.id.design_bottom_sheet)
        val topHeaderView: TapBrandView = view.findViewById(R.id.tab_brand_view)
        val inLineCardLayout: FrameLayout = view.findViewById(R.id.inline_container)
        val headerLayout: LinearLayout = view.findViewById(R.id.headerLayout)
        val checkoutLayout: LinearLayout = view.findViewById(R.id.fragment_all)
        val frameLayoutForNFc: FrameLayout = view.findViewById(R.id.fragment_container_nfc_lib)
        val webFrameLayout: FrameLayout = view.findViewById(R.id.webFrameLayout)

        bottomSheetLayout?.let {
            viewModel.setBottomSheetLayout(it)
        }
        viewModel.initLayoutManager(
            bottomSheetDialog,
            requireContext(),
            childFragmentManager,
            checkoutLayout,
            frameLayoutForNFc,
            webFrameLayout,
            inLineCardLayout,
            this,
            requireActivity().intent,
            cardViewModel = cardViewModel, this,
            headerLayout,
            topHeaderView
        )

        topHeaderView.visibility = View.GONE
        doAfterSpecificTime(50) {
            topHeaderView.startPoweredByAnimation(
                delayTime = Constants.PoweredByLayoutAnimationDelay,
                topHeaderView.poweredByImage, onAnimationEnd =
                {
                    if (viewModel.isItemsAreOpend.value == false) {
                        poweredByTapAnimationEnds()
                    }
                }
            )
        }


    }

    private fun poweredByTapAnimationEnds() {
        viewModel.powerdByTapAnimationFinished.value = true
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        LocalizationManager.setLocale(context, PaymentDataSource.getSDKLocale())

    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun enableSections(): ArrayList<SectionType> {
        val enabledSections = ArrayList<SectionType>()
        enabledSections.add(SectionType.BUSINESS)
        enabledSections.add(SectionType.AMOUNT_ITEMS)
        enabledSections.add(SectionType.FRAGMENT)
        enabledSections.add(SectionType.ActionButton)

        if (_resetFragment) {
            if (hideAllView) {
                if (::status.isInitialized)
                    viewModel.showOnlyButtonView(
                        status,
                        checkOutActivity,
                        this
                    )

            } else {

                viewModel.displayStartupLayout(enabledSections)
                viewModel.getDatasfromAPIs(
                    PaymentDataSource.getMerchantData(),
                    PaymentDataSource.getPaymentOptionsResponse()
                )

            }
        } else {
            if (::status.isInitialized)
                viewModel.showOnlyButtonView(status, checkOutActivity, this)
        }

        return enabledSections
    }

    override fun onScanCardFailed(e: Exception?) {
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        (dialog as BottomSheetDialog).behavior.isFitToContents = true
        return dialog
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onScanCardFinished(card: Card?, cardImage: ByteArray?) {
    }


    override fun onDetach() {
        super.onDetach()
        if (view == null) {
            return
        }
        if (!isNfcOpened)
            checkOutActivity?.onBackPressed()

        if (!isScannerOpened)
            checkOutActivity?.onBackPressed()

    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun resetTabAnimatedButton() {
        SDKSession.sessionActive = false
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.RESET)
        if (checkOutActivity?.isGooglePayClicked == false) {
            checkOutActivity?.overridePendingTransition(0, R.anim.slide_down_exit)
            checkOutActivity?.finishAfterTransition()
        }
        tabAnimatedActionButton?.isClickable = true
        tabAnimatedActionButton?.isEnabled = true
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun dismissBottomSheetDialog() {
        resetSessionAndThemeManager()
        resetTabAnimatedButton()

    }

    private fun resetSessionAndThemeManager() {
        ThemeManager.currentTheme = ""
        ThemeManager.currentThemeName = ""
        sessionDelegate?.sessionCancelled()
        LocalizationManager.currentLocalized = JSONObject()
        bottomSheetDialog.dismiss()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDestroy() {
        super.onDestroy()
        resetTabAnimatedButton()
    }


}

