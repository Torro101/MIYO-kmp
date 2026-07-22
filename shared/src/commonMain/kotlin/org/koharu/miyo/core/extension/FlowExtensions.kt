package org.koharu.miyo.core.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.koharu.miyo.core.util.AsyncResult

fun <T> Flow<T>.asResult(): Flow<AsyncResult<T>> {
	return this
		.map<T, AsyncResult<T>> { AsyncResult.Success(it) }
		.catch { emit(AsyncResult.Failure(it)) }
}

fun <T> Flow<AsyncResult<T>>.getOrDefault(default: T): Flow<T> =
	this.map { it.getOrDefault(default) }

fun <T> Flow<AsyncResult<T>>.getOrNull(): Flow<T?> =
	this.map { it.getOrNull() }

fun <T, R> Flow<AsyncResult<T>>.mapSuccess(transform: (T) -> R): Flow<AsyncResult<R>> =
	this.map { result ->
		when (result) {
			is AsyncResult.Success -> AsyncResult.Success(transform(result.data))
			is AsyncResult.Failure -> AsyncResult.Failure(result.error)
			is AsyncResult.Loading -> AsyncResult.Loading(result.progress)
		}
	}

fun <T> Flow<AsyncResult<T>>.filterSuccess(): Flow<T> =
	this.mapNotNull { it.getOrNull() }
