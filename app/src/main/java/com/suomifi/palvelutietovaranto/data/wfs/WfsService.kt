package com.suomifi.palvelutietovaranto.data.wfs

import com.suomifi.palvelutietovaranto.data.wfs.dto.GetFeaturesResult
import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Query

interface WfsService {

    @GET("wfs?service=WFS&version=1.1.0&request=GetFeature&typename=ptv:locationCoordinate&outputFormat=application/json&srsname=EPSG:4326")
    fun getFeatures(@Query("bbox") bbox: String): Maybe<GetFeaturesResult>

}
