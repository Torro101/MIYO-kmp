package org.koharu.miyo.core.di.expect

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSTemporaryDirectory

actual fun getFileSystem(): FileSystem = FileSystem.SYSTEM

actual fun getCacheDir(): Path =
	(NSHomeDirectory() + "/Library/Caches/miyo").toPath()

actual fun getFilesDir(): Path =
	(NSHomeDirectory() + "/Documents/miyo").toPath()

actual fun getTempDir(): Path = NSTemporaryDirectory().trimEnd('/').toPath()
