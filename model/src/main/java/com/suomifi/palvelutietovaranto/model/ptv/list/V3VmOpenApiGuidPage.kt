package com.suomifi.palvelutietovaranto.model.ptv.list

import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiItem

/**
 * A list of service ids with paging.
 */
data class V3VmOpenApiGuidPage(
        /**
         * Page number. Page numbering starts from one.
         */
        val pageNumber: Int?,
        /**
         * How many results per page are returned.
         */
        val pageSize: Int?,
        /**
         * Total count of pages.
         */
        val pageCount: Int?,
        /**
         * List of entity Guids.
         */
        val itemList: List<VmOpenApiItem>?
)
