package org.koharu.miyo.list.ui.model

data class ButtonFooter(
	val textResId: Int,
) : ListModel {
	override fun areItemsTheSame(other: ListModel): Boolean =
		other is ButtonFooter && textResId == other.textResId
}
