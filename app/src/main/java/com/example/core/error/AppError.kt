package com.example.core.error

sealed class AppError(
  open val message: String,
  open val cause: Throwable? = null
) {
  data class NetworkError(
    override val message: String,
    val statusCode: Int? = null,
    override val cause: Throwable? = null
  ) : AppError(message, cause)

  data class RateLimitError(
    override val message: String = "API rate limit reached. Add a personal access token in Settings to increase limit.",
    val resetTimeEpochSeconds: Long? = null
  ) : AppError(message)

  data class ParseError(
    override val message: String,
    val rawContent: String? = null,
    override val cause: Throwable? = null
  ) : AppError(message, cause)

  data class StorageError(
    override val message: String,
    override val cause: Throwable? = null
  ) : AppError(message, cause)
}
