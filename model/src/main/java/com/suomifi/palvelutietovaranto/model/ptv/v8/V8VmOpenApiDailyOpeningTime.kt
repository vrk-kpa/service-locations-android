package com.suomifi.palvelutietovaranto.model.ptv.v8

/** View Model of Daily opening hours. */
data class V8VmOpenApiDailyOpeningTime(
        /** Starts from weekday (e.g. Monday). */
        val dayFrom: String?,
        /** Ends to weekday (e.g. Monday). */
        val dayTo: String?,
        /** Start time for example 10:00. */
        val from: String,
        /** End time for example 20:00. */
        val to: String
)
