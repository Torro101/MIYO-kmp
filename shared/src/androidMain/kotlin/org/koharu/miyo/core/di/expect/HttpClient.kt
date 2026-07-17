package org.koharu.miyo.core.di.expect

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

actual class HttpClient(private val client: OkHttpClient) {

	actual suspend fun get(url: String, headers: Map<String, String>): HttpResponse =
		withContext(Dispatchers.IO) {
			val requestBuilder = Request.Builder().url(url).get()
			headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
			val response = client.newCall(requestBuilder.build()).execute()
			HttpResponse(response)
		}

	actual suspend fun post(
		url: String,
		body: String,
		headers: Map<String, String>,
	): HttpResponse = withContext(Dispatchers.IO) {
		val requestBuilder =
			Request.Builder().url(url).post(body.toRequestBody(null))
		headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
		val response = client.newCall(requestBuilder.build()).execute()
		HttpResponse(response)
	}

	actual fun close() {
		client.dispatcher.executorService.shutdown()
		client.connectionPool.evictAll()
	}
}

actual class HttpResponse(private val response: okhttp3.Response) {
	actual val code: Int
		get() = response.code

	actual val body: String
		get() = response.body?.string() ?: ""

	actual val headers: Map<String, String>
		get() = response.headers.toMultimap().mapValues { it.value.joinToString(", ") }
}

actual fun createHttpClient(): HttpClient {
	val client =
		OkHttpClient.Builder()
			.connectTimeout(30, TimeUnit.SECONDS)
			.readTimeout(30, TimeUnit.SECONDS)
			.writeTimeout(30, TimeUnit.SECONDS)
			.build()
	return HttpClient(client)
}
