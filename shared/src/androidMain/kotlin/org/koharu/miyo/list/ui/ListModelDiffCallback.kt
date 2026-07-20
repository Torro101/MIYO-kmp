package org.koharu.miyo.list.ui

import androidx.recyclerview.widget.DiffUtil
import org.koharu.miyo.list.ui.model.ListModel

open class ListModelDiffCallback<T : ListModel> : DiffUtil.ItemCallback<T>() {

	override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
		return oldItem.areItemsTheSame(newItem)
	}

	override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
		return oldItem == newItem
	}

	override fun getChangePayload(oldItem: T, newItem: T): Any? {
		return newItem.getChangePayload(oldItem)
	}

	companion object : ListModelDiffCallback<ListModel>() {

		val PAYLOAD_CHECKED_CHANGED = ListModelDiffPayloads.PAYLOAD_CHECKED_CHANGED
		val PAYLOAD_NESTED_LIST_CHANGED = ListModelDiffPayloads.PAYLOAD_NESTED_LIST_CHANGED
		val PAYLOAD_PROGRESS_CHANGED = ListModelDiffPayloads.PAYLOAD_PROGRESS_CHANGED
		val PAYLOAD_ANYTHING_CHANGED = ListModelDiffPayloads.PAYLOAD_ANYTHING_CHANGED
	}
}
