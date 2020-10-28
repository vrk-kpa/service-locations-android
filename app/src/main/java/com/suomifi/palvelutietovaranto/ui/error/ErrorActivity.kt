package com.suomifi.palvelutietovaranto.ui.error

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.suomifi.palvelutietovaranto.R
import com.suomifi.palvelutietovaranto.ui.common.BaseActivity
import kotlinx.android.synthetic.main.fragment_tutorial.*

class ErrorActivity : BaseActivity() {

    companion object {
        private const val EXTRA_ERROR_STRING_RES_ID = "EXTRA_ERROR_STRING_RES_ID"
        fun start(context: Context, errorStringResId: Int) {
            context.startActivity(Intent(context, ErrorActivity::class.java).apply {
                putExtra(EXTRA_ERROR_STRING_RES_ID, errorStringResId)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_tutorial)
        image_tutorial_logo.setImageResource(R.drawable.ic_emoticon_sad)
        text_tutorial_title.setText(R.string.error_title_sorry)
        intent?.getIntExtra(EXTRA_ERROR_STRING_RES_ID, 0)?.takeIf { it != 0 }?.let {
            text_tutorial_description.setText(it)
        }
    }

}
