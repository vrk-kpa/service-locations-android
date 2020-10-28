package com.suomifi.palvelutietovaranto.domain.ptv.model

import java.util.*

enum class DayOfTheWeek(
        private val jsonName: String,
        private val calendarDay: Int
) {
    MONDAY("Monday", Calendar.MONDAY),
    TUESDAY("Tuesday", Calendar.TUESDAY),
    WEDNESDAY("Wednesday", Calendar.WEDNESDAY),
    THURSDAY("Thursday", Calendar.THURSDAY),
    FRIDAY("Friday", Calendar.FRIDAY),
    SATURDAY("Saturday", Calendar.SATURDAY),
    SUNDAY("Sunday", Calendar.SUNDAY);

    fun isCalendarDay(calendarDay: Int) = this.calendarDay == calendarDay

    companion object {
        fun getByJsonName(jsonName: String) = values().find { dayOfTheWeek ->
            dayOfTheWeek.jsonName == jsonName
        }
    }

}
