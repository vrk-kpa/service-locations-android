package com.suomifi.palvelutietovaranto.data.search

import android.content.Context
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.suomifi.palvelutietovaranto.data.search.entity.Locations
import java.io.InputStream
import java.io.InputStreamReader

object SearchLocationsParser {

    private const val XML_FILE_LOCATION = "search/locations.json"

    fun parseLocationsFile(context: Context): Locations {
        return context.assets.open(XML_FILE_LOCATION).use { inputStream ->
            parseLocations(inputStream)
        }
    }

    private fun parseLocations(inputStream: InputStream): Locations {
        return JsonReader(InputStreamReader(inputStream)).use { reader ->
            Gson().fromJson<Locations>(reader, Locations::class.java)
        }
    }

}
