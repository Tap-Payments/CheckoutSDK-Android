package company.tap.checkout.utils

import android.view.ViewGroup
import androidx.transition.*
import company.tap.checkout.utils.AnimationEngine.Type.FADE
import company.tap.checkout.utils.AnimationEngine.Type.SLIDE

/**
 *
 * Created by Mario Gamal on 6/25/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
object AnimationEngine {

    fun applyTransition(sceneRoot: ViewGroup, type: Type, duration: Long? = null) {
        val transitionSet = when (type) {
            FADE -> fadeTransition
            SLIDE -> slideTransition
        }
        duration?.let { transitionSet.setDuration(it) }
        TransitionManager.beginDelayedTransition(sceneRoot, transitionSet)
    }

    private val slideTransition = TransitionSet()
        .setOrdering(TransitionSet.ORDERING_SEQUENTIAL)
        .addTransition(Fade())
        .addTransition(ChangeBounds())
        .addTransition(Slide())

    private val fadeTransition = TransitionSet()
        .setOrdering(TransitionSet.ORDERING_SEQUENTIAL)
        .addTransition(Fade())
        .addTransition(ChangeBounds())

    enum class Type {
        SLIDE,
        FADE
    }

}