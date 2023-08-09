package company.tap.cardinputwidget.views

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import company.tap.cardinputwidget.CardBrand
import company.tap.cardinputwidget.CardBrandSingle
import company.tap.cardinputwidget.R
import company.tap.cardinputwidget.databinding.CardBrandViewBinding
import company.tap.tapuilibrary.themekit.ThemeManager

class CardBrandView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val viewBinding: CardBrandViewBinding = CardBrandViewBinding.inflate(
        LayoutInflater.from(context),
        this,true
    )
  //   val iconView = viewBinding.icon
      var iconView :ImageView

    private var animationApplied = false

    @ColorInt
    internal var tintColorInt: Int = 0

    internal var onScanClicked: () -> Unit = {}
    @DrawableRes
    val iconViewRes: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            R.drawable.card_icon_dark
        }else if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("light")) {
            R.drawable.card_icon_light
        }else  {
            R.drawable.card_icon_light
        }

    init {
        isFocusable = false

        iconView = findViewById(R.id.icon)
        iconView.setImageResource(iconViewRes)
        setScanClickListener()
    }

   internal fun showBrandIcon(brand: CardBrand, shouldShowErrorIcon: Boolean , iconUrl: String?=null) {
        iconView.setOnClickListener(null)
       if (shouldShowErrorIcon) {
            if(ThemeManager.currentTheme.contains("dark")){
                iconView.setImageResource(brand.errorIconDark)

            }else iconView.setImageResource(brand.errorIconLight)

        } else {
            println("brand val"+brand)
            println("iconUrl val"+iconUrl)
           if(iconUrl!=null)
               //Glide.with(context).load(iconUrl) .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(iconView)
          else if (animationApplied) {
                animationApplied = false
                animateImageChange(brand.icon)
            }
           else iconView.setImageResource(brand.icon)
            if (brand == CardBrand.Unknown) {
                applyTint(false)
                setScanClickListener()
            }
        }
    }

    internal fun showBrandIconSingle(brand: CardBrandSingle, shouldShowErrorIcon: Boolean) {
        iconView.setOnClickListener(null)
        if (shouldShowErrorIcon) {
            if(ThemeManager.currentTheme.contains("dark")){
                iconView.setImageResource(brand.errorIconDark)

            }else iconView.setImageResource(brand.errorIconLight)

        } else {
            if (animationApplied) {
                animationApplied = false
                animateImageChange(brand.icon)
            } else
                iconView.setImageResource(brand.icon)
            if (brand.name == CardBrand.Unknown.name) {
                applyTint(false)
                setScanClickListener()
            }
        }
    }

    internal fun showBrandIconSingle(brand: CardBrandSingle, iconUrl : String, shouldShowErrorIcon: Boolean) {
        iconView.setOnClickListener(null)
        println("shouldShowErrorIcon"+shouldShowErrorIcon)
        if (shouldShowErrorIcon) {
            if(ThemeManager.currentTheme.contains("dark")){
                iconView.setImageResource(brand.errorIconDark)

            }else iconView.setImageResource(brand.errorIconLight)
        } else {
            if (animationApplied) {
                animationApplied = false
                animateImageChange(brand.icon)
            } else
                //GlideToVectorYou.justLoadImage(context as Activity, iconUrl.toUri(),iconView)
                Glide.with(context).load(iconUrl) .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(iconView)
              //  iconView.setImageURI(brand.icon)
            if (brand.name == CardBrand.Unknown.name) {
                applyTint(false)
                setScanClickListener()
            }
        }
    }


    private fun setScanClickListener() {
        iconView?.setOnClickListener { onScanClicked() }
    }

    internal fun showCvcIcon(brand: CardBrand) {
        if (animationApplied) {
          return
        }

        if (brand == CardBrand.AmericanExpress) {
            if(ThemeManager.currentTheme.contains("dark")){
                iconView.setImageResource(brand.cvcIconDark)

            }else  iconView.setImageResource(brand.cvcIconLight)
            applyTint(false)
            return
        }else
            if(ThemeManager.currentTheme.contains("dark")){
            iconView.setImageResource(brand.cvcIconDark)
            animateImageChange(brand.cvcIconDark)

        }else    animateImageChange(brand.cvcIconLight)
        return

        animationApplied = true

        iconView.setOnClickListener(null)
    }

    private fun animateImageChange(cvcIcon: Int) {
        iconView.rotationY = 0f
        iconView.animate().setDuration(300).rotationY(90f)
            .setListener(object : Animator.AnimatorListener {

                override fun onAnimationEnd(animation: Animator?) {
                    iconView.setImageResource(cvcIcon)
                    iconView.rotationY = 270f
                    iconView.animate().rotationY(360f).setListener(null)
                    if (animationApplied)
                        applyTint(false)
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}

            })
    }

    internal fun applyTint(apply: Boolean) {
        if (!apply)
            return
        val icon = iconView.drawable
        val compatIcon = DrawableCompat.wrap(icon)
       // DrawableCompat.setTint(compatIcon.mutate(), tintColorInt)
        iconView.setImageDrawable(DrawableCompat.unwrap(compatIcon))
    }
}
