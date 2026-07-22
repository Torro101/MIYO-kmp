package org.koharu.miyo.core.di.expect

import platform.Foundation.NSLog

actual class Logger {
	actual fun d(tag: String, message: String) {
		NSLog("D/%s: %s", tag, message)
	}

	actual fun i(tag: String, message: String) {
		NSLog("I/%s: %s", tag, message)
	}

	actual fun w(tag: String, message: String) {
		NSLog("W/%s: %s", tag, message)
	}

	actual fun e(tag: String, message: String, throwable: Throwable?) {
		if (throwable != null) {
			NSLog("E/%s: %s — %s", tag, message, throwable.message ?: throwable.toString())
		} else {
			NSLog("E/%s: %s", tag, message)
		}
	}

	actual fun v(tag: String, message: String) {
		NSLog("V/%s: %s", tag, message)
	}
}

actual fun createLogger(): Logger = Logger()
