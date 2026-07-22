package org.koharu.miyo.scrobbling.common.ui.selector.model
import org.koharu.miyo.list.ui.model.ListModel
import androidx.annotation.DrawableRes

data class ScrobblerHint(
	@DrawableRes val icon: Int,
	@StringRes val textPrimary: Int,
	@StringRes val textSecondary: Int,
	val error: Throwable?,
	@StringRes val actionStringRes: Int,
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is ScrobblerHint && other.textPrimary == textPrimary
	}
}
