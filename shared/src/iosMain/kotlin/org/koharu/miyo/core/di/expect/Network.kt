package org.koharu.miyo.core.di.expect

actual class NetworkMonitor actual constructor() {
	actual suspend fun isConnected(): Boolean = true
	actual suspend fun isWifiConnected(): Boolean = false
	actual suspend fun isMobileDataConnected(): Boolean = false
	actual suspend fun getConnectionType(): ConnectionType = ConnectionType("UNKNOWN")
}

actual class ConnectionType(actual val name: String)

actual fun createNetworkMonitor(): NetworkMonitor = NetworkMonitor()
