package com.suomifi.palvelutietovaranto.di

import android.os.Build
import com.crashlytics.android.answers.Answers
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.suomifi.palvelutietovaranto.BuildConfig
import com.suomifi.palvelutietovaranto.data.TLSSocketFactory
import com.suomifi.palvelutietovaranto.data.wfs.GetFeaturesDeserializer
import com.suomifi.palvelutietovaranto.data.wfs.dto.GetFeaturesResult
import com.suomifi.palvelutietovaranto.ui.model.PoiTarget
import com.suomifi.palvelutietovaranto.ui.model.PoiTargetDeserializer
import com.suomifi.palvelutietovaranto.utils.Constants.Network.TIMEOUT
import com.suomifi.palvelutietovaranto.utils.metrics.ResponseTimeEvent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

fun buildGson(): Gson {
    return GsonBuilder()
            .registerTypeAdapter(GetFeaturesResult::class.java, GetFeaturesDeserializer())
            .registerTypeAdapter(PoiTarget::class.java, PoiTargetDeserializer())
            .create()
}

fun buildRetrofit(baseUrl: String, gson: Gson): Retrofit {
    return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .setSSLSocketFactory()
                    .addLoggingInterceptor()
                    .addResponseTimeLoggerInterceptor()
                    .build())
            .build()
}

private fun OkHttpClient.Builder.setSSLSocketFactory(): OkHttpClient.Builder = apply {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
        val tlsSocketFactory = TLSSocketFactory()
        sslSocketFactory(tlsSocketFactory, tlsSocketFactory.trustManager)
    }
}

private fun OkHttpClient.Builder.addLoggingInterceptor(): OkHttpClient.Builder = apply {
    if (BuildConfig.DEBUG) {
        addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
            Timber.tag("OkHttp").d(message)
        }).apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
    }
}

private fun OkHttpClient.Builder.addResponseTimeLoggerInterceptor(): OkHttpClient.Builder = if (BuildConfig.DEBUG) {
    this
} else {
    apply {
        addInterceptor { chain ->
            val startNs = System.nanoTime()
            val response = chain.proceed(chain.request())
            val responseTimeMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
            if (response.code() == 200) {
                val url = response.request().url()
                Answers.getInstance().logCustom(ResponseTimeEvent(url.toString(), responseTimeMs))
            }
            response
        }
    }
}
