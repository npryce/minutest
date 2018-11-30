package com.oneeyedmen.minutest

import com.oneeyedmen.minutest.internal.ParentContext
import com.oneeyedmen.minutest.internal.RootContext

interface NodeBuilder<F> {
    fun buildNode(parent: ParentContext<F>): RuntimeNode
}

fun NodeBuilder<Unit>.buildRootNode(): RuntimeNode = buildNode(RootContext)