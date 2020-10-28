package com.suomifi.palvelutietovaranto.model.ptv.details

data class V2VmOpenApiDailyOpeningTime(
        /**
         * Starts from weekday (e.g. Monday).
         */
        val dayFrom: String?,
        /**
         * Ends to weekday (e.g. Monday).
         */
        val dayTo: String?,
        /**
         * Start time for example 10:00.
         */
        val from: String,
        /**
         *  End time for example 20:00.
         */
        val to: String,
        /**
         * Set to true to have extra time for a day. Enables to have open times like 10:00-14:00
         * and also on the same day 16:00-20:00. So the latter time is extra time.
         */
        val isExtra: Boolean?
)