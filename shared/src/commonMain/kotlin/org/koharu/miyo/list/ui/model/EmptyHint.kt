package org.koharu.miyo.list.ui.model

data class EmptyHint(
	val icon: Int,
	val textPrimary: Int,
	val textSecondary: Int,
	val actionStringRes: Int,
) : ListModel {
	fun toState() = EmptyState(icon, textPrimary, textSecondary, actionStringRes)

	override fun areItemsTheSame(other: ListModel): Boolean =
		other is EmptyHint && textPrimary == other.textPrimary
}
