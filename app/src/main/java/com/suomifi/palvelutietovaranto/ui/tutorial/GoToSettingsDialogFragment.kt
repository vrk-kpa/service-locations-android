package com.suomifi.palvelutietovaranto.ui.tutorial

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.suomifi.palvelutietovaranto.R
import org.koin.android.viewmodel.ext.android.sharedViewModel

class GoToSettingsDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "GoToSettingsDialogFragment"
        fun newInstance() = GoToSettingsDialogFragment()
    }

    private val viewModel: PermissionsViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
                .setTitle(R.string.tutorial_settings_required_title)
                .setMessage(R.string.tutorial_settings_required_message)
                .setPositiveButton(R.string.tutorial_settings_required_go_to_settings) { _, _ ->
                    dismiss()
                    viewModel.goToSettings.call()
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    dismiss()
                }
                .setCancelable(false)
                .create()
    }

}
