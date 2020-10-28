package com.suomifi.palvelutietovaranto.model.ptv.location

import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiLanguageItem

/** View Model of extra types. */
data class VmOpenApiExtraType(
        /** Type of the area (Asti). */
        val type: String?,
        /**
         * Code of the extra type. In Asti case the code can be DocumentReceived, GuidanceToOnlineSelfService,
         * LostAndFound, OnlineSelfServicePoint, OnsiteGuidance, OnsiteGuidanceByServiceAuthor or RemoteGuidance.
         */
        val code: String?,
        /** List of localized extra type descriptions. */
        val description: List<VmOpenApiLanguageItem>?
)
