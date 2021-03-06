// explicit name for generated class so that tests are not coupled to the behaviour of the kotlin compiler
@file:JvmName("ExampleMinutest")

package example

import com.oneeyedmen.minutest.experimental.context
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import java.util.Stack

val `example context` = context<Unit> {
    test("a failing test") {
        fail("example failure")
    }
    
    test("a passing test") {
        assertTrue(true, "example success")
    }
}

