package com.suomifi.palvelutietovaranto.ui.poi.common

sealed class ProgressViewState
object ProgressViewHidden : ProgressViewState()
object ProgressViewLoading : ProgressViewState()
sealed class ProgressViewErrorState : ProgressViewState()
object ProgressViewLocationError : ProgressViewErrorState()
object ProgressViewPoiLoadingError : ProgressViewErrorState()
object ProgressViewZoomedOutError : ProgressViewErrorState()
