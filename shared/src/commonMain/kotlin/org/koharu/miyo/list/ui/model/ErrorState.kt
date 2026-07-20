package org.koharu.miyo.list.ui.model

data class ErrorState(
	val exception: Throwable,
	val icon: Int,
	val canRetry: Boolean,
	val buttonText: Int,
	val secondaryButtonText: Int,
) : ListModel {
	override fun areItemsTheSame(other: ListModel): Boolean = other is ErrorState
}
