package com.suomifi.palvelutietovaranto.domain

import com.suomifi.palvelutietovaranto.domain.ptv.model.Poi

interface SelectedPoiSupplier {
    fun getSelectedPoi(): Poi?
    fun selectPoi(poi: Poi)
    fun deselectPoi()
    fun addListener(listener: Listener)
    fun removeListener(listener: Listener)

    interface Listener {
        fun onPoiSelectionChanged(poiSelection: PoiSelection)
    }
}

sealed class PoiSelection
class Selected(poi: Poi) : PoiSelection()
object Deselected : PoiSelection()
