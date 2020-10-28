package com.suomifi.palvelutietovaranto.model.ptv.v8

import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiLanguageItem

/** View Model of a accessibility sentence. */
data class VmOpenApiSentenceValue(
        /** List of localized sentences. */
        val sentence: List<VmOpenApiLanguageItem>?
)
