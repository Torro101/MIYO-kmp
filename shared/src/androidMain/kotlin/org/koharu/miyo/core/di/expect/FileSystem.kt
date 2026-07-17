package org.koharu.miyo.core.di.expect

import android.content.Context
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

private lateinit var appContext: Context

fun initFileSystem(context: Context) {
	appContext = context.applicationContext
}

actual fun getFileSystem(): FileSystem = FileSystem.SYSTEM

actual fun getCacheDir(): Path =
	if (::appContext.isInitialized) {
		appContext.cacheDir.absolutePath.toPath()
	} else {
		"/tmp/miyo-cache".toPath()
	}

actual fun getFilesDir(): Path =
	if (::appContext.isInitialized) {
		appContext.filesDir.absolutePath.toPath()
	} else {
		"/tmp/miyo-files".toPath()
	}

actual fun getTempDir(): Path =
	if (::appContext.isInitialized) {
		appContext.cacheDir.absolutePath.toPath()
	} else {
		"/tmp/miyo-temp".toPath()
	}
