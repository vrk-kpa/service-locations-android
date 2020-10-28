package com.suomifi.palvelutietovaranto.model.ptv

import com.suomifi.palvelutietovaranto.model.ptv.details.V2VmOpenApiDailyOpeningTime

data class V4VmOpenApiServiceHour(
        /**
         * Type of service hour. Valid values are: Standard, Exception or Special.
         */
        val serviceHourType: String,
        /**
         * Date time where from this entry is valid.
         */
        val validFrom: String?,
        /**
         * Date time to this entry is valid.
         */
        val validTo: String?,
        /**
         * Set to true to present a time between the valid from and to times as closed.
         */
        val isClosed: Boolean?,
        /**
         * Set to true to present that this entry is valid for now.
         */
        val validForNow: Boolean?,
        /**
         * Localized list of additional information. (Max.Length: 100).
         */
        val additionalInformation: List<VmOpenApiLanguageItem>?,
        /**
         * Gets or sets the opening hour.
         */
        val openingHour: List<V2VmOpenApiDailyOpeningTime>?
)
