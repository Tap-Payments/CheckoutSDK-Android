package company.tap.tapuilibrary.uikit.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.core.view.setMargins
import com.bumptech.glide.Glide
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.fontskit.enums.TapFont
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.animation.MorphingAnimation
import company.tap.tapuilibrary.uikit.animation.MorphingAnimation.AnimationTarget.*
import company.tap.tapuilibrary.uikit.datasource.ActionButtonDataSource
import company.tap.tapuilibrary.uikit.datasource.AnimationDataSource
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
import company.tap.tapuilibrary.uikit.enums.ActionButtonState.*
import company.tap.tapuilibrary.uikit.interfaces.TapActionButtonInterface
import company.tap.tapuilibrary.uikit.ktx.setImage


/**
 *
 * Created on 6/24/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TabAnimatedActionButton : CardView, MorphingAnimation.OnAnimationEndListener,
    TapLoadingView.OnProgressCompletedListener {

    private lateinit var morphingAnimation: MorphingAnimation
    private lateinit var state: ActionButtonState
    private var dataSource: ActionButtonDataSource? = null
    private var backgroundDrawable: GradientDrawable = GradientDrawable()
    private var actionButtonInterface: TapActionButtonInterface? = null
    private var displayMetrics: Int? = null
    private var tapLoadingView: TapLoadingView? = null
    private val textView by lazy {TextView(context)  }
    private var counter=0
   private lateinit var animationDataSource :AnimationDataSource

    @DrawableRes
    val loaderGif: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")){
            R.drawable.loader_black
        }  else if (ThemeManager.currentTheme.isNotEmpty() && !ThemeManager.currentTheme.contains("dark")){
            R.drawable.loader
        }else    R.drawable.loader

    @DrawableRes
    val loaderSuccessGif: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")){
            R.drawable.success_black
        }  else if (ThemeManager.currentTheme.isNotEmpty() && !ThemeManager.currentTheme.contains("dark")){
            R.drawable.success_white
        }else    R.drawable.success_white

    @DrawableRes
    val loaderErrorGif: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")){
            R.drawable.error_gif_black
        }  else if (ThemeManager.currentTheme.isNotEmpty() && !ThemeManager.currentTheme.contains("dark")){
            R.drawable.error_gif_white
        }else    R.drawable.error_gif_white
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        morphingAnimation = MorphingAnimation(this)
        morphingAnimation.setAnimationEndListener(this)
        initActionButtonDataSourceValid()
        this@TabAnimatedActionButton.isClickable = true
        this@TabAnimatedActionButton.isEnabled = true

    }

    private fun initActionButtonDataSourceValid(backgroundColor: Int? = null, textColor:Int? = null, buttonText: String? = null,backgroundColorArray: IntArray?=null ){
        val btnText:String
        val _textColor:Int
        val btnBackground:Int
        if(buttonText ==null){
            if(LocalizationManager.currentLocalized.length()!=0)
            btnText = LocalizationManager.getValue("pay", "ActionButton")
            else btnText = context.getString(R.string.payText)
        }else {
            btnText =buttonText
        }
        if(textColor ==null){
            if(ThemeManager.currentTheme.isNotEmpty())
            _textColor = Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
            else _textColor = R.color.ValidBtntitleLabelColor
        }else {
            _textColor =textColor
        }
        if(backgroundColor ==null){
            if(ThemeManager.currentTheme.isNotEmpty())
            btnBackground = Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor"))
          //  else btnBackground = Color.parseColor(R.color.ValidBtnColor.toString())
            else btnBackground = R.color.ValidBtnColor
        }else {
            btnBackground =backgroundColor
        }
        dataSource = ActionButtonDataSource(
            text = btnText ,
            textSize = 16f,
            textColor = _textColor,
            cornerRadius = 100f,
            successImageResources = loaderSuccessGif,
            errorImageResources = loaderErrorGif,
            backgroundColor = btnBackground,
            backgroundArrayInt = backgroundColorArray
        )

    }

    private fun initActionButtonDataSourceInValid(backgroundColor: Int? = null, textColor:Int? = null, buttonText: String? = null ){
        val btnText:String
        val _textColor:Int
        val btnBackground:Int
        if(buttonText ==null){
            if(LocalizationManager.currentLocalized.length()!=0)
            btnText = LocalizationManager.getValue("pay", "ActionButton")
            else btnText = context.getString(R.string.payText)
        }else {
            btnText =buttonText
        }
        if(textColor ==null){
            if(ThemeManager.currentTheme.isNotEmpty())
            _textColor = Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
           else _textColor = R.color.ValidBtntitleLabelColor
        }else {
            _textColor =textColor
        }
        if(backgroundColor ==null){
            if(ThemeManager.currentTheme.isNotEmpty())
            btnBackground = Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor"))
            else btnBackground = R.color.InvalidBtnColor
        }else {
            btnBackground =backgroundColor
        }
        dataSource = ActionButtonDataSource(
            text = btnText ,
            textSize = 16f,
            textColor = _textColor,
            cornerRadius = 100f,
            successImageResources = loaderSuccessGif,
            errorImageResources = loaderErrorGif,
            backgroundColor = btnBackground
        )

    }


    /**
     * public setter for action button interface
     *
     * @param actionButtonInterface
     */
    fun setButtonInterface(actionButtonInterface: TapActionButtonInterface) {
        this.actionButtonInterface = actionButtonInterface
    }
    fun setDisplayMetrics(displayMetrics: Int) {
        this.displayMetrics = displayMetrics
    }
    fun setButtonDataSource(isValid: Boolean = false,lang : String? = null, buttonText: String?= null, backgroundColor: Int, textColor:Int? = null , backgroundColorArray: IntArray?=null) {
        if (isValid)
        {
            initValidBackground(backgroundColor,backgroundColorArray)
            initActionButtonDataSourceValid(backgroundColor, textColor,buttonText,backgroundColorArray)
        } else{
            initInvalidBackground(backgroundColor)
            initActionButtonDataSourceInValid(backgroundColor, textColor, buttonText)
        }
        removeAllViews()
        addView(getTextView(lang?: "en"))
    }

    fun setInValidBackground(isValid: Boolean =false,backgroundColor: Int){
       dataSource?.backgroundColor = backgroundColor
        backgroundDrawable.color = ColorStateList.valueOf(backgroundColor)

        elevation = 0F

    }

    fun addTapLoadingView() {
        tapLoadingView = TapLoadingView(context, null)
        tapLoadingView?.setOnProgressCompleteListener(this)
        addChildView(tapLoadingView!!)
    }

    fun changeButtonState(state: ActionButtonState,loopCount:Int=0) {
        this.state = state
        when (state) {
            SUCCESS -> {
                addTapLoadingView()
                startStateAnimation()
                addChildView(getImageView(loaderSuccessGif,loopCount) {})
            }
            ERROR -> {
                addTapLoadingView()
                startStateAnimation()
                addChildView(getImageView(loaderErrorGif,loopCount) {})
            }
            LOADING ->{
                addTapLoadingView()
                startStateAnimation()
                addChildView(getImageView(loaderGif,loopCount) {
                    morphingAnimation.end(animationDataSource, WIDTH, HEIGHT, CORNERS)

                })
            }
            IDLE -> {
                removeAllViews()
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    120
                )
                params.setMargins(60, 40,60,60)
                params.gravity = Gravity.CENTER
                this.layoutParams = params
                init()
             }
            RESET -> {
                removeView(getImageView(loaderSuccessGif,0) {})
                removeView(getImageView(loaderErrorGif,0) {})
                addChildView(getTextView(LocalizationManager.getLocale(context).language))
                if(displayMetrics == DisplayMetrics.DENSITY_450 ||displayMetrics == DisplayMetrics.DENSITY_420 ||displayMetrics == DisplayMetrics.DENSITY_400||displayMetrics == DisplayMetrics.DENSITY_440||displayMetrics == DisplayMetrics.DENSITY_XXHIGH||displayMetrics == DisplayMetrics.DENSITY_560)
                {

                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        100
                    )
                    params.setMargins(60, 40,60,60)
                    params.gravity = Gravity.CENTER
                    this.layoutParams = params
                    init()



                }else if (displayMetrics == DisplayMetrics.DENSITY_280||displayMetrics == DisplayMetrics.DENSITY_260||displayMetrics == DisplayMetrics.DENSITY_300||displayMetrics == DisplayMetrics.DENSITY_XHIGH || displayMetrics == DisplayMetrics.DENSITY_340||displayMetrics == DisplayMetrics.DENSITY_360){

                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        80
                    )
                    params.setMargins(50, 40,50,40)
                    params.gravity = Gravity.CENTER
                    this.layoutParams = params
                    init()


                } else if(displayMetrics == DisplayMetrics.DENSITY_560|| displayMetrics == DisplayMetrics.DENSITY_600)
                {

                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        130
                    )
                    params.setMargins(60, 40,60,60)
                    params.gravity = Gravity.CENTER
                    this.layoutParams = params
                    init()


                }
                morphingAnimation.setAnimationEndListener(this)
                this.isClickable = true
                this.isEnabled = true
            }
            else ->{
                morphingAnimation.setAnimationEndListener(this)
                init()
            }

        }
    }




    /**
     * setup the initValidBackground background drawable color and corner radius from datasource
     */

    private fun initValidBackground(backgroundColor: Int ,backgroundColorArray: IntArray?=null) {

        if(backgroundColorArray!=null){
            backgroundDrawable.colors = backgroundColorArray
            backgroundDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
           backgroundDrawable= GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, backgroundColorArray,
                )
        }else {
            dataSource?.cornerRadius?.let {
                backgroundDrawable.cornerRadius = it
            }
            backgroundDrawable.color =
                ColorStateList.valueOf(backgroundColor) ?: ColorStateList.valueOf(
                    Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor"))
                )
            background = backgroundDrawable
            elevation = 0F
        }
    }

    /**
     * setup the initInvalidBackground background drawable color and corner radius from datasource
     */
    private fun initInvalidBackground(backgroundColor: Int? = null) {
        dataSource?.cornerRadius?.let {
            backgroundDrawable.cornerRadius = it
        }
        backgroundDrawable.color = ColorStateList.valueOf(Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")))
       // backgroundDrawable.color = backgroundColor?.let { ColorStateList.valueOf(it) } ?: ColorStateList.valueOf(Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")))

        background = backgroundDrawable
        elevation = 0F
    }

    private fun getTextView(lang : String): TextView {
        if (lang == "en") setFontEnglish(textView)
        else setFontArabic(textView)


        dataSource?.text?.let {
            textView.text = it
        }
        dataSource?.textSize?.let {
            textView.textSize = it
        }
        dataSource?.textColor?.let {
            textView.setTextColor(it)
        }
        textView.gravity = Gravity.CENTER
        return textView
    }

    private fun setFontEnglish(textView:TextView ){
        textView.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
    }

    private fun setFontArabic(textView:TextView){
        textView.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
    }

    fun getImageView(@DrawableRes imageRes: Int, gifLoopCount: Int,  actionAfterAnimationDone: ()-> Unit): ImageView {
        val image = ImageView(context)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        params.setMargins(20)
        image.layoutParams = params

        return image.setImage(context,image,imageRes,gifLoopCount, actionAfterAnimationDone)
    }


    fun getImageViewUrl( imageRes: String): ImageView {
        val image = ImageView(context)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        params.setMargins(20)
        image.layoutParams = params

        Glide.with(context)
            .load(imageRes)
            .into(image)
        return image
    }
    private fun startStateAnimation() {

        if(displayMetrics == DisplayMetrics.DENSITY_450 ||displayMetrics == DisplayMetrics.DENSITY_420 ||displayMetrics == DisplayMetrics.DENSITY_400||displayMetrics == DisplayMetrics.DENSITY_440||displayMetrics == DisplayMetrics.DENSITY_XXHIGH||displayMetrics == DisplayMetrics.DENSITY_560){
            animationDataSource =
                AnimationDataSource(
                    fromHeight = height,
                    toHeight = 70,
                    fromWidth = width,
                    toWidth = 70+40,
                    fromCorners = dataSource?.cornerRadius,
                    toCorners = MAX_CORNERS,
                    fromColor = dataSource?.backgroundColor,
                    toColor = dataSource?.errorColor,
                    duration = MAX_DURATION,
                    background = backgroundDrawable
                )
        }else if (displayMetrics == DisplayMetrics.DENSITY_260||displayMetrics == DisplayMetrics.DENSITY_280||displayMetrics == DisplayMetrics.DENSITY_300||displayMetrics == DisplayMetrics.DENSITY_XHIGH || displayMetrics == DisplayMetrics.DENSITY_340||displayMetrics == DisplayMetrics.DENSITY_360) {
            animationDataSource =
                AnimationDataSource(
                    fromHeight = height,
                    toHeight = 40,
                    fromWidth = width,
                    toWidth = 40 + 40,
                    fromCorners = dataSource?.cornerRadius,
                    toCorners = MAX_CORNERS,
                    fromColor = dataSource?.backgroundColor,
                    toColor = dataSource?.errorColor,
                    duration = MAX_DURATION,
                    background = backgroundDrawable
                )
        }else if( displayMetrics == DisplayMetrics.DENSITY_560|| displayMetrics == DisplayMetrics.DENSITY_600){
            animationDataSource =
                AnimationDataSource(
                    fromHeight = height,
                    toHeight = 100,
                    fromWidth = width,
                    toWidth = 100 + 40,
                    fromCorners = dataSource?.cornerRadius,
                    toCorners = MAX_CORNERS,
                    fromColor = dataSource?.backgroundColor,
                    toColor = dataSource?.errorColor,
                    duration = MAX_DURATION,
                    background = backgroundDrawable
                )

        }




                morphingAnimation.start(animationDataSource, WIDTH, HEIGHT, CORNERS)
    }



    /**
     * accept any view to be added inside the action button
     *
     * @param view the child view
     */
    fun addChildView(view: View) {
//        AnimationEngine.applyTransition(this)
        removeAllViews()
        addView(view)
    }

    override fun onMorphAnimationEnd() {
        tapLoadingView?.completeProgress()
    }

    override fun onMorphAnimationReverted() {
        println("onMorphAnimationReverted is called"+counter)
        counter += 1
      //  if(counter<=1) {
            when (state) {
                ERROR -> {
                    /* dataSource?.errorImageResources?.let {
                    addChildView(getImageView(it,1) {})
                }
                dataSource?.errorColor?.let {
//                    AnimationEngine.applyTransition(this)
                    backgroundDrawable.color = ColorStateList.valueOf(it)
                }*/
                    morphingAnimation.setAnimationEndListener(this)
                    this@TabAnimatedActionButton.isEnabled = true
                    this@TabAnimatedActionButton.isClickable = true
                    clearAnimation()
                    changeButtonState(RESET)

                    //   morphingAnimation.end(animationDataSource, WIDTH, HEIGHT, CORNERS)
                }
                SUCCESS ->{
                    morphingAnimation.setAnimationEndListener(this)
                    this@TabAnimatedActionButton.isEnabled = true
                    this@TabAnimatedActionButton.isClickable = true
                    clearAnimation()
                    changeButtonState(RESET)
                } /*dataSource?.successImageResources?.let {

                    addChildView(getImageView(it, 1) {})
                }*/

                else -> {
                    morphingAnimation.setAnimationEndListener(this)
                    this@TabAnimatedActionButton.isEnabled = true
                    this@TabAnimatedActionButton.isClickable = true
                    clearAnimation()
                    changeButtonState(RESET)

                }

            }
       /* } else {
            morphingAnimation.setAnimationEndListener(this)
            this@TabAnimatedActionButton.isEnabled = true
            changeButtonState(RESET)
           // init()
        }*/
        clearAnimation()
        clearFocus()
        cleanupLayoutState( getImageView(loaderErrorGif,0){})
        cleanupLayoutState(  getTextView(LocalizationManager.getLocale(context).language))
        cleanupLayoutState( getImageView(loaderSuccessGif,0) {})

        getImageView(loaderErrorGif,0) {}.clearAnimation();//This Line Added
        getImageView(loaderSuccessGif,0) {}.clearAnimation();//This Line Added
        getTextView(LocalizationManager.getLocale(context).language).clearAnimation();//This Line Added
    }

    override fun onProgressCompleted() {
        when (state) {
            ERROR -> {
                dataSource?.errorImageResources?.let {
                    addChildView(getImageView(it,1) {})
                }
                dataSource?.errorColor?.let {
//                    AnimationEngine.applyTransition(this)
                    backgroundDrawable.color = ColorStateList.valueOf(it)
                }
               // initActionButtonDataSource()
                morphingAnimation.end(animationDataSource, WIDTH, HEIGHT, CORNERS)
            }
            SUCCESS -> dataSource?.successImageResources?.let {
                addChildView(getImageView(it,1) {})
                morphingAnimation.end(animationDataSource, WIDTH, HEIGHT, CORNERS)
            }

            else ->  morphingAnimation.end(animationDataSource, WIDTH, HEIGHT, CORNERS)
        }
    }
    /**
     * Constants values
     */
    companion object {
        const val MAX_CORNERS = 100f
        const val MAX_RADIUS = 40
        const val MAX_DURATION = 2000
    }
}