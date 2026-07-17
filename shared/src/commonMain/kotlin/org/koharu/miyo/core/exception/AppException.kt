package org.koharu.miyo.core.exception

/**
 * Cross-platform application exceptions.
 */
sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause) {
	class NetworkException(message: String, cause: Throwable? = null) : AppException(message, cause)
	class DatabaseException(message: String, cause: Throwable? = null) : AppException(message, cause)
	class ParseException(message: String, cause: Throwable? = null) : AppException(message, cause)
	class AuthenticationException(message: String, cause: Throwable? = null) : AppException(message, cause)
	class AuthorizationException(message: String, cause: Throwable? = null) : AppException(message, cause)
	class NotFoundException(message: String, cause: Throwable? = null) : AppException(message, cause)
	class RateLimitException(message: String, cause: Throwable? = null) : AppException(message, cause)
	class CacheException(message: String, cause: Throwable? = null) : AppException(message, cause)
	class ValidationException(message: String, cause: Throwable? = null) : AppException(message, cause)
	class InitializationException(message: String, cause: Throwable? = null) : AppException(message, cause)
	class PlatformNotSupportedException(message: String) : AppException(message)
	class UnsupportedFeatureException(message: String) : AppException(message)

	companion object {
		fun network(message: String, cause: Throwable? = null) = NetworkException(message, cause)
		fun database(message: String, cause: Throwable? = null) = DatabaseException(message, cause)
		fun parse(message: String, cause: Throwable? = null) = ParseException(message, cause)
		fun auth(message: String, cause: Throwable? = null) = AuthenticationException(message, cause)
		fun forbidden(message: String, cause: Throwable? = null) = AuthorizationException(message, cause)
		fun notFound(message: String, cause: Throwable? = null) = NotFoundException(message, cause)
		fun rateLimited(message: String, cause: Throwable? = null) = RateLimitException(message, cause)
		fun cache(message: String, cause: Throwable? = null) = CacheException(message, cause)
		fun validation(message: String, cause: Throwable? = null) = ValidationException(message, cause)
		fun init(message: String, cause: Throwable? = null) = InitializationException(message, cause)
	}
}
