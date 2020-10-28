package com.suomifi.palvelutietovaranto.ui.common

import android.view.View
import com.github.reline.GoogleMapsBottomSheetBehavior.*
import timber.log.Timber

/**
 * Callback detecting when user manually hides the bottom sheet.
 */
class BottomSheetHiddenDetector(
        private val bottomSheetHiddenCallback: BottomSheetHiddenCallback
) : BottomSheetCallback() {

    private var wasDragged = false

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        // noop
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        Timber.d("onStateChanged - newState: %s", stateToString(newState))
        if ((newState == STATE_HIDDEN) && wasDragged) {
            Timber.d("onStateChanged - bottom sheet manually closed")
            bottomSheetHiddenCallback.onBottomSheetManuallyClosed()
        }
        if (newState != STATE_SETTLING) {
            wasDragged = (newState == STATE_DRAGGING)
        }
        bottomSheetHiddenCallback.onStateChanged(newState)
    }

    companion object {
        private fun stateToString(bottomSheetState: Int) = when (bottomSheetState) {
            STATE_DRAGGING -> "STATE_DRAGGING"
            STATE_SETTLING -> "STATE_SETTLING"
            STATE_EXPANDED -> "STATE_EXPANDED"
            STATE_COLLAPSED -> "STATE_COLLAPSED"
            STATE_HIDDEN -> "STATE_HIDDEN"
            STATE_ANCHORED -> "STATE_ANCHORED"
            else -> "unrecognized state"
        }
    }

}
