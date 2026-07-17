package org.koharu.miyo.core.util

/**
 * Cross-platform Result type for handling success and failure cases.
 */
sealed class Result<out T> {
	data class Success<T>(val data: T) : Result<T>()
	data class Failure(val error: Throwable) : Result<Nothing>()
	data class Loading(val progress: Float = 0f) : Result<Nothing>()

	val isSuccess: Boolean get() = this is Success
	val isFailure: Boolean get() = this is Failure
	val isLoading: Boolean get() = this is Loading

	fun getOrNull(): T? = when (this) {
		is Success -> data
		is Failure -> null
		is Loading -> null
	}

	fun exceptionOrNull(): Throwable? = when (this) {
		is Success -> null
		is Failure -> error
		is Loading -> null
	}

	fun <R> map(transform: (T) -> R): Result<R> = when (this) {
		is Success -> Success(transform(data))
		is Failure -> this
		is Loading -> Loading(progress)
	}

	fun <R> flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
		is Success -> transform(data)
		is Failure -> this
		is Loading -> Loading(progress)
	}

	fun getOrDefault(default: @UnsafeVariance T): T = when (this) {
		is Success -> data
		is Failure -> default
		is Loading -> default
	}

	fun getOrElse(onFailure: (Throwable) -> @UnsafeVariance T): T = when (this) {
		is Success -> data
		is Failure -> onFailure(error)
		is Loading -> throw IllegalStateException("Result is loading")
	}

	companion object {
		fun <T> success(data: T): Result<T> = Success(data)
		fun <T> failure(error: Throwable): Result<T> = Failure(error)
		fun <T> loading(progress: Float = 0f): Result<T> = Loading(progress)

		suspend fun <T> runCatching(block: suspend () -> T): Result<T> {
			return try {
				success(block())
			} catch (e: Exception) {
				failure(e)
			}
		}
	}
}

/**
 * Extension function to convert a Result to a Flow.
 */
fun <T> Result<T>.toFlow(): kotlinx.coroutines.flow.Flow<Result<T>> {
	return kotlinx.coroutines.flow.flow {
		emit(this@toFlow)
	}
}
