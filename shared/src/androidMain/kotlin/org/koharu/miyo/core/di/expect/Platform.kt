package org.koharu.miyo.core.di.expect

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File

actual object Platform {
	actual val name: String = "Android"
	actual val version: String = Build.VERSION.RELEASE
	actual val isDebug: Boolean = true // Will be set properly in production
}

private lateinit var appContext: Context

fun initPlatform(context: Context) {
	appContext = context.applicationContext
}

actual fun initializePlatform() {
	// Platform-specific initialization will be done here
}
