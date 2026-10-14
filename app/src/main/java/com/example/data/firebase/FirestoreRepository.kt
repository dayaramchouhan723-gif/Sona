package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    suspend fun saveUserProfile(email: String, name: String, photo: String?) {
        val uid = requireUserId()
        val userDoc = db.collection("users").document(uid)
        val snapshot = try {
            userDoc.get().await()
        } catch (e: Exception) {
            null
        }

        val payload = mutableMapOf<String, Any>(
            "userId" to uid,
            "email" to email,
            "name" to name,
            "freeLifetime" to true,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (photo != null) {
            payload["photo"] = photo
        }

        if (snapshot == null || !snapshot.exists()) {
            payload["createdAt"] = FieldValue.serverTimestamp()
            userDoc.set(payload).await()
            Log.d("FirestoreRepo", "Created new user profile in Firestore for uid: $uid")
        } else {
            userDoc.set(payload, SetOptions.merge()).await()
            Log.d("FirestoreRepo", "Updated user profile in Firestore for uid: $uid")
        }
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val userDoc = db.collection("users").document(userId)
        userDoc.snapshots()
            .map { snap -> if (snap.exists()) snap.toObject(UserProfile::class.java) else null }
            .catch { e ->
                Log.e("FirestoreRepo", "Error observing user profile: ${e.message}", e)
                emit(null)
            }
            .collect { emit(it) }
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        val snap = db.collection("users").document(userId).get().await()
        return if (snap.exists()) snap.toObject(UserProfile::class.java) else null
    }

    suspend fun saveAgentMemory(agentId: String, agentName: String, memoryJson: String) {
        val uid = requireUserId()
        val memoryDoc = db.collection("users").document(uid).collection("agent_memories").document(agentId)
        val snapshot = try {
            memoryDoc.get().await()
        } catch (e: Exception) {
            null
        }

        val payload = mutableMapOf<String, Any>(
            "id" to agentId,
            "agentId" to agentId,
            "userId" to uid,
            "agentName" to agentName,
            "memoryJson" to memoryJson,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        if (snapshot == null || !snapshot.exists()) {
            payload["createdAt"] = FieldValue.serverTimestamp()
            memoryDoc.set(payload).await()
        } else {
            memoryDoc.set(payload, SetOptions.merge()).await()
        }
    }

    fun observeAgentMemories(userId: String): Flow<List<AgentMemoryEntity>> = flow {
        val memoriesCollection = db.collection("users").document(userId).collection("agent_memories")
        memoriesCollection.snapshots()
            .map { snapshot -> snapshot.toObjects(AgentMemoryEntity::class.java) }
            .catch { e ->
                Log.e("FirestoreRepo", "Error observing agent memories: ${e.message}", e)
                emit(emptyList())
            }
            .collect { emit(it) }
    }

    suspend fun syncAll11MemoriesToFirestore(
        memoryFilenames: List<Pair<String, String>>,
        memoryContentProvider: (String) -> String
    ) {
        val uid = requireUserId()
        for ((agentId, filename) in memoryFilenames) {
            val content = memoryContentProvider(filename)
            saveAgentMemory(
                agentId = agentId,
                agentName = agentId.uppercase(),
                memoryJson = content
            )
        }
        Log.d("FirestoreRepo", "Successfully synced all 11 memory JSON files to Firestore for user: $uid")
    }
}
