package com.suomifi.palvelutietovaranto.model.ptv.details

import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiLocalizedListItem

/**
 * Detailed information about a service.
 */
data class V7VmOpenApiService(
        /**
         * Entity identifier.
         */
        val id: String?,
        /**
         * List of localized service names. (Max.Length: 100).
         */
        val serviceNames: List<VmOpenApiLocalizedListItem>?,
        /**
         * List of localized service descriptions.
         */
        val serviceDescriptions: List<VmOpenApiLocalizedListItem>?,
        /**
         * List of linked service channels including relationship data.
         */
        val serviceChannels: List<V7VmOpenApiServiceServiceChannel>?
)
