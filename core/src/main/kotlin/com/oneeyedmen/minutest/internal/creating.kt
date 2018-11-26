package com.oneeyedmen.minutest.internal

import com.oneeyedmen.minutest.Context
import com.oneeyedmen.minutest.NodeBuilder
import com.oneeyedmen.minutest.RuntimeNode
import com.oneeyedmen.minutest.TestDescriptor
import kotlin.reflect.KType


fun <F> topLevelContext(
    name: String,
    type: KType,
    builder: Context<Unit, F>.() -> Unit
): RuntimeNode =
    ContextBuilder<Unit, F>(name, type, fixtureFactoryFor(type), explicitFixtureFactory = false)
        .apply(builder).toRootRuntimeNode()

fun <F> topLevelContext(
    name: String,
    type: KType,
    fixture: F,
    builder: Context<Unit, F>.() -> Unit
): RuntimeNode =
    topLevelContext<F>(name, type, { _, _ -> fixture }, builder)

fun <F> topLevelContext(
    name: String,
    type: KType,
    fixtureFactory: ((Unit, TestDescriptor) -> F)?,
    builder: Context<Unit, F>.() -> Unit
): RuntimeNode =
    ContextBuilder(name, type, fixtureFactory, explicitFixtureFactory = true)
        .apply(builder).toRootRuntimeNode()


internal fun NodeBuilder<Unit>.toRootRuntimeNode(): RuntimeNode =
    this.buildNode(RootContext)

@Suppress("UNCHECKED_CAST")
private fun <F> fixtureFactoryFor(type: KType): ((Unit, TestDescriptor) -> F)? =
    if (type.classifier == Unit::class) {
        { _, _ -> Unit as F }
    }
    else null

