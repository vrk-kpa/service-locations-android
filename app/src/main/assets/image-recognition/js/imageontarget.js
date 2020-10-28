var World = {
	loaded: false,

    // different POI-Marker assets
    markerDrawable_idle: null,
    markerDrawable_selected: null,
    markerDrawable_directionIndicator: null,

    // list of AR.GeoObjects that are currently shown in the scene / World
    markerList: [],

    // The last selected marker
    currentMarker: null,

    lastActionTime: Date.now(),

    showParkingMeters: false,

	init: function initFn() {
	    // The maximum distance at which objects are visible in the scene, in meters.
	    // If an object's distance to the user is further than the culling distance, the object will not be visible in the scene.
	    // Must be a positive whole number.
        // Default: 50000
	    AR.context.scene.cullingDistance = 2500000
	    // The amount of scaling that is applied to all drawables in the scene.
	    // Use this value to alter the drawable size on different devices so that the world appears in a common look when running on different devices.
        // Must be in the range of [-1, MAX_FLOAT].
        // Setting the global scale to 0 will scale all drawables to a size of 0.
        // Setting the global scale to -1 indicates that the SDK should calculate the value by itself, so that all drawables are equal in pixel size among different devices.
        // Default: 1.0
	    AR.context.scene.globalScale = -1
	    // The distance, in meters, at which objects will keep their size on the screen even when the user moves further away.
	    // If the user is further than maxScalingDistance, the object will not appear any smaller than the size it took on at maxScalingDistance.
	    // Must be a positive whole number.
        // Default: 20000
	    AR.context.scene.maxScalingDistance = 300
	    // The amount of scaling that is applied between minScalingDistance and maxScalingDistance.
	    // The scalingFactor controls the size the object takes on at maxScalingDistance, in percentage of the size it took on at minScalingDistance.
        // Must be in the range of [0,1].
        // Set the scalingFactor to 1 if no scaling should be applied for the objects.
        // Default: 0.1
	    AR.context.scene.scalingFactor = 0.05
        // start loading marker assets
        World.markerDrawable_idle = new AR.ImageResource("assets/map-marker.png");
        World.markerDrawable_selected = new AR.ImageResource("assets/map-marker-selected.png");
        World.markerDrawable_directionIndicator = new AR.ImageResource("assets/indi.png");
	},

    removePoi: function removePoiFn(poiData) {
        for (var i = 0; i < World.markerList.length; i++) {
            if (World.markerList[i].poiData.id == poiData.id) {
                World.markerList[i].destroy();
                World.markerList.splice(i, 1);
                if (World.currentMarker) {
                    if (World.currentMarker.poiData.id == poiData.id) {
                        World.currentMarker = null;
                    }
                }
            }
        }
    },

    addPoi: function addPoiFn(poiData) {
        var singlePoi = {
            "id": poiData.id,
            "latitude": parseFloat(poiData.latitude),
            "longitude": parseFloat(poiData.longitude),
            "altitude": parseFloat(poiData.altitude),
            "name": poiData.name,
            "alpha": poiData.alpha,
            "isParkingMeter": poiData.isParkingMeter
        };
        var marker = new Marker(singlePoi);
        World.markerList.push(marker);
        marker.toggleParkingMetersVisibility(marker, World.showParkingMeters)
    },

    // fired when user pressed marker in cam
    onMarkerSelected: function onMarkerSelectedFn(marker) {

        // deselect previous marker
        if (World.currentMarker) {
            if (World.currentMarker.poiData.id == marker.poiData.id) {
                return;
            }
            World.currentMarker.setDeselected(World.currentMarker, false);
        }

        World.currentMarker = marker;
    },

    // called from the native code
    deselectPoi: function deselectPoiFn() {
        if (World.currentMarker) {
            World.currentMarker.setDeselected(World.currentMarker, false);
            World.currentMarker = null;
        }
    },

    // screen was clicked but no geo-object was hit
    deselectPoiAndNotify: function deselectPoiAndNotifyFn() {
        if (World.currentMarker && !Marker.prototype.isAnyAnimationRunning(World.currentMarker)) {
            if (Date.now() - World.lastActionTime > 500) {
                World.currentMarker.setDeselected(World.currentMarker, true);
                World.currentMarker = null;
            }
        }
    },

    // select specified marker
    selectMarker: function selectMarkerFn(markerId) {
        for (var counter = 0; counter < World.markerList.length; counter++) {
            if (World.markerList[counter].poiData.id == markerId.id) {
                World.onMarkerSelected(World.markerList[counter]);
                World.markerList[counter].setSelected(World.markerList[counter], false);
                return;
            }
        }
    },

    toggleParkingMetersVisibility: function toggleParkingMetersVisibilityFn(showParkingMeters) {
        World.showParkingMeters = showParkingMeters;
        for (var counter = 0; counter < World.markerList.length; counter++) {
            var marker = World.markerList[counter];
            marker.toggleParkingMetersVisibility(marker, showParkingMeters);
        }
    },

    destroyAll: function destroyAllFn() {
        AR.context.destroyAll();
    }
};

/* forward clicks in empty area to World */
AR.context.onScreenClick = World.deselectPoiAndNotify;

World.init();
