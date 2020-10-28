package com.suomifi.palvelutietovaranto.ui.poi.ar

import android.arch.lifecycle.Observer
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.suomifi.palvelutietovaranto.R
import com.suomifi.palvelutietovaranto.domain.SelectedPoiSupplier
import com.suomifi.palvelutietovaranto.domain.location.model.GeoLocation
import com.suomifi.palvelutietovaranto.domain.ptv.model.Poi
import com.suomifi.palvelutietovaranto.ui.common.NonNullObserver
import com.suomifi.palvelutietovaranto.ui.model.PoiTarget
import com.suomifi.palvelutietovaranto.ui.poi.PoiViewModel
import com.suomifi.palvelutietovaranto.ui.poi.common.ProgressViewState
import com.suomifi.palvelutietovaranto.utils.Constants.Ar.AR_FEATURES
import com.wikitude.architect.ArchitectJavaScriptInterfaceListener
import com.wikitude.architect.ArchitectStartupConfiguration
import com.wikitude.common.camera.CameraSettings
import kotlinx.android.synthetic.main.fragment_ar_poi.*
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.IOException

class ArPoiFragment : Fragment(), ArchitectJavaScriptInterfaceListener {

    companion object {
        fun newInstance() = ArPoiFragment()
    }

    private val arPoiViewModel: ArPoiViewModel by viewModel()
    private val poiViewModel: PoiViewModel by sharedViewModel()
    private val gson: Gson by inject()
    private val selectedPoiSupplier: SelectedPoiSupplier by inject()
    private val deselectPoiObserver = Observer<Unit> {
        deselectPoi()
    }
    private val retryClickedObserver = Observer<Unit> {
        arPoiViewModel.onRetryClicked()
    }
    private val userLocationObserver = NonNullObserver<GeoLocation> { userLocation ->
        Timber.d("userLocationObserver - new value: $userLocation")
        architectView.setLocation(userLocation.latitude, userLocation.longitude, userLocation.altitude, userLocation.accuracy)
    }
    private val poiToDisplayObserver = NonNullObserver<Poi> { poi ->
        Timber.d("poiToDisplayObserver - new value: $poi")
        architectView.callJavascript("World.addPoi(${poi.toJsonObject()})")
        selectedPoiSupplier.getSelectedPoi()?.let { selectedPoi ->
            if (poi == selectedPoi) {
                architectView.callJavascript("World.selectMarker(${poi.idToJsonObject()})")
            }
        }
    }
    private val poiToRemoveObserver = NonNullObserver<Set<Poi>> { pois ->
        pois.forEach { poi ->
            Timber.d("poiToRemoveObserver - removing POI with ID: ${poi.id}")
            architectView.callJavascript("World.removePoi(${JSONObject().apply {
                put("id", poi.id)
            }})")
        }
    }
    private val progressViewStateObserver = NonNullObserver<ProgressViewState> { progressViewState ->
        poiViewModel.progressViewVisibility.value = progressViewState
    }
    private val showParkingMetersObserver = NonNullObserver<Boolean> { showParkingMeters ->
        Timber.d("showParkingMetersObserver - new value: $showParkingMeters")
        this.showParkingMeters = showParkingMeters
        architectView.callJavascript("World.toggleParkingMetersVisibility($showParkingMeters)")
    }
    private var showParkingMeters = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        poiViewModel.navigationBarColor.value = android.R.color.transparent
        poiViewModel.fabMenuVisible.value = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ar_poi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enableFullscreen(view)
        initArchitectView()
        observeViewModel()
    }

    private fun observeViewModel() {
        poiViewModel.retryClicked.observe(viewLifecycleOwner, retryClickedObserver)
        poiViewModel.showParkingMeters.observe(viewLifecycleOwner, showParkingMetersObserver)
        arPoiViewModel.userLocation.observe(viewLifecycleOwner, userLocationObserver)
        arPoiViewModel.poiToDisplay.observe(viewLifecycleOwner, poiToDisplayObserver)
        arPoiViewModel.poisToRemove.observe(viewLifecycleOwner, poiToRemoveObserver)
        arPoiViewModel.progressViewState.observe(viewLifecycleOwner, progressViewStateObserver)
        arPoiViewModel.deselectPoiLiveData.observe(viewLifecycleOwner, deselectPoiObserver)
    }

    private fun enableFullscreen(view: View) {
        view.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

    private fun initArchitectView() {
        Timber.d("initArchitectView()")
        architectView.onCreate(createArConfig())
        // The ArchitectJavaScriptInterfaceListener has to be added to the Architect view after ArchitectView.onCreate.
        architectView.addArchitectJavaScriptInterfaceListener(this)
        architectView.onPostCreate()
        try {
            architectView.load("image-recognition/index.html")
        } catch (exception: IOException) {
            Timber.e(exception)
            Toast.makeText(context, R.string.error_loading_ar_experience, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createArConfig() = ArchitectStartupConfiguration().apply {
        licenseKey = getString(R.string.wikitude_license_key)
        // The default camera is the first camera available for the system.
        cameraPosition = CameraSettings.CameraPosition.BACK
        // The camera2 api is disabled by default (old camera api is used).
        isCamera2Enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
        // This tells the ArchitectView which AR-features it is going to use, the default is all of them.
        features = AR_FEATURES
    }

    override fun onResume() {
        super.onResume()
        // mandatory ArchitectView lifecycle call
        architectView.onResume()
        architectView.callJavascript("World.toggleParkingMetersVisibility($showParkingMeters)")
        arPoiViewModel.onAttach()
    }

    override fun onPause() {
        super.onPause()
        arPoiViewModel.onDetach()
        // mandatory ArchitectView lifecycle call
        architectView.onPause()
    }

    override fun onDestroyView() {
        architectView.callJavascript("World.destroyAll()")
        // The ArchitectJavaScriptInterfaceListener has to be removed from the Architect view before ArchitectView.onDestroy.
        architectView.removeArchitectJavaScriptInterfaceListener(this)
        /*
         * Deletes all cached files of this instance of the ArchitectView.
         * This guarantees that internal storage for this instance of the ArchitectView
         * is cleaned and app-memory does not grow each session.
         *
         * This should be called before architectView.onDestroy
         */
        architectView.clearCache()
        // mandatory ArchitectView lifecycle call
        architectView.onDestroy()
        super.onDestroyView()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        // optional ArchitectView lifecycle call
        architectView.onLowMemory()
    }

    override fun onJSONObjectReceived(jsonObject: JSONObject?) {
        jsonObject?.let {
            poiViewModel.onArTargetReceived(gson.fromJson(it.toString(), PoiTarget::class.java))
        } ?: Timber.w("onJSONObjectReceived - jsonObject is null!")
    }

    private fun deselectPoi() {
        Timber.d("deselectPoi")
        architectView.callJavascript("World.deselectPoi()")
    }

}
