package com.oneeyedmen.minutest.experimental

import com.oneeyedmen.minutest.RuntimeGroup
import com.oneeyedmen.minutest.RuntimeNode
import com.oneeyedmen.minutest.RuntimeTest

fun skipping(node: RuntimeNode): RuntimeNode = when (node) {
    is Skipped -> skippingContext("Skipped ${node.name}", node.parent)
    is RuntimeGroup -> node.mapChildren(::skipping)
    is RuntimeTest -> node
}

