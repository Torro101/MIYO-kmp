package org.koharu.miyo.core.di

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Cross-platform service locator for dependency injection.
 * This provides a simple way to register and resolve dependencies.
 */
object ServiceLocator {
	private val services = mutableMapOf<String, Any>()
	private val mutex = Mutex()

	suspend fun <T : Any> register(name: String, service: T) {
		mutex.withLock {
			services[name] = service
		}
	}

	suspend fun <T : Any> resolve(name: String): T {
		return mutex.withLock {
			@Suppress("UNCHECKED_CAST")
			services[name] as? T
				?: throw IllegalArgumentException("Service '$name' not found")
		}
	}

	suspend fun <T : Any> resolveOrNull(name: String): T? {
		return mutex.withLock {
			@Suppress("UNCHECKED_CAST")
			services[name] as? T
		}
	}

	suspend fun <T : Any> resolveOrCreate(name: String, factory: () -> T): T {
		return mutex.withLock {
			@Suppress("UNCHECKED_CAST")
			services.getOrPut(name) { factory() } as T
		}
	}

	suspend fun unregister(name: String) {
		mutex.withLock {
			services.remove(name)
		}
	}

	suspend fun clear() {
		mutex.withLock {
			services.clear()
		}
	}

	suspend fun contains(name: String): Boolean {
		return mutex.withLock {
			services.containsKey(name)
		}
	}
}

/**
 * Type-safe service locator extension functions.
 */
suspend inline fun <reified T : Any> ServiceLocator.register(service: T) {
	register(T::class.simpleName ?: "Anonymous", service)
}

suspend inline fun <reified T : Any> ServiceLocator.resolve(): T {
	return resolve(T::class.simpleName ?: "Anonymous")
}

suspend inline fun <reified T : Any> ServiceLocator.resolveOrNull(): T? {
	return resolveOrNull(T::class.simpleName ?: "Anonymous")
}

suspend inline fun <reified T : Any> ServiceLocator.resolveOrCreate(noinline factory: () -> T): T {
	return resolveOrCreate(T::class.simpleName ?: "Anonymous", factory)
}
