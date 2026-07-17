package org.koharu.miyo.core.di.expect

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

actual class NetworkMonitor(private val context: Context) {
	actual suspend fun isConnected(): Boolean {
		val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val network = connectivityManager.activeNetwork ?: return false
		val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
		return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
	}

	actual suspend fun isWifiConnected(): Boolean {
		val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val network = connectivityManager.activeNetwork ?: return false
		val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
		return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
	}

	actual suspend fun isMobileDataConnected(): Boolean {
		val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val network = connectivityManager.activeNetwork ?: return false
		val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
		return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
	}

	actual suspend fun getConnectionType(): ConnectionType {
		val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val network = connectivityManager.activeNetwork ?: return ConnectionType("NONE")
		val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return ConnectionType("NONE")

		return when {
			capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType("WIFI")
			capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType("MOBILE")
			capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectionType("ETHERNET")
			else -> ConnectionType("OTHER")
		}
	}
}

actual class ConnectionType actual constructor(actual val name: String)

actual fun createNetworkMonitor(): NetworkMonitor {
	throw UnsupportedOperationException("Use Android-specific initialization with Context")
}
