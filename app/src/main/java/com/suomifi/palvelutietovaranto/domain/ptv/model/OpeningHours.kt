package com.suomifi.palvelutietovaranto.domain.ptv.model

import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

data class OpeningHours(
        val dayOfTheWeek: DayOfTheWeek,
        val from: String,
        val to: String
) {

    companion object {
        private val inputDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        private val outputDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    }

    private fun getFormattedDate(date: String) = try {
        outputDateFormat.format(inputDateFormat.parse(date))
    } catch (e: Exception) {
        Timber.e(e)
        from
    }

    fun getFormattedDateFrom(): String = getFormattedDate(from)
    fun getFormattedDateTo(): String = getFormattedDate(to)

}
