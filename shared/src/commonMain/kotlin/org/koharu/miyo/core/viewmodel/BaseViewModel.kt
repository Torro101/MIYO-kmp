package org.koharu.miyo.core.viewmodel

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Cross-platform base ViewModel.
 */
open class BaseViewModel {
	protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

	private val _isLoading = MutableStateFlow(false)
	val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

	private val _error = MutableStateFlow<String?>(null)
	val error: StateFlow<String?> = _error.asStateFlow()

	private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
		_error.value = throwable.message
		_isLoading.value = false
	}

	protected fun launchWithLoading(block: suspend CoroutineScope.() -> Unit) {
		scope.launch(exceptionHandler) {
			_isLoading.value = true
			try {
				block()
			} finally {
				_isLoading.value = false
			}
		}
	}

	protected fun launch(block: suspend CoroutineScope.() -> Unit) {
		scope.launch(exceptionHandler, block = block)
	}

	fun clearError() {
		_error.value = null
	}

	fun onCleared() {
		scope.coroutineContext.cancel()
	}
}
