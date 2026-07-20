package org.koharu.miyo.core.prefs

import android.net.ConnectivityManager

fun NetworkPolicy.isNetworkAllowed(cm: ConnectivityManager): Boolean = when (this) {
	NetworkPolicy.NEVER -> false
	NetworkPolicy.ALWAYS -> true
	NetworkPolicy.NON_METERED -> !cm.isActiveNetworkMetered
}
