package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testAgentCollectionCount() {
    val expectedAgents = listOf(
      "myra", "maya", "kayra", "stonic", "zoya",
      "vyron", "jarvis", "archer", "nova", "luna", "sona"
    )
    assertEquals(11, expectedAgents.size)
    assertTrue(expectedAgents.contains("myra"))
    assertTrue(expectedAgents.contains("sona"))
    assertTrue(expectedAgents.contains("maya"))
    assertTrue(expectedAgents.contains("kayra"))
  }
}
