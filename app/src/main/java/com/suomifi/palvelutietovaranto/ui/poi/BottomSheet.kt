package com.suomifi.palvelutietovaranto.ui.poi

import android.graphics.Typeface
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.reline.GoogleMapsBottomSheetBehavior
import com.suomifi.palvelutietovaranto.R
import com.suomifi.palvelutietovaranto.domain.localization.Language
import com.suomifi.palvelutietovaranto.domain.ptv.model.DayOfTheWeek
import com.suomifi.palvelutietovaranto.domain.ptv.model.DayOfTheWeek.*
import com.suomifi.palvelutietovaranto.domain.ptv.model.PoiDetails
import com.suomifi.palvelutietovaranto.ui.common.BottomSheetHiddenCallback
import com.suomifi.palvelutietovaranto.ui.common.BottomSheetHiddenDetector
import java.util.*

class BottomSheet(
        private val bottomSheetView: View,
        private val timeBetween: String,
        private val closed: String
) {

    private val sheetBehavior = GoogleMapsBottomSheetBehavior.from(bottomSheetView)

    private val layoutHeader by lazy { sheetBehavior.headerLayout as ViewGroup }
    private val textName by lazy { layoutHeader.findViewById<TextView>(R.id.text_name) }
    private val layoutActionDirections by lazy { layoutHeader.findViewById<View>(R.id.layout_action_directions) }
    private val layoutActionPhone by lazy { layoutHeader.findViewById<View>(R.id.layout_action_phone) }
    private val layoutActionEmail by lazy { layoutHeader.findViewById<View>(R.id.layout_action_email) }
    private val layoutActionWeb by lazy { layoutHeader.findViewById<View>(R.id.layout_action_web) }
    private val imageMoreChevron by lazy { layoutHeader.findViewById<ImageView>(R.id.image_more_chevron) }

    private val layoutContent by lazy { sheetBehavior.contentLayout as ViewGroup }
    private val layoutMoreLocation by lazy { layoutContent.findViewById<View>(R.id.layout_more_location) }
    private val layoutMorePhone by lazy { layoutContent.findViewById<View>(R.id.layout_more_phone) }
    private val layoutMoreEmail by lazy { layoutContent.findViewById<View>(R.id.layout_more_email) }
    private val layoutMoreWeb by lazy { layoutContent.findViewById<View>(R.id.layout_more_web) }
    private val layoutMoreOpeningHours by lazy { layoutContent.findViewById<View>(R.id.layout_more_opening_hours) }
    private val layoutMoreAccessibility by lazy { layoutContent.findViewById<View>(R.id.layout_more_accessibility) }
    private val layoutMoreAccessibilityInformation by lazy { layoutContent.findViewById<View>(R.id.layout_more_accessibility_information) }
    private val textMoreLocation by lazy { layoutContent.findViewById<TextView>(R.id.text_more_location) }
    private val textMorePhone by lazy { layoutContent.findViewById<TextView>(R.id.text_more_phone) }
    private val textMoreEmail by lazy { layoutContent.findViewById<TextView>(R.id.text_more_email) }
    private val textMoreWeb by lazy { layoutContent.findViewById<TextView>(R.id.text_more_web) }
    private val textDescription by lazy { layoutContent.findViewById<TextView>(R.id.text_description) }
    private val textMonday by lazy { layoutMoreOpeningHours.findViewById<TextView>(R.id.text_more_opening_hours_monday) }
    private val textTuesday by lazy { layoutMoreOpeningHours.findViewById<TextView>(R.id.text_more_opening_hours_tuesday) }
    private val textWednesday by lazy { layoutMoreOpeningHours.findViewById<TextView>(R.id.text_more_opening_hours_wednesday) }
    private val textThursday by lazy { layoutMoreOpeningHours.findViewById<TextView>(R.id.text_more_opening_hours_thursday) }
    private val textFriday by lazy { layoutMoreOpeningHours.findViewById<TextView>(R.id.text_more_opening_hours_friday) }
    private val textSaturday by lazy { layoutMoreOpeningHours.findViewById<TextView>(R.id.text_more_opening_hours_saturday) }
    private val textSunday by lazy { layoutMoreOpeningHours.findViewById<TextView>(R.id.text_more_opening_hours_sunday) }
    private val textMondayLabel by lazy { layoutMoreOpeningHours.findViewById<TextView>(R.id.text_more_opening_hours_monday_label) }
    private val textTuesdayLabel by lazy { layoutMoreOpeningHours.findViewById<TextView>(R.id.text_more_opening_hours_tuesday_label) }
    private val textWednesdayLabel by lazy { layoutMoreOpeningHours.findViewById<TextView>(R.id.text_more_opening_hours_wednesday_label) }
    private val textThursdayLabel by lazy { layoutMoreOpeningHours.findViewById<TextView>(R.id.text_more_opening_hours_thursday_label) }
    private val textFridayLabel by lazy { layoutMoreOpeningHours.findViewById<TextView>(R.id.text_more_opening_hours_friday_label) }
    private val textSaturdayLabel by lazy { layoutMoreOpeningHours.findViewById<TextView>(R.id.text_more_opening_hours_saturday_label) }
    private val textSundayLabel by lazy { layoutMoreOpeningHours.findViewById<TextView>(R.id.text_more_opening_hours_sunday_label) }

    private val textMoreAccessibility by lazy { layoutMoreAccessibility.findViewById<TextView>(R.id.text_more_accessibility) }
    private val imageAccessibilityChevron by lazy { layoutMoreAccessibility.findViewById<ImageView>(R.id.image_accessibility_chevron) }

    private val daysOfTheWeekTextMap by lazy {
        mapOf(
                MONDAY to Pair(textMonday, textMondayLabel),
                TUESDAY to Pair(textTuesday, textTuesdayLabel),
                WEDNESDAY to Pair(textWednesday, textWednesdayLabel),
                THURSDAY to Pair(textThursday, textThursdayLabel),
                FRIDAY to Pair(textFriday, textFridayLabel),
                SATURDAY to Pair(textSaturday, textSaturdayLabel),
                SUNDAY to Pair(textSunday, textSundayLabel)
        )
    }

    fun init(anchorEnabled: Boolean,
             bottomSheetHiddenCallback: BottomSheetHiddenCallback,
             moreClicked: OnClickListener,
             phoneClicked: OnClickListener,
             emailClicked: OnClickListener,
             webClicked: OnClickListener,
             directionsClicked: OnClickListener,
             locationClicked: OnClickListener,
             accessibilityClicked: OnClickListener) {
        sheetBehavior.setBottomSheetCallback(BottomSheetHiddenDetector(bottomSheetHiddenCallback))
        if (anchorEnabled) {
            bottomSheetView.viewTreeObserver.addOnGlobalLayoutListener {
                val parent = bottomSheetView.parent as View
                val anchorMaxHeight = parent.height - parent.width * 9 / 16
                sheetBehavior.anchorHeight = Math.min(bottomSheetView.height, anchorMaxHeight)
            }
        } else {
            sheetBehavior.anchorHeight = 0
        }

        layoutHeader.findViewById<View>(R.id.layout_more).setOnClickListener(moreClicked)
        layoutActionPhone.setOnClickListener(phoneClicked)
        layoutActionEmail.setOnClickListener(emailClicked)
        layoutActionWeb.setOnClickListener(webClicked)
        layoutActionDirections.setOnClickListener(directionsClicked)
        val headerView = layoutHeader.findViewById<View>(R.id.view_peek)
        headerView.viewTreeObserver.addOnGlobalLayoutListener {
            sheetBehavior.peekHeight = headerView.height
        }

        layoutMorePhone.setOnClickListener(phoneClicked)
        layoutMoreEmail.setOnClickListener(emailClicked)
        layoutMoreWeb.setOnClickListener(webClicked)
        layoutMoreLocation.setOnClickListener(locationClicked)
        layoutMoreAccessibilityInformation.setOnClickListener(accessibilityClicked)
    }

    fun setState(state: Int) {
        sheetBehavior.state = state
    }

    fun setAccessibilityVisibility(visible: Boolean) {
        if (visible) {
            textMoreAccessibility.visibility = View.VISIBLE
            imageAccessibilityChevron.setImageResource(R.drawable.ic_chevron_down)
        } else {
            textMoreAccessibility.visibility = View.GONE
            imageAccessibilityChevron.setImageResource(R.drawable.ic_chevron_right)
        }
    }

    fun displayPoiDetails(poiDetails: PoiDetails, language: Language) {
        textName.text = poiDetails.name(language)
        setDetailsText(textMoreLocation, poiDetails.address(language), layoutActionDirections, layoutMoreLocation)
        setDetailsText(textMorePhone, poiDetails.phone(language), layoutActionPhone, layoutMorePhone)
        setDetailsText(textMoreEmail, poiDetails.email(language), layoutActionEmail, layoutMoreEmail)
        setDetailsText(textMoreWeb, poiDetails.webPage(language), layoutActionWeb, layoutMoreWeb)
        setDetailsText(textDescription, poiDetails.description(language), textDescription)
        setOpeningHoursText(poiDetails)
        setAccessibilityInformation(poiDetails, language)
    }

    private fun setDetailsText(textView: TextView, text: String?, vararg viewsToHide: View) {
        textView.text = text
        val visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
        viewsToHide.forEach { it.visibility = visibility }
    }

    private fun setOpeningHoursText(poiDetails: PoiDetails) {
        poiDetails.openingHours?.let { openingHours ->
            layoutMoreOpeningHours.visibility = View.VISIBLE
            DayOfTheWeek.values().forEach { dayOfTheWeek ->
                daysOfTheWeekTextMap.getValue(dayOfTheWeek).let { pair ->
                    val text = pair.first
                    val label = pair.second
                    text.text = openingHours.find {
                        it.dayOfTheWeek == dayOfTheWeek
                    }?.let { it ->
                        String.format(timeBetween, it.getFormattedDateFrom(), it.getFormattedDateTo())
                    } ?: closed
                    val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                    val typeface = if (dayOfTheWeek.isCalendarDay(today)) {
                        Typeface.BOLD
                    } else {
                        Typeface.NORMAL
                    }
                    text.setTypeface(null, typeface)
                    label.setTypeface(null, typeface)
                }
            }
        } ?: run {
            layoutMoreOpeningHours.visibility = View.GONE
        }
    }

    private fun setAccessibilityInformation(poiDetails: PoiDetails, selectedLanguage: Language) {
        val accessibilityInformation = poiDetails.accessibilityInformation(selectedLanguage)
        if (accessibilityInformation.isNullOrEmpty()) {
            layoutMoreAccessibility.visibility = View.GONE
        } else {
            layoutMoreAccessibility.visibility = View.VISIBLE
            textMoreAccessibility.text = accessibilityInformation
        }
    }

    fun displayChevronDown() {
        imageMoreChevron.setImageResource(R.drawable.ic_chevron_down)
    }

    fun displayChevronRight() {
        imageMoreChevron.setImageResource(R.drawable.ic_chevron_right)
    }

}
