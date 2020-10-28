package com.suomifi.palvelutietovaranto.ui.error

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.suomifi.palvelutietovaranto.R
import com.suomifi.palvelutietovaranto.domain.localization.Language.English
import com.suomifi.palvelutietovaranto.ui.poi.PoiViewModel
import com.suomifi.palvelutietovaranto.ui.poi.common.ProgressViewHidden
import com.suomifi.palvelutietovaranto.utils.Constants.Ar.AR_FEATURES
import com.suomifi.palvelutietovaranto.utils.extensions.selectedLanguage
import com.wikitude.architect.ArchitectView
import kotlinx.android.synthetic.main.fragment_tutorial.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ErrorFragment : Fragment() {

    companion object {
        fun newInstance() = ErrorFragment()
    }

    private val poiViewModel: PoiViewModel by sharedViewModel()
    private val sharedPreferences: SharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        poiViewModel.navigationBarColor.value = R.color.colorPrimary
        poiViewModel.progressViewVisibility.value = ProgressViewHidden
        poiViewModel.fabMenuVisible.value = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image_tutorial_logo.setImageResource(R.drawable.ic_emoticon_sad)
        text_tutorial_title.setText(R.string.error_title_sorry)
        text_tutorial_description.text = getDescription()
    }

    private fun getDescription(): String {
        val unsupportedDevice = getString(R.string.error_unsupported_device)
        return getMessageDetails()?.let { messageDetails ->
            getString(R.string.error_device_not_supported_details, unsupportedDevice, messageDetails)
        } ?: unsupportedDevice
    }

    private fun getMessageDetails(): String? {
        if (sharedPreferences.selectedLanguage() != English) return null
        return context?.let { context ->
            ArchitectView.isDeviceSupported(context, AR_FEATURES).missingFeatureMessage
        }
    }

}