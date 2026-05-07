package org.koharu.miyo.browser.cloudflare

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.koharu.miyo.browser.BrowserClient
import org.koharu.miyo.core.network.cookies.MutableCookieJar
import org.koharu.miyo.core.network.webview.CaptchaNavigationGuard
import org.koharu.miyo.core.network.webview.adblock.AdBlock
import org.koitharu.kotatsu.parsers.network.CloudFlareHelper

private const val LOOP_COUNTER = 5

class CloudFlareClient(
	private val cookieJar: MutableCookieJar,
	private val callback: CloudFlareCallback,
	adBlock: AdBlock,
	private val targetUrl: String,
) : BrowserClient(callback, adBlock) {

	private val oldClearance = getClearance()
	private val targetHost = targetUrl.toHttpUrlOrNull()?.host
	private var counter = 0

	override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
		super.onPageStarted(view, url, favicon)
		checkClearance(url, acceptExistingClearance = false)
	}

	override fun onPageCommitVisible(view: WebView, url: String) {
		super.onPageCommitVisible(view, url)
		callback.onPageLoaded()
	}

	override fun onPageFinished(webView: WebView, url: String) {
		super.onPageFinished(webView, url)
		callback.onPageLoaded()
		checkClearance(url, acceptExistingClearance = true)
	}

	fun reset() {
		counter = 0
	}

	@Deprecated("Deprecated in Java")
	override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
		if (CaptchaNavigationGuard.shouldBlockMainFrameNavigation(url, targetUrl)) {
			callback.onPageLoaded()
			return true
		}
		return super.shouldOverrideUrlLoading(view, url)
	}

	override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
		val isMainFrame = request?.isForMainFrame ?: true
		if (isMainFrame && CaptchaNavigationGuard.shouldBlockMainFrameNavigation(request?.url?.toString(), targetUrl)) {
			callback.onPageLoaded()
			return true
		}
		return super.shouldOverrideUrlLoading(view, request)
	}

	private fun checkClearance(url: String?, acceptExistingClearance: Boolean) {
		val clearance = getClearance()
		if (clearance != null && (clearance != oldClearance || acceptExistingClearance && url.isTargetNavigation())) {
			callback.onCheckPassed()
		} else if (url.isChallengeNavigation()) {
			counter++
			if (counter >= LOOP_COUNTER) {
				reset()
				callback.onLoopDetected()
			}
		}
	}

	private fun getClearance() = CloudFlareHelper.getClearanceCookie(cookieJar, targetUrl)

	private fun String?.isTargetNavigation(): Boolean {
		val host = this?.toHttpUrlOrNull()?.host ?: return false
		val target = targetHost ?: return true
		return host.equals(target, ignoreCase = true) || host.endsWith(".$target", ignoreCase = true)
	}

	private fun String?.isChallengeNavigation(): Boolean {
		val value = this ?: return false
		val url = value.toHttpUrlOrNull() ?: return false
		if (!url.host.isTargetNavigationHost()) {
			return false
		}
		return value.contains("/cdn-cgi/", ignoreCase = true) ||
			value.contains("__cf_chl", ignoreCase = true) ||
			value.contains("cf_chl", ignoreCase = true)
	}

	private fun String.isTargetNavigationHost(): Boolean {
		val target = targetHost ?: return true
		return equals(target, ignoreCase = true) || endsWith(".$target", ignoreCase = true)
	}
}
