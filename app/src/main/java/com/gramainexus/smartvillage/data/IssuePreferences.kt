package com.gramainexus.smartvillage.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.UUID

class IssuePreferences {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()

    // =====================================================
    // ISSUE CATEGORIES
    // =====================================================

    companion object {

        val ISSUE_CATEGORIES = listOf(
            "Drainage",
            "Water Problem",
            "Street Light",
            "Power Supply",
            "Sound Problem (Factory)",
            "Pollution",
            "Wastages",
            "Other"
        )
    }

    // =====================================================
    // SAVE ISSUE TO FIRESTORE
    // =====================================================

    suspend fun saveIssue(
        village: String,
        category: String,
        description: String,
        location: String,
        photoUri: String = "",
        gpsLocation: String = ""
    ): String {

        val user =
            auth.currentUser
                ?: throw Exception("User is not logged in")

        val issueId =
            UUID.randomUUID().toString()

        val currentTime =
            System.currentTimeMillis()

        val issue = hashMapOf(

            "id" to issueId,

            // Firebase Authentication UID
            "userId" to user.uid,

            // Logged-in user's email
            "userEmail" to (user.email ?: ""),

            "village" to village,

            "category" to category,

            "description" to description,

            "location" to location,

            "gpsLocation" to gpsLocation,

            "status" to "Pending",

            // Local app-private image path
            "photoUri" to photoUri,

            // Kept for compatibility
            "imageUrl" to "",

            "priority" to "Medium",

            "duplicateScore" to 0.0,

            "duplicateOf" to "",

            "createdAt" to currentTime,

            "updatedAt" to currentTime
        )

        firestore
            .collection("issues")
            .document(issueId)
            .set(issue)
            .await()

        return issueId
    }

    // =====================================================
    // UPDATE IMAGE URL
    // =====================================================

    suspend fun updateIssueImageUrl(
        issueId: String,
        imageUrl: String
    ) {

        firestore
            .collection("issues")
            .document(issueId)
            .set(
                mapOf(
                    "imageUrl" to imageUrl,
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )
            .await()
    }

    // =====================================================
    // GET ALL ISSUES
    //
    // Use this only when you really need every issue.
    // =====================================================

    suspend fun getAllIssues(): List<IssueData> {

        val snapshot =
            firestore
                .collection("issues")
                .get()
                .await()

        return snapshot.documents.mapNotNull { document ->
            document.toIssueData()
        }
    }

    // =====================================================
    // GET ISSUES FOR CURRENT LOGGED-IN USER
    // =====================================================

    suspend fun getIssuesForCurrentUser(): List<IssueData> {

        val user =
            auth.currentUser
                ?: throw Exception("User is not logged in")

        val snapshot =
            firestore
                .collection("issues")
                .whereEqualTo(
                    "userId",
                    user.uid
                )
                .get()
                .await()

        return snapshot.documents.mapNotNull { document ->
            document.toIssueData()
        }
    }

    // =====================================================
    // GET LATEST ISSUE FOR CURRENT LOGGED-IN USER
    // =====================================================

    suspend fun getLatestIssueForCurrentUser(): IssueData? {

        return getIssuesForCurrentUser()
            .maxByOrNull { issue ->
                issue.createdAt
            }
    }

    // =====================================================
    // GET ISSUE BY ID
    // =====================================================

    suspend fun getIssueById(
        issueId: String
    ): IssueData? {

        val document =
            firestore
                .collection("issues")
                .document(issueId)
                .get()
                .await()

        return if (document.exists()) {
            document.toIssueData()
        } else {
            null
        }
    }

    // =====================================================
    // CONVERT FIRESTORE DOCUMENT TO ISSUE DATA
    // =====================================================

    private fun com.google.firebase.firestore.DocumentSnapshot
            .toIssueData(): IssueData {

        return IssueData(

            id =
                getString("id")
                    ?: id,

            village =
                getString("village")
                    ?: "",

            category =
                getString("category")
                    ?: "",

            description =
                getString("description")
                    ?: "",

            location =
                getString("location")
                    ?: "",

            gpsLocation =
                getString("gpsLocation")
                    ?: "",

            status =
                getString("status")
                    ?: "Pending",

            photoUri =
                getString("photoUri")
                    ?: "",

            imageUrl =
                getString("imageUrl")
                    ?: "",

            priority =
                getString("priority")
                    ?: "Medium",

            duplicateScore =
                getDouble("duplicateScore")
                    ?: 0.0,

            duplicateOf =
                getString("duplicateOf")
                    ?: "",

            createdAt =
                getLong("createdAt")
                    ?: 0L,

            updatedAt =
                getLong("updatedAt")
                    ?: 0L
        )
    }
}

// =========================================================
// ISSUE DATA
// =========================================================

data class IssueData(

    val id: String = "",

    val village: String = "",

    val category: String = "",

    val description: String = "",

    val location: String = "",

    val gpsLocation: String = "",

    val status: String = "Pending",

    val photoUri: String = "",

    val imageUrl: String = "",

    val priority: String = "Medium",

    val duplicateScore: Double = 0.0,

    val duplicateOf: String = "",

    val createdAt: Long = 0L,

    val updatedAt: Long = 0L
)