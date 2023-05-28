package company.tap.checkout.open


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ui.InlineViewCallback
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.apiresponse.UserRepository
import company.tap.checkout.internal.cache.SharedPrefManager
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.utils.*
import company.tap.checkout.internal.utils.Constants.PoweredByLayoutAnimationDelay
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.controller.SDKSession.sessionDelegate
import company.tap.checkout.open.controller.SDKSession.tabAnimatedActionButton
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.uikit.animation.MorphingAnimation
import company.tap.tapuilibraryy.uikit.atoms.TapImageView
import company.tap.tapuilibraryy.uikit.atoms.TapTextViewNew
import company.tap.tapuilibraryy.uikit.enums.ActionButtonState
import company.tap.tapuilibraryy.uikit.interfaces.TabAnimatedButtonListener
import company.tap.tapuilibraryy.uikit.interfaces.TapBottomDialogInterface
import company.tap.tapuilibraryy.uikit.ktx.setTopBorders
import company.tap.tapuilibraryy.uikit.views.TapBottomSheetDialog
import company.tap.tapuilibraryy.uikit.views.TapBrandView
import kotlinx.android.synthetic.main.fragment_checkouttaps.*
import org.json.JSONObject
import java.util.*


/**
 * A simple [Fragment] subclass.
// * Use the [CheckoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class CheckoutFragment : TapBottomSheetDialog(), TapBottomDialogInterface, InlineViewCallback {

    @JvmField
    var viewModel: CheckoutViewModel? = null

    lateinit var userRepository: UserRepository
    var checkOutActivity: CheckOutActivity? = null
    var hideAllView = false
    lateinit var status: ChargeStatus
    private var _resetFragment: Boolean = true

    @JvmField
    var scrollView: NestedScrollView? = null

    @JvmField
    var isNfcOpened: Boolean = false

    @JvmField
    var isScannerOpened: Boolean = false


    private var inLineCardLayout: FrameLayout? = null
    lateinit var topHeaderView: TapBrandView
    lateinit var headerLayout: LinearLayout
    lateinit var checkoutLayout: LinearLayout
    lateinit var frameLayoutForNFc: FrameLayout
    lateinit var webFrameLayout: FrameLayout


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
        val viewModel: CheckoutViewModel by viewModels()
        val cardViewModel: CardViewModel by viewModels()
        cardViewModel.getContext(requireContext())
        userRepository = UserRepository(requireContext(), viewModel)
        userRepository.getUserIpAddress()
        this.viewModel = viewModel
        initViews(view)
        topHeaderView = view.findViewById(R.id.tab_brand_view)
        topHeaderView.visibility = View.GONE

        sessionDelegate?.sessionHasStarted()
        bottomSheetLayout?.let {
            viewModel.setBottomSheetLayout(it)
        }

        viewModel.localCurrencyReturned.observe(this, androidx.lifecycle.Observer {
            with(viewModel) {
                /**
                 * check if data cached and different currency present
                 * should put : @for check !isUserCurrencySameToMainCurrency()
                 */
                if (cacheUserLocalCurrency() && !requireActivity().isUserCurrencySameToMainCurrency()) {
                    viewModel.powerdByTapAnimationFinished.observe(this@CheckoutFragment) {
                        if (it == true) {
                            doAfterSpecificTime {
                                viewModel.addTitlePaymentAndFlag()
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

        viewModel.initLayoutManager(
            bottomSheetDialog,
            requireContext(),
            childFragmentManager,
            checkoutLayout,
            frameLayoutForNFc,
            webFrameLayout,
            inLineCardLayout!!,
            this,
            requireActivity().intent,
            cardViewModel, this,
            headerLayout,
            topHeaderView
        )
        enableSections()
        topHeaderView.startPoweredByAnimation(
            delayTime = PoweredByLayoutAnimationDelay,
            topHeaderView.poweredByImage, onAnimationEnd =
            {
                poweredByTapAnimationEnds(viewModel)
            }
        )


    }

    private fun poweredByTapAnimationEnds(viewModel: CheckoutViewModel) {
        viewModel.powerdByTapAnimationFinished.value = true
    }

    /**
     * Logic to obtain ISO country code **/
    fun getSimIsoCountryCurrency(): String? {
        return SharedPrefManager.getUserSupportedLocaleForTransactions(requireContext())?.symbol

    }

    private fun initViews(view: View) {
        bottomSheetLayout = bottomSheetDialog.findViewById(R.id.design_bottom_sheet)
//        closeText = view.findViewById(R.id.closeText)
//        closeImage = view.findViewById(R.id.closeImage)
        scrollView = view.findViewById(R.id.scrollView)
        inLineCardLayout = view.findViewById(R.id.inline_container)
        headerLayout = view.findViewById(R.id.headerLayout)
        checkoutLayout = view.findViewById(R.id.fragment_all)
        frameLayoutForNFc = view.findViewById(R.id.fragment_container_nfc_lib)
        webFrameLayout = view.findViewById(R.id.webFrameLayout)
        webFrameLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            requireContext().getDeviceSpecs().first - 100
        )
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
                    viewModel?.showOnlyButtonView(
                        status,
                        checkOutActivity,
                        this
                    )

            } else {

                viewModel?.displayStartupLayout(enabledSections)
                viewModel?.getDatasfromAPIs(
                    PaymentDataSource.getMerchantData(),
                    PaymentDataSource.getPaymentOptionsResponse()
                )

            }
        } else {
            if (::status.isInitialized)
                viewModel?.showOnlyButtonView(status, checkOutActivity, this)
        }

        return enabledSections
    }


    companion object {
        // TODO: Rename and change types and number of parameters
        const val RESET_FRAG = "resetFragment"

        @JvmStatic
        fun newInstance(context: Context, activity: Activity?, resetFragment: Boolean) =
            CheckoutFragment().apply {
                arguments = Bundle().apply {}
                _resetFragment = resetFragment
                requireArguments().putBoolean(RESET_FRAG, resetFragment)
            }
    }

    override fun onScanCardFailed(e: Exception?) {
        println("onScanCardFailed")
        // _viewModel?.handleScanFailedResult()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        (dialog as BottomSheetDialog).behavior.isFitToContents = true
        return dialog
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onScanCardFinished(card: Card?, cardImage: ByteArray?) {
        if (card != null) {
            println("scanned card is$card")
            //  _viewModel?.handleScanSuccessResult(card)

        }
    }


    override fun onDetach() {
        super.onDetach()
        if (view == null) {
            return
        }
        if (isNfcOpened) {
        } else {
            checkOutActivity?.onBackPressed()
        }

        if (isScannerOpened) {

        } else {
            //_viewModel?.incrementalCount =0
            checkOutActivity?.onBackPressed()
        }


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
        ThemeManager.currentTheme = ""
        sessionDelegate?.sessionCancelled()
        LocalizationManager.currentLocalized = JSONObject()
        bottomSheetDialog.dismiss()
        resetTabAnimatedButton()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDestroy() {
        super.onDestroy()
        resetTabAnimatedButton()
    }


}

