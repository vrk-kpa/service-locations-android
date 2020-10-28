package com.suomifi.palvelutietovaranto.model.ptv.location

import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiLanguageItem
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiMunicipality

/** View Model of Area */
data class VmOpenApiArea(
        /**
         * Type of the area. Possible values are: Municipality, Region, BusinessSubRegion, HospitalDistrict.
         * In version 7 and older: Municipality, Province, BusinessRegions, HospitalRegions.
         */
        val type: String?,
        /** Code of the area. */
        val code: String?,
        /** Localized list of names for the area. */
        val name: List<VmOpenApiLanguageItem>?,
        /** List of municipalities including municipality code and a localized list of municipality names. */
        val municipalities: List<VmOpenApiMunicipality>?
)
