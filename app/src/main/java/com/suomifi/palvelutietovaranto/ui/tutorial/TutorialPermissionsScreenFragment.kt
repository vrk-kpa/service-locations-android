package com.suomifi.palvelutietovaranto.ui.tutorial

import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.suomifi.palvelutietovaranto.R
import com.suomifi.palvelutietovaranto.utils.Constants.Permissions.ALL_PERMISSIONS
import com.suomifi.palvelutietovaranto.utils.Constants.RequestCodes.PERMISSIONS_REQUEST_CODE
import com.suomifi.palvelutietovaranto.utils.extensions.hasRequiredPermissions
import com.suomifi.palvelutietovaranto.utils.extensions.requestPermissions
import com.suomifi.palvelutietovaranto.utils.extensions.startSystemSettings
import kotlinx.android.synthetic.main.fragment_tutorial.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

class TutorialPermissionsScreenFragment : TutorialScreenFragment() {

    companion object {
        fun newInstance() = TutorialPermissionsScreenFragment()
    }

    private val viewModel: PermissionsViewModel by sharedViewModel()
    private val sharedPreferences: SharedPreferences by inject()
    private val requestPermissionsObserver = Observer<Void> {
        if (hasRequiredPermissions()) {
            viewModel.permissionsGranted.call()
        } else {
            requestPermissions(sharedPreferences, ALL_PERMISSIONS)
        }
    }
    private val goToSettingsObserver = Observer<Void> {
        startSystemSettings()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image_tutorial_logo.setImageResource(R.drawable.ic_tutorial_permissions)
        text_tutorial_title.setText(R.string.tutorial_screen_2_title)
        text_tutorial_description.setText(R.string.tutorial_screen_2_desc)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.requestPermissions.observe(viewLifecycleOwner, requestPermissionsObserver)
        viewModel.goToSettings.observe(viewLifecycleOwner, goToSettingsObserver)
    }

    override fun canMoveFurther() = hasRequiredPermissions()

    override fun cantMoveFurtherErrorMessage(): String {
        requestPermissions(sharedPreferences, ALL_PERMISSIONS)
        return getString(R.string.tutorial_screen_2_error_permissions_required)
    }

    private fun hasRequiredPermissions() = context?.hasRequiredPermissions() ?: false

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkIfPermissionsGranted(requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        checkIfPermissionsGranted(requestCode)
    }

    private fun checkIfPermissionsGranted(requestCode: Int) {
        if (requestCode == PERMISSIONS_REQUEST_CODE && hasRequiredPermissions()) {
            viewModel.permissionsGranted.call()
        }
    }

}
