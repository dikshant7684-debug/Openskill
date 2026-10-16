package com.example.lib.network

sealed interface NetworkResult<out T> {
  data class Success<out T>(val data: T) : NetworkResult<T>
  data class Error(val message: String, val code: Int? = null, val cause: Throwable? = null) : NetworkResult<Nothing>
  data object Loading : NetworkResult<Nothing>

  val isSuccess: Boolean
    get() = this is Success

  fun getOrNull(): T? = when (this) {
    is Success -> data
    else -> null
  }
}
