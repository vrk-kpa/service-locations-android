package com.suomifi.palvelutietovaranto.ui.poi

import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.databinding.DataBindingUtil
import android.location.LocationManager
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.view.View.*
import com.github.reline.GoogleMapsBottomSheetBehavior.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest.Builder
import com.google.android.gms.location.LocationSettingsStatusCodes.RESOLUTION_REQUIRED
import com.suomifi.palvelutietovaranto.R
import com.suomifi.palvelutietovaranto.databinding.ActivityPoiBinding
import com.suomifi.palvelutietovaranto.domain.ptv.model.PoiDetails
import com.suomifi.palvelutietovaranto.ui.common.BaseActivity
import com.suomifi.palvelutietovaranto.ui.common.BottomSheetHiddenCallback
import com.suomifi.palvelutietovaranto.ui.error.ErrorFragment
import com.suomifi.palvelutietovaranto.ui.poi.ar.ArPoiFragment
import com.suomifi.palvelutietovaranto.ui.poi.common.*
import com.suomifi.palvelutietovaranto.ui.poi.map.MapPoiFragment
import com.suomifi.palvelutietovaranto.ui.poi.permission.MissingPermissionsFragment
import com.suomifi.palvelutietovaranto.ui.search.LocationSearchActivity
import com.suomifi.palvelutietovaranto.ui.search.LocationSearchActivity.Companion.LOCATION_SEARCH_RESULT
import com.suomifi.palvelutietovaranto.ui.search.LocationSearchResult
import com.suomifi.palvelutietovaranto.ui.settings.SettingsActivity
import com.suomifi.palvelutietovaranto.utils.Constants.Ar.AR_FEATURES
import com.suomifi.palvelutietovaranto.utils.Constants.Permissions.AR_PERMISSIONS
import com.suomifi.palvelutietovaranto.utils.Constants.Permissions.MAP_PERMISSIONS
import com.suomifi.palvelutietovaranto.utils.Constants.RequestCodes.ENABLE_LOCATION_REQUEST_CODE
import com.suomifi.palvelutietovaranto.utils.Constants.RequestCodes.LOCATION_SEARCH_REQUEST_CODE
import com.suomifi.palvelutietovaranto.utils.extensions.*
import com.suomifi.palvelutietovaranto.utils.ignoreExceptions
import com.wikitude.architect.ArchitectView
import kotlinx.android.synthetic.main.layout_fab_menu.*
import kotlinx.android.synthetic.main.view_bottom_sheet.*
import kotlinx.android.synthetic.main.view_poi_list.*
import kotlinx.android.synthetic.main.view_progress.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class PoiActivity : BaseActivity(), BottomSheetHiddenCallback {

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }

        fun start(context: Context) {
            context.startActivity(Intent(context, PoiActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

    private val poiViewModel: PoiViewModel by viewModel()
    private val locationManager: LocationManager by inject()

    private lateinit var bottomSheet: BottomSheet

    private var locationDialogShown = false

    private val anchorEnabled by lazy { resources.configuration.orientation == ORIENTATION_PORTRAIT }
    private val adapter = PoisAdapter(object : PoiItemClickedCallback {
        override fun onPoiClicked(poiDetails: PoiDetails) {
            poiViewModel.onPoiSelected(poiDetails)
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (DataBindingUtil.setContentView(this, R.layout.activity_poi) as ActivityPoiBinding).apply {
            viewModel = this@PoiActivity.poiViewModel
        }
        initBottomSheet()
        initPoiList()
        initFragment()
        observeViewModel()
        initFab()
    }

    private fun initFab() {
        fab_menu.setClosedOnTouchOutside(true)
        fab_menu.menuIconView.setImageResource(R.drawable.ic_filter)
        fab_menu.addIconAnimation(R.drawable.ic_filter, R.drawable.ic_close)
    }

    private fun observeViewModel() {
        poiViewModel.progressViewVisibility.observeNotNull(this) { state ->
            setProgressViewVisibility(state)
        }
        poiViewModel.displayedPoi.observeNotNull(this) { poiDetails ->
            displayPoiDetails(poiDetails)
        }
        poiViewModel.permissionsGranted.observe(this) {
            initFragment()
        }
        poiViewModel.navigationBarColor.observeNotNull(this) { color ->
            setNavigationBarColor(color)
        }
        poiViewModel.settingsClicked.observe(this) {
            fab_menu.close(false)
            SettingsActivity.start(this)
        }
        poiViewModel.searchClicked.observe(this) {
            fab_menu.close(false)
            startLocationSearch()
        }
        poiViewModel.openEmailEvent.observeNotNull(this) { email ->
            sendEmail(email)
        }
        poiViewModel.bottomSheetState.observeNotNull(this) { state ->
            bottomSheet.setState(state)
            toggleChevronState(state)
        }
        poiViewModel.closeScreenEvent.observe(this) {
            finish()
        }
        poiViewModel.callPhoneEvent.observeNotNull(this) { phoneNr ->
            callPhoneNr(phoneNr)
        }
        poiViewModel.openWebPageEvent.observeNotNull(this) { webPage ->
            openWebPage(webPage)
        }
        poiViewModel.displayedPois.observe(this) { selectedPois ->
            adapter.submitList(selectedPois ?: emptyList())
        }
        poiViewModel.accessibilityInformationVisibility.observeNotNull(this) { visible ->
            bottomSheet.setAccessibilityVisibility(visible)
        }
        poiViewModel.showParkingMeters.observeNotNull(this) { showParkingMeters ->
            changeParkingMetersLabel(showParkingMeters)
        }
        poiViewModel.fabMenuVisible.observeNotNull(this) { visible ->
            if (visible) {
                fab_menu.showMenu(false)
            } else {
                fab_menu.hideMenu(false)
            }
        }
    }

    private fun changeParkingMetersLabel(showParkingMeters: Boolean) {
        fab_menu_button_parking_meters?.labelText = getString(if (showParkingMeters) {
            R.string.fab_button_hide_parking_meters
        } else {
            R.string.fab_button_show_parking_meters
        })
    }

    private fun startLocationSearch() {
        startActivityForResult(Intent(this, LocationSearchActivity::class.java), LOCATION_SEARCH_REQUEST_CODE)
    }

    private fun initBottomSheet() {
        bottomSheet = BottomSheet(bottom_sheet,
                getString(R.string.bottom_sheet_time_between),
                getString(R.string.bottom_sheet_closed))
        bottomSheet.init(anchorEnabled, this,
                moreClicked = OnClickListener { poiViewModel.onBottomSheetMoreClicked(anchorEnabled) },
                phoneClicked = OnClickListener { poiViewModel.onPhoneClicked(selectedLanguage()) },
                emailClicked = OnClickListener { poiViewModel.onEmailClicked(selectedLanguage()) },
                webClicked = OnClickListener { poiViewModel.onWebClicked(selectedLanguage()) },
                directionsClicked = OnClickListener { poiViewModel.onDirectionsClicked() },
                locationClicked = OnClickListener { poiViewModel.onLocationClicked() },
                accessibilityClicked = OnClickListener { poiViewModel.onAccessibilityInformationClicked() }
        )
        // HACK: rotating the screen when bottom sheet is in anchor state doesn't work...
        if (poiViewModel.bottomSheetState.value == STATE_ANCHORED) {
            poiViewModel.bottomSheetState.value = STATE_COLLAPSED
        }
    }

    private fun initPoiList() {
        recycler_pois.adapter = adapter
        recycler_pois.addItemDecoration(DividerItemDecoration(this, VERTICAL))
    }

    private fun initFragment() {
        val fragment = if (isPortrait()) {
            if (hasMapPermissions()) {
                MapPoiFragment.newInstance()
            } else {
                MissingPermissionsFragment.newInstance(MAP_PERMISSIONS)
            }
        } else {
            if (!isARSupported()) {
                ErrorFragment.newInstance()
            } else if (hasArPermissions()) {
                ArPoiFragment.newInstance()
            } else {
                MissingPermissionsFragment.newInstance(AR_PERMISSIONS)
            }
        }
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment, "tag")
                .commitNow()
    }

    private fun isARSupported(): Boolean {
        return !ArchitectView.isDeviceSupported(this, AR_FEATURES).areFeaturesMissing()
    }

    private fun setNavigationBarColor(color: Int) {
        if (SDK_INT >= LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(this, color)
        }
    }

    private fun displayPoiDetails(poiDetails: PoiDetails) {
        Timber.d("displayPoiDetails(%s)", poiDetails.id)
        bottomSheet.displayPoiDetails(poiDetails, selectedLanguage())
    }

    private fun callPhoneNr(phoneNr: String) {
        safeStartActivity(Intent(ACTION_DIAL, Uri.parse("tel:$phoneNr")))
    }

    private fun sendEmail(email: String) {
        safeStartActivity(Intent(ACTION_SENDTO, Uri.parse("mailto:$email")))
    }

    private fun openWebPage(webPage: String) {
        safeStartActivity(Intent(ACTION_VIEW, Uri.parse(webPage)))
    }

    override fun onBottomSheetManuallyClosed() {
        poiViewModel.onPoiDetailsManuallyClosed()
    }

    override fun onStateChanged(newState: Int) {
        if (newState != STATE_EXPANDED) {
            bottom_sheet.scrollTo(0, 0)
        }
        poiViewModel.bottomSheetState.value = newState
    }

    private fun toggleChevronState(state: Int) {
        if (state == STATE_DRAGGING || state == STATE_SETTLING) return
        if (state == STATE_COLLAPSED || (!isPortrait() && state == STATE_ANCHORED)) {
            bottomSheet.displayChevronRight()
        } else {
            bottomSheet.displayChevronDown()
        }
    }

    override fun onBackPressed() {
        when {
            fab_menu.isOpened -> fab_menu.close(true)
            else -> poiViewModel.onBackPressed(anchorEnabled)
        }
    }

    private fun setProgressViewVisibility(progressViewState: ProgressViewState) {
        Timber.d("setProgressViewVisibility($progressViewState)")
        when (progressViewState) {
            ProgressViewHidden -> view_progress.visibility = INVISIBLE
            ProgressViewLoading -> {
                view_progress.visibility = VISIBLE
                progress_bar.visibility = VISIBLE
                button_retry.visibility = GONE
                text_progress.setText(R.string.loading_service_locations)
            }
            ProgressViewPoiLoadingError -> {
                displayStatusViewError(R.string.loading_service_locations_error)
            }
            ProgressViewLocationError -> {
                displayStatusViewError(R.string.loading_service_locations_location_error)
                if (!locationManager.isAnyLocationProviderEnabled()) {
                    displayEnableLocationDialog()
                }
            }
            ProgressViewZoomedOutError -> {
                view_progress.visibility = VISIBLE
                progress_bar.visibility = GONE
                button_retry.visibility = GONE
                text_progress.setText(R.string.loading_service_locations_zoomed_out_error)
            }
        }
    }

    private fun displayStatusViewError(@StringRes stringResId: Int) {
        view_progress.visibility = VISIBLE
        progress_bar.visibility = GONE
        button_retry.visibility = VISIBLE
        text_progress.setText(stringResId)
    }

    private fun displayEnableLocationDialog() {
        if (locationDialogShown) return
        locationDialogShown = true
        LocationServices.getSettingsClient(this).checkLocationSettings(
                Builder().addLocationRequest(LocationRequest.create()).build()
        ).addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                if (exception.statusCode == RESOLUTION_REQUIRED) {
                    ignoreExceptions {
                        (exception as ResolvableApiException).startResolutionForResult(this, ENABLE_LOCATION_REQUEST_CODE)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LOCATION_SEARCH_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    data?.getParcelableExtra<LocationSearchResult>(LOCATION_SEARCH_RESULT)?.let { result ->
                        poiViewModel.searchResult.value = result.latLng
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
