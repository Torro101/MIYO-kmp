package org.koharu.miyo.core.domain

import kotlinx.serialization.Serializable

/**
 * Cross-platform app update model.
 */
@Serializable
data class AppUpdate(
	val versionName: String,
	val versionCode: Int,
	val changelog: String = "",
	val downloadUrl: String = "",
	val releaseDate: Long = 0,
	val isMandatory: Boolean = false,
	val fileSize: Long = 0,
	val md5: String = ""
) {
	val displayVersion: String
		get() = "v$versionName"

	val displayFileSize: String
		get() = org.koharu.miyo.core.util.FormatUtils.formatFileSize(fileSize)

	val hasDownload: Boolean
		get() = downloadUrl.isNotBlank()

	val isDownloadable: Boolean
		get() = hasDownload && fileSize > 0
}
