@file:JvmName("Extensions")
@file:JvmMultifileClass

package com.suomifi.palvelutietovaranto.utils.extensions

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

fun <T> MutableLiveData<T>.observe(lifecycleOwner: LifecycleOwner, action: (T?) -> Unit) {
    observe(lifecycleOwner, Observer {
        action(it)
    })
}

fun <T> MutableLiveData<T>.observeNotNull(lifecycleOwner: LifecycleOwner, action: (T) -> Unit) {
    observe(lifecycleOwner) {
        it?.let(action)
    }
}
