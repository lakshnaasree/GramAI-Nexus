package com.gramainexus.smartvillage.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

private val Context.userDataStore by preferencesDataStore(
    name = "smart_village_users"
)

class UserPreferences(
    private val context: Context
) {

    companion object {

        private val BIOMETRIC_USER_ID_KEY =
            stringPreferencesKey("biometric_user_id")

        private val USERS_KEY =
            stringPreferencesKey("users")

        private val LOGGED_IN_USER_ID_KEY =
            stringPreferencesKey("logged_in_user_id")

        private val BIOMETRIC_ENABLED_KEY =
            booleanPreferencesKey("biometric_enabled")
    }

    /*
     * Register a new user
     */
    suspend fun saveUser(
        village: String,
        name: String,
        phone: String,
        password: String
    ) {

        context.userDataStore.edit { preferences ->

            val existingUsers =
                preferences[USERS_KEY] ?: "[]"

            val usersArray =
                try {
                    JSONArray(existingUsers)
                } catch (e: Exception) {
                    JSONArray()
                }

            /*
             * Check whether this phone number
             * is already registered in this village.
             */
            for (i in 0 until usersArray.length()) {

                val user =
                    usersArray.optJSONObject(i)
                        ?: continue

                if (
                    user.optString("village") == village &&
                    user.optString("phone") == phone
                ) {
                    return@edit
                }
            }

            val newUser =
                JSONObject().apply {

                    put(
                        "id",
                        UUID.randomUUID().toString()
                    )

                    put(
                        "village",
                        village
                    )

                    put(
                        "name",
                        name
                    )

                    put(
                        "phone",
                        phone
                    )

                    put(
                        "password",
                        password
                    )
                }

            usersArray.put(newUser)

            preferences[USERS_KEY] =
                usersArray.toString()

            /*
             * This newly registered user becomes
             * the currently logged-in user.
             */
            preferences[LOGGED_IN_USER_ID_KEY] =
                newUser.getString("id")

            /*
             * New account does not automatically
             * enable biometric login.
             */
            preferences[BIOMETRIC_ENABLED_KEY] =
                false
        }
    }

    /*
     * Normal phone + password login
     */
    suspend fun isValidLogin(
        village: String,
        phone: String,
        password: String
    ): Boolean {

        val preferences =
            context.userDataStore.data.first()

        val savedUsers =
            preferences[USERS_KEY] ?: "[]"

        val usersArray =
            try {
                JSONArray(savedUsers)
            } catch (e: Exception) {
                JSONArray()
            }

        for (i in 0 until usersArray.length()) {

            val user =
                usersArray.optJSONObject(i)
                    ?: continue

            if (
                user.optString("village") == village &&
                user.optString("phone") == phone &&
                user.optString("password") == password
            ) {

                context.userDataStore.edit { prefs ->

                    prefs[LOGGED_IN_USER_ID_KEY] =
                        user.optString("id")
                }

                return true
            }
        }

        return false
    }

    /*
     * Check whether a user exists in a village
     */
    suspend fun hasRegisteredUser(
        village: String
    ): Boolean {

        val preferences =
            context.userDataStore.data.first()

        val savedUsers =
            preferences[USERS_KEY] ?: "[]"

        val usersArray =
            try {
                JSONArray(savedUsers)
            } catch (e: Exception) {
                JSONArray()
            }

        for (i in 0 until usersArray.length()) {

            val user =
                usersArray.optJSONObject(i)
                    ?: continue

            if (
                user.optString("village") == village
            ) {
                return true
            }
        }

        return false
    }

    /*
     * Check whether any user is currently logged in
     */
    suspend fun isLoggedIn(): Boolean {

        val preferences =
            context.userDataStore.data.first()

        return preferences[LOGGED_IN_USER_ID_KEY] != null
    }

    /*
     * Get currently logged-in user
     */
    suspend fun getLoggedInUser(): UserData? {

        val preferences =
            context.userDataStore.data.first()

        val loggedInUserId =
            preferences[LOGGED_IN_USER_ID_KEY]
                ?: return null

        val savedUsers =
            preferences[USERS_KEY] ?: "[]"

        val usersArray =
            try {
                JSONArray(savedUsers)
            } catch (e: Exception) {
                JSONArray()
            }

        for (i in 0 until usersArray.length()) {

            val user =
                usersArray.optJSONObject(i)
                    ?: continue

            if (
                user.optString("id") == loggedInUserId
            ) {

                return UserData(
                    id = user.optString("id"),
                    village = user.optString("village"),
                    name = user.optString("name"),
                    phone = user.optString("phone")
                )
            }
        }

        return null
    }

    /*
     * Get the phone number of the currently logged-in user.
     *
     * This is important because Firebase profile data
     * will use the phone number as the user document ID.
     */
    suspend fun getLoggedInUserPhone(): String? {

        val user =
            getLoggedInUser()

        return user?.phone
    }

    /*
     * Enable / disable biometric login
     */
    suspend fun setBiometricEnabled(
        enabled: Boolean
    ) {

        context.userDataStore.edit { preferences ->

            if (enabled) {

                val loggedInUserId =
                    preferences[LOGGED_IN_USER_ID_KEY]

                if (loggedInUserId != null) {

                    preferences[BIOMETRIC_USER_ID_KEY] =
                        loggedInUserId

                    preferences[BIOMETRIC_ENABLED_KEY] =
                        true
                }

            } else {

                preferences[BIOMETRIC_ENABLED_KEY] =
                    false

                preferences.remove(
                    BIOMETRIC_USER_ID_KEY
                )
            }
        }
    }

    /*
     * Check whether biometric login is enabled
     * for a particular village.
     */
    suspend fun isBiometricEnabledForVillage(
        village: String
    ): Boolean {

        val preferences =
            context.userDataStore.data.first()

        if (
            preferences[BIOMETRIC_ENABLED_KEY] != true
        ) {
            return false
        }

        val biometricUserId =
            preferences[BIOMETRIC_USER_ID_KEY]
                ?: return false

        val savedUsers =
            preferences[USERS_KEY] ?: "[]"

        val usersArray =
            try {
                JSONArray(savedUsers)
            } catch (e: Exception) {
                JSONArray()
            }

        for (i in 0 until usersArray.length()) {

            val user =
                usersArray.optJSONObject(i)
                    ?: continue

            if (
                user.optString("id") == biometricUserId &&
                user.optString("village") == village
            ) {
                return true
            }
        }

        return false
    }

    /*
     * Authenticate the saved biometric user.
     *
     * Returns the PHONE NUMBER of the biometric user.
     */
    suspend fun loginWithBiometricUser(
        village: String
    ): String? {

        val preferences =
            context.userDataStore.data.first()

        if (
            preferences[BIOMETRIC_ENABLED_KEY] != true
        ) {
            return null
        }

        val biometricUserId =
            preferences[BIOMETRIC_USER_ID_KEY]
                ?: return null

        val savedUsers =
            preferences[USERS_KEY] ?: "[]"

        val usersArray =
            try {
                JSONArray(savedUsers)
            } catch (e: Exception) {
                JSONArray()
            }

        for (i in 0 until usersArray.length()) {

            val user =
                usersArray.optJSONObject(i)
                    ?: continue

            if (
                user.optString("id") == biometricUserId &&
                user.optString("village") == village
            ) {

                /*
                 * Set this user as the current user.
                 */
                context.userDataStore.edit { prefs ->

                    prefs[LOGGED_IN_USER_ID_KEY] =
                        biometricUserId
                }

                /*
                 * Return the phone number belonging
                 * to this biometric account.
                 */
                return user.optString("phone")
            }
        }

        return null
    }

    /*
     * Logout
     */
    suspend fun setLoggedIn(
        value: Boolean
    ) {

        context.userDataStore.edit { preferences ->

            if (!value) {

                preferences.remove(
                    LOGGED_IN_USER_ID_KEY
                )
            }
        }
    }

    suspend fun logout() {

        context.userDataStore.edit { preferences ->

            preferences.remove(
                LOGGED_IN_USER_ID_KEY
            )
        }
    }
}

data class UserData(
    val id: String,
    val village: String,
    val name: String,
    val phone: String
)