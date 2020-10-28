package com.suomifi.palvelutietovaranto.model.ptv

/** View Model of municipality */
data class VmOpenApiMunicipality(
        /** Municipality code (like 491 or 091). */
        val code: String?,
        /** List of localized municipality names. */
        val name: List<VmOpenApiLanguageItem>?
)
