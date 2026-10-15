package com.gramainexus.smartvillage.data

import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.tasks.await

class WaterPreferences {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getLatestWaterCheck(
        villageName: String
    ): Map<String, Any>? {
        return try {
            val snapshot = firestore
                .collection("waterDailyChecks")
                .whereEqualTo("village", villageName)
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.data
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun getWaterSchedule(
        villageName: String
    ): Map<String, Any>? {
        return try {
            val snapshot = firestore
                .collection("waterSchedules")
                .whereEqualTo("village", villageName)
                .whereEqualTo("active", true)
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getWaterFacilities(
        villageName: String
    ): List<Map<String, Any>> {
        return try {
            val snapshot = firestore
                .collection("waterFacilities")
                .whereEqualTo("village", villageName)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}