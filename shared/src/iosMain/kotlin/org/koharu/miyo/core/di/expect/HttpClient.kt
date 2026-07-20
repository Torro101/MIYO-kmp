package org.koharu.miyo.core.di.expect

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.setHTTPBody
import platform.Foundation.setHTTPMethod
import platform.Foundation.setValue
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class HttpClient {
	private val session = NSURLSession.sharedSession

	actual suspend fun get(url: String, headers: Map<String, String>): HttpResponse =
		perform(url, "GET", null, headers)

	actual suspend fun post(
		url: String,
		body: String,
		headers: Map<String, String>,
	): HttpResponse = perform(url, "POST", body, headers)

	private suspend fun perform(
		url: String,
		method: String,
		body: String?,
		headers: Map<String, String>,
	): HttpResponse = suspendCancellableCoroutine { cont ->
		val nsUrl = NSURL.URLWithString(url)
		if (nsUrl == null) {
			cont.resumeWithException(IllegalArgumentException("Invalid URL: $url"))
			return@suspendCancellableCoroutine
		}
		val request = NSMutableURLRequest.requestWithURL(nsUrl)
		request.setHTTPMethod(method)
		headers.forEach { (key, value) ->
			request.setValue(value, forHTTPHeaderField = key)
		}
		if (body != null) {
			val nsBody = (body as NSString).dataUsingEncoding(NSUTF8StringEncoding)
			request.setHTTPBody(nsBody)
		}

		val task = session.dataTaskWithRequest(request) { data, response, error ->
			if (error != null) {
				cont.resumeWithException(RuntimeException(error.localizedDescription))
				return@dataTaskWithRequest
			}
			val httpResponse = response as? NSHTTPURLResponse
			val responseBody = data?.let {
				NSString.create(it, NSUTF8StringEncoding)?.toString()
			} ?: ""
			@Suppress("UNCHECKED_CAST")
			val responseHeaders = (httpResponse?.allHeaderFields as? Map<Any?, Any?>)
				?.mapKeys { it.key.toString() }
				?.mapValues { it.value.toString() }
				?: emptyMap()
			val code = httpResponse?.statusCode?.toInt() ?: 0
			cont.resume(HttpResponse(code, responseBody, responseHeaders))
		}
		task.resume()
		cont.invokeOnCancellation { task.cancel() }
	}

	actual fun close() {
		session.invalidateAndCancel()
	}
}

actual class HttpResponse actual constructor(
	actual val code: Int,
	actual val body: String,
	actual val headers: Map<String, String>,
)

actual fun createHttpClient(): HttpClient = HttpClient()
