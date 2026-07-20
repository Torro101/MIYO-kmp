package org.koharu.miyo.core.nativeio

import java.io.File

/**
 * Android actuals delegate to the existing JNI-backed helpers.
 * Instantiates without Hilt so common code can call without DI.
 */
actual object PlatformNativeImage {
	private val probe by lazy { NativeImageProbe() }

	actual val isAvailable: Boolean
		get() = probe.isAvailable

	actual fun probeMime(path: String): String? {
		if (!isAvailable) return null
		val mime = probe.probeFormat(File(path))
		return mime.ifBlank { null }
	}

	actual fun probeDimensions(path: String): Triple<Int, Int, Boolean>? {
		if (!isAvailable) return null
		val info = probe.probe(File(path)) ?: return null
		return Triple(info.width, info.height, info.isCorrupt)
	}
}

actual object PlatformNativeZip {
	private val writer by lazy { NativeZipWriter() }

	actual val isAvailable: Boolean
		get() = writer.isAvailable

	actual fun open(path: String, append: Boolean): Long {
		if (!isAvailable) return 0L
		return writer.openZip(File(path), append)
	}

	actual fun close(handle: Long) {
		if (handle != 0L) writer.closeZip(handle)
	}

	actual fun finish(handle: Long): Boolean {
		if (handle == 0L) return false
		return writer.finishZip(handle)
	}

	actual fun addDirectory(handle: Long, entryName: String): Boolean {
		if (handle == 0L) return false
		return writer.addDirectory(handle, entryName)
	}

	actual fun appendFile(handle: Long, entryName: String, sourcePath: String): Boolean {
		if (handle == 0L) return false
		return writer.appendFileFromDisk(handle, entryName, File(sourcePath))
	}
}
