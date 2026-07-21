package org.koharu.miyo.core.di.expect

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

actual class HttpClient actual constructor() {
	private val client: OkHttpClient = OkHttpClient.Builder()
		.connectTimeout(30, TimeUnit.SECONDS)
		.readTimeout(30, TimeUnit.SECONDS)
		.writeTimeout(30, TimeUnit.SECONDS)
		.build()

	actual suspend fun get(url: String, headers: Map<String, String>): HttpResponse =
		withContext(Dispatchers.IO) {
			val requestBuilder = Request.Builder().url(url).get()
			headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
			client.newCall(requestBuilder.build()).execute().use { response ->
				HttpResponse(
					code = response.code,
					body = response.body?.string().orEmpty(),
					headers = response.headers.toMultimap().mapValues { it.value.joinToString(", ") },
				)
			}
		}

	actual suspend fun post(
		url: String,
		body: String,
		headers: Map<String, String>,
	): HttpResponse = withContext(Dispatchers.IO) {
		val requestBuilder = Request.Builder().url(url).post(body.toRequestBody(null))
		headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
		client.newCall(requestBuilder.build()).execute().use { response ->
			HttpResponse(
				code = response.code,
				body = response.body?.string().orEmpty(),
				headers = response.headers.toMultimap().mapValues { it.value.joinToString(", ") },
			)
		}
	}

	actual fun close() {
		client.dispatcher.executorService.shutdown()
		client.connectionPool.evictAll()
	}
}

actual class HttpResponse(
	actual val code: Int,
	actual val body: String,
	actual val headers: Map<String, String>,
)

actual fun createHttpClient(): HttpClient = HttpClient()
