package org.koharu.miyo.core.util.ext

/** Shared default — flavor overrides can live in :app if needed. */
fun Throwable.printStackTraceDebug() {
	printStackTrace()
}

fun assertNotInMainThread() = Unit
