package company.tap.checkout.open


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
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
import company.tap.checkout.internal.apiresponse.CardViewState
import company.tap.checkout.internal.apiresponse.Resource
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.internal.utils.ResizeAnimation
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.controller.SDKSession.sessionDelegate
import company.tap.checkout.open.controller.SDKSession.tabAnimatedActionButton
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapImageView
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
import company.tap.tapuilibrary.uikit.interfaces.TapBottomDialogInterface
import company.tap.tapuilibrary.uikit.ktx.setTopBorders
import company.tap.tapuilibrary.uikit.utils.BlurBuilder
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog
import company.tap.tapuilibrary.uikit.views.TapBrandView
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
// * Use the [CheckoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckoutFragment : TapBottomSheetDialog(), TapBottomDialogInterface, InlineViewCallback {

    private var _Context: Context? = null

    @JvmField
    var _viewModel: CheckoutViewModel? = null
    private lateinit var cardViewModel: CardViewModel
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

    @JvmField
    var isNfcOpened: Boolean = false

    @JvmField
    var isScannerOpened: Boolean = false

    @JvmField
    var isFullscreen = false
    var heightIn: Int = 0
    private var inLineCardLayout: FrameLayout? = null
    private var relativeLL: RelativeLayout? = null
    private var mainCardLayout: CardView? = null
    private var topHeaderView: TapBrandView? = null
    private var displayMetrics: Int? = 0
    private var targetHeight: Int? = 0

    private val mBackgroundBlurRadius = 30
    private val mBlurBehindRadius = 60

    // We set a different dim amount depending on whether window blur is enabled or disabled
    private val mDimAmountWithBlur = 0.1f
    private val mDimAmountNoBlur = 0.4f

    // We set a different alpha depending on whether window blur is enabled or disabled
    private val mWindowBackgroundAlphaWithBlur = 170
    private val mWindowBackgroundAlphaNoBlur = 255

    // Use a rectangular shape drawable for the window background. The outline of this drawable
    // dictates the shape and rounded corners for the window background blur area.
    private var mWindowBackgroundDrawable: Drawable? = null
    var originalHeight: Int? = 0

    var blurView: BlurView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activity = activity?.parent
        this._Context = context


    }

    /*  @RequiresApi(Build.VERSION_CODES.N)
      override fun onDestroyView() {
          println("onDestroyView>>>")
          *//*if (view?.parent != null) {
            (view?.parent as ViewGroup).removeView(view)
        }*//*
        resetTabAnimatedButton()
        super.onDestroyView()
    }*/


    override fun onDestroy() {
        super.onDestroy()

        // if (!this.isDestroyed()) {
        Glide.with(this).pauseRequests()
        // }
        resetTabAnimatedButton()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        return inflater.inflate(R.layout.fragment_checkouttaps, container, false)
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: CheckoutViewModel by viewModels()
        val cardViewModel: CardViewModel by viewModels()
        this._viewModel = viewModel
        _Context?.let { cardViewModel.getContext(it) }
        backgroundColor = (Color.parseColor(ThemeManager.getValue("tapBottomSheet.dimmedColor")))
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        bottomSheetDialog.behavior.isDraggable = true
        val checkoutLayout: LinearLayout? = view.findViewById(R.id.fragment_all)
        val frameLayout: FrameLayout? = view.findViewById(R.id.fragment_container_nfc_lib)
        val webFrameLayout: FrameLayout? = view.findViewById(R.id.webFrameLayout)
        webFrameLayout?.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                Resources.getSystem().displayMetrics.heightPixels
            )
        )
        inLineCardLayout = view.findViewById(R.id.inline_container)
        val headerLayout: LinearLayout? = view.findViewById(R.id.headerLayout)
        bottomSheetLayout = bottomSheetDialog.findViewById(R.id.design_bottom_sheet)
        closeText = view.findViewById(R.id.closeText)
        closeImage = view.findViewById(R.id.closeImage)
        scrollView = view.findViewById(R.id.scrollView)
        relativeLL = view.findViewById(R.id.relativeLL)
        mainCardLayout = view.findViewById(R.id.mainCardLayout)
        blurView = BlurView(context)

        topHeaderView = context?.let { TapBrandView(it) }
        topHeaderView?.visibility = View.GONE

        displayMetrics = CustomUtils.getDeviceDisplayMetrics(context as Activity)


        val heightscreen: Int = Resources.getSystem().getDisplayMetrics().heightPixels;
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

        bottomSheetLayout?.let {
            viewModel.setBottomSheetLayout(it)
        }

        if (checkoutLayout != null) {
            context?.let {
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
                                        cardViewModel, this, headerLayout
                                    )
                                }
                            }

                        }
                    }
                }
            }
            enableSections()
            originalHeight = checkoutLayout.measuredHeight

            checkoutLayout.addView(topHeaderView, 0)


        }
        inLineCardLayout?.minimumHeight = heightscreen - checkoutLayout?.height!!
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation

        bottomSheetDialog.setOnShowListener {
            //Handler().postDelayed({
            bottomSheetDialog.behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            //  }, 0)
        }
        val borderColor: String =
            ThemeManager.getValue<String>("poweredByTap.backgroundColor").toString()
        var borderOpacityVal: String? = null
        //Workaround since we don't have direct method for extraction
        borderOpacityVal = borderColor.substring(borderColor.length - 2)
        newColorVal = "#" + borderOpacityVal + borderColor.substring(0, borderColor.length - 2)
            .replace("#", "")
        println("color iii>>" + newColorVal)

        //  topHeaderView?.setBackgroundResource(R.drawable.blurviewnew)
          topHeaderView?.setBackgroundColor(Color.TRANSPARENT)
        scrollView?.let {
            setTopBorders(
                it,
                40f,// corner raduis
                0.0f,
                Color.parseColor(
                    newColorVal
                ),
                Color.parseColor(
                    newColorVal
                ),// tint color
                Color.parseColor(
                    newColorVal
                )
            )
        }


        relativeLL.let { it1 ->
            if (it1 != null) {
                setTopBorders(
                    it1,
                    35f,// corner raduis
                    0.0f,
                    Color.parseColor(
                        newColorVal
                    ),// stroke color
                    Color.parseColor(newColorVal),// tint color
                    Color.parseColor(newColorVal)
                )
            }
        }
        mainCardLayout.let { it1 ->
            if (it1 != null) {
                setTopBorders(
                    it1,
                    35f,// corner raduis
                    0.0f,
                    Color.parseColor(newColorVal),// stroke color
                    Color.parseColor(newColorVal),// tint color
                    Color.parseColor(newColorVal)
                )
            }
        }

        closeText.setOnClickListener {
            bottomSheetDialog.dismissWithAnimation
            bottomSheetDialog.hide()
            bottomSheetDialog.dismiss()
            viewModel.incrementalCount = 0
            resetTabAnimatedButton()


        }
        closeImage.setOnClickListener {
            bottomSheetDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
            bottomSheetDialog.hide()
            resetTabAnimatedButton()
            viewModel.incrementalCount = 0

        }

        adjustHeightAccToDensity()

        Handler(Looper.getMainLooper()).postDelayed({
            topHeaderView?.visibility = View.VISIBLE
            topHeaderView?.setAlpha(0.8f)
        }, 1000)
        val resizeAnimation =
            targetHeight?.let {
                ResizeAnimation(
                    topHeaderView,
                    it,
                    0, true
                )
            }

        resizeAnimation?.duration = 1500
        topHeaderView?.startAnimation(resizeAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            topHeaderView?.visibility = View.VISIBLE
        }, 3000)

    }

    private fun adjustHeightAccToDensity() {
        if (displayMetrics == DisplayMetrics.DENSITY_260 || displayMetrics == DisplayMetrics.DENSITY_280 || displayMetrics == DisplayMetrics.DENSITY_300 || displayMetrics == DisplayMetrics.DENSITY_XHIGH || displayMetrics == DisplayMetrics.DENSITY_340 || displayMetrics == DisplayMetrics.DENSITY_360) {
            targetHeight = 90
        } else if (displayMetrics == DisplayMetrics.DENSITY_450 || displayMetrics == DisplayMetrics.DENSITY_420 || displayMetrics == DisplayMetrics.DENSITY_400) {
            targetHeight = 120
        } else targetHeight = 140
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
                    _viewModel?.showOnlyButtonView(
                        status,
                        checkOutActivity as CheckOutActivity?,
                        this
                    )

            } else {

                _viewModel?.displayStartupLayout(enabledSections)
                _viewModel?.getDatasfromAPIs(
                    PaymentDataSource.getMerchantData(),
                    PaymentDataSource.getPaymentOptionsResponse()
                )

            }
        } else {
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
                _resetFragment = resetFragment
                requireArguments().putBoolean(RESET_FRAG, resetFragment)
            }
    }

    override fun onScanCardFailed(e: Exception?) {
        println("onScanCardFailed")
        // _viewModel?.handleScanFailedResult()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onScanCardFinished(card: Card?, cardImage: ByteArray?) {
        if (card != null) {
            println("scanned card is$card")
            //  _viewModel?.handleScanSuccessResult(card)

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
        checkOutActivity?.displayMetrics?.let { tabAnimatedActionButton?.setDisplayMetrics(it) }
        SDKSession.sessionActive = false
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.RESET)
        if (checkOutActivity?.isGooglePayClicked == false) {
            checkOutActivity?.overridePendingTransition(0, R.anim.slide_down_exit)
            checkOutActivity?.finishAfterTransition()
        }
        //  checkOutActivity?.finish()

        val payString: String = LocalizationManager.getValue("pay", "ActionButton")
        tabAnimatedActionButton?.setButtonDataSource(
            true,
            context?.let { LocalizationManager.getLocale(it).language },
            payString,
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor")),
        )
        /*  tabAnimatedActionButton?.setOnClickListener {
              requireActivity().supportFragmentManager.let { it1 -> SDKSession.contextSDK?.let { it2 ->
                  SDKSession.startSDK(it1,
                      it2,SDKSession.contextSDK as Activity)
              } }
          }*/
        tabAnimatedActionButton?.isClickable = true
        tabAnimatedActionButton?.isEnabled = true
    }


    /*  private fun initKeyBoardListener() {
          // Минимальное значение клавиатуры.
          // Threshold for minimal keyboard height.
          val MIN_KEYBOARD_HEIGHT_PX = 150
          // Окно верхнего уровня view.
          // Top-level window decor view.
              // val decorView: View = netscape.javascript.JSObject.getWindow().getDecorView()
          // Регистрируем глобальный слушатель. Register global layout listener.
          decorView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
              // Видимый прямоугольник внутри окна.
              // Retrieve visible rectangle inside window.
              private val windowVisibleDisplayFrame: Rect = Rect()
              private var lastVisibleDecorViewHeight = 0
              override fun onGlobalLayout() {
                  decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame)
                  val visibleDecorViewHeight: Int = windowVisibleDisplayFrame.height()
                  if (lastVisibleDecorViewHeight != 0) {
                      if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {

                      } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {

                      }
                  }
                  // Сохраняем текущую высоту view до следующего вызова.
                  // Save current decor view height for the next call.
                  lastVisibleDecorViewHeight = visibleDecorViewHeight
              }
          })
      }*/

    fun dismissBottomSheetDialog() {
        //bottomSheetDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        ThemeManager.currentTheme = ""
        LocalizationManager.currentLocalized = JSONObject()
        bottomSheetDialog.dismissWithAnimation
        bottomSheetDialog.hide()
        bottomSheetDialog.dismiss()
        resetTabAnimatedButton()
        sessionDelegate?.sessionCancelled()

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        return BottomSheetDialog(requireContext(), R.style.MyTransparentBottomSheetDialogTheme)
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private fun setupWindowBlurListener() {
        val windowBlurEnabledListener: (Boolean) -> Unit = this::updateWindowForBlurs
        activity?.window?.decorView?.addOnAttachStateChangeListener(
            object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    activity?.getWindowManager()?.addCrossWindowBlurEnabledListener(
                        windowBlurEnabledListener
                    )

                }

                override fun onViewDetachedFromWindow(v: View) {
                    activity?.getWindowManager()?.removeCrossWindowBlurEnabledListener(
                        windowBlurEnabledListener
                    )
                }
            })
    }

    private fun updateWindowForBlurs(blursEnabled: Boolean) {
        mWindowBackgroundDrawable?.alpha =
            if (blursEnabled && mBackgroundBlurRadius > 0) mWindowBackgroundAlphaWithBlur else mWindowBackgroundAlphaNoBlur
        activity?.getWindow()
            ?.setDimAmount(if (blursEnabled && mBlurBehindRadius > 0) mDimAmountWithBlur else mDimAmountNoBlur)
        if (buildIsAtLeastS()) {
            // Set the window background blur and blur behind radii
            activity?.getWindow()
                ?.setBackgroundBlurRadius(mBackgroundBlurRadius)
            activity?.getWindow()
                ?.getAttributes()
                ?.setBlurBehindRadius(mBlurBehindRadius)
            activity?.getWindow()
                ?.setAttributes(
                    activity?.getWindow()
                        ?.getAttributes()
                )
        }
    }

    private fun buildIsAtLeastS(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

}

