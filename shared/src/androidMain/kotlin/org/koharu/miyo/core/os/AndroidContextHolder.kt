package org.koharu.miyo.core.os

import android.content.Context

/**
 * Holds Application context for expect/actual factories that need Android services.
 */
object AndroidContextHolder {
	@Volatile
	private var appContext: Context? = null

	fun init(context: Context) {
		appContext = context.applicationContext
	}

	val applicationContext: Context
		get() = appContext
			?: error("AndroidContextHolder not initialized — call AndroidContextHolder.init() in Application.onCreate")

	val isInitialized: Boolean get() = appContext != null
}
