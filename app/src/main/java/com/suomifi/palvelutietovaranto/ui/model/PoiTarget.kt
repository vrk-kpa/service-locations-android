package com.suomifi.palvelutietovaranto.ui.model

import com.suomifi.palvelutietovaranto.domain.ptv.model.Poi

data class PoiTarget(
        val poi: Poi,
        val action: PoiTargetAction
)
