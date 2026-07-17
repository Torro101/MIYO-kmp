package org.koharu.miyo.core.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.onCompletion
import org.koharu.miyo.core.util.Result

/**
 * Cross-platform Flow extensions for Result handling.
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> {
	return this
		.map<T, Result<T>> { Result.Success(it) }
		.catch { emit(Result.Failure(it)) }
}

fun <T> Flow<Result<T>>.onSuccess(action: suspend (T) -> Unit): Flow<Result<T>> {
	return this.map { result ->
		if (result is Result.Success) {
			action(result.data)
		}
		result
	}
}

fun <T> Flow<Result<T>>.onFailure(action: suspend (Throwable) -> Unit): Flow<Result<T>> {
	return this.map { result ->
		if (result is Result.Failure) {
			action(result.error)
		}
		result
	}
}

fun <T> Flow<Result<T>>.onLoading(action: suspend (Float) -> Unit): Flow<Result<T>> {
	return this.map { result ->
		if (result is Result.Loading) {
			action(result.progress)
		}
		result
	}
}

fun <T> Flow<Result<T>>.getOrDefault(default: T): Flow<T> {
	return this.map { result ->
		result.getOrDefault(default)
	}
}

fun <T> Flow<Result<T>>.getOrNull(): Flow<T?> {
	return this.map { result ->
		result.getOrNull()
	}
}

fun <T, R> Flow<Result<T>>.mapSuccess(transform: suspend (T) -> R): Flow<Result<R>> {
	return this.map { result ->
		result.map(transform)
	}
}

fun <T> Flow<Result<T>>.filterSuccess(): Flow<T> {
	return this.mapNotNull { result ->
		result.getOrNull()
	}
}

fun <T> Flow<Result<T>>.retryOnFailure(retries: Int = 3): Flow<Result<T>> {
	return this.catch { exception ->
		if (retries > 0) {
			emit(Result.Loading())
			// Retry logic would go here
		} else {
			emit(Result.Failure(exception))
		}
	}
}
