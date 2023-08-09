package company.tap.tapuilibrary.uikit.animation

import android.animation.*
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnRepeat
import company.tap.tapuilibrary.uikit.animation.MorphingAnimation.AnimationTarget.*
import company.tap.tapuilibrary.uikit.datasource.AnimationDataSource
import kotlinx.coroutines.delay

/**
 * Copyright © 2020 Tap Payments. All rights reserved.
 */
class MorphingAnimation(private val animatedView: View) {

    private var animationEndListener: OnAnimationEndListener? = null

    fun setAnimationEndListener(animationEndListener: OnAnimationEndListener) {
        this.animationEndListener = animationEndListener
        animatedView.clearAnimation()
        animatedView.isClickable = true
        animatedView.isEnabled = true
    }

    fun start(dataSource: AnimationDataSource, vararg targets: AnimationTarget) {
        val animators = ArrayList<Animator>()
        targets.forEach {
            when (it) {
                CORNERS -> {
                    if (dataSource.background != null && dataSource.fromCorners != null && dataSource.toCorners != null)
                        animators.add(
                            getCornerAnimation(
                                dataSource.background,
                                dataSource.fromCorners,
                                dataSource.toCorners
                            )
                        )
                }
                COLOR -> {
                    if (dataSource.background != null && dataSource.fromColor != null && dataSource.toColor != null)
                        animators.add(
                            getColorAnimation(
                                dataSource.background,
                                dataSource.fromColor,
                                dataSource.toColor
                            )
                        )
                }
                WIDTH -> {
                    if (dataSource.fromWidth != null && dataSource.toWidth != null)
                        animators.add(
                            getDimensionAnimation(
                                WIDTH,
                                dataSource.fromWidth,
                                dataSource.toWidth
                            )
                        )
                }
            }
        }

        val animatorSet = AnimatorSet()
        dataSource.duration?.let {
            animatorSet.duration = it.toLong()
        }
        animatorSet.playTogether(animators)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                animationEndListener?.onMorphAnimationEnd()
            }

            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                animationEndListener?.onMorphAnimationStarted()
            }

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
            }
        })

      /*  animatorSet.doOnEnd {
            animatorSet.reverse()
            animationEndListener?.onMorphAnimationEnd()
            animatorSet.removeAllListeners()

        }*/
        animatorSet.start()
    }

    fun end(dataSource: AnimationDataSource, vararg targets: AnimationTarget) {
        val animators = ArrayList<Animator>()
        targets.forEach {
            when (it) {
                CORNERS -> {
                    if (dataSource.background != null && dataSource.fromCorners != null && dataSource.toCorners != null)
                        animators.add(
                            getCornerAnimation(
                                dataSource.background,
                                dataSource.fromCorners,
                                dataSource.toCorners
                            )
                        )
                }
                COLOR -> {
                    if (dataSource.background != null && dataSource.fromColor != null && dataSource.toColor != null)
                        animators.add(
                            getColorAnimation(
                                dataSource.background,
                                dataSource.fromColor,
                                dataSource.toColor
                            )
                        )
                }
                WIDTH -> {
                    if (dataSource.fromWidth != null && dataSource.toWidth != null)
                        animators.add(
                            getDimensionAnimation(
                                WIDTH,
                                dataSource.fromWidth,
                                dataSource.toWidth
                            )
                        )
                }
            }
        }

        val animatorSet = AnimatorSet()
        dataSource.duration?.let {
            animatorSet.duration = it.toLong()
        }
        animatorSet.playSequentially(animators)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
                if(isReverse) {
                    animationEndListener?.onMorphAnimationReverted()

                }

            }

        })

        animatorSet.reverse()
    }
    private fun getCornerAnimation(background: GradientDrawable, from: Float, to: Float): Animator {
        return ObjectAnimator.ofFloat(background, "cornerRadius", from, to)
    }

    private fun getColorAnimation(
        background: GradientDrawable,
        @ColorInt from: Int,
        @ColorInt to: Int
    ): Animator {
        val bgColorAnimation = ObjectAnimator.ofInt(background, "color", from, to)
        bgColorAnimation?.setEvaluator(ArgbEvaluator())
        return bgColorAnimation
    }

    private fun getDimensionAnimation(dimension: AnimationTarget, from: Int, to: Int): Animator {
        val dimensionAnimation = ValueAnimator.ofInt(from, to)
        dimensionAnimation?.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = animatedView.layoutParams
            when (dimension) {
                HEIGHT -> layoutParams.height = animatedValue
                WIDTH -> layoutParams.width = animatedValue
                else -> return@addUpdateListener
            }
            animatedView.layoutParams = layoutParams
        }
        return dimensionAnimation
    }

    enum class AnimationTarget {
        HEIGHT,
        WIDTH,
        COLOR,
        CORNERS
    }

    interface OnAnimationEndListener {
        fun onMorphAnimationEnd()
        fun onMorphAnimationReverted()

        fun onMorphAnimationStarted()

    }
}