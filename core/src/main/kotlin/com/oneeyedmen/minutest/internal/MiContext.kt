package com.oneeyedmen.minutest.internal

import com.oneeyedmen.minutest.Node
import com.oneeyedmen.minutest.Test
import com.oneeyedmen.minutest.TestContext


internal class MiContext<F>(
    override val name: String,
    builder: MiContext<F>.() -> Unit
) : TestContext<F> {

    internal val children = mutableListOf<Node<F>>()
    internal val operations = MutableOperations<F>()

    init {
        this.builder()
    }

    override fun before_(transform: F.() -> F) {
        operations.befores.add(transform)
    }

    override fun before(transform: F.() -> Unit) = before_ { this.apply(transform) }

    override fun after(transform: F.() -> Unit) = after_ { this.apply(transform) }

    override fun after_(transform: F.() -> F) {
        operations.afters.add(transform)
    }

    override fun test_(name: String, f: F.() -> F) {
        MinuTest(
            name,
            f).also { children.add(it) }
    }

    override fun test(name: String, f: F.() -> Unit) {
        test_(name) {
            apply { f(this) }
        }
    }

    override fun context(name: String, builder: TestContext<F>.() -> Unit) =
        MiContext(name, builder).also { children.add(it) }

    override fun addTransform(testTransform: (Test<F>) -> Test<F>) {
        operations.transforms.add(testTransform)
    }

    @Suppress("UNCHECKED_CAST")
    fun runTest(myTest: Test<F>, parentOperations: Operations<F>) {
        try {
            val combinedOperations = parentOperations + operations
            val beforeResult = combinedOperations.applyBeforesTo(Unit as F)
            val nextResult = beforeResult.flatMap { fixture ->
                try {
                    val transformedTests = combinedOperations.applyTransformsTo(myTest)
                    OpResult(null, transformedTests.invoke(fixture))
                } catch (t: Throwable) {
                    OpResult(t, fixture)
                }
            }
            combinedOperations.applyAftersTo(nextResult.lastValue)
            nextResult.orThrow()
        } catch (x: ClassCastException) {
            // TODO - this could be thrown in test code and reach here
            // Provided a fixture has been set, the Unit never makes it as far as any functions that cast it to F, so
            // this works. And if the type of F is Unit, you don't need to set a fixture, as the Unit will do. Simples.
            error("You need to set a fixture by calling fixture(...)")
        }
    }
}