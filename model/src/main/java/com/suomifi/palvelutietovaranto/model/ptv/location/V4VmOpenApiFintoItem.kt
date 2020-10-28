package com.suomifi.palvelutietovaranto.model.ptv.location

import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiLanguageItem

/** View Model of finto item. */
data class V4VmOpenApiFintoItem(
        /** List of localized entity names. */
        val name: VmOpenApiLanguageItem?,
        /** Entity code. */
        val code: String?,
        /** Ontology term type. */
        val ontologyType: String?,
        /** Entity uri. */
        val uri: String?,
        /** Entity parent identifier. */
        val parentId: String?,
        /** Entity parent uri. */
        val parentUri: String?
)
