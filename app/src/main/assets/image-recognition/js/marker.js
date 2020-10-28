var kMarker_AnimationDuration_ChangeDrawable = 500;
var kMarker_AnimationDuration_Resize = 1000;

var LABEL_BACKGROUND_COLOR = '#00000080';
var LABEL_HEIGHT = 2;

function Marker(poiData) {

    this.poiData = poiData;
    this.isSelected = false;

    /*
     With AR.PropertyAnimations you are able to animate almost any property of ARchitect objects.
     This sample will animate the opacity of both background drawables so that one will fade out
     while the other one fades in. The scaling is animated too. The marker size changes over time so
     the labels need to be animated too in order to keep them relative to the background drawable.
     AR.AnimationGroups are used to synchronize all animations in parallel or sequentially.
     */

    this.animationGroup_idle = null;
    this.animationGroup_selected = null;


    // create the AR.GeoLocation from the poi data
    var markerLocation = new AR.GeoLocation(poiData.latitude, poiData.longitude, poiData.altitude);

    // create an AR.ImageDrawable for the marker in idle state
    this.markerDrawable_idle = new AR.ImageDrawable(World.markerDrawable_idle, 10, {
        zOrder: 0,
        opacity: 1.0,
        /*
         To react on user interaction, an onClick property can be set for each AR.Drawable.
         The property is a function which will be called each time the user taps on the drawable.
         The function called on each tap is returned from the following helper function defined in marker.js.
         The function returns a function which checks the selected state with the help of the variable
         isSelected and executes the appropriate function. The clicked marker is passed as an argument.
         */
        onClick: Marker.prototype.getOnClickTrigger(this)
    });

    // create an AR.ImageDrawable for the marker in selected state
    this.markerDrawable_selected = new AR.ImageDrawable(World.markerDrawable_selected, 10, {
        zOrder: 0,
        opacity: 0.0,
        onClick: null
    });

    // create an AR.Label for the marker's title
    // Parameters:
    // text - The text to be displayed on the Label
    // height - The height of the Label, in SDUs
    // options - Setup-Parameters to customize additional object properties
    this.titleLabel = new AR.Label(poiData.name, LABEL_HEIGHT, {
        verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP,
        zOrder: 1,
        translate: {
            y: -5
        },
        style: {
            textColor: '#FFFFFF',
            fontStyle: AR.CONST.FONT_STYLE.NORMAL,
            backgroundColor: LABEL_BACKGROUND_COLOR
        },
        opacity: poiData.alpha
    });

    /*
     Create an AR.ImageDrawable using the AR.ImageResource for the direction indicator which was
     created in the World.
     Set options regarding the offset and anchor of the image so that it will be displayed
     correctly on the edge of the screen.
     */
    this.directionIndicatorDrawable = new AR.ImageDrawable(World.markerDrawable_directionIndicator, 0.1, {
        enabled: false,
        verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
    });

    /*
     Create the AR.GeoObject with the drawable objects and define the AR.ImageDrawable as an
     indicator target on the marker AR.GeoObject.
     The direction indicator is displayed automatically when necessary.
     AR.Drawable subclasses (e.g. AR.Circle) can be used as direction indicators.
     */
    this.markerObject = new AR.GeoObject(markerLocation, {
        drawables: {
            cam: [this.markerDrawable_idle, this.markerDrawable_selected, this.titleLabel],
            indicator: this.directionIndicatorDrawable
        }
    });

    return this;
}

Marker.prototype.getOnClickTrigger = function(marker) {

    /*
     The setSelected and setDeselected functions are prototype Marker functions.
     Both functions perform the same steps but inverted.
     */
    return function() {
        if (!Marker.prototype.isAnyAnimationRunning(marker)) {
            if (!marker.isSelected) {
                Marker.prototype.setSelected(marker, true);
                try {
                    World.onMarkerSelected(marker);
                } catch (err) {
                    alert(err);
                }
            }
        } else {
            AR.logger.debug('an animation is already running');
        }
        return true;
    };
};

/*
 Property Animations allow constant changes to a numeric value/property of an object, dependent on
 start-value, end-value and the duration of the animation.
 Animations can be seen as functions defining the progress of the change on the value.
 The Animation can be parametrized via easing curves.
 */

Marker.prototype.setSelected = function(marker, notify) {
    World.lastActionTime = Date.now();
    marker.isSelected = true;

    // New: 
    if (marker.animationGroup_selected === null) {

        // create AR.PropertyAnimation that animates the opacity to 0.0 in order to hide the idle-state-drawable
        var hideIdleDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_idle, "opacity", null, 0.0, kMarker_AnimationDuration_ChangeDrawable);
        // create AR.PropertyAnimation that animates the opacity to 1.0 in order to show the selected-state-drawable
        var showSelectedDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_selected, "opacity", null, 1.0, kMarker_AnimationDuration_ChangeDrawable);

        // create AR.PropertyAnimation that animates the scaling of the idle-state-drawable to 1.2
        var idleDrawableResizeAnimationX = new AR.PropertyAnimation(marker.markerDrawable_idle, 'scale.x', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the selected-state-drawable to 1.2
        var selectedDrawableResizeAnimationX = new AR.PropertyAnimation(marker.markerDrawable_selected, 'scale.x', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the title label to 1.2
        var titleLabelResizeAnimationX = new AR.PropertyAnimation(marker.titleLabel, 'scale.x', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));

        // create AR.PropertyAnimation that animates the scaling of the idle-state-drawable to 1.2
        var idleDrawableResizeAnimationY = new AR.PropertyAnimation(marker.markerDrawable_idle, 'scale.y', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the selected-state-drawable to 1.2
        var selectedDrawableResizeAnimationY = new AR.PropertyAnimation(marker.markerDrawable_selected, 'scale.y', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the title label to 1.2
        var titleLabelResizeAnimationY = new AR.PropertyAnimation(marker.titleLabel, 'scale.y', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));

        /*
         There are two types of AR.AnimationGroups.
         Parallel animations are running at the same time, sequentials are played one after another.
         This example uses a parallel AR.AnimationGroup.
         */
        marker.animationGroup_selected = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [hideIdleDrawableAnimation, showSelectedDrawableAnimation, idleDrawableResizeAnimationX, selectedDrawableResizeAnimationX, titleLabelResizeAnimationX, idleDrawableResizeAnimationY, selectedDrawableResizeAnimationY, titleLabelResizeAnimationY]);
    }

    // removes function that is set on the onClick trigger of the idle-state marker
    marker.markerDrawable_idle.onClick = null;
    // sets the click trigger function for the selected state marker
    marker.markerDrawable_selected.onClick = Marker.prototype.getOnClickTrigger(marker);

    // enables the direction indicator drawable for the current marker
    marker.directionIndicatorDrawable.enabled = true;
    // starts the selected-state animation
    marker.animationGroup_selected.start();

    marker.titleLabel.height = 2.25;
    marker.titleLabel.opacity = 1.0;
    marker.titleLabel.style.backgroundColor = '#000000FF';
    marker.markerObject.renderingOrder = 1;

    if (notify) {
        // The sendJSONObject method can be used to send data from javascript to the native code.
        AR.platform.sendJSONObject({"action": "SELECTED", "poi": {"latitude": marker.poiData.latitude, "longitude": marker.poiData.longitude, "name": marker.poiData.name, "isParkingMeter": marker.poiData.isParkingMeter}});
    }
};

Marker.prototype.setDeselected = function(marker, notify) {
    World.lastActionTime = Date.now();
    marker.isSelected = false;

    if (marker.animationGroup_idle === null) {

        // create AR.PropertyAnimation that animates the opacity to 1.0 in order to show the idle-state-drawable
        var showIdleDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_idle, "opacity", null, 1.0, kMarker_AnimationDuration_ChangeDrawable);
        // create AR.PropertyAnimation that animates the opacity to 0.0 in order to hide the selected-state-drawable
        var hideSelectedDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_selected, "opacity", null, 0, kMarker_AnimationDuration_ChangeDrawable);
        // create AR.PropertyAnimation that animates the scaling of the idle-state-drawable to 1.0
        var idleDrawableResizeAnimationX = new AR.PropertyAnimation(marker.markerDrawable_idle, 'scale.x', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the selected-state-drawable to 1.0
        var selectedDrawableResizeAnimationX = new AR.PropertyAnimation(marker.markerDrawable_selected, 'scale.x', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the title label to 1.0
        var titleLabelResizeAnimationX = new AR.PropertyAnimation(marker.titleLabel, 'scale.x', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the idle-state-drawable to 1.0
        var idleDrawableResizeAnimationY = new AR.PropertyAnimation(marker.markerDrawable_idle, 'scale.y', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the selected-state-drawable to 1.0
        var selectedDrawableResizeAnimationY = new AR.PropertyAnimation(marker.markerDrawable_selected, 'scale.y', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the title label to 1.0
        var titleLabelResizeAnimationY = new AR.PropertyAnimation(marker.titleLabel, 'scale.y', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));

        /*
         There are two types of AR.AnimationGroups. Parallel animations are running at the same time, sequentials are played one after another. This example uses a parallel AR.AnimationGroup.
         */
        marker.animationGroup_idle = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [showIdleDrawableAnimation, hideSelectedDrawableAnimation, idleDrawableResizeAnimationX, selectedDrawableResizeAnimationX, titleLabelResizeAnimationX, idleDrawableResizeAnimationY, selectedDrawableResizeAnimationY, titleLabelResizeAnimationY]);
    }

    // sets the click trigger function for the idle state marker
    marker.markerDrawable_idle.onClick = Marker.prototype.getOnClickTrigger(marker);
    // removes function that is set on the onClick trigger of the selected-state marker
    marker.markerDrawable_selected.onClick = null;

    // disables the direction indicator drawable for the current marker
    marker.directionIndicatorDrawable.enabled = false;
    // starts the idle-state animation
    marker.animationGroup_idle.start();

    marker.titleLabel.height = LABEL_HEIGHT;
    marker.titleLabel.opacity = marker.poiData.alpha;
    marker.titleLabel.style.backgroundColor = LABEL_BACKGROUND_COLOR;
    marker.markerObject.renderingOrder = 0;

    if (notify) {
        // The sendJSONObject method can be used to send data from javascript to the native code.
        AR.platform.sendJSONObject({"action": "DESELECTED", "poi": {"latitude": marker.poiData.latitude, "longitude": marker.poiData.longitude, "name": marker.poiData.name, "isParkingMeter": marker.poiData.isParkingMeter}});
    }
};

Marker.prototype.isAnyAnimationRunning = function(marker) {

    if (marker.animationGroup_idle === null || marker.animationGroup_selected === null) {
        return false;
    } else {
        if ((marker.animationGroup_idle.isRunning() === true) || (marker.animationGroup_selected.isRunning() === true)) {
            return true;
        } else {
            return false;
        }
    }
};

Marker.prototype.destroy = function() {
    this.markerDrawable_idle.destroy();
    this.markerDrawable_selected.destroy();
    this.titleLabel.destroy();
    this.directionIndicatorDrawable.destroy();
    this.markerObject.destroy();
}

Marker.prototype.toggleParkingMetersVisibility = function(marker, showParkingMeters) {
    if (marker.poiData.isParkingMeter) {
        marker.markerObject.enabled = showParkingMeters;
    }
}
