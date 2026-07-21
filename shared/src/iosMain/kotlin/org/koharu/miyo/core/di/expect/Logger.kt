package org.koharu.miyo.core.di.expect

actual class Logger {
	actual fun d(tag: String, message: String) = Unit
	actual fun i(tag: String, message: String) = Unit
	actual fun w(tag: String, message: String) = Unit
	actual fun e(tag: String, message: String, throwable: Throwable?) = Unit
	actual fun v(tag: String, message: String) = Unit
}

actual fun createLogger(): Logger = Logger()
