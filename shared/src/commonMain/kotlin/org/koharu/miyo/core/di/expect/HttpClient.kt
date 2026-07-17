package org.koharu.miyo.core.di.expect

/**
 * Platform-agnostic HTTP client interface.
 * Android: backed by OkHttp
 * iOS: backed by NSURLSession
 */
expect class HttpClient {
	suspend fun get(url: String, headers: Map<String, String> = emptyMap()): HttpResponse
	suspend fun post(url: String, body: String, headers: Map<String, String> = emptyMap()): HttpResponse
	fun close()
}

expect class HttpResponse {
	val code: Int
	val body: String
	val headers: Map<String, String>
}

expect fun createHttpClient(): HttpClient
