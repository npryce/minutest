package com.oneeyedmen.minutest.experimental

import com.oneeyedmen.minutest.RuntimeGroup
import com.oneeyedmen.minutest.RuntimeNode
import com.oneeyedmen.minutest.RuntimeTest


fun focusing(node: RuntimeNode): RuntimeNode = when (node) {
    is RuntimeGroup -> focusFilter(node)
    is RuntimeTest -> node
}

private fun focusFilter(context: RuntimeGroup): RuntimeNode =
    if (context.children.hasFocus()) context.mapChildren(::skipUnlessFocused) else context

private fun Iterable<RuntimeNode>.hasFocus(): Boolean = this.any { it.hasFocus() }

private fun RuntimeNode.hasFocus() =
    when (this) {
        is Focused -> true
        is RuntimeTest -> false
        is RuntimeGroup -> this.children.hasFocus()
    }

private fun skipUnlessFocused(node: RuntimeNode): RuntimeNode =
    when (node) {
        is Focused -> node.child
        is RuntimeTest -> SkippedTest(node.name, node.parent)
        is RuntimeGroup -> when {
            node.children.hasFocus() -> node.mapChildren(::skipUnlessFocused)
            else -> skippingContext("Skipped ${node.name}", node.parent)
        }
    }

