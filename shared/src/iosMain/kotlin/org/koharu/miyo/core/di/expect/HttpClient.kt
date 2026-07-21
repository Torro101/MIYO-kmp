package org.koharu.miyo.core.di.expect

actual class HttpClient actual constructor() {
	actual suspend fun get(url: String, headers: Map<String, String>): HttpResponse =
		HttpResponse(0, "", emptyMap())

	actual suspend fun post(url: String, body: String, headers: Map<String, String>): HttpResponse =
		HttpResponse(0, "", emptyMap())

	actual fun close() = Unit
}

actual class HttpResponse(
	actual val code: Int,
	actual val body: String,
	actual val headers: Map<String, String>,
)

actual fun createHttpClient(): HttpClient = HttpClient()
