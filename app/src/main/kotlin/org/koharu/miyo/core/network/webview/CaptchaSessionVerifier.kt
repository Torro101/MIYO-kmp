package org.koharu.miyo.core.network.webview

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.koharu.miyo.core.network.CloudFlareInterceptor
import org.koharu.miyo.core.network.CommonHeaders
import org.koharu.miyo.core.network.MangaHttpClient
import org.koharu.miyo.core.network.cookies.MutableCookieJar
import org.koitharu.kotatsu.parsers.network.CloudFlareHelper
import java.io.IOException
import javax.inject.Inject

class CaptchaSessionVerifier @Inject constructor(
	@MangaHttpClient private val okHttpClient: OkHttpClient,
	private val cookieJar: MutableCookieJar,
) {

	private val verifierClient by lazy(LazyThreadSafetyMode.PUBLICATION) {
		okHttpClient.newBuilder().apply {
			interceptors().removeAll { it is CloudFlareInterceptor }
		}.build()
	}

	suspend fun verify(
		url: String?,
		headers: Map<String, String>,
		sourceName: String?,
	): Result = runInterruptible(Dispatchers.IO) {
		val httpUrl = url?.toHttpUrlOrNull() ?: return@runInterruptible Result.Verified
		val cookies = cookieJar.loadForRequest(httpUrl)
		if (cookies.none { CloudFlareHelper.isCloudFlareCookie(it.name) }) {
			return@runInterruptible Result.UnverifiedNetworkError
		}
		val request = Request.Builder()
			.url(httpUrl)
			.get()
			.header(CommonHeaders.ACCEPT, ACCEPT_HTML)
			.header(CommonHeaders.CACHE_CONTROL, "no-cache")
			.apply {
				headers[CommonHeaders.REFERER]?.let {
					header(CommonHeaders.REFERER, it)
				}
				headers[CommonHeaders.USER_AGENT]?.let {
					header(CommonHeaders.USER_AGENT, it)
				}
				sourceName?.let {
					header(CommonHeaders.MANGA_SOURCE, it)
				}
			}
			.build()
		try {
			verifierClient.newCall(request).execute().use { response ->
				when (CloudFlareHelper.checkResponseForProtection(response)) {
					CloudFlareHelper.PROTECTION_BLOCKED,
					CloudFlareHelper.PROTECTION_CAPTCHA -> Result.NeedsCaptcha

					else -> Result.Verified
				}
			}
		} catch (e: IOException) {
			Result.UnverifiedNetworkError
		}
	}

	fun clearCloudFlareCookies(url: HttpUrl) {
		runCatching {
			cookieJar.removeCookies(url) { cookie ->
				CloudFlareHelper.isCloudFlareCookie(cookie.name)
			}
			cookieJar.flush()
		}
	}

	enum class Result {
		Verified,
		NeedsCaptcha,
		UnverifiedNetworkError,
	}

	private companion object {

		private const val ACCEPT_HTML =
			"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
	}
}
