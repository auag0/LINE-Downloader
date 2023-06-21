package com.anago.linedownloader.network

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


object API {
    var okHttpClient = OkHttpClient().newBuilder().apply {
        callTimeout(5, TimeUnit.SECONDS)
        connectTimeout(5, TimeUnit.SECONDS)
        readTimeout(5, TimeUnit.SECONDS)
        writeTimeout(5, TimeUnit.SECONDS)
    }.build()
}