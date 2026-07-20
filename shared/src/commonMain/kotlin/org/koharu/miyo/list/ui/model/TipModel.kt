package org.koharu.miyo.list.ui.model

data class TipModel(
	val key: String,
	val title: Int,
	val text: Int,
	val icon: Int,
	val primaryButtonText: Int,
	val secondaryButtonText: Int,
) : ListModel {
	override fun areItemsTheSame(other: ListModel): Boolean =
		other is TipModel && other.key == key
}
