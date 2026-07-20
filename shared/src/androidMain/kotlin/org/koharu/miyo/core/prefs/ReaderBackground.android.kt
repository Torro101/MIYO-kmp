package org.koharu.miyo.core.prefs

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import org.koharu.miyo.core.util.ext.getThemeDrawable
import org.koharu.miyo.core.util.ext.isNightMode
import com.google.android.material.R as materialR

fun ReaderBackground.resolve(context: Context): Drawable? = when (this) {
	ReaderBackground.DEFAULT -> context.getThemeDrawable(android.R.attr.windowBackground)
	ReaderBackground.LIGHT -> ContextThemeWrapper(context, materialR.style.ThemeOverlay_Material3_Light)
		.getThemeDrawable(android.R.attr.windowBackground)
	ReaderBackground.DARK -> ContextThemeWrapper(context, materialR.style.ThemeOverlay_Material3_Dark)
		.getThemeDrawable(android.R.attr.windowBackground)
	ReaderBackground.WHITE -> ContextCompat.getColor(context, android.R.color.white).toDrawable()
	ReaderBackground.BLACK -> ContextCompat.getColor(context, android.R.color.black).toDrawable()
}

fun ReaderBackground.isLight(context: Context): Boolean = when (this) {
	ReaderBackground.DEFAULT -> !context.resources.isNightMode
	ReaderBackground.LIGHT, ReaderBackground.WHITE -> true
	ReaderBackground.DARK, ReaderBackground.BLACK -> false
}
