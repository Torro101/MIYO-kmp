package org.koharu.miyo.core.network

import org.koharu.miyo.core.di.expect.HttpClient
import org.koharu.miyo.core.di.expect.createHttpClient

/**
 * Cross-platform API client for manga sources.
 */
class ApiClient {
	private val httpClient = createHttpClient()

	suspend fun get(url: String, headers: Map<String, String> = emptyMap()): ApiResponse {
		return try {
			val response = httpClient.get(url, headers)
			ApiResponse(
				success = response.code in 200..299,
				code = response.code,
				body = response.body,
				headers = response.headers
			)
		} catch (e: Exception) {
			ApiResponse(
				success = false,
				code = -1,
				body = "",
				headers = emptyMap(),
				error = e.message
			)
		}
	}

	suspend fun post(
		url: String,
		body: String,
		headers: Map<String, String> = emptyMap()
	): ApiResponse {
		return try {
			val response = httpClient.post(url, body, headers)
			ApiResponse(
				success = response.code in 200..299,
				code = response.code,
				body = response.body,
				headers = response.headers
			)
		} catch (e: Exception) {
			ApiResponse(
				success = false,
				code = -1,
				body = "",
				headers = emptyMap(),
				error = e.message
			)
		}
	}

	fun close() {
		httpClient.close()
	}
}

data class ApiResponse(
	val success: Boolean,
	val code: Int,
	val body: String,
	val headers: Map<String, String>,
	val error: String? = null
)
