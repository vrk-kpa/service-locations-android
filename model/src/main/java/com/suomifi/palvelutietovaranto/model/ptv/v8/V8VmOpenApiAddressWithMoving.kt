package com.suomifi.palvelutietovaranto.model.ptv.v8

import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiAddressPostOfficeBox
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiAddressStreetWithCoordinates
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiLanguageItem
import com.suomifi.palvelutietovaranto.model.ptv.location.VmOpenApiAddressStreetWithOrder

/** View Model of address */
data class V8VmOpenApiAddressWithMoving(
        /** Address type, Location or Postal. */
        val type: String?,
        /** Address sub type, Single, Street, PostOfficeBox, Abroad or Multipoint. */
        val subType: String?,
        val postOfficeBoxAddress: VmOpenApiAddressPostOfficeBox?,
        val streetAddress: VmOpenApiAddressStreetWithCoordinates?,
        /** Localized list of foreign address information. */
        val locationAbroad: List<VmOpenApiLanguageItem>?,
        /** Moving address. Includes several street addresses. */
        val multipointLocation: List<VmOpenApiAddressStreetWithOrder>?,
        /** Entrances for an address. Includes accessibility sentences. */
        val entrances: List<VmOpenApiEntrance>?,
        /** Country code (ISO 3166-1 alpha-2), for example FI. */
        val country: String?
)