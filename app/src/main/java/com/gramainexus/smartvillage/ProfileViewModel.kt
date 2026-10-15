package com.gramainexus.smartvillage

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.gramainexus.smartvillage.data.FarmerProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.FileOutputStream

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _profile = MutableStateFlow(FarmerProfile())
    val profile: StateFlow<FarmerProfile> = _profile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message


    // ---------------------------------------------------------
    // GET CURRENT FIREBASE USER
    // ---------------------------------------------------------

    private fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }


    // ---------------------------------------------------------
    // LOAD PROFILE
    // ---------------------------------------------------------

    fun loadProfile(context: Context) {

        val uid = getCurrentUserUid()

        if (uid.isNullOrBlank()) {
            _message.value = "Logged-in user not found"
            return
        }

        _isLoading.value = true

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val loadedProfile =
                        document.toObject(FarmerProfile::class.java)

                    if (loadedProfile != null) {
                        _profile.value = loadedProfile
                    }

                } else {

                    val firebaseUser = auth.currentUser

                    _profile.value = FarmerProfile(
                        name = firebaseUser?.displayName ?: "",
                        email = firebaseUser?.email ?: ""
                    )
                }

                _isLoading.value = false
            }
            .addOnFailureListener { exception ->

                _isLoading.value = false

                _message.value =
                    exception.message ?: "Failed to load profile"
            }
    }


    // ---------------------------------------------------------
    // SAVE PROFILE IMAGE LOCALLY
    // ---------------------------------------------------------

    fun saveProfileImage(
        context: Context,
        imageUri: Uri
    ) {

        val uid = getCurrentUserUid()

        if (uid.isNullOrBlank()) {
            _message.value = "Logged-in user not found"
            return
        }

        _isLoading.value = true

        try {

            // Create a folder inside the app's private storage
            val profileImagesDirectory =
                File(context.filesDir, "profile_images")

            if (!profileImagesDirectory.exists()) {
                profileImagesDirectory.mkdirs()
            }

            // Create a unique image file
            val imageFile =
                File(
                    profileImagesDirectory,
                    "profile_${uid}.jpg"
                )

            // Open the selected image
            val inputStream =
                context.contentResolver.openInputStream(imageUri)

            if (inputStream == null) {

                _isLoading.value = false
                _message.value = "Unable to read selected image"
                return
            }

            // Copy selected image into app storage
            FileOutputStream(imageFile).use { outputStream ->

                inputStream.use { input ->

                    input.copyTo(outputStream)
                }
            }

            // Local path of the saved image
            val imagePath = imageFile.absolutePath

            // Save local image path in Firestore
            firestore.collection("users")
                .document(uid)
                .set(
                    mapOf(
                        "profilePhotoUrl" to imagePath
                    ),
                    SetOptions.merge()
                )
                .addOnSuccessListener {

                    // Update the screen immediately
                    _profile.value =
                        _profile.value.copy(
                            profilePhotoUrl = imagePath
                        )

                    _isLoading.value = false

                    _message.value =
                        "Profile photo updated successfully"
                }
                .addOnFailureListener { exception ->

                    _isLoading.value = false

                    _message.value =
                        exception.message
                            ?: "Failed to save image information"
                }

        } catch (exception: Exception) {

            _isLoading.value = false

            _message.value =
                exception.message
                    ?: "Failed to save profile image"
        }
    }

    fun saveResume(
        context: Context,
        resumeUri: Uri
    ) {

        val uid = getCurrentUserUid()

        if (uid.isNullOrBlank()) {
            _message.value = "Logged-in user not found"
            return
        }

        _isLoading.value = true

        try {

            val resumeDirectory =
                File(
                    context.filesDir,
                    "resumes"
                )

            if (!resumeDirectory.exists()) {
                resumeDirectory.mkdirs()
            }

            val resumeFile =
                File(
                    resumeDirectory,
                    "resume_${uid}.pdf"
                )

            val inputStream =
                context.contentResolver
                    .openInputStream(resumeUri)

            if (inputStream == null) {

                _isLoading.value = false

                _message.value =
                    "Unable to read selected resume"

                return
            }

            FileOutputStream(resumeFile).use { outputStream ->

                inputStream.use { input ->

                    input.copyTo(outputStream)
                }
            }

            val resumePath =
                resumeFile.absolutePath

            firestore.collection("users")
                .document(uid)
                .set(
                    mapOf(
                        "resumePath" to resumePath
                    ),
                    SetOptions.merge()
                )
                .addOnSuccessListener {

                    _profile.value =
                        _profile.value.copy(
                            resumePath = resumePath
                        )

                    _isLoading.value = false

                    _message.value =
                        "Resume uploaded successfully"
                }
                .addOnFailureListener { exception ->

                    _isLoading.value = false

                    _message.value =
                        exception.message
                            ?: "Failed to save resume"
                }

        } catch (exception: Exception) {

            _isLoading.value = false

            _message.value =
                exception.message
                    ?: "Failed to upload resume"
        }
    }

    // ---------------------------------------------------------
    // SAVE PROFILE
    // ---------------------------------------------------------

    fun saveProfile(
        context: Context,
        farmerProfile: FarmerProfile
    ) {

        val uid = getCurrentUserUid()

        if (uid.isNullOrBlank()) {
            _message.value = "Logged-in user not found"
            return
        }

        _isLoading.value = true

        firestore.collection("users")
            .document(uid)
            .set(
                farmerProfile,
                SetOptions.merge()
            )
            .addOnSuccessListener {

                _profile.value = farmerProfile

                _isLoading.value = false

                _message.value =
                    "Profile saved successfully"
            }
            .addOnFailureListener { exception ->

                _isLoading.value = false

                _message.value =
                    exception.message
                        ?: "Failed to save profile"
            }
    }


    // ---------------------------------------------------------
    // CLEAR MESSAGE
    // ---------------------------------------------------------

    fun clearMessage() {
        _message.value = ""
    }
}