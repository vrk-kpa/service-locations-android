package com.suomifi.palvelutietovaranto.domain.localization

sealed class Language(
        val ptvLanguageCode: String,
        val parkingMeterString: String,
        val feedbackUrl: String
) {
    object Finnish : Language("FI", "pysäköinti", "https://www.suomi.fi/palaute")
    object Swedish : Language("SV", "parkering", "https://www.suomi.fi/respons")
    object English : Language("EN", "parking", "https://www.suomi.fi/feedback")

    companion object {
        val all = arrayOf(Finnish, Swedish, English)

        fun getLanguageByPtvCode(ptvLanguageCode: String) = all.find { language ->
            language.ptvLanguageCode.equals(ptvLanguageCode, true)
        }
    }
}
