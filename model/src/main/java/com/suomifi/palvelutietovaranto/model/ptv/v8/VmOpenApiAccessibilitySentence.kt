package com.suomifi.palvelutietovaranto.model.ptv.v8

import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiLanguageItem

/** View Model of accessibility sentences. */
data class VmOpenApiAccessibilitySentence(
        /** List of localized sentence group names. */
        val sentenceGroup: List<VmOpenApiLanguageItem>?,
        /** List of localized sentences. */
        val sentences: List<VmOpenApiSentenceValue>?
)
