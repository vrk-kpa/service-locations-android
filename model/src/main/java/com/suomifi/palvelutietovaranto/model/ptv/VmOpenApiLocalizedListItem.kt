package com.suomifi.palvelutietovaranto.model.ptv

/** View Model of localized list item. */
data class VmOpenApiLocalizedListItem(
        /** Language code. */
        val language: String,
        /** Value of the item. */
        val value: String,
        /**
         * Type of the item. For example: Name, AlternativeName (in versions 7 and down
         * AlternateName), Description, Summary (in versions 7 and down ShortDescription).
         */
        val type: String
)
