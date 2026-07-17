package org.koharu.miyo.core.config

import kotlinx.serialization.Serializable

/**
 * Cross-platform application configuration.
 */
@Serializable
data class AppConfig(
	val appName: String = "Miyo",
	val version: String = "1.0.0",
	val buildNumber: Int = 1,
	val isDebug: Boolean = false,
	val environment: Environment = Environment.PRODUCTION,
	val logging: LoggingConfig = LoggingConfig(),
	val network: NetworkConfig = NetworkConfig(),
	val cache: CacheConfig = CacheConfig(),
	val database: DatabaseConfig = DatabaseConfig()
)

@Serializable
enum class Environment {
	DEVELOPMENT,
	STAGING,
	PRODUCTION
}

@Serializable
data class LoggingConfig(
	val enabled: Boolean = true,
	val level: LogLevel = LogLevel.INFO,
	val tag: String = "Miyo"
)

@Serializable
enum class LogLevel {
	VERBOSE,
	DEBUG,
	INFO,
	WARN,
	ERROR
}

@Serializable
data class NetworkConfig(
	val baseUrl: String = "",
	val timeoutMs: Long = 30_000,
	val maxRetries: Int = 3,
	val rateLimitRequests: Int = 10,
	val rateLimitWindowMs: Long = 60_000,
	val userAgent: String = "Miyo/1.0.0"
)

@Serializable
data class CacheConfig(
	val enabled: Boolean = true,
	val maxSize: Int = 1000,
	val expirationTimeMs: Long = 30 * 60 * 1000, // 30 minutes
	val diskCacheSize: Long = 100 * 1024 * 1024 // 100 MB
)

@Serializable
data class DatabaseConfig(
	val name: String = "miyo_database",
	val version: Int = 1,
	val enableMigrations: Boolean = true,
	val enableWAL: Boolean = true
)
