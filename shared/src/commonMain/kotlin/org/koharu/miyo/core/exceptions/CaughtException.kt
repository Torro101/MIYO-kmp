package org.koharu.miyo.core.exceptions

class CaughtException(
	override val cause: Throwable
) : RuntimeException("${cause::class.simpleName}(${cause.message})", cause)
