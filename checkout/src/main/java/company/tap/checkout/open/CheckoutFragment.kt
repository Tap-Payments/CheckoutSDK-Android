package company.tap.checkout.open


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ui.InlineViewCallback
import com.bumptech.glide.Glide
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
import company.tap.tapuilibrary.uikit.atoms.TapImageView
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.interfaces.TapBottomDialogInterface
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog
import java.util.*
import kotlin.collections.ArrayList


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

    @JvmField
    var scrollView: NestedScrollView? = null

    @JvmField
    var isNfcOpened: Boolean = false

    @JvmField
    var isFullscreen = false
    var heightIn: Int = 0
    private var inLineCardLayout: FrameLayout? = null

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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: CheckoutViewModel by viewModels()
        val cardViewModel: CardViewModel by viewModels()
        this._viewModel = viewModel
        _Context?.let { cardViewModel.getContext(it) }
        backgroundColor = (Color.parseColor(ThemeManager.getValue("GlobalValues.Colors.clear")))
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        bottomSheetDialog.behavior.isDraggable = true
        val checkoutLayout: LinearLayout? = view.findViewById(R.id.fragment_all)
        val frameLayout: FrameLayout? = view.findViewById(R.id.fragment_container_nfc_lib)
        val webFrameLayout: FrameLayout? = view.findViewById(R.id.webFrameLayout)
        inLineCardLayout = view.findViewById(R.id.inline_container)
        val headerLayout: LinearLayout? = view.findViewById(R.id.headerLayout)
        bottomSheetLayout = bottomSheetDialog.findViewById(R.id.design_bottom_sheet)
        closeText = view.findViewById(R.id.closeText)
        closeImage = view.findViewById(R.id.closeImage)
        scrollView = view.findViewById(R.id.scrollView)
        val heightscreen: Int = Resources.getSystem().getDisplayMetrics().heightPixels;
        // bottomSheetDialog.behavior.peekHeight = heightscreen

        println("heightscreen" + heightscreen)
        println("sdkLayoutheight" + checkoutLayout?.height)
        println("bottomSheetLayout" + bottomSheetLayout)



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
        closeText.setOnClickListener {
            bottomSheetDialog.dismissWithAnimation
            bottomSheetDialog.hide()
            bottomSheetDialog.dismiss()
            resetTabAnimatedButton()

        }
        closeImage.setOnClickListener {
            bottomSheetDialog.dismissWithAnimation
            bottomSheetDialog.hide()
            bottomSheetDialog.dismiss()
            resetTabAnimatedButton()

        }



        LocalizationManager.loadTapLocale(resources, R.raw.lang)
        sessionDelegate?.sessionIsStarting()
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
        }
        inLineCardLayout?.minimumHeight = heightscreen - checkoutLayout?.height!!

        enableSections()

        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation

        bottomSheetDialog.setOnShowListener {
            //Handler().postDelayed({
            bottomSheetDialog.behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            //  }, 0)
        }


        bottomSheetDialog.behavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                /*  println("111heightscreen>>>>"+heightscreen)
                  println("1111sdkLayoutheight>>>>>"+checkoutLayout?.height)
                  println("1111newState>>>>>"+newState)
                  println("1111difff>>>>>"+ checkoutLayout?.height?.let { heightscreen.minus(it) })
                  println("1111peek>>>>>"+bottomSheetDialog.behavior.peekHeight)
                  var diff = checkoutLayout?.height?.let { heightscreen.minus(it) }*/


                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    resetTabAnimatedButton()
                    dismiss()
                }

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetDialog.behavior.peekHeight = heightscreen

                    bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    // bottomSheetDialog.behavior.saveFlags = BottomSheetBehavior.SAVE_FIT_TO_CONTENTS
                    scrollView?.smoothScrollTo(0, heightscreen)
                    bottomSheetDialog.behavior.isDraggable = true

                }
            }

            override fun onSlide(view: View, slideOffset: Float) {
                // println("onSlide"+p1)
                //  bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                scrollView?.smoothScrollTo(0, heightscreen)
            }
        })


    }

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


    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun resetTabAnimatedButton() {
        checkOutActivity?.displayMetrics?.let { tabAnimatedActionButton?.setDisplayMetrics(it) }
        SDKSession.sessionActive = false
        //tabAnimatedActionButton?.changeButtonState(ActionButtonState.RESET)
        if (checkOutActivity?.isGooglePayClicked == false) {
            checkOutActivity?.finish()
        }
        //  checkOutActivity?.finish()

        val payString: String = LocalizationManager.getValue("pay", "ActionButton")
        tabAnimatedActionButton?.setButtonDataSource(
            true,
            context?.let { LocalizationManager.getLocale(it).language },
            payString,
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
            null,
        )
        /*  tabAnimatedActionButton?.setOnClickListener {
              requireActivity().supportFragmentManager.let { it1 -> SDKSession.contextSDK?.let { it2 ->
                  SDKSession.startSDK(it1,
                      it2,SDKSession.contextSDK as Activity)
              } }
          }*/
        tabAnimatedActionButton?.isClickable = true
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
        bottomSheetDialog.dismissWithAnimation
        bottomSheetDialog.hide()
        bottomSheetDialog.dismiss()
        resetTabAnimatedButton()
        sessionDelegate?.sessionCancelled()

    }


}

