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

class CloudFlareClient(
	private val cookieJar: MutableCookieJar,
	private val callback: CloudFlareCallback,
	adBlock: AdBlock,
	private val targetUrl: String,
) : BrowserClient(callback, adBlock) {

	private var oldClearance = getClearance()
	private var hasSeenChallenge = false
	private var isPassed = false

	fun resetClearanceBaseline() {
		oldClearance = getClearance()
		hasSeenChallenge = false
		isPassed = false
	}

	override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
		super.onPageStarted(view, url, favicon)
		checkClearance()
	}

	override fun onPageCommitVisible(view: WebView, url: String) {
		super.onPageCommitVisible(view, url)
		callback.onPageLoaded()
		inspectChallengeState(view, url)
	}

	override fun onPageFinished(webView: WebView, url: String) {
		super.onPageFinished(webView, url)
		callback.onPageLoaded()
		checkClearance()
		inspectChallengeState(webView, url)
	}

	@Deprecated("Deprecated in Java")
	override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
		if (CaptchaNavigationGuard.shouldBlockMainFrameNavigation(url, targetUrl)) {
			view?.stopLoading()
			callback.onPageLoaded()
			return true
		}
		return super.shouldOverrideUrlLoading(view, url)
	}

	override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
		val isMainFrame = request?.isForMainFrame ?: true
		if (isMainFrame && CaptchaNavigationGuard.shouldBlockMainFrameNavigation(request?.url?.toString(), targetUrl)) {
			view?.stopLoading()
			callback.onPageLoaded()
			return true
		}
		return super.shouldOverrideUrlLoading(view, request)
	}

	private fun checkClearance() {
		val clearance = getClearance()
		if (clearance != null && clearance != oldClearance) {
			notifyCheckPassed()
		}
	}

	private fun getClearance() = CloudFlareHelper.getClearanceCookie(cookieJar, targetUrl)

	private fun inspectChallengeState(view: WebView, url: String?) {
		if (isPassed) {
			return
		}
		val pageUrl = url ?: view.url
		view.evaluateJavascript(PAGE_STATE_SCRIPT) { rawState ->
			if (isPassed) {
				return@evaluateJavascript
			}
			val state = rawState.orEmpty()
			val isChallenge = state.containsAny(CHALLENGE_MARKERS)
			if (isChallenge) {
				hasSeenChallenge = true
			}
			if (state.isChallengeSuccess() || (hasSeenChallenge && isTargetPage(pageUrl) && !isChallenge)) {
				notifyCheckPassed()
			}
		}
	}

	private fun notifyCheckPassed() {
		if (isPassed) {
			return
		}
		isPassed = true
		callback.onCheckPassed()
	}

	private fun isTargetPage(url: String?): Boolean {
		val host = url?.toHttpUrlOrNull()?.host ?: return false
		val targetHost = targetUrl.toHttpUrlOrNull()?.host ?: return false
		return host.equals(targetHost, ignoreCase = true) || host.endsWith(".$targetHost", ignoreCase = true)
	}

	private fun String.containsAny(markers: Array<String>): Boolean {
		return markers.any { contains(it, ignoreCase = true) }
	}

	private fun String.isChallengeSuccess(): Boolean {
		return contains("verification successful", ignoreCase = true) ||
			(contains("waiting for", ignoreCase = true) && contains("to respond", ignoreCase = true))
	}

	private companion object {

		private const val PAGE_STATE_SCRIPT =
			"(function(){return [document.title||'',document.body&&document.body.innerText||''].join('\\n');})()"

		private val CHALLENGE_MARKERS = arrayOf(
			"just a moment",
			"performing security verification",
			"verifies you are not a bot",
			"checking your browser",
			"cloudflare",
		)
	}
}
