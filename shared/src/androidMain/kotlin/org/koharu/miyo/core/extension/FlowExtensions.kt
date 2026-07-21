package org.koharu.miyo.core.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.koharu.miyo.core.util.Result

fun <T> Flow<T>.asResult(): Flow<Result<T>> {
	return this
		.map<T, Result<T>> { Result.Success(it) }
		.catch { emit(Result.Failure(it)) }
}

fun <T> Flow<Result<T>>.getOrDefault(default: T): Flow<T> =
	this.map { it.getOrDefault(default) }

fun <T> Flow<Result<T>>.getOrNull(): Flow<T?> =
	this.map { it.getOrNull() }

fun <T, R> Flow<Result<T>>.mapSuccess(transform: (T) -> R): Flow<Result<R>> =
	this.map { result ->
		when (result) {
			is Result.Success -> Result.Success(transform(result.data))
			is Result.Failure -> Result.Failure(result.error)
			is Result.Loading -> Result.Loading(result.progress)
		}
	}

fun <T> Flow<Result<T>>.filterSuccess(): Flow<T> =
	this.mapNotNull { it.getOrNull() }
