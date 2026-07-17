package org.koharu.miyo.core.di.expect

import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.Foundation.NSURLResponse
import platform.Foundation.URLSession
import platform.Foundation.dataUsingEncoding
import platform.Foundation.create
import platform.darwin.NSObject
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class HttpClient {
	private val session = NSURLSession.sharedSession

	actual suspend fun get(url: String, headers: Map<String, String>): HttpResponse =
		suspendCancellableCoroutine { cont ->
			val nsUrl = NSURL(string = url)
			val request = NSMutableURLRequest(nsUrl)
			request.HTTPMethod = "GET"
			headers.forEach { (key, value) -> request.setValue(value, forHTTPHeaderField = key) }

			val task =
				session.dataTaskWithRequest(request) { data, response, error ->
					if (error != null) {
						cont.resumeWithException(RuntimeException(error.localizedDescription))
						return@dataTaskWithRequest
					}
					val httpResponse = response as? NSHTTPURLResponse
					val body =
						data?.let {
							NSString.create(it, NSUTF8StringEncoding) as String
						} ?: ""
					val responseHeaders =
						httpResponse?.allHeaderFields?.mapValues { it.value.toString() }
							?: emptyMap()
					cont.resume(
						HttpResponse(httpResponse?.statusCode?.toInt() ?: 0, body, responseHeaders)
					)
				}
			task.resume()
			cont.invokeOnCancellation { task.cancel() }
		}

	actual suspend fun post(
		url: String,
		body: String,
		headers: Map<String, String>,
	): HttpResponse = suspendCancellableCoroutine { cont ->
		val nsUrl = NSURL(string = url)
		val request = NSMutableURLRequest(nsUrl)
		request.HTTPMethod = "POST"
		request.HTTPBody =
			(body as NSString).dataUsingEncoding(NSUTF8StringEncoding)
		headers.forEach { (key, value) -> request.setValue(value, forHTTPHeaderField = key) }

		val task =
			session.dataTaskWithRequest(request) { data, response, error ->
				if (error != null) {
					cont.resumeWithException(RuntimeException(error.localizedDescription))
					return@dataTaskWithRequest
				}
				val httpResponse = response as? NSHTTPURLResponse
				val responseBody =
					data?.let {
						NSString.create(it, NSUTF8StringEncoding) as String
					} ?: ""
				val responseHeaders =
					httpResponse?.allHeaderFields?.mapValues { it.value.toString() }
						?: emptyMap()
				cont.resume(
					HttpResponse(
						httpResponse?.statusCode?.toInt() ?: 0,
						responseBody,
						responseHeaders,
					)
				)
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
