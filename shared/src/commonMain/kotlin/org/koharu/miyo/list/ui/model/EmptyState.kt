package org.koharu.miyo.list.ui.model

data class EmptyState(
	val icon: Int,
	val textPrimary: Int,
	val textSecondary: Int,
	val actionStringRes: Int,
) : ListModel {
	override fun areItemsTheSame(other: ListModel): Boolean = other is EmptyState
}
