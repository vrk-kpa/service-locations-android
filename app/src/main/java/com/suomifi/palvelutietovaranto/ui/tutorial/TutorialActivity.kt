package com.suomifi.palvelutietovaranto.ui.tutorial

import agency.tango.materialintroscreen.MaterialIntroActivity
import agency.tango.materialintroscreen.MessageButtonBehaviour
import agency.tango.materialintroscreen.widgets.SwipeableViewPager
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View.OnClickListener
import com.suomifi.palvelutietovaranto.R
import com.suomifi.palvelutietovaranto.ui.poi.PoiActivity
import com.suomifi.palvelutietovaranto.utils.LanguageChanger
import com.suomifi.palvelutietovaranto.utils.extensions.selectedLanguage
import com.suomifi.palvelutietovaranto.utils.extensions.setTutorialSeen
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class TutorialActivity : MaterialIntroActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, TutorialActivity::class.java))
        }
    }

    private lateinit var pager: SwipeableViewPager
    private val viewModel: PermissionsViewModel by viewModel()
    private val sharedPreferences: SharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pager = findViewById(agency.tango.materialintroscreen.R.id.swipeable_view_pager)
        viewModel.permissionsGranted.observe(this, Observer {
            // HACK: ugly hack because of the way material-intro-screen library is working
            pager.post {
                pager.moveToNextPage()
            }
        })
        addSlides()
    }

    private fun addSlides() {
        addSlide(TutorialScreenFragment.newInstance(
                R.drawable.ic_tutorial_logo,
                R.string.tutorial_screen_1_title,
                R.string.tutorial_screen_1_desc))
        addSlide(TutorialPermissionsScreenFragment.newInstance(),
                MessageButtonBehaviour(OnClickListener {
                    viewModel.requestPermissions.call()
                }, getString(R.string.tutorial_screen_2_grant_permissions)))
        addSlide(TutorialScreenFragment.newInstance(
                R.drawable.ic_phone_rotate_landscape,
                R.string.tutorial_screen_3_title,
                R.string.tutorial_screen_3_desc))
    }

    override fun onFinish() {
        sharedPreferences.setTutorialSeen()
        PoiActivity.start(this)
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(newBase)
        super.attachBaseContext(LanguageChanger.onAttach(newBase, Locale(prefs.selectedLanguage().ptvLanguageCode)))
    }

}
