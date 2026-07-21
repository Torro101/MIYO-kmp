package org.koharu.miyo.core.di.expect

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import org.koharu.miyo.core.os.AndroidContextHolder

actual fun getFileSystem(): FileSystem = FileSystem.SYSTEM

actual fun getCacheDir(): Path =
	if (AndroidContextHolder.isInitialized) {
		AndroidContextHolder.applicationContext.cacheDir.absolutePath.toPath()
	} else {
		"/tmp/miyo-cache".toPath()
	}

actual fun getFilesDir(): Path =
	if (AndroidContextHolder.isInitialized) {
		AndroidContextHolder.applicationContext.filesDir.absolutePath.toPath()
	} else {
		"/tmp/miyo-files".toPath()
	}

actual fun getTempDir(): Path =
	if (AndroidContextHolder.isInitialized) {
		AndroidContextHolder.applicationContext.cacheDir.absolutePath.toPath()
	} else {
		"/tmp/miyo-temp".toPath()
	}
