package com.suomifi.palvelutietovaranto.di

import android.content.Context
import android.location.LocationManager
import android.preference.PreferenceManager
import com.suomifi.palvelutietovaranto.data.db.DatabaseBuilder
import com.suomifi.palvelutietovaranto.data.ptv.PtvService
import com.suomifi.palvelutietovaranto.data.wfs.WfsService
import com.suomifi.palvelutietovaranto.domain.SelectedPoiSupplier
import com.suomifi.palvelutietovaranto.domain.SelectedPoiSupplierImpl
import com.suomifi.palvelutietovaranto.domain.interactors.ObserveSelectedPoiUseCase
import com.suomifi.palvelutietovaranto.domain.interactors.OpenNavigationToUseCase
import com.suomifi.palvelutietovaranto.domain.interactors.SearchLocationUseCase
import com.suomifi.palvelutietovaranto.domain.interactors.ar.LoadPoisUseCase
import com.suomifi.palvelutietovaranto.domain.interactors.ar.ObservePoisChangesUseCase
import com.suomifi.palvelutietovaranto.domain.interactors.ar.ObserveUserLocationUseCase
import com.suomifi.palvelutietovaranto.domain.location.provider.UserLocationProvider
import com.suomifi.palvelutietovaranto.domain.location.provider.UserLocationProviderImpl
import com.suomifi.palvelutietovaranto.domain.preferences.PreferenceChangeNotifier
import com.suomifi.palvelutietovaranto.domain.preferences.PreferenceChangeNotifierImpl
import com.suomifi.palvelutietovaranto.domain.ptv.cache.PtvCache
import com.suomifi.palvelutietovaranto.domain.ptv.cache.PtvCacheImpl
import com.suomifi.palvelutietovaranto.domain.ptv.repository.PtvRepository
import com.suomifi.palvelutietovaranto.domain.ptv.repository.PtvRepositoryImpl
import com.suomifi.palvelutietovaranto.ui.poi.PoiViewModel
import com.suomifi.palvelutietovaranto.ui.poi.ar.ArPoiViewModel
import com.suomifi.palvelutietovaranto.ui.poi.map.MapPoiViewModel
import com.suomifi.palvelutietovaranto.ui.search.LocationSearchViewModel
import com.suomifi.palvelutietovaranto.ui.tutorial.PermissionsViewModel
import com.suomifi.palvelutietovaranto.utils.Constants.Network.PTV_OPEN_API_ADDRESS
import com.suomifi.palvelutietovaranto.utils.Constants.Network.WFS_GEOSERVER_ADDRESS
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.experimental.builder.viewModel
import org.koin.dsl.module.module
import org.koin.experimental.builder.create
import org.koin.experimental.builder.factory
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider

object KoinModules {

    private val appModule = module {
        single<PtvCache> { create<PtvCacheImpl>() }
        single { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
        single { androidContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager }
        single { buildGson() }
        factory<UserLocationProvider> { create<UserLocationProviderImpl>() }
        single<SelectedPoiSupplier> { create<SelectedPoiSupplierImpl>() }
        single { DatabaseBuilder.createDatabase(androidContext()) }
    }

    private val poiActivityModule = module {
        viewModel<PoiViewModel>()
        factory { ReactiveLocationProvider(androidContext()) }
        factory { buildRetrofit(WFS_GEOSERVER_ADDRESS, get()).create(WfsService::class.java) }
        factory { buildRetrofit(PTV_OPEN_API_ADDRESS, get()).create(PtvService::class.java) }
        factory<PtvRepository> { create<PtvRepositoryImpl>() }
    }

    private val mapModule = module {
        viewModel<MapPoiViewModel>()
    }

    private val arModule = module {
        factory<PreferenceChangeNotifier> { create<PreferenceChangeNotifierImpl>() }
        viewModel<ArPoiViewModel>()
    }

    private val tutorialModule = module {
        viewModel<PermissionsViewModel>()
    }

    private val interactorsModule = module {
        factory<OpenNavigationToUseCase>()
        factory<ObserveUserLocationUseCase>()
        factory<ObservePoisChangesUseCase>()
        factory<LoadPoisUseCase>()
        factory<ObserveSelectedPoiUseCase>()
        factory<SearchLocationUseCase>()
    }

    private val locationSearchModule = module {
        viewModel<LocationSearchViewModel>()
    }

    val modules = listOf(
            appModule, poiActivityModule, mapModule, arModule, tutorialModule, interactorsModule,
            locationSearchModule
    )

}
