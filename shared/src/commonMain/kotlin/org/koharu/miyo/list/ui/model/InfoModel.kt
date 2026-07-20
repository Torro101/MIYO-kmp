package org.koharu.miyo.list.ui.model

data class InfoModel(
	val key: String,
	val title: Int,
	val text: Int,
	val icon: Int,
) : ListModel {
	override fun areItemsTheSame(other: ListModel): Boolean =
		other is InfoModel && other.key == key
}
