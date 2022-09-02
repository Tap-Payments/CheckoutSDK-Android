package company.tap.checkout.internal.utils

import android.view.Gravity
import android.view.Gravity.BOTTOM
import android.view.ViewGroup
import androidx.constraintlayout.widget.Barrier.BOTTOM
import androidx.transition.*
import company.tap.checkout.internal.utils.AnimationEngine.Type.*

/**
 *
 * Created by Mario Gamal on 6/25/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
object AnimationEngine {
    var fadeIn = Fade(Visibility.MODE_OUT)
    var slideDown= Slide(Gravity.BOTTOM)

    fun applyTransition(sceneRoot: ViewGroup, type: Type, duration: Long? = null) {
        val transitionSet = when (type) {
            FADE -> fadeTransition
            SLIDE -> slideTransition
            FADE_IN ->fadeInTransition
            SLIDE_DOWN->slideDownTransition
        }
        duration?.let { transitionSet.setDuration(it) }
        TransitionManager.beginDelayedTransition(sceneRoot, transitionSet)
    }

    private val slideTransition = TransitionSet()
        .setOrdering(TransitionSet.ORDERING_SEQUENTIAL)
        .addTransition(Slide())
        .addTransition(ChangeBounds())
       // .addTransition(Fade())

    private val slideDownTransition = TransitionSet()
        .setOrdering(TransitionSet.ORDERING_TOGETHER)
        .addTransition(slideDown)
        .addTransition(ChangeBounds())
        //.addTransition(Fade())

    private val fadeInTransition = TransitionSet()
        .setOrdering(TransitionSet.ORDERING_SEQUENTIAL)
        .addTransition(fadeIn)
       //  .addTransition(ChangeBounds())
        .addTransition(slideDown)
    private val fadeTransition = TransitionSet()
        .setOrdering(TransitionSet.ORDERING_SEQUENTIAL)
        .addTransition(Fade())
        .addTransition(ChangeBounds())

    enum class Type {
        SLIDE,
        FADE,
        FADE_IN,
        SLIDE_DOWN
    }

}