package hu.bme.aut.android.scanmynotes.data.models

sealed class Result<T> {
    data class Success<T>(val data: T): Result<T>()
    data class Failure<T>(val message: String): Result<T>()

    companion object {
        fun <T> success(data: T) = Success(data)
        fun <T> failure(message: String) = Failure<T>(message)
    }
}

