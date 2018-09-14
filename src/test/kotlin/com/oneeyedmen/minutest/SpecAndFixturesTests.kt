package com.oneeyedmen.minutest

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import kotlin.streams.asSequence


object SpecAndFixturesTests {

    data class Fixture(var thing: String)

    @TestFactory fun `with fixtures`() = context<Fixture> {

        fixture { Fixture("banana") }

        test("can mutate fixture without affecting following tests") {
            thing = "kumquat"
            assertEquals("kumquat", thing)
        }

        test("previous test did not affect me") {
            assertEquals("banana", thing)
        }

        context("sub-context inheriting fixture") {
            test("has the fixture from its parent") {
                assertEquals("banana", thing)
            }
        }

        context("sub-context overriding fixture") {
            fixture { Fixture("apple") }

            test("does not have the fixture from its parent") {
                assertEquals("apple", thing)
            }
        }

        context("sub-context modifying fixture") {
            modifyFixture { thing += "s" }

            test("sees the modified fixture") {
                assertEquals("bananas", thing)
            }
        }

        context("sub-context replacing fixture") {
            replaceFixture { Fixture("green $thing") }

            test("sees the replaced fixture") {
                assertEquals("green banana", thing)
            }
        }

    }

    @TestFactory fun wrappers() = context<Fixture> {
        fixture { Fixture("banana") }

        fun repeatValue(t: MinuTest<Fixture>): MinuTest<Fixture> = SingleTest(t.name) {
            t.f(Fixture(thing + thing))
        }

        context("can decorate tests") {
            wrapped(::repeatValue) {
                test("decorated") {
                    assertEquals("bananabanana", thing)
                }
            }
        }
    }


    @TestFactory fun `no fixture`() = context<Unit> {
        test("I need not have a fixture") {
            assertNotNull("banana")
        }
    }

    @Test fun `no fixture when one is needed`() {
        val tests: List<DynamicNode> = context<Fixture> {
            test("I report not having a fixture") {
                assertEquals("banana", thing)
            }
        }
        assertThrows<IllegalStateException> {
            ((tests.first() as DynamicContainer).children.asSequence().first() as DynamicTest).executable.execute()
        }
    }
}