package org.koharu.miyo.core.di.expect

/**
 * Platform-agnostic network state monitoring.
 */
expect class NetworkMonitor() {
	suspend fun isConnected(): Boolean
	suspend fun isWifiConnected(): Boolean
	suspend fun isMobileDataConnected(): Boolean
	suspend fun getConnectionType(): ConnectionType
}

expect class ConnectionType {
	val name: String
}

expect fun createNetworkMonitor(): NetworkMonitor
