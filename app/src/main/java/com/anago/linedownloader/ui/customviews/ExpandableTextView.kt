package com.anago.linedownloader.ui.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class ExpandableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var isExpanded = false

    init {
        setOnClickListener {
            toggleExpandedState()
        }
        setExpandedState(false)
    }

    private fun toggleExpandedState() {
        isExpanded = !isExpanded
        setExpandedState(isExpanded)
    }

    private fun setExpandedState(expanded: Boolean) {
        maxLines = if (expanded) {
            Int.MAX_VALUE
        } else {
            2
        }
    }
}