package com.oneeyedmen.minutest.experimental

import com.oneeyedmen.minutest.Context
import com.oneeyedmen.minutest.Named
import com.oneeyedmen.minutest.NodeBuilder
import com.oneeyedmen.minutest.RuntimeContext
import com.oneeyedmen.minutest.RuntimeNode
import com.oneeyedmen.minutest.RuntimeTest
import com.oneeyedmen.minutest.buildRootNode
import com.oneeyedmen.minutest.internal.askType
import com.oneeyedmen.minutest.internal.topLevelContext
import com.oneeyedmen.minutest.junit.toStreamOfDynamicNodes
import org.junit.jupiter.api.DynamicNode
import org.opentest4j.TestAbortedException
import java.util.stream.Stream

@Deprecated("junitTests now supports this")
inline fun <reified F> Any.transformedJunitTests(
    transform: (RuntimeNode) -> RuntimeNode,
    noinline builder: Context<Unit, F>.() -> Unit
): Stream<out DynamicNode> =
    topLevelContext(javaClass.canonicalName, askType<F>(), builder)
        .buildRootNode()
        .run(transform)
        .toStreamOfDynamicNodes()


internal fun skippingContext(name: String, parent: Named?) =
    PlainContext("Skipped $name", parent).apply {
        withChildren(listOf(SkippingTest(this)))
    }

internal data class PlainContext(
    override val name: String,
    override val parent: Named?,
    override val children: List<RuntimeNode> = emptyList()
) : RuntimeContext() {
    override fun withChildren(children: List<RuntimeNode>) = copy(children = children)
}

internal class SkippingTest(override val parent: Named) : RuntimeTest() {
    override val name = "skipped"
    override fun run() {
        throw TestAbortedException("skipped")
    }
}

internal class SkippedTest(
    override val name: String,
    override val parent: Named?
) : RuntimeTest() {
    override fun run() {
        throw TestAbortedException("skipped")
    }
}

