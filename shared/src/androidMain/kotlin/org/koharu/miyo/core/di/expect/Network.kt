package org.koharu.miyo.core.di.expect

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import org.koharu.miyo.core.os.AndroidContextHolder

actual class NetworkMonitor actual constructor() {
	private val context: Context get() = AndroidContextHolder.applicationContext

	actual suspend fun isConnected(): Boolean {
		val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val network = cm.activeNetwork ?: return false
		val capabilities = cm.getNetworkCapabilities(network) ?: return false
		return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
	}

	actual suspend fun isWifiConnected(): Boolean {
		val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val network = cm.activeNetwork ?: return false
		val capabilities = cm.getNetworkCapabilities(network) ?: return false
		return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
	}

	actual suspend fun isMobileDataConnected(): Boolean {
		val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val network = cm.activeNetwork ?: return false
		val capabilities = cm.getNetworkCapabilities(network) ?: return false
		return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
	}

	actual suspend fun getConnectionType(): ConnectionType {
		val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val network = cm.activeNetwork ?: return ConnectionType("NONE")
		val capabilities = cm.getNetworkCapabilities(network) ?: return ConnectionType("NONE")
		return when {
			capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType("WIFI")
			capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType("MOBILE")
			capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectionType("ETHERNET")
			else -> ConnectionType("OTHER")
		}
	}
}

actual class ConnectionType(actual val name: String)

actual fun createNetworkMonitor(): NetworkMonitor = NetworkMonitor()
