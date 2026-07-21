package org.koharu.miyo.core.di.expect

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

actual class Preferences {
	private val store = mutableMapOf<String, Any?>()

	actual fun getString(key: String, defaultValue: String): String =
		store[key] as? String ?: defaultValue

	actual fun getInt(key: String, defaultValue: Int): Int =
		store[key] as? Int ?: defaultValue

	actual fun getLong(key: String, defaultValue: Long): Long =
		store[key] as? Long ?: defaultValue

	actual fun getFloat(key: String, defaultValue: Float): Float =
		store[key] as? Float ?: defaultValue

	actual fun getBoolean(key: String, defaultValue: Boolean): Boolean =
		store[key] as? Boolean ?: defaultValue

	actual fun getStringSet(key: String, defaultValue: Set<String>): Set<String> {
		@Suppress("UNCHECKED_CAST")
		return store[key] as? Set<String> ?: defaultValue
	}

	actual fun putString(key: String, value: String) { store[key] = value }
	actual fun putInt(key: String, value: Int) { store[key] = value }
	actual fun putLong(key: String, value: Long) { store[key] = value }
	actual fun putFloat(key: String, value: Float) { store[key] = value }
	actual fun putBoolean(key: String, value: Boolean) { store[key] = value }
	actual fun putStringSet(key: String, values: Set<String>) { store[key] = values }
	actual fun remove(key: String) { store.remove(key) }
	actual fun clear() { store.clear() }
	actual fun contains(key: String): Boolean = store.containsKey(key)

	actual fun observe(key: String): Flow<String> = flowOf(getString(key, ""))
	actual fun observeInt(key: String): Flow<Int> = flowOf(getInt(key, 0))
	actual fun observeBoolean(key: String): Flow<Boolean> = flowOf(getBoolean(key, false))
}

actual fun createPreferences(name: String): Preferences = Preferences()
