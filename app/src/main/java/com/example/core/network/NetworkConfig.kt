package com.example.core.network

import com.example.lib.constants.AppConstants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

data class ApiCallLog(
  val id: Long = System.currentTimeMillis(),
  val time: String,
  val method: String,
  val url: String,
  val host: String,
  val statusCode: Int,
  val latencyMs: Long,
  val isSuccessful: Boolean,
  val rateLimitRemaining: Int? = null,
  val rateLimitTotal: Int? = null
)

data class ApiTelemetry(
  val gitHubStatus: String = "Ready (Live API)",
  val gitLabStatus: String = "Ready (Live API)",
  val rateLimitRemaining: Int = 60,
  val rateLimitTotal: Int = 60,
  val lastLatencyMs: Long = 0,
  val isAuthenticated: Boolean = false,
  val lastQueryUrl: String = "",
  val totalRequestsMade: Int = 0,
  val recentCalls: List<ApiCallLog> = emptyList()
)

object NetworkConfig {
  val moshi: Moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

  private val _telemetry = MutableStateFlow(ApiTelemetry())
  val telemetry: StateFlow<ApiTelemetry> = _telemetry.asStateFlow()

  private val callLogs = CopyOnWriteArrayList<ApiCallLog>()
  private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

  fun createHeaderInterceptor(
    getGitHubToken: () -> String,
    getGitLabToken: () -> String
  ): Interceptor = Interceptor { chain ->
    val original = chain.request()
    val ghToken = getGitHubToken()
    val glToken = getGitLabToken()

    val requestBuilder = original.newBuilder()
      .header("User-Agent", AppConstants.USER_AGENT)
      .header("Accept", "application/vnd.github.v3+json, application/json")

    if (ghToken.isNotBlank() && original.url.host.contains("github")) {
      requestBuilder.header("Authorization", "Bearer $ghToken")
    }

    if (glToken.isNotBlank() && original.url.host.contains("gitlab")) {
      requestBuilder.header("PRIVATE-TOKEN", glToken)
    }

    val startTime = System.currentTimeMillis()
    val request = requestBuilder.build()
    val response = chain.proceed(request)
    val latency = System.currentTimeMillis() - startTime

    val remaining = response.header("x-ratelimit-remaining")?.toIntOrNull()
    val limit = response.header("x-ratelimit-limit")?.toIntOrNull()

    val host = request.url.host
    val isGitHub = host.contains("github")
    val isGitLab = host.contains("gitlab")

    val statusText = when {
      response.isSuccessful -> "Connected (${response.code} OK)"
      response.code == 403 -> "Rate-Limited (403)"
      else -> "HTTP ${response.code}"
    }

    val now = timeFormat.format(Date())
    val callLog = ApiCallLog(
      id = System.currentTimeMillis() + (0..1000).random(),
      time = now,
      method = request.method,
      url = request.url.toString(),
      host = host,
      statusCode = response.code,
      latencyMs = latency,
      isSuccessful = response.isSuccessful,
      rateLimitRemaining = remaining,
      rateLimitTotal = limit
    )

    callLogs.add(0, callLog)
    while (callLogs.size > 15) {
      callLogs.removeAt(callLogs.size - 1)
    }

    _telemetry.value = _telemetry.value.copy(
      gitHubStatus = if (isGitHub) statusText else _telemetry.value.gitHubStatus,
      gitLabStatus = if (isGitLab) statusText else _telemetry.value.gitLabStatus,
      rateLimitRemaining = remaining ?: _telemetry.value.rateLimitRemaining,
      rateLimitTotal = limit ?: _telemetry.value.rateLimitTotal,
      lastLatencyMs = latency,
      lastQueryUrl = request.url.encodedPath + (request.url.query?.let { "?$it" } ?: ""),
      totalRequestsMade = _telemetry.value.totalRequestsMade + 1,
      isAuthenticated = ghToken.isNotBlank(),
      recentCalls = callLogs.toList()
    )

    response
  }

  fun buildOkHttpClient(headerInterceptor: Interceptor): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(headerInterceptor)
      .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
      })
      .connectTimeout(AppConstants.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
      .readTimeout(AppConstants.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
      .build()
  }

  fun buildRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(client)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
  }
}
