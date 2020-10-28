package com.suomifi.palvelutietovaranto.model.ptv

/** View Model of post office box address */
data class VmOpenApiAddressPostOfficeBox(
        /** Post office box like PL 310 (Max.Length: 100). */
        val postOfficeBox: List<VmOpenApiLanguageItem>,
        /** Postal code, for example 00100. */
        val postalCode: String,
        /** List of localized Post offices, for example Helsinki, Helsingfors. */
        val postOffice: List<VmOpenApiLanguageItem>?,
        val municipality: VmOpenApiMunicipality?,
        /** Localized list of additional information about the address. (Max.Length: 150). */
        val additionalInformation: List<VmOpenApiLanguageItem>?
)
