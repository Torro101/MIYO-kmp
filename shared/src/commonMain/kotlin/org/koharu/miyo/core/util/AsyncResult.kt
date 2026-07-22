package org.koharu.miyo.core.util

/**
 * Cross-platform async result (success / failure / loading).
 * Named [AsyncResult] so it does not shadow [kotlin.Result] or WorkManager Result.
 */
sealed class AsyncResult<out T> {
	data class Success<T>(val data: T) : AsyncResult<T>()
	data class Failure(val error: Throwable) : AsyncResult<Nothing>()
	data class Loading(val progress: Float = 0f) : AsyncResult<Nothing>()

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

	fun <R> map(transform: (T) -> R): AsyncResult<R> = when (this) {
		is Success -> Success(transform(data))
		is Failure -> this
		is Loading -> Loading(progress)
	}

	fun <R> flatMap(transform: (T) -> AsyncResult<R>): AsyncResult<R> = when (this) {
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
		is Loading -> throw IllegalStateException("AsyncResult is loading")
	}

	companion object {
		fun <T> success(data: T): AsyncResult<T> = Success(data)
		fun <T> failure(error: Throwable): AsyncResult<T> = Failure(error)
		fun <T> loading(progress: Float = 0f): AsyncResult<T> = Loading(progress)

		suspend fun <T> runCatching(block: suspend () -> T): AsyncResult<T> {
			return try {
				success(block())
			} catch (e: Exception) {
				failure(e)
			}
		}
	}
}

/** Extension function to convert an [AsyncResult] to a Flow. */
fun <T> AsyncResult<T>.toFlow(): kotlinx.coroutines.flow.Flow<AsyncResult<T>> {
	return kotlinx.coroutines.flow.flow {
		emit(this@toFlow)
	}
}
