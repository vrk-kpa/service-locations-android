package com.suomifi.palvelutietovaranto.domain.interactors.base

import io.reactivex.Flowable

abstract class FlowableUseCase<Params, Result> {
    abstract fun execute(params: Params): Flowable<Result>
}
