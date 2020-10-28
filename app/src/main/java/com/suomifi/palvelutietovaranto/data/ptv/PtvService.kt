package com.suomifi.palvelutietovaranto.data.ptv

import com.suomifi.palvelutietovaranto.model.ptv.v8.V8VmOpenApiServiceLocationChannel
import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PtvService {

    @GET("ServiceChannel/{id}")
    fun serviceLocation(@Path("id") id: String): Maybe<V8VmOpenApiServiceLocationChannel>

    @GET("ServiceChannel/list")
    fun serviceLocations(@Query("guids") guids: String): Maybe<List<V8VmOpenApiServiceLocationChannel>>

}
