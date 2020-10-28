package com.suomifi.palvelutietovaranto.model.ptv

/** View Model of web page with order number */
data class VmOpenApiWebPageWithOrderNumber(
        /** The order of web pages. */
        val orderNumber: String,
        /** Web page url. (Max.Length: 500). */
        val url: String,
        /** Name of the web page. (Max.Length: 110). */
        val value: String?,
        /** Language code. */
        val language: String
)
