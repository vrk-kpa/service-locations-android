package com.suomifi.palvelutietovaranto.domain.interactors

import com.suomifi.palvelutietovaranto.domain.PoiSelection
import com.suomifi.palvelutietovaranto.domain.SelectedPoiSupplier
import com.suomifi.palvelutietovaranto.domain.interactors.base.ObservableUseCase
import io.reactivex.Observable

class ObserveSelectedPoiUseCase(
        private val selectedPoiSupplier: SelectedPoiSupplier
) : ObservableUseCase<Unit, PoiSelection>() {

    override fun execute(params: Unit): Observable<PoiSelection> {
        return Observable.create<PoiSelection> { emitter ->
            val listener = object : SelectedPoiSupplier.Listener {
                override fun onPoiSelectionChanged(poiSelection: PoiSelection) {
                    emitter.onNext(poiSelection)
                }
            }
            selectedPoiSupplier.addListener(listener)
            emitter.setCancellable {
                selectedPoiSupplier.removeListener(listener)
            }
        }
    }

}
