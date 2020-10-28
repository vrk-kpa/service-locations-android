package com.suomifi.palvelutietovaranto.model.ptv

/** View Model of address with type */
data class VmOpenApiAddressStreetWithCoordinates(
        /** List of localized street addresses. (Max.Length: 100). */
        val street: List<VmOpenApiLanguageItem>?,
        /** Street number for street address. (Max.Length: 30). */
        val streetNumber: String?,
        /** Postal code, for example 00100. */
        val postalCode: String,
        /** List of localized Post offices, for example Helsinki, Helsingfors. */
        val postOffice: List<VmOpenApiLanguageItem>?,
        val municipality: VmOpenApiMunicipality?,
        /** Localized list of additional information about the address. (Max.Length: 150). */
        val additionalInformation: List<VmOpenApiLanguageItem>?,
        /** Location latitude coordinate. */
        val latitude: String?,
        /** Location longitude coordinate. */
        val longitude: String?,
        /**
         * State of coordinates. Coordinates are fetched from a service provided by
         * Maanmittauslaitos (WFS). Possible values are: Loading, Ok, Failed, NotReceived,
         * EmptyInputReceived, MultipleResultsReceived, WrongFormatReceived or EnteredByUser.
         */
        val coordinateState: String?
)
