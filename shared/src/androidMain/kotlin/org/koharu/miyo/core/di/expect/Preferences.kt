package org.koharu.miyo.core.di.expect

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koharu.miyo.core.os.AndroidContextHolder

actual class Preferences internal constructor(
	private val prefs: SharedPreferences,
) {
	actual fun getString(key: String, defaultValue: String): String =
		prefs.getString(key, defaultValue) ?: defaultValue

	actual fun getInt(key: String, defaultValue: Int): Int =
		prefs.getInt(key, defaultValue)

	actual fun getLong(key: String, defaultValue: Long): Long =
		prefs.getLong(key, defaultValue)

	actual fun getFloat(key: String, defaultValue: Float): Float =
		prefs.getFloat(key, defaultValue)

	actual fun getBoolean(key: String, defaultValue: Boolean): Boolean =
		prefs.getBoolean(key, defaultValue)

	actual fun getStringSet(key: String, defaultValue: Set<String>): Set<String> =
		prefs.getStringSet(key, defaultValue) ?: defaultValue

	actual fun putString(key: String, value: String) {
		prefs.edit().putString(key, value).apply()
	}

	actual fun putInt(key: String, value: Int) {
		prefs.edit().putInt(key, value).apply()
	}

	actual fun putLong(key: String, value: Long) {
		prefs.edit().putLong(key, value).apply()
	}

	actual fun putFloat(key: String, value: Float) {
		prefs.edit().putFloat(key, value).apply()
	}

	actual fun putBoolean(key: String, value: Boolean) {
		prefs.edit().putBoolean(key, value).apply()
	}

	actual fun putStringSet(key: String, values: Set<String>) {
		prefs.edit().putStringSet(key, values).apply()
	}

	actual fun remove(key: String) {
		prefs.edit().remove(key).apply()
	}

	actual fun clear() {
		prefs.edit().clear().apply()
	}

	actual fun contains(key: String): Boolean = prefs.contains(key)

	actual fun observe(key: String): Flow<String> = callbackFlow {
		trySend(getString(key, ""))
		val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changed ->
			if (changed == null || changed == key) {
				trySend(getString(key, ""))
			}
		}
		prefs.registerOnSharedPreferenceChangeListener(listener)
		awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
	}.distinctUntilChanged()

	actual fun observeInt(key: String): Flow<Int> = callbackFlow {
		trySend(getInt(key, 0))
		val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changed ->
			if (changed == null || changed == key) {
				trySend(getInt(key, 0))
			}
		}
		prefs.registerOnSharedPreferenceChangeListener(listener)
		awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
	}.distinctUntilChanged()

	actual fun observeBoolean(key: String): Flow<Boolean> = callbackFlow {
		trySend(getBoolean(key, false))
		val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changed ->
			if (changed == null || changed == key) {
				trySend(getBoolean(key, false))
			}
		}
		prefs.registerOnSharedPreferenceChangeListener(listener)
		awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
	}.distinctUntilChanged()
}

actual fun createPreferences(name: String): Preferences {
	val ctx = AndroidContextHolder.applicationContext
	return Preferences(ctx.getSharedPreferences(name, Context.MODE_PRIVATE))
}
