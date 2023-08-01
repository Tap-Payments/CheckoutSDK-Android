package company.tap.checkout.open


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import company.tap.checkout.R
import company.tap.checkout.TapCheckOutSDK
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.apiresponse.CardViewEvent
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.cache.SharedPrefManager
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.utils.*
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.internal.webview.WebFragment.Companion.isWebViewOpened
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.controller.SDKSession.checkOutDelegate
import company.tap.checkout.open.controller.SDKSession.tabAnimatedActionButton
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.uikit.enums.ActionButtonState
import company.tap.tapuilibraryy.uikit.interfaces.TapBottomDialogInterface
import company.tap.tapuilibraryy.uikit.views.TapBottomSheetDialog
import company.tap.tapuilibraryy.uikit.views.TapBrandView
import org.json.JSONObject
import java.util.*


class CheckoutFragment : TapBottomSheetDialog(), TapBottomDialogInterface {
    val viewModel: CheckoutViewModel by viewModels()
    val cardViewModel: CardViewModel by viewModels()

    var checkOutActivity: CheckOutActivity? = null
    var hideAllView = false
    lateinit var status: ChargeStatus
    private var _resetFragment: Boolean = true
    lateinit var topHeaderView: TapBrandView
    lateinit var checkoutLayout: LinearLayout

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
        cardViewModel.processEvent(
            event = CardViewEvent.IpAddressEvent,
            viewModel = viewModel,
            context = context
        )
        initViews(view)
        isWebViewOpened = false
        checkOutDelegate?.sessionHasStarted()



        viewModel.localCurrencyReturned.observe(this, androidx.lifecycle.Observer {
            with(viewModel) {
                /**
                 * check if data cached and different currency present
                 * should put : @for check !isUserCurrencySameToMainCurrency()
                 */
                if (cacheUserLocalCurrency()) {
                    powerdByTapAnimationFinished.observe(this@CheckoutFragment) {
                        if (it == true) {
                            doAfterSpecificTime(500) {
                                addTitlePaymentAndFlag()
                                isUserCurrencySameAsCurrencyOfApplication.observe(
                                    this@CheckoutFragment
                                ) {
                                    if (it) removevisibiltyCurrency()
                                    else showVisibiltyOfCurrency()
                                }
                            }
                        } else {
                            removevisibiltyCurrency()
                        }
                    }
                } else {
                    removevisibiltyCurrency()
                }
            }

        })

        viewModel.isWebViewHolderFor3dsOpened.observe(
            this,
            androidx.lifecycle.Observer { isWebViewHolderOpend ->
                topHeaderView.backButtonLinearLayout.isVisibileWithAnimation(isWebViewHolderOpend)
            })

        topHeaderView.backButtonLinearLayout.setOnClickListener {
            viewModel.resetViewsAlreadyDismissed()
            viewModel.isWebViewHolderFor3dsOpened.value = false

        }


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
        topHeaderView = view.findViewById(R.id.tab_brand_view)
        val inLineCardLayout: FrameLayout = view.findViewById(R.id.inline_container)
        checkoutLayout = view.findViewById(R.id.fragment_all)
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
            cardViewModel = cardViewModel,
            this,
            topHeaderView
        )

        topHeaderView.visibility = View.GONE
        bottomSheetDialog.behavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {


            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
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
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })


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
                        status
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
                viewModel.showOnlyButtonView(status)
        }

        return enabledSections
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        (dialog as BottomSheetDialog).behavior.isFitToContents = true
        return dialog
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
        TapCheckOutSDK.displayMonoLight = false
        TapCheckOutSDK.displayColoredDark = false
        checkOutDelegate?.sessionCancelled()
        LocalizationManager.currentLocalized = JSONObject()
        bottomSheetDialog.dismiss()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDestroy() {
        super.onDestroy()
        resetTabAnimatedButton()
    }


}

