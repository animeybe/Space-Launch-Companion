package com.animeybe.spacelaunchcompanion

import org.junit.Test
import org.junit.Assert.*

class SimpleDiagnosticTest {

    @Test
    fun `basic arithmetic test`() {
        println("=== SIMPLE TEST IS EXECUTING ===")
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `string concatenation test`() {
        val result = "Hello" + " " + "World"
        assertEquals("Hello World", result)
    }

    @Test
    fun `list operations test`() {
        val numbers = listOf(1, 2, 3, 4, 5)
        assertTrue(numbers.size == 5)
        assertTrue(numbers.contains(3))
    }
}