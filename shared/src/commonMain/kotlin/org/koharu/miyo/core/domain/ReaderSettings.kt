package org.koharu.miyo.core.domain

import kotlinx.serialization.Serializable

/**
 * Cross-platform reader settings model.
 */
@Serializable
data class ReaderSettings(
	val mode: ReaderMode = ReaderMode.LEFT_TO_RIGHT,
	val isHorizontalpagerEnabled: Boolean = true,
	val isAnimationEnabled: Boolean = true,
	val isZoomingEnabled: Boolean = true,
	val isAutoCropEnabled: Boolean = false,
	val isSplitPagesEnabled: Boolean = false,
	val backgroundColor: Int = 0,
	val brightness: Int = 50,
	val contrast: Float = 1.0f,
	val saturation: Float = 1.0f,
	val sharpness: Float = 0f,
	val isKeepScreenOn: Boolean = true,
	val isShowPageNumber: Boolean = true,
	val isShowBatteryLevel: Boolean = false,
	val isShowTimeRemaining: Boolean = false,
	val isVolumeButtonsEnabled: Boolean = false,
	val isDoubleTapEnabled: Boolean = true,
	val isSwipeEnabled: Boolean = true,
	val isPinchZoomEnabled: Boolean = true
) {
	val isWebtoonMode: Boolean
		get() = mode == ReaderMode.WEBTOON

	val isVerticalMode: Boolean
		get() = mode == ReaderMode.VERTICAL

	val isHorizontalMode: Boolean
		get() = mode == ReaderMode.HORIZONTAL || mode == ReaderMode.LEFT_TO_RIGHT || mode == ReaderMode.RIGHT_TO_LEFT

	val isRtlMode: Boolean
		get() = mode == ReaderMode.RIGHT_TO_LEFT

	val hasBrightnessAdjustment: Boolean
		get() = brightness != 50

	val hasColorFilter: Boolean
		get() = contrast != 1.0f || saturation != 1.0f || sharpness != 0f

	val defaultMode: ReaderMode
		get() = when (mode) {
			ReaderMode.LEFT_TO_RIGHT -> ReaderMode.LEFT_TO_RIGHT
			ReaderMode.RIGHT_TO_LEFT -> ReaderMode.RIGHT_TO_LEFT
			ReaderMode.WEBTOON -> ReaderMode.WEBTOON
			ReaderMode.VERTICAL -> ReaderMode.VERTICAL
			ReaderMode.HORIZONTAL -> ReaderMode.HORIZONTAL
		}
}
