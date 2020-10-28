package com.suomifi.palvelutietovaranto.model.ptv.location

import com.suomifi.palvelutietovaranto.model.ptv.V4VmOpenApiServiceHour
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiLanguageItem
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiLocalizedListItem
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiWebPageWithOrderNumber

/**
 * Location channel.
 */
data class V7VmOpenApiServiceLocationChannel(
        /**
         * PTV identifier for the service channel
         */
        val id: String?,
        /**
         * External system identifier for this service channel. User needs to be logged in to be able to get/set value
         */
        val sourceId: String?,
        /**
         * Type of the service channel. Channel types: EChannel, WebPage, PrintableForm, Phone or ServiceLocation
         */
        val serviceChannelType: String?,
        /**
         * PTV organization identifier responsible for the channel
         */
        val organizationId: String?,
        /**
         * Localized list of service channel names
         */
        val serviceChannelNames: List<VmOpenApiLocalizedListItem>?,
        /**
         * List of localized service channel descriptions. (Max.Length: 2500 Description)
         */
        val serviceChannelDescriptions: List<VmOpenApiLocalizedListItem>?,
        /**
         * Area type. Possible values are: WholeCountry, WholeCountryExceptAlandIslands, AreaType
         */
        val areaType: String?,
        /**
         * List of service channel areas
         */
        val areas: List<VmOpenApiArea>?,
        /**
         * List of phone numbers for the service channel. Includes also fax numbers
         */
        val phoneNumbers: List<V4VmOpenApiPhoneWithType>?,
        /**
         * List email addresses for the service channel. (Max.Length: 100)
         */
        val emails: List<VmOpenApiLanguageItem>?,
        /**
         * List of languages the service channel is available in (two letter language code).
         */
        val languages: List<String>?,
        /**
         * List of service channel web pages.
         */
        val webPages: List<VmOpenApiWebPageWithOrderNumber>?,
        /**
         * List of service location addresses.
         */
        val addresses: List<V7VmOpenApiAddressWithMoving>?,
        /**
         * List of service channel service hours.
         */
        val serviceHours: List<V4VmOpenApiServiceHour>?,
        /**
         * List of linked services including relationship data.
         */
        val services: List<VmOpenApiServiceChannelService>?,
        /**
         * Publishing status. Possible values are: Draft, Published, Deleted or Modified.
         */
        val publishingStatus: String,
        /**
         * Date when item was modified/created (UTC).
         */
        val modified: String?
)
