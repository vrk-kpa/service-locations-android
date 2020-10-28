package com.suomifi.palvelutietovaranto.domain

import com.suomifi.palvelutietovaranto.domain.SelectedPoiSupplier.Listener
import com.suomifi.palvelutietovaranto.domain.ptv.model.Poi
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference

class SelectedPoiSupplierImpl : SelectedPoiSupplier {

    private val selectedPoiReference = AtomicReference<Poi>()
    private val listeners = CopyOnWriteArrayList<Listener>()

    override fun getSelectedPoi(): Poi? {
        return selectedPoiReference.get()
    }

    override fun selectPoi(poi: Poi) {
        selectedPoiReference.set(poi)
        val action = Selected(poi)
        listeners.forEach { listener ->
            listener.onPoiSelectionChanged(action)
        }
    }

    override fun deselectPoi() {
        selectedPoiReference.set(null)
        listeners.forEach { listener ->
            listener.onPoiSelectionChanged(Deselected)
        }
    }

    override fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

}
