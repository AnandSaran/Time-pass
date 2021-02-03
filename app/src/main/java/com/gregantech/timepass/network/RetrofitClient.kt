package com.gregantech.timepass.network

import com.gregantech.timepass.BuildConfig
import com.gregantech.timepass.BuildConfig.DEBUG
import com.gregantech.timepass.network.jsonadapter.NullToEmptyStringAdapter
import com.gregantech.timepass.network.jsonadapter.StringToBooleanAdapter
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by anand
 * Main entry point for network access
 */
object RetrofitClient {
    private const val TIME_OUT_WRITE: Long = 120
    private const val TIME_OUT_READ: Long = 120
    private const val TIME_OUT_CONNECT: Long = 120

    /**
     * Define the interceptor, add authentication header(s)
     * header - replace the value with similar key
     * addHeader - add another value even with similar key
     */
    private val interceptor = Interceptor { chain ->
        val original = chain.request()

        /**
         * Request customization: add request headers
         */
        val requestBuilder = original.newBuilder()
        val request = requestBuilder.build()

        chain.proceed(request)
    }

    private val loggingLevel = if (DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.NONE
    }

    /**
     * Printing out the http request body in logcat
     */
    private var httpLoggingInterceptor = HttpLoggingInterceptor().setLevel(loggingLevel)
    private var okHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIME_OUT_CONNECT, TimeUnit.SECONDS)  // connect timeout
        .writeTimeout(TIME_OUT_WRITE, TimeUnit.SECONDS)
        .readTimeout(TIME_OUT_READ, TimeUnit.SECONDS)      // socket timeout
        // .addInterceptor(interceptor)
        .addInterceptor(httpLoggingInterceptor)
        .build()

    /**
     * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
     * full Kotlin compatibility.
     */
    private val moShi = Moshi.Builder()
        .add(StringToBooleanAdapter())
        .add(NullToEmptyStringAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    /**
     * Initialize retrofit client.
     * 1. factory - coroutine
     * 2. json parse - moshi
     * */
    private val mRetrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASEURL)
        .addConverterFactory(MoshiConverterFactory.create(moShi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(okHttpClient)
        .build()

    val retrofit: Retrofit
        get() = mRetrofit
}