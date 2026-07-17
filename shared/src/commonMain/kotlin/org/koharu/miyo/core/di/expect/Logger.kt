package org.koharu.miyo.core.di.expect

/**
 * Platform-agnostic logging interface.
 * Android: backed by android.util.Log
 * iOS: backed by os.log or NSLog
 */
expect class Logger {
	fun d(tag: String, message: String)
	fun i(tag: String, message: String)
	fun w(tag: String, message: String)
	fun e(tag: String, message: String, throwable: Throwable? = null)
	fun v(tag: String, message: String)
}

expect fun createLogger(): Logger
