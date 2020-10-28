package com.suomifi.palvelutietovaranto.domain.ptv.cache

import android.util.LruCache
import com.suomifi.palvelutietovaranto.domain.ptv.model.PoiDetails
import com.suomifi.palvelutietovaranto.utils.Constants.Cache.CACHE_SIZE
import io.reactivex.Flowable
import timber.log.Timber

class PtvCacheImpl : PtvCache {

    private val lruCache = LruCache<String, PoiDetails>(CACHE_SIZE)

    override fun storePoi(poiDetails: PoiDetails) {
        lruCache.put(poiDetails.id, poiDetails)
        Timber.d("storePoi - stored POI with id: %s", poiDetails.id)
    }

    override fun getAll(): Flowable<PoiDetails> {
        return Flowable.fromIterable(lruCache.snapshot().values)
    }

}
