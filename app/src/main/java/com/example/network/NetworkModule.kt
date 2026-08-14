package com.example.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Módulo de red empresarial para Abadalabs, Inc.
 * Configura la comunicación con los 3 microservicios del ecosistema HECTRON Live Universe.
 */
object NetworkModule {
    // Entorno de Desarrollo (Emulador a Localhost)
    // En producción (Vercel/GCP) estas URLs apuntarían a hectron.abadalabs.com
    private const val API_BASE_URL = "http://10.0.2.2:3000/"
    private const val AUTONOMY_BASE_URL = "http://10.0.2.2:3001/"
    private const val UNIVERSE_BASE_URL = "http://10.0.2.2:3002/"

    // Token empresarial de Abadalabs para acceso seguro
    private const val AGENT_TOKEN = "abadalabs_hectron_agent_token_2026"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer \$AGENT_TOKEN")
                .addHeader("X-Company-ID", "Abadalabs_Inc")
                .build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // 1. Game API (Puerto 3000) - Estado del juego y economía
    val gameApi: GameApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GameApiService::class.java)
    }

    // 2. Autonomy API (Puerto 3001) - Telemetría y automatización (OBS/ElevenLabs)
    val autonomyApi: AutonomyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(AUTONOMY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AutonomyApiService::class.java)
    }

    // 3. Universe API (Puerto 3002) - Lore masivo y BigQuery
    val universeApi: UniverseApiService by lazy {
        Retrofit.Builder()
            .baseUrl(UNIVERSE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(UniverseApiService::class.java)
    }
}
