package org.koharu.miyo.core.network

import okhttp3.Interceptor
import okhttp3.Response

/** Shared no-op (debug flavor may replace behavior later). */
class CurlLoggingInterceptor : Interceptor {
	override fun intercept(chain: Interceptor.Chain): Response {
		return chain.proceed(chain.request())
	}
}
