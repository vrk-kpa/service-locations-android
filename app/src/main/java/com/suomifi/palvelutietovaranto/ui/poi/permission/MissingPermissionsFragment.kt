package com.suomifi.palvelutietovaranto.ui.poi.permission

import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.suomifi.palvelutietovaranto.R
import com.suomifi.palvelutietovaranto.ui.poi.PoiViewModel
import com.suomifi.palvelutietovaranto.ui.poi.common.ProgressViewHidden
import com.suomifi.palvelutietovaranto.ui.tutorial.PermissionsViewModel
import com.suomifi.palvelutietovaranto.utils.Constants.RequestCodes.PERMISSIONS_REQUEST_CODE
import com.suomifi.palvelutietovaranto.utils.extensions.hasPermissions
import com.suomifi.palvelutietovaranto.utils.extensions.requestPermissions
import com.suomifi.palvelutietovaranto.utils.extensions.startSystemSettings
import kotlinx.android.synthetic.main.fragment_missing_permission.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

class MissingPermissionsFragment : Fragment() {

    companion object {
        private const val ARGUMENT_PERMISSIONS = "ARGUMENT_PERMISSIONS"
        fun newInstance(permissions: Array<String>) = MissingPermissionsFragment().apply {
            arguments = Bundle().apply {
                putStringArray(ARGUMENT_PERMISSIONS, permissions)
            }
        }
    }

    private val poiViewModel: PoiViewModel by sharedViewModel()
    private val permissionsViewModel: PermissionsViewModel by sharedViewModel()
    private val sharedPreferences: SharedPreferences by inject()
    private val permissions by lazy {
        arguments!!.getStringArray(ARGUMENT_PERMISSIONS)
    }
    private val goToSettingsObserver = Observer<Void> {
        startSystemSettings()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        poiViewModel.navigationBarColor.value = R.color.colorPrimary
        poiViewModel.progressViewVisibility.value = ProgressViewHidden
        poiViewModel.fabMenuVisible.value = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_missing_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            requestPermissions(sharedPreferences, permissions)
        }
        button_grant_permissions.setOnClickListener {
            requestPermissions(sharedPreferences, permissions)
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        permissionsViewModel.goToSettings.observe(viewLifecycleOwner, goToSettingsObserver)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkIfPermissionsGranted(requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        checkIfPermissionsGranted(requestCode)
    }

    private fun checkIfPermissionsGranted(requestCode: Int) {
        context?.let { context ->
            if (requestCode == PERMISSIONS_REQUEST_CODE && context.hasPermissions(permissions)) {
                poiViewModel.permissionsGranted.call()
            }
        }
    }

}
