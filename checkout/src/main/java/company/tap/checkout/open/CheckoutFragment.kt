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
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ui.InlineViewCallback
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.apiresponse.CardViewState
import company.tap.checkout.internal.apiresponse.Resource
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
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.SeparatorViewTheme
import company.tap.tapuilibrary.uikit.atoms.TapImageView
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
import company.tap.tapuilibrary.uikit.interfaces.TapBottomDialogInterface
import company.tap.tapuilibrary.uikit.ktx.loadAppThemManagerFromPath
import company.tap.tapuilibrary.uikit.ktx.setTopBorders
import company.tap.tapuilibrary.uikit.models.DialogConfigurations
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog
import company.tap.tapuilibrary.uikit.views.TapBrandView
import org.json.JSONObject
import java.util.*


/**
 * A simple [Fragment] subclass.
// * Use the [CheckoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class CheckoutFragment : BottomSheetDialogFragment(), TapBottomDialogInterface, InlineViewCallback {

    private var _Context: Context? = null

    @JvmField
    var viewModel: CheckoutViewModel? = null

    lateinit var userRepository: UserRepository

    var _activity: Activity? = null
    var checkOutActivity: CheckOutActivity? = null
    lateinit var closeText: TapTextView
    lateinit var closeImage: TapImageView
    var hideAllView = false
    lateinit var status: ChargeStatus
    private var _resetFragment: Boolean = true
    var newColorVal: String? = null

    @JvmField
    var scrollView: NestedScrollView? = null
    var coordinatorLayout: CoordinatorLayout? = null
    lateinit var  bottomSheetLayout: FrameLayout
    lateinit var bottomSheetDialog: BottomSheetDialog


    @JvmField
    var isNfcOpened: Boolean = false

    @JvmField
    var isScannerOpened: Boolean = false


    private var inLineCardLayout: FrameLayout? = null
    private var topHeaderView: TapBrandView? = null
    var headerLayout: LinearLayout? = null
    var sdkCardView: CardView? = null
    private var displayMetrics: Int? = 0
    var originalHeight: Int? = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)

    }



    override fun onDestroy() {
        super.onDestroy()
        Glide.with(this).pauseRequests()
        resetTabAnimatedButton()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        bottomSheetDialog.setOnShowListener {
            val dialog = it as BottomSheetDialog
            bottomSheetLayout = dialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)!!
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
            bottomSheetBehavior.isDraggable
            bottomSheetBehavior.isHideable = true
            bottomSheetDialog.behavior.peekHeight


            //  setSeparatorTheme()
        }
        return inflater.inflate(R.layout.fragment_checkouttaps,null,false)
    }

    private fun changeBackground() {
//        bottomSheetDialog.setOnShowListener {
//            bottomSheetLayout = bottomSheetDialog.findViewById(R.id.design_bottom_sheet)
////            bottomSheetLayout?.background = getBackgroundDrawable()
////            tapBottomDialogInterface?.onShow()
//        }
    }

    private fun setDialogConfigurations() {
//        arguments?.let {
//            dialog?.setCanceledOnTouchOutside(it.getBoolean(DialogConfigurations.Cancelable, true))
//            dialog?.window?.setDimAmount(it.getFloat(DialogConfigurations.Dim, 1.5f))
//            backgroundColor = it.getInt(
//                DialogConfigurations.Color,
//                Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor"))
//            )
//            val corners = it.getFloatArray(DialogConfigurations.Corners)
//            corners?.let { array ->
//                topLeftCorner = array[0]
//                topRightCorner = array[1]
//                bottomRightCorner = array[2]
//                bottomLeftCorner = array[3]
//            }
//        }
    }

    fun setSeparatorTheme() {
//        topLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")))
//        val separatorViewTheme = SeparatorViewTheme()
//        separatorViewTheme.strokeColor =
//            Color.parseColor(ThemeManager.getValue("tapSeparationLine.backgroundColor"))
//        separatorViewTheme.strokeHeight = ThemeManager.getValue("tapSeparationLine.height")
//        indicatorSeparator.setTheme(separatorViewTheme)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: CheckoutViewModel by viewModels()
        val cardViewModel: CardViewModel by viewModels()
        setDialogConfigurations()
        changeBackground()
        userRepository = UserRepository(requireContext(), viewModel)
        userRepository.getUserIpAddress()
        this.viewModel = viewModel
        _Context?.let { cardViewModel.getContext(it) }

        //bottomSheetDialog.behavior.isDraggable = true

        val checkoutLayout: LinearLayout? = view.findViewById(R.id.fragment_all)
        val frameLayout: FrameLayout? = view.findViewById(R.id.fragment_container_nfc_lib)
        val webFrameLayout: FrameLayout? = view.findViewById(R.id.webFrameLayout)
        webFrameLayout?.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            requireContext().getDeviceSpecs().first - 100
        )
        inLineCardLayout = view.findViewById(R.id.inline_container)
        headerLayout = view.findViewById(R.id.headerLayout)
        sdkCardView = view.findViewById(R.id.sdkCardView)
        initViews(view)

        topHeaderView = context?.let { TapBrandView(it) }

        topHeaderView?.visibility = View.GONE
        topHeaderView?.poweredByImage?.setImageResource(R.drawable.powered_by_tap)
        topHeaderView?.poweredByImage?.scaleType = ImageView.ScaleType.CENTER_CROP
        topHeaderView?.poweredByImage?.layoutParams?.width = context.getDimensionsInDp(120)
        topHeaderView?.poweredByImage?.layoutParams?.height = context.getDimensionsInDp(22)

        displayMetrics = CustomUtils.getDeviceDisplayMetrics(context as Activity)
        val heightscreen: Int = Resources.getSystem().displayMetrics.heightPixels
        if (LocalizationManager.currentLocalized.length() != 0)
            closeText.text = LocalizationManager.getValue("close", "Common")

        if (SDKSession.showCloseImage == true) {

            if (!hideAllView) {
                Handler().postDelayed({
                    closeImage.visibility = View.GONE

                }, 3000)
            }

        } else {
            if (!hideAllView) {
                Handler().postDelayed({
                    closeText.visibility = View.GONE

                }, 3000)
            }

        }

        //LocalizationManager.loadTapLocale(resources, R.raw.lang)
        sessionDelegate?.sessionHasStarted()

//        bottomSheetLayout.let {
//            viewModel.setBottomSheetLayout(it)
//        }
        if (::bottomSheetDialog.isInitialized){
            requireContext().showToast("Initialized")
            if (checkoutLayout != null) {
                requireContext().let {
                    if (frameLayout != null) {
                        webFrameLayout?.let { it1 ->
                            if (inLineCardLayout != null) {
                                activity?.intent?.let { it2 ->
                                    if (headerLayout != null) {
                                        viewModel.initLayoutManager(
                                            bottomSheetDialog,
                                            it,
                                            childFragmentManager,
                                            checkoutLayout,
                                            frameLayout,
                                            it1,
                                            inLineCardLayout!!,
                                            this,
                                            it2,
                                            cardViewModel, this, headerLayout!!,
                                            bottomSheetLayout
                                        )
                                    }
                                }

                            }
                        }
                    }
                }
            }

        }else{
            requireContext().showToast("not Initialized")
        }

//        viewModel.localCurrencyReturned.observe(this, androidx.lifecycle.Observer {
//            with(viewModel) {
//                /**
//                 * check if data cached and different currency present
//                 * should put : @for check !isUserCurrencySameToMainCurrency()
//                 */
//                if (cacheUserLocalCurrency() && !requireActivity().isUserCurrencySameToMainCurrency()) {
//                    viewModel.powerdByTapAnimationFinished.observe(this@CheckoutFragment) {
//                        if (it == true) {
//                            doAfterSpecificTime {
//                                viewModel.addTitlePaymentAndFlag()
//                            }
//                        } else {
//                            viewModel.removevisibiltyCurrency()
//                        }
//                    }
//                } else {
//                    viewModel.removevisibiltyCurrency()
//                }
//            }
//
//        })

//
//            val borderColor: String =
//                ThemeManager.getValue<String>("poweredByTap.backgroundColor").toString()
//            var borderOpacityVal: String? = null
//            //Workaround since we don't have direct method for extraction
//            borderOpacityVal = borderColor.substring(borderColor.length - 2)
//            newColorVal = "#" + borderOpacityVal + borderColor.substring(0, borderColor.length - 2)
//                .replace("#", "")
//            Log.e("color", newColorVal.toString())
//            enableSections()
//            originalHeight = checkoutLayout.measuredHeight
//            /**
//             *
//             * Discuss with aslm if it affected or not**/
//
//            /*      topHeaderView?.backgroundHeader?.setBackgroundDrawable(
//                      createDrawableGradientForBlurry(
//                          intArrayOf(
//                              Color.parseColor(newColorVal),
//                              Color.parseColor(context?.getString(R.color.black_blur_12)),
//                              Color.parseColor(newColorVal)
//                          )
//                      )
//                  )*/
//            topHeaderView?.backgroundHeader?.setBackgroundDrawable(null)
//            //  bottomSheetLayout?.addView(topHeaderView, 0)
//        }
        topHeaderView?.visibility = View.VISIBLE


        inLineCardLayout?.minimumHeight = heightscreen - checkoutLayout?.height!!
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation

        adjustHeightAccToDensity(displayMetrics)
        topHeaderView?.startPoweredByAnimation(
            delayTime = PoweredByLayoutAnimationDelay,
            topHeaderView?.poweredByImage, onAnimationEnd = {
                poweredByTapAnimationEnds(viewModel)
            }
        )

//        sdkCardView?.setBackgroundDrawable(
//            createDrawableGradientForBlurry(
//                intArrayOf(
//                    Color.parseColor(newColorVal),
//                    Color.parseColor(context?.getString(R.color.black_blur_12)),
//                    Color.parseColor(newColorVal)
//                )
//            )
//        )

//        setTopBorders(
//            checkoutLayout,
//            105f,
//            strokeColor = Color.parseColor(
//                newColorVal
//            ),
//            tintColor = Color.parseColor(
//                newColorVal
//            ),// tint color
//            shadowColor = Color.parseColor(
//                newColorVal
//            )
//        )

//        checkoutLayout.setBackgroundDrawable(
//            createDrawableGradientForBlurry(
//                intArrayOf(
//                    Color.parseColor(newColorVal),
//                    Color.parseColor(newColorVal),
//                    Color.parseColor(newColorVal)
//                )
//            )
//        )


        closeText.setOnClickListener {
//            bottomSheetDialog.dismissWithAnimation
//            bottomSheetDialog.hide()
//            bottomSheetDialog.dismiss()
            viewModel.incrementalCount = 0
            resetTabAnimatedButton()


        }
        closeImage.setOnClickListener {
//            bottomSheetDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
//            bottomSheetDialog.hide()
            resetTabAnimatedButton()
            viewModel.incrementalCount = 0

        }


        //   topHeaderView?.outerConstraint?.applyBluryToView()


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

        closeText = view.findViewById(R.id.closeText)
        closeImage = view.findViewById(R.id.closeImage)
        scrollView = view.findViewById(R.id.scrollView)
        coordinatorLayout = view.findViewById(R.id.coordinator)
        coordinatorLayout?.addView(topHeaderView, 0)
        // relativeLL = view.findViewById(R.id.relativeLL)
        // mainCardLayout = view.findViewById(R.id.mainCardLayout)
        /**Added to init the lib of getting dynamic flags*/
        //        World.init(context)
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

        println("_resetFragment>>" + _resetFragment)
        println("hideAllView>>" + hideAllView)
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

        //setBottomSheetInterface(this)

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
                _resetFragment = resetFragment
                requireArguments().putBoolean(RESET_FRAG, resetFragment)
            }
    }

    override fun onScanCardFailed(e: Exception?) {
        println("onScanCardFailed")
        // _viewModel?.handleScanFailedResult()
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
//        bottomSheetDialog.behavior.isFitToContents = true
//        return bottomSheetDialog
//    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onScanCardFinished(card: Card?, cardImage: ByteArray?) {
        if (card != null) {
            println("scanned card is$card")
            //  _viewModel?.handleScanSuccessResult(card)

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
        checkOutActivity?.displayMetrics?.let {
            tabAnimatedActionButton?.setDisplayMetricsTheme(
                it,
                CustomUtils.getCurrentTheme()
            )
        }
        SDKSession.sessionActive = false
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.RESET)
        if (checkOutActivity?.isGooglePayClicked == false) {
            checkOutActivity?.overridePendingTransition(0, R.anim.slide_down_exit)
            checkOutActivity?.finishAfterTransition()
        }
        //  checkOutActivity?.finish()

        val payString: String = LocalizationManager.getValue("pay", "ActionButton")
        val nowString: String = LocalizationManager.getValue("now", "ActionButton")
        /**
         * Stopped reseting the view of button cz of loader will test if not required will remove this code*/
        /*   tabAnimatedActionButton?.setButtonDataSource(
               true,
               context?.let { LocalizationManager.getLocale(it).language },
               payString + " " + nowString,
               Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
               Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor")),
           )*/
        /*  tabAnimatedActionButton?.setOnClickListener {
              requireActivity().supportFragmentManager.let { it1 -> SDKSession.contextSDK?.let { it2 ->
                  SDKSession.startSDK(it1,
                      it2,SDKSession.contextSDK as Activity)
              } }
          }*/
        tabAnimatedActionButton?.isClickable = true
        tabAnimatedActionButton?.isEnabled = true
    }


    fun dismissBottomSheetDialog() {
        //bottomSheetDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        ThemeManager.currentTheme = ""
        LocalizationManager.currentLocalized = JSONObject()
//        bottomSheetDialog.dismissWithAnimation
//        bottomSheetDialog.hide()
//        bottomSheetDialog.dismiss()
        resetTabAnimatedButton()
        sessionDelegate?.sessionCancelled()

    }


}

