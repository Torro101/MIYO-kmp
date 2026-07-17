package org.koharu.miyo.core.di.expect

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

actual class Preferences(private val prefs: SharedPreferences) {
	private val listeners = mutableMapOf<String, MutableStateFlow<String>>()

	actual fun getString(key: String, defaultValue: String): String {
		return prefs.getString(key, defaultValue) ?: defaultValue
	}

	actual fun getInt(key: String, defaultValue: Int): Int {
		return prefs.getInt(key, defaultValue)
	}

	actual fun getLong(key: String, defaultValue: Long): Long {
		return prefs.getLong(key, defaultValue)
	}

	actual fun getFloat(key: String, defaultValue: Float): Float {
		return prefs.getFloat(key, defaultValue)
	}

	actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
		return prefs.getBoolean(key, defaultValue)
	}

	actual fun getStringSet(key: String, defaultValue: Set<String>): Set<String> {
		return prefs.getStringSet(key, defaultValue) ?: defaultValue
	}

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

	actual fun contains(key: String): Boolean {
		return prefs.contains(key)
	}

	actual fun observe(key: String): Flow<String> {
		val state = listeners.getOrPut(key) { MutableStateFlow(getString(key)) }
		prefs.registerOnSharedPreferenceChangeListener { _, changedKey ->
			if (changedKey == key) {
				state.value = getString(key)
			}
		}
		return state
	}

	actual fun observeInt(key: String): Flow<Int> {
		return flow {
			val state = MutableStateFlow(getInt(key))
			prefs.registerOnSharedPreferenceChangeListener { _, changedKey ->
				if (changedKey == key) {
					state.value = getInt(key)
				}
			}
			emit(state.value)
		}
	}

	actual fun observeBoolean(key: String): Flow<Boolean> {
		return flow {
			val state = MutableStateFlow(getBoolean(key))
			prefs.registerOnSharedPreferenceChangeListener { _, changedKey ->
				if (changedKey == key) {
					state.value = getBoolean(key)
				}
			}
			emit(state.value)
		}
	}
}

actual fun createPreferences(name: String): Preferences {
	// Note: This requires a Context to be initialized
	// In practice, use the Android-specific initialization
	throw UnsupportedOperationException("Use Android-specific initialization")
}
