package org.koharu.miyo.core.di.expect

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import platform.Foundation.NSUserDefaults
import platform.Foundation.standardUserDefaults

actual class Preferences(private val defaults: NSUserDefaults) {
	actual fun getString(key: String, defaultValue: String): String {
		return defaults.stringForKey(key) ?: defaultValue
	}

	actual fun getInt(key: String, defaultValue: Int): Int {
		val number = defaults.objectForKey(key) as? Number
		return number?.toInt() ?: defaultValue
	}

	actual fun getLong(key: String, defaultValue: Long): Long {
		val number = defaults.objectForKey(key) as? Number
		return number?.toLong() ?: defaultValue
	}

	actual fun getFloat(key: String, defaultValue: Float): Float {
		val number = defaults.objectForKey(key) as? Number
		return number?.toFloat() ?: defaultValue
	}

	actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
		val number = defaults.objectForKey(key) as? Number
		return number?.boolValue ?: defaultValue
	}

	actual fun getStringSet(key: String, defaultValue: Set<String>): Set<String> {
		val array = defaults.arrayForKey(key) ?: return defaultValue
		return array.filterIsInstance<String>().toSet()
	}

	actual fun putString(key: String, value: String) {
		defaults.setObject(value, forKey = key)
	}

	actual fun putInt(key: String, value: Int) {
		defaults.setInteger(value, forKey = key)
	}

	actual fun putLong(key: String, value: Long) {
		defaults.setInteger(value.toInt(), forKey = key)
	}

	actual fun putFloat(key: String, value: Float) {
		defaults.setDouble(value.toDouble(), forKey = key)
	}

	actual fun putBoolean(key: String, value: Boolean) {
		defaults.setBool(value, forKey = key)
	}

	actual fun putStringSet(key: String, values: Set<String>) {
		defaults.setObject(values.toList(), forKey = key)
	}

	actual fun remove(key: String) {
		defaults.removeObjectForKey(key)
	}

	actual fun clear() {
		val domain = defaults.persistentDomainForName(defaults.volatileDomainName)
		domain.keys.forEach { key ->
			defaults.removeObjectForKey(key)
		}
	}

	actual fun contains(key: String): Boolean {
		return defaults.objectForKey(key) != null
	}

	actual fun observe(key: String): Flow<String> {
		val state = MutableStateFlow(getString(key))
		return state
	}

	actual fun observeInt(key: String): Flow<Int> {
		val state = MutableStateFlow(getInt(key))
		return state
	}

	actual fun observeBoolean(key: String): Flow<Boolean> {
		val state = MutableStateFlow(getBoolean(key))
		return state
	}
}

actual fun createPreferences(name: String): Preferences {
	return Preferences(NSUserDefaults.standardUserDefaults)
}
