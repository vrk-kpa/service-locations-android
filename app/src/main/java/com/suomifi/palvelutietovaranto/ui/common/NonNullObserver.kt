package com.suomifi.palvelutietovaranto.ui.common

import android.arch.lifecycle.Observer

/**
 * [Observer] which ignores null values emitted in [onChanged].
 * @param action Action to be called when non-null value is emitted in [onChanged].
 */
class NonNullObserver<T>(
        private val action: (T) -> Unit
) : Observer<T> {
    override fun onChanged(item: T?) {
        item?.let(action)
    }
}
