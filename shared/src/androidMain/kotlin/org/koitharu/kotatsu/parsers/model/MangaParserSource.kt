package org.koitharu.kotatsu.parsers.model

/**
 * Minimal stub for MIYO. kotatsu-parsers [MangaSource] only requires [name];
 * display fields are local extras for UI (not interface overrides).
 */
enum class MangaParserSource(
	val title: String,
	val locale: String,
	val contentType: ContentType,
	val isBroken: Boolean,
) : MangaSource {
	MIYO_PLACEHOLDER("MIYO", "", ContentType.MANGA, false),
	;
}
