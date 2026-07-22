package org.koharu.miyo.core.di.expect

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSFileManager
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUserDomainMask

actual fun getFileSystem(): FileSystem = FileSystem.SYSTEM

actual fun getCacheDir(): Path {
	val urls = NSFileManager.defaultManager.URLsForDirectory(NSCachesDirectory, NSUserDomainMask)
	val path = urls.firstOrNull()?.path ?: (NSTemporaryDirectory() + "miyo-cache")
	return path.toString().toPath()
}

actual fun getFilesDir(): Path {
	val urls = NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
	val path = urls.firstOrNull()?.path ?: (NSTemporaryDirectory() + "miyo-files")
	return path.toString().toPath()
}

actual fun getTempDir(): Path = NSTemporaryDirectory().toPath()
