package com.suomifi.palvelutietovaranto.domain.interactors.base

import io.reactivex.Observable

abstract class ObservableUseCase<Params, Result> {
    abstract fun execute(params: Params): Observable<Result>
}
