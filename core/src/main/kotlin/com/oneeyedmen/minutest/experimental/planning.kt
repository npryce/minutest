package com.oneeyedmen.minutest.experimental

import com.oneeyedmen.minutest.NodeBuilder
import com.oneeyedmen.minutest.RuntimeContext
import com.oneeyedmen.minutest.RuntimeNode
import com.oneeyedmen.minutest.RuntimeTest
import com.oneeyedmen.minutest.RuntimeWrapper
import com.oneeyedmen.minutest.internal.ParentContext

typealias RuntimeTransform = (RuntimeNode) -> RuntimeNode

class TransformedNodeBuilder<F>(
    private val transform: RuntimeTransform,
    private val original: NodeBuilder<F>
) : NodeBuilder<F> by original {
    override fun buildNode(parent: ParentContext<F>): RuntimeNode {
        return transform(original.buildNode(parent))
    }
}

fun RuntimeNode.mapChildren(f: (RuntimeNode) -> RuntimeNode) = when (this) {
    is RuntimeTest -> this
    is RuntimeContext -> this.withChildren(children.map(f))
    is RuntimeWrapper -> this.withChild(f(child))
}


data class Focused(override val child: RuntimeNode) : RuntimeWrapper() {
    override fun withChild(child: RuntimeNode) = copy(child = child)
}

val FOCUS = ::Focused


data class Skipped(override val child: RuntimeNode) : RuntimeWrapper() {
    override fun withChild(child: RuntimeNode) = copy(child = child)
}

val SKIP = ::Skipped

data class Tag(val tags: List<String>, override val child: RuntimeNode): RuntimeWrapper() {
    override fun withChild(child: RuntimeNode) = copy(child = child)
}

fun TAG(vararg tags: String) = fun (child: RuntimeNode) = Tag(tags.toList(), child)
