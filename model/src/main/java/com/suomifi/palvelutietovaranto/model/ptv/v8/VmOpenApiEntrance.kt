package com.suomifi.palvelutietovaranto.model.ptv.v8

import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiLanguageItem

/** View Model of entrance for address. */
data class VmOpenApiEntrance(
        /** List of localized service names. */
        val name: List<VmOpenApiLanguageItem>?,
        /** Indicates if entrance is main entrance. */
        val isMainEntrance: Boolean?,
        val coordinates: VmOpenApiCoordinates?,
        /** List of accessibility sentences. */
        val accessibilitySentences: List<VmOpenApiAccessibilitySentence>?
)
