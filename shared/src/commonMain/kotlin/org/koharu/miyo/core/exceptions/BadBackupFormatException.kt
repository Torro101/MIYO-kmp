package org.koharu.miyo.core.exceptions

class BadBackupFormatException(
	message: String? = null,
	cause: Throwable? = null,
) : Exception(message, cause)
