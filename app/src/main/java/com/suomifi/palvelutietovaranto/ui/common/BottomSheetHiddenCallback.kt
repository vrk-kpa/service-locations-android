package com.suomifi.palvelutietovaranto.ui.common

/**
 * Callback to be notified when user has manually closed the bottom sheet component.
 */
interface BottomSheetHiddenCallback {

    /**
     * Called when bottom sheet was manually closed by the user.
     */
    fun onBottomSheetManuallyClosed()

    /**
     * Called when state of the bottom sheet has changed.
     * @param newState New state of the bottom sheet.
     */
    fun onStateChanged(newState: Int)

}
