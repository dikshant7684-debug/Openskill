package com.example.data.remote

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

object ApiClient {
  private val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

  @Volatile
  var githubToken: String = ""
    set(value) {
      field = value
      _telemetry.value = _telemetry.value.copy(isAuthenticated = value.isNotBlank())
    }

  @Volatile
  var gitlabToken: String = ""

  private val _telemetry = MutableStateFlow(ApiTelemetry())
  val telemetry: StateFlow<ApiTelemetry> = _telemetry.asStateFlow()

  private val callLogs = java.util.concurrent.CopyOnWriteArrayList<ApiCallLog>()
  private val timeFormat = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US)

  private val headerInterceptor = Interceptor { chain ->
    val original = chain.request()
    val requestBuilder = original.newBuilder()
      .header("User-Agent", "Openskill-Android-Discovery/1.0")
      .header("Accept", "application/vnd.github.v3+json, application/json")

    // Attach GitHub token if present
    if (githubToken.isNotBlank() && original.url.host.contains("github")) {
      requestBuilder.header("Authorization", "Bearer $githubToken")
    }

    // Attach GitLab token if present
    if (gitlabToken.isNotBlank() && original.url.host.contains("gitlab")) {
      requestBuilder.header("PRIVATE-TOKEN", gitlabToken)
    }

    val startTime = System.currentTimeMillis()
    val request = requestBuilder.build()
    val response = chain.proceed(request)
    val latency = System.currentTimeMillis() - startTime

    // Parse live rate limit headers from GitHub
    val remaining = response.header("x-ratelimit-remaining")?.toIntOrNull()
    val limit = response.header("x-ratelimit-limit")?.toIntOrNull()

    val host = request.url.host
    val isGitHub = host.contains("github")
    val isGitLab = host.contains("gitlab")

    val statusText = if (response.isSuccessful) {
      "Connected (${response.code} OK)"
    } else if (response.code == 403) {
      "Rate-Limited (403)"
    } else {
      "HTTP ${response.code}"
    }

    val now = timeFormat.format(java.util.Date())
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
      recentCalls = callLogs.toList()
    )

    response
  }

  private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(headerInterceptor)
    .addInterceptor(HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BASIC
    })
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .build()

  val gitHubApi: GitHubApiService by lazy {
    Retrofit.Builder()
      .baseUrl("https://api.github.com/")
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
      .create(GitHubApiService::class.java)
  }

  val gitLabApi: GitLabApiService by lazy {
    Retrofit.Builder()
      .baseUrl("https://gitlab.com/")
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
      .create(GitLabApiService::class.java)
  }
}
