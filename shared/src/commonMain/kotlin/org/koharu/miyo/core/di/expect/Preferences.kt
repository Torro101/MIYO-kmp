package org.koharu.miyo.core.di.expect

import kotlinx.coroutines.flow.Flow

/**
 * Platform-agnostic preferences interface.
 * Android: backed by SharedPreferences
 * iOS: backed by NSUserDefaults
 */
expect class Preferences {
	fun getString(key: String, defaultValue: String = ""): String
	fun getInt(key: String, defaultValue: Int = 0): Int
	fun getLong(key: String, defaultValue: Long = 0L): Long
	fun getFloat(key: String, defaultValue: Float = 0f): Float
	fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
	fun getStringSet(key: String, defaultValue: Set<String> = emptySet()): Set<String>

	fun putString(key: String, value: String)
	fun putInt(key: String, value: Int)
	fun putLong(key: String, value: Long)
	fun putFloat(key: String, value: Float)
	fun putBoolean(key: String, value: Boolean)
	fun putStringSet(key: String, values: Set<String>)

	fun remove(key: String)
	fun clear()
	fun contains(key: String): Boolean

	fun observe(key: String): Flow<String>
	fun observeInt(key: String): Flow<Int>
	fun observeBoolean(key: String): Flow<Boolean>
}

expect fun createPreferences(name: String): Preferences
