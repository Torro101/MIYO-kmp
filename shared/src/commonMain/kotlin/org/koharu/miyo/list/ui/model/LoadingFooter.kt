package org.koharu.miyo.list.ui.model

data class LoadingFooter(
	val key: Int = 0,
) : ListModel {
	override fun areItemsTheSame(other: ListModel): Boolean =
		other is LoadingFooter && key == other.key
}
