package com.suomifi.palvelutietovaranto.model.ptv.v8

import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiLanguageItem
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiWebPageWithOrderNumber
import com.suomifi.palvelutietovaranto.model.ptv.details.V4VmOpenApiPhone
import com.suomifi.palvelutietovaranto.model.ptv.details.V7VmOpenApiAddressContact

/** View Model of service location channel - base version. */
data class V8VmOpenApiContactDetails(
        /** List of connection related email addresses. */
        val emails: List<VmOpenApiLanguageItem>?,
        /** List of connection related phone numbers. */
        val phones: List<V4VmOpenApiPhone>,
        /** List of connection related web pages. */
        val webPages: List<VmOpenApiWebPageWithOrderNumber>?,
        /** List of service location addresses. */
        val addresses: List<V7VmOpenApiAddressContact>?
)
