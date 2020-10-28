package com.suomifi.palvelutietovaranto.domain.interactors.base

import io.reactivex.Completable

abstract class CompletableUseCase<Params> {
    abstract fun execute(params: Params): Completable
}
