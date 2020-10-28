package com.suomifi.palvelutietovaranto.ui.tutorial

import agency.tango.materialintroscreen.SlideFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.suomifi.palvelutietovaranto.R
import kotlinx.android.synthetic.main.fragment_tutorial.*

open class TutorialScreenFragment : SlideFragment() {

    companion object {
        private const val ARGUMENT_IMAGE_RES_ID = "ARGUMENT_IMAGE_RES_ID"
        private const val ARGUMENT_TITLE_STRING_RES_ID = "ARGUMENT_TITLE_STRING_RES_ID"
        private const val ARGUMENT_DESC_STRING_RES_ID = "ARGUMENT_DESC_STRING_RES_ID"
        fun newInstance(imageResId: Int, titleStringResId: Int, descStringResId: Int) =
                TutorialScreenFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARGUMENT_IMAGE_RES_ID, imageResId)
                        putInt(ARGUMENT_TITLE_STRING_RES_ID, titleStringResId)
                        putInt(ARGUMENT_DESC_STRING_RES_ID, descStringResId)
                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { arguments ->
            image_tutorial_logo.setImageResource(arguments.getInt(ARGUMENT_IMAGE_RES_ID))
            text_tutorial_title.setText(arguments.getInt(ARGUMENT_TITLE_STRING_RES_ID))
            text_tutorial_description.setText(arguments.getInt(ARGUMENT_DESC_STRING_RES_ID))
        }
    }

    override fun backgroundColor() = R.color.colorPrimary
    override fun buttonsColor() = R.color.colorAccent
    override fun canMoveFurther() = true
    override fun cantMoveFurtherErrorMessage() = ""

}
