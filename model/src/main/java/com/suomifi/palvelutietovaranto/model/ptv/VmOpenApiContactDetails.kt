package com.suomifi.palvelutietovaranto.model.ptv

import com.suomifi.palvelutietovaranto.model.ptv.details.V4VmOpenApiPhone
import com.suomifi.palvelutietovaranto.model.ptv.details.V7VmOpenApiAddressContact

data class VmOpenApiContactDetails(
        /**
         * List of connection related email addresses.
         */
        val emails: List<VmOpenApiLanguageItem>?,
        /**
         * List of connection related phone numbers.
         */
        val phones: List<V4VmOpenApiPhone>?,
        /**
         * List of connection related web pages.
         */
        val webPages: List<VmOpenApiWebPageWithOrderNumber>?,
        /**
         * List of service location addresses.
         */
        val addresses: List<V7VmOpenApiAddressContact>?
)
