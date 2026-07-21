package org.koharu.miyo.core.di.expect

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

actual fun getFileSystem(): FileSystem = FileSystem.SYSTEM

actual fun getCacheDir(): Path = "/tmp/miyo-cache".toPath()

actual fun getFilesDir(): Path = "/tmp/miyo-files".toPath()

actual fun getTempDir(): Path = "/tmp/miyo-temp".toPath()
