package com.suomifi.palvelutietovaranto.domain.interactors.base

import io.reactivex.Single

abstract class SingleUseCase<Params, Result> {
    abstract fun execute(params: Params): Single<Result>
}
