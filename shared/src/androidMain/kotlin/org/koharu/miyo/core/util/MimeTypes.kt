package org.koharu.miyo.core.util

import android.os.Build
import android.webkit.MimeTypeMap
import org.jetbrains.annotations.Blocking
import org.koharu.miyo.core.util.ext.MimeType
import org.koharu.miyo.core.util.ext.toMimeTypeOrNull
import org.koitharu.kotatsu.parsers.util.nullIfEmpty
import org.koitharu.kotatsu.parsers.util.removeSuffix
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import java.io.File
import java.nio.file.Files
import coil3.util.MimeTypeMap as CoilMimeTypeMap

/**
 * Android MIME helpers. Coil3 [CoilMimeTypeMap] is opt-in via
 * `-opt-in=coil3.annotation.InternalCoilApi` in shared/build.gradle.kts.
 */
object MimeTypes {

	fun getMimeTypeFromExtension(fileName: String): MimeType? {
		val ext = getNormalizedExtension(fileName) ?: return null
		return CoilMimeTypeMap.getMimeTypeFromExtension(ext)?.toMimeTypeOrNull()
	}

	fun getMimeTypeFromUrl(url: String): MimeType? {
		return CoilMimeTypeMap.getMimeTypeFromUrl(url)?.toMimeTypeOrNull()
	}

	fun getExtension(mimeType: MimeType?): String? {
		return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType?.toString() ?: return null)?.nullIfEmpty()
	}

	@Blocking
	fun probeMimeType(file: File): MimeType? {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			runCatchingCancellable {
				Files.probeContentType(file.toPath())?.toMimeTypeOrNull()
			}.getOrNull()?.let { return it }
		}
		return getMimeTypeFromExtension(file.name)
	}

	fun getNormalizedExtension(name: String): String? = name
		.lowercase()
		.removeSuffix('~')
		.removeSuffix(".tmp")
		.substringAfterLast('.', "")
		.takeIf { it.length in 2..5 }
}
