package com.suomifi.palvelutietovaranto.data.search.entity

data class Location(
        val translations: Map<String, String>,
        val coordinates: Coordinates
)
