package org.koharu.miyo.browser.cloudflare

import android.graphics.Bitmap
import android.webkit.WebView
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.koharu.miyo.browser.BrowserClient
import org.koharu.miyo.core.network.cookies.MutableCookieJar
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
		checkClearance(url)
	}

	override fun onPageCommitVisible(view: WebView, url: String) {
		super.onPageCommitVisible(view, url)
		callback.onPageLoaded()
	}

	override fun onPageFinished(webView: WebView, url: String) {
		super.onPageFinished(webView, url)
		callback.onPageLoaded()
		checkClearance(url)
	}

	fun reset() {
		counter = 0
	}

	private fun checkClearance(url: String?) {
		val clearance = getClearance()
		if (clearance != null && clearance != oldClearance) {
			callback.onCheckPassed()
		} else if (url.isTargetNavigation()) {
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
		return host == target || host.endsWith(".$target")
	}
}
