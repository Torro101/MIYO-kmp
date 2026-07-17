package org.koharu.miyo.core.di.expect

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

actual fun getFileSystem(): FileSystem = FileSystem.SYSTEM

actual fun getCacheDir(): Path {
	val paths =
		NSSearchPathForDirectoriesInDomains(
			NSCachesDirectory,
			NSUserDomainMask,
			true,
		)
	return (paths.first() as String).toPath()
}

actual fun getFilesDir(): Path {
	val paths =
		NSSearchPathForDirectoriesInDomains(
			NSDocumentDirectory,
			NSUserDomainMask,
			true,
		)
	return (paths.first() as String).toPath()
}

actual fun getTempDir(): Path = NSTemporaryDirectory().toPath()
