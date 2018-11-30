package com.oneeyedmen.minutest

sealed class RuntimeNode : Named

abstract class RuntimeTest: RuntimeNode() {
    abstract fun run()
}

sealed class RuntimeGroup : RuntimeNode() {
    abstract val children: List<RuntimeNode>
}

abstract class RuntimeContext : RuntimeGroup() {
    abstract fun withChildren(children: List<RuntimeNode>): RuntimeContext
}

abstract class RuntimeWrapper : RuntimeGroup() {
    abstract val child: RuntimeNode
    abstract fun withChild(child: RuntimeNode): RuntimeWrapper
    
    final override val children get() = listOf(child)
    final override val name get() = child.name
    final override val parent get() = child.parent
}
