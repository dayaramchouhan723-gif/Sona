package com.example.data.firebase

import com.example.base.FirestoreEmulatorTestBase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class FirestoreRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveUserProfile_authenticatedUser_succeeds() = runBlocking {
        signInTestUser("alice@chouhanempire.com")
        val repository = FirestoreRepository(firestore)

        repository.saveUserProfile(
            email = "alice@chouhanempire.com",
            name = "Alice Chouhan",
            photo = null
        )

        val profile = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getUserProfile(auth.currentUser!!.uid)
        }

        assertNotNull(profile)
        assertEquals("alice@chouhanempire.com", profile?.email)
        assertEquals("Alice Chouhan", profile?.name)
        assertTrue(profile?.freeLifetime == true)
    }

    @Test
    fun saveAgentMemory_authenticatedUser_succeeds() = runBlocking {
        signInTestUser("alice@chouhanempire.com")
        val repository = FirestoreRepository(firestore)

        repository.saveAgentMemory(
            agentId = "myra",
            agentName = "MYRA",
            memoryJson = "{\"version\":\"v2.2.10\"}"
        )

        val memories = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.observeAgentMemories(auth.currentUser!!.uid).first { it.isNotEmpty() }
        }

        assertNotNull(memories)
        assertTrue(memories.any { it.agentId == "myra" })
    }

    @Test
    fun saveUserProfile_unauthenticatedUser_failsWithPermissionOrStateError() = runBlocking {
        auth.signOut()
        val repository = FirestoreRepository(firestore)

        try {
            repository.saveUserProfile("test@test.com", "Test", null)
            fail("Expected IllegalStateException for unauthenticated user")
        } catch (e: IllegalStateException) {
            assertTrue(e.message?.contains("User must be signed in") == true)
        }
    }

    private companion object {
        const val DEFAULT_TIMEOUT_MS = 6000L
    }
}
