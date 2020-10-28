package com.suomifi.palvelutietovaranto.ui.poi.map

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener
import com.suomifi.palvelutietovaranto.R
import com.suomifi.palvelutietovaranto.domain.SelectedPoiSupplier
import com.suomifi.palvelutietovaranto.domain.location.model.GeoLocation
import com.suomifi.palvelutietovaranto.domain.ptv.model.Poi
import com.suomifi.palvelutietovaranto.ui.common.NonNullObserver
import com.suomifi.palvelutietovaranto.ui.model.PoiTarget
import com.suomifi.palvelutietovaranto.ui.model.PoiTargetAction.DESELECTED
import com.suomifi.palvelutietovaranto.ui.model.PoiTargetAction.SELECTED
import com.suomifi.palvelutietovaranto.ui.poi.PoiViewModel
import com.suomifi.palvelutietovaranto.ui.poi.common.ProgressViewState
import com.suomifi.palvelutietovaranto.utils.Constants.GeoLocation.MINIMUM_MAP_ZOOM_LEVEL
import com.suomifi.palvelutietovaranto.utils.extensions.moveTo
import kotlinx.android.synthetic.main.fragment_map_poi.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class MapPoiFragment : Fragment(), OnMapReadyCallback, OnMapClickListener,
        OnCameraIdleListener, OnClusterItemClickListener<PoiClusterItem> {

    companion object {
        fun newInstance() = MapPoiFragment()
    }

    private val mapPoiViewModel: MapPoiViewModel by viewModel()
    private val poiViewModel: PoiViewModel by sharedViewModel()
    private val selectedPoiSupplier: SelectedPoiSupplier by inject()
    private var googleMap: GoogleMap? = null
    private var clusterManager: ClusterManager<PoiClusterItem>? = null
    private lateinit var supportMapFragment: SupportMapFragment
    private val deselectPoiObserver = Observer<Unit> {
        deselectPoi()
    }
    private val retryClickedObserver = Observer<Unit> {
        mapPoiViewModel.onRetryClicked()
    }
    private val progressViewStateObserver = NonNullObserver<ProgressViewState> { progressViewState ->
        Timber.d("progressViewStateObserver - new state: $progressViewState")
        poiViewModel.progressViewVisibility.value = progressViewState
    }
    private val poisToDisplayObserver = NonNullObserver<List<Poi>> { pois ->
        pois.forEach { poi ->
            displayPoi(poi)
        }
    }
    private val searchResultObserver = NonNullObserver<LatLng> { latLng ->
        moveMapTo(latLng)
    }
    private val userLocationObserver = NonNullObserver<GeoLocation> { userLocation ->
        Timber.d("userLocationObserver - userLocation: $userLocation")
        moveMapTo(userLocation.toLatLng())
    }
    private val showParkingMetersObserver = NonNullObserver<Boolean> { showParkingMeters ->
        Timber.d("showParkingMetersObserver - showParkingMeters: $showParkingMeters")
        this.showParkingMeters = showParkingMeters
        (clusterManager?.renderer as? PoiClusterRenderer)?.toggleParkingMetersVisibility()
    }

    private var showParkingMeters = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        poiViewModel.navigationBarColor.value = R.color.colorPrimary
        poiViewModel.fabMenuVisible.value = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map_poi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        supportMapFragment = childFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
        observeViewModel()
        view_my_location.setOnClickListener { mapPoiViewModel.onMyLocationClicked() }
    }

    private fun observeViewModel() {
        poiViewModel.retryClicked.observe(viewLifecycleOwner, retryClickedObserver)
        poiViewModel.searchResult.observe(viewLifecycleOwner, searchResultObserver)
        poiViewModel.showParkingMeters.observe(viewLifecycleOwner, showParkingMetersObserver)
        mapPoiViewModel.progressViewState.observe(viewLifecycleOwner, progressViewStateObserver)
        mapPoiViewModel.poisToDisplay.observe(viewLifecycleOwner, poisToDisplayObserver)
        mapPoiViewModel.userLocationLiveData.observe(viewLifecycleOwner, userLocationObserver)
        mapPoiViewModel.deselectPoiLiveData.observe(viewLifecycleOwner, deselectPoiObserver)
    }

    override fun onResume() {
        super.onResume()
        googleMap?.let {
            mapPoiViewModel.onViewReady()
        }
    }

    override fun onPause() {
        super.onPause()
        mapPoiViewModel.onDetach()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        // disable default toolbar displayed for a selected marker
        googleMap.uiSettings.isMapToolbarEnabled = false
        // disable 'my location' button
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        // show user location on the map
        googleMap.isMyLocationEnabled = true
        googleMap.setOnMapClickListener(this)
        googleMap.setOnCameraIdleListener(this)
        context?.let { context ->
            clusterManager = ClusterManager<PoiClusterItem>(context, googleMap).apply {
                renderer = PoiClusterRenderer(context, googleMap, this) {
                    showParkingMeters
                }
                setOnClusterItemClickListener(this@MapPoiFragment)
            }
            googleMap.setOnMarkerClickListener(clusterManager)
        }
        if (isResumed) {
            mapPoiViewModel.onViewReady()
        }
    }

    private fun moveMapTo(latLng: LatLng) = googleMap?.moveTo(latLng)

    private fun displayPoi(poi: Poi) {
        Timber.d("displayPoi($poi)")
        clusterManager?.let { clusterManager ->
            val poiClusterItem = PoiClusterItem(poi)
            clusterManager.addItem(poiClusterItem)
            clusterManager.cluster()
            selectedPoiSupplier.getSelectedPoi()?.let { selectedPoi ->
                if (selectedPoi == poi) {
                    setItemSelected(poiClusterItem, true)
                }
            }
        }
    }

    override fun onMapClick(latLng: LatLng?) {
        Timber.d("onMapClick(%s)", latLng)
        getSelectedItem()?.let { selectedItem ->
            setItemSelected(selectedItem, false)
            poiViewModel.onArTargetReceived(PoiTarget(selectedItem.poi, DESELECTED))
        }
    }

    override fun onCameraIdle() {
        Timber.d("onCameraIdle - zoom: ${googleMap?.cameraPosition?.zoom}")
        googleMap?.let { googleMap ->
            if (googleMap.cameraPosition.zoom > MINIMUM_MAP_ZOOM_LEVEL) {
                googleMap.projection?.visibleRegion?.latLngBounds?.let { latLngBounds ->
                    mapPoiViewModel.onMapVisibleRegionChanged(latLngBounds)
                }
            } else {
                mapPoiViewModel.onZoomedOut()
            }
        }
        clusterManager?.onCameraIdle()
    }

    override fun onClusterItemClick(clusterItem: PoiClusterItem): Boolean {
        val poi = clusterItem.poi
        Timber.d("onClusterItemClick - poi: $poi")
        if (clusterItem.selected) return true
        deselectPoi()
        setItemSelected(clusterItem, true)
        poiViewModel.onArTargetReceived(PoiTarget(poi, SELECTED))
        return true
    }

    private fun getSelectedItem() = clusterManager?.algorithm?.items?.find { it.selected }

    private fun setItemSelected(clusterItem: PoiClusterItem, selected: Boolean) {
        clusterManager?.renderer?.let { renderer ->
            clusterItem.selected = selected
            (renderer as PoiClusterRenderer).updateMarker(clusterItem)
        }
    }

    private fun deselectPoi() {
        Timber.d("deselectPoi")
        getSelectedItem()?.let {
            setItemSelected(it, false)
        }
    }

}
