package org.koharu.miyo.core.di.expect

import okio.FileSystem
import okio.Path
import okio.Source
import okio.Sink

/**
 * Platform-agnostic file system operations.
 * Uses okio for cross-platform I/O.
 */
expect fun getFileSystem(): FileSystem

expect fun getCacheDir(): Path

expect fun getFilesDir(): Path

expect fun getTempDir(): Path
