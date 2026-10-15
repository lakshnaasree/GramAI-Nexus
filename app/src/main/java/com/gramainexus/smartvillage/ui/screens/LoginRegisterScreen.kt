package com.gramainexus.smartvillage.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


@Composable
fun LoginRegisterScreen(
    villageName: String,
    selectedLanguage: String,
    onLoginSuccess: (String, String) -> Unit
) {

    val context = LocalContext.current

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

    val isTamil = selectedLanguage == "தமிழ்"


    // =====================================================
    // UI STATE
    // =====================================================

    var isLoginMode by remember {
        mutableStateOf(true)
    }

    var name by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var phone by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var confirmPasswordVisible by remember {
        mutableStateOf(false)
    }

    var rememberMe by remember {
        mutableStateOf(true)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }


    // =====================================================
    // BIOMETRIC PREFERENCE
    // =====================================================

    val biometricPreferences = remember {
        context.getSharedPreferences(
            "smart_village_biometric",
            Context.MODE_PRIVATE
        )
    }

    var biometricEnabled by remember {
        mutableStateOf(
            biometricPreferences.getBoolean(
                "biometric_enabled",
                false
            )
        )
    }


    // =====================================================
    // CHECK BIOMETRIC SUPPORT
    // =====================================================

    fun isBiometricAvailable(): Boolean {

        val biometricManager =
            BiometricManager.from(context)

        val result =
            biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            )

        return result ==
                BiometricManager.BIOMETRIC_SUCCESS
    }


    // =====================================================
    // LOAD USER PROFILE
    // =====================================================

    fun loadUserAndContinue(
        firebaseUser: FirebaseUser,
        showSuccessToast: Boolean = true
    ) {

        isLoading = true

        firestore
            .collection("users")
            .document(firebaseUser.uid)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {

                    isLoading = false

                    errorMessage =
                        if (isTamil) {
                            "இந்த கணக்கிற்கான விவசாயி சுயவிவரம் கிடைக்கவில்லை"
                        } else {
                            "Farmer profile not found for this account"
                        }

                    return@addOnSuccessListener
                }


                // -----------------------------------------
                // PHONE
                // -----------------------------------------

                val firestorePhone =
                    document
                        .getString("phone")
                        ?.trim()
                        .orEmpty()


                // -----------------------------------------
                // VILLAGE
                // -----------------------------------------

                val firestoreVillage =
                    document
                        .getString("village")
                        ?.trim()


                /*
                 * Village priority:
                 *
                 * 1. Firestore village
                 * 2. Selected village
                 */
                val finalVillage =
                    if (!firestoreVillage.isNullOrBlank()) {
                        firestoreVillage
                    } else {
                        villageName.trim()
                    }


                // -----------------------------------------
                // VILLAGE MISSING
                // -----------------------------------------

                if (finalVillage.isBlank()) {

                    isLoading = false

                    errorMessage =
                        if (isTamil) {
                            "இந்த கணக்கிற்கான கிராம தகவல் கிடைக்கவில்லை"
                        } else {
                            "Village information is missing for this account"
                        }

                    return@addOnSuccessListener
                }


                // -----------------------------------------
                // REPAIR OLD FIRESTORE ACCOUNT
                // -----------------------------------------

                if (firestoreVillage.isNullOrBlank()) {

                    firestore
                        .collection("users")
                        .document(firebaseUser.uid)
                        .set(
                            mapOf(
                                "village" to finalVillage
                            ),
                            SetOptions.merge()
                        )
                        .addOnFailureListener {
                            // Continue even if repair fails.
                        }
                }


                // -----------------------------------------
                // PHONE FALLBACK
                // -----------------------------------------

                val finalPhone =
                    if (firestorePhone.isNotBlank()) {
                        firestorePhone
                    } else {
                        firebaseUser.phoneNumber
                            ?.trim()
                            .orEmpty()
                    }


                if (finalPhone.isBlank()) {

                    isLoading = false

                    errorMessage =
                        if (isTamil) {
                            "பயனர் தொலைபேசி தகவல் கிடைக்கவில்லை"
                        } else {
                            "User phone information not found"
                        }

                    return@addOnSuccessListener
                }


                // -----------------------------------------
                // SUCCESS
                // -----------------------------------------

                isLoading = false

                if (showSuccessToast) {

                    Toast.makeText(
                        context,
                        if (isTamil) {
                            "உள்நுழைவு வெற்றி"
                        } else {
                            "Login successful"
                        },
                        Toast.LENGTH_SHORT
                    ).show()
                }


                // -----------------------------------------
                // SEND PHONE + VILLAGE TO NAVIGATION
                // -----------------------------------------

                onLoginSuccess(
                    finalPhone,
                    finalVillage
                )
            }
            .addOnFailureListener { exception ->

                isLoading = false

                errorMessage =
                    exception.message
                        ?: if (isTamil) {
                            "பயனர் தகவலைப் பெற முடியவில்லை"
                        } else {
                            "Unable to load user profile"
                        }
            }
    }


    // =====================================================
    // ENABLE BIOMETRIC
    // =====================================================

    fun enableBiometric() {

        if (!isBiometricAvailable()) {

            Toast.makeText(
                context,
                if (isTamil) {
                    "இந்த சாதனத்தில் பயோமெட்ரிக் உள்நுழைவு கிடைக்கவில்லை"
                } else {
                    "Biometric authentication is not available on this device"
                },
                Toast.LENGTH_LONG
            ).show()

            return
        }


        biometricPreferences
            .edit()
            .putBoolean(
                "biometric_enabled",
                true
            )
            .apply()

        biometricEnabled = true


        Toast.makeText(
            context,
            if (isTamil) {
                "பயோமெட்ரிக் உள்நுழைவு இயக்கப்பட்டது"
            } else {
                "Biometric login enabled"
            },
            Toast.LENGTH_SHORT
        ).show()
    }


    // =====================================================
    // BIOMETRIC LOGIN
    // =====================================================

    fun startBiometricLogin() {

        if (!isBiometricAvailable()) {

            errorMessage =
                if (isTamil) {
                    "இந்த சாதனத்தில் பயோமெட்ரிக் உள்நுழைவு கிடைக்கவில்லை"
                } else {
                    "Biometric authentication is not available"
                }

            return
        }


        val activity =
            context as? FragmentActivity

        if (activity == null) {

            errorMessage =
                if (isTamil) {
                    "பயோமெட்ரிக் உள்நுழைவைத் தொடங்க முடியவில்லை"
                } else {
                    "Unable to start biometric login"
                }

            return
        }


        val executor =
            androidx.core.content.ContextCompat.getMainExecutor(
                context
            )


        val biometricPrompt =
            BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)

                        val currentUser =
                            auth.currentUser

                        if (currentUser == null) {

                            errorMessage =
                                if (isTamil) {
                                    "உங்கள் கணக்கு அமர்வு கிடைக்கவில்லை. மீண்டும் உள்நுழையவும்."
                                } else {
                                    "Your account session was not found. Please login again."
                                }

                            return
                        }

                        loadUserAndContinue(
                            firebaseUser = currentUser,
                            showSuccessToast = true
                        )
                    }


                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(
                            errorCode,
                            errString
                        )

                        errorMessage =
                            if (isTamil) {
                                "பயோமெட்ரிக் சரிபார்ப்பு தோல்வியடைந்தது"
                            } else {
                                "Biometric authentication failed"
                            }
                    }


                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()

                        errorMessage =
                            if (isTamil) {
                                "பயோமெட்ரிக்கை மீண்டும் முயற்சிக்கவும்"
                            } else {
                                "Biometric not recognized. Try again."
                            }
                    }
                }
            )


        val promptInfo =
            BiometricPrompt.PromptInfo.Builder()
                .setTitle(
                    if (isTamil) {
                        "SmartVillage உள்நுழைவு"
                    } else {
                        "SmartVillage Login"
                    }
                )
                .setSubtitle(
                    if (isTamil) {
                        "உங்கள் அடையாளத்தை உறுதிப்படுத்தவும்"
                    } else {
                        "Verify your identity"
                    }
                )
                .setDescription(
                    if (isTamil) {
                        "உங்கள் கைரேகை அல்லது சாதன பயோமெட்ரிக்கைப் பயன்படுத்தவும்"
                    } else {
                        "Use your fingerprint or device biometric"
                    }
                )
                .setNegativeButtonText(
                    if (isTamil) {
                        "ரத்து"
                    } else {
                        "Cancel"
                    }
                )
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG
                )
                .build()


        biometricPrompt.authenticate(
            promptInfo
        )
    }


    // =====================================================
    // NORMAL LOGIN
    // =====================================================

    fun loginUser() {

        errorMessage = ""

        val cleanEmail =
            email.trim().lowercase()

        if (cleanEmail.isBlank()) {

            errorMessage =
                if (isTamil) {
                    "மின்னஞ்சல் முகவரியை உள்ளிடவும்"
                } else {
                    "Please enter your email"
                }

            return
        }


        if (password.isBlank()) {

            errorMessage =
                if (isTamil) {
                    "கடவுச்சொல்லை உள்ளிடவும்"
                } else {
                    "Please enter your password"
                }

            return
        }


        isLoading = true

        auth
            .signInWithEmailAndPassword(
                cleanEmail,
                password
            )
            .addOnSuccessListener { result ->

                val firebaseUser =
                    result.user

                if (firebaseUser == null) {

                    isLoading = false

                    errorMessage =
                        if (isTamil) {
                            "பயனர் தகவல் கிடைக்கவில்லை"
                        } else {
                            "User information not found"
                        }

                    return@addOnSuccessListener
                }


                loadUserAndContinue(
                    firebaseUser = firebaseUser,
                    showSuccessToast = true
                )
            }
            .addOnFailureListener { exception ->

                isLoading = false

                errorMessage =
                    exception.message
                        ?: if (isTamil) {
                            "உள்நுழைவு தோல்வியடைந்தது"
                        } else {
                            "Login failed"
                        }
            }
    }


    // =====================================================
    // REGISTER
    // =====================================================

    fun registerUser() {

        errorMessage = ""

        val cleanName =
            name.trim()

        val cleanEmail =
            email.trim().lowercase()

        val cleanPhone =
            phone.trim()

        val cleanVillage =
            villageName.trim()


        if (cleanName.isBlank()) {

            errorMessage =
                if (isTamil) {
                    "பெயரை உள்ளிடவும்"
                } else {
                    "Please enter your name"
                }

            return
        }


        if (cleanEmail.isBlank()) {

            errorMessage =
                if (isTamil) {
                    "மின்னஞ்சல் முகவரியை உள்ளிடவும்"
                } else {
                    "Please enter your email"
                }

            return
        }


        if (cleanPhone.isBlank()) {

            errorMessage =
                if (isTamil) {
                    "தொலைபேசி எண்ணை உள்ளிடவும்"
                } else {
                    "Please enter your phone number"
                }

            return
        }


        if (password.length < 6) {

            errorMessage =
                if (isTamil) {
                    "கடவுச்சொல் குறைந்தது 6 எழுத்துகள் இருக்க வேண்டும்"
                } else {
                    "Password must contain at least 6 characters"
                }

            return
        }


        if (password != confirmPassword) {

            errorMessage =
                if (isTamil) {
                    "கடவுச்சொற்கள் பொருந்தவில்லை"
                } else {
                    "Passwords do not match"
                }

            return
        }


        if (cleanVillage.isBlank()) {

            errorMessage =
                if (isTamil) {
                    "கிராமத்தைத் தேர்ந்தெடுக்கவும்"
                } else {
                    "Please select a village"
                }

            return
        }


        isLoading = true


        auth
            .createUserWithEmailAndPassword(
                cleanEmail,
                password
            )
            .addOnSuccessListener { result ->

                val firebaseUser =
                    result.user

                if (firebaseUser == null) {

                    isLoading = false

                    errorMessage =
                        if (isTamil) {
                            "கணக்கை உருவாக்க முடியவில்லை"
                        } else {
                            "Unable to create account"
                        }

                    return@addOnSuccessListener
                }


                val uid =
                    firebaseUser.uid


                val farmerData =
                    hashMapOf(
                        "uid" to uid,
                        "name" to cleanName,
                        "email" to cleanEmail,
                        "phone" to cleanPhone,
                        "village" to cleanVillage,
                        "profilePhotoUrl" to "",
                        "district" to "",
                        "state" to "",
                        "landArea" to "",
                        "crops" to "",
                        "about" to "",
                        "education" to "",
                        "skills" to "",
                        "experience" to "",
                        "certifications" to "",
                        "languages" to "",
                        "openToWork" to false,
                        "resumePath" to ""
                    )


                firestore
                    .collection("users")
                    .document(uid)
                    .set(farmerData)
                    .addOnSuccessListener {

                        isLoading = false

                        /*
                         * Enable biometric immediately after
                         * successful account creation if supported.
                         */
                        if (isBiometricAvailable()) {

                            biometricPreferences
                                .edit()
                                .putBoolean(
                                    "biometric_enabled",
                                    true
                                )
                                .apply()

                            biometricEnabled = true
                        }


                        Toast.makeText(
                            context,
                            if (isTamil) {
                                "கணக்கு வெற்றிகரமாக உருவாக்கப்பட்டது"
                            } else {
                                "Account created successfully"
                            },
                            Toast.LENGTH_SHORT
                        ).show()


                        onLoginSuccess(
                            cleanPhone,
                            cleanVillage
                        )
                    }
                    .addOnFailureListener { exception ->

                        isLoading = false

                        errorMessage =
                            exception.message
                                ?: if (isTamil) {
                                    "பயனர் தகவலை சேமிக்க முடியவில்லை"
                                } else {
                                    "Unable to save user profile"
                                }
                    }
            }
            .addOnFailureListener { exception ->

                isLoading = false

                errorMessage =
                    exception.message
                        ?: if (isTamil) {
                            "கணக்கை உருவாக்க முடியவில்லை"
                        } else {
                            "Unable to create account"
                        }
            }
    }


    // =====================================================
    // SCREEN UI
    // =====================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFFF5F9F5)
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(28.dp)
            )


            // -------------------------------------------------
            // LOGO
            // -------------------------------------------------

            Text(
                text = "🌾",
                fontSize = 52.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )


            Text(
                text = "SmartVillage",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF176B38)
            )


            Text(
                text =
                    if (isTamil) {
                        "ஸ்மார்ட் கிராமம்"
                    } else {
                        "Smart Rural Community"
                    },
                fontSize = 14.sp,
                color = Color.Gray
            )


            Spacer(
                modifier = Modifier.height(22.dp)
            )


            // -------------------------------------------------
            // VILLAGE CARD
            // -------------------------------------------------

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE7F4E9)
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "📍",
                        fontSize = 25.sp
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Column {

                        Text(
                            text =
                                if (isTamil) {
                                    "தேர்ந்தெடுக்கப்பட்ட கிராமம்"
                                } else {
                                    "Selected Village"
                                },
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Text(
                            text = villageName.ifBlank {

                                if (isTamil)
                                    "கிராமம் தேர்ந்தெடுக்கப்படவில்லை"
                                else
                                    "No village selected"

                            },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF176B38)
                        )
                    }
                }
            }


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // -------------------------------------------------
            // LOGIN / REGISTER SWITCH
            // -------------------------------------------------

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        RoundedCornerShape(14.dp)
                    )
                    .padding(4.dp)
            ) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isLoginMode)
                                Color(0xFF176B38)
                            else
                                Color.Transparent,
                            RoundedCornerShape(11.dp)
                        )
                        .clickable {

                            isLoginMode = true
                            errorMessage = ""
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            if (isTamil)
                                "உள்நுழைவு"
                            else
                                "Login",
                        fontWeight = FontWeight.Bold,
                        color =
                            if (isLoginMode)
                                Color.White
                            else
                                Color.DarkGray
                    )
                }


                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (!isLoginMode)
                                Color(0xFF176B38)
                            else
                                Color.Transparent,
                            RoundedCornerShape(11.dp)
                        )
                        .clickable {

                            isLoginMode = false
                            errorMessage = ""
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            if (isTamil)
                                "பதிவு"
                            else
                                "Register",
                        fontWeight = FontWeight.Bold,
                        color =
                            if (!isLoginMode)
                                Color.White
                            else
                                Color.DarkGray
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // -------------------------------------------------
            // FORM CARD
            // -------------------------------------------------

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {

                    // -----------------------------------------
                    // REGISTER NAME
                    // -----------------------------------------

                    if (!isLoginMode) {

                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                errorMessage = ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = {

                                Text(
                                    if (isTamil)
                                        "பெயர்"
                                    else
                                        "Full Name"
                                )
                            },
                            leadingIcon = {

                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null
                                )
                            },
                            shape =
                                RoundedCornerShape(14.dp)
                        )


                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )


                        // -------------------------------------
                        // PHONE
                        // -------------------------------------

                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                phone = it
                                errorMessage = ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = {

                                Text(
                                    if (isTamil)
                                        "தொலைபேசி"
                                    else
                                        "Phone Number"
                                )
                            },
                            leadingIcon = {

                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = null
                                )
                            },
                            shape =
                                RoundedCornerShape(14.dp)
                        )


                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )
                    }


                    // -----------------------------------------
                    // EMAIL
                    // -----------------------------------------

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = {

                            Text(
                                if (isTamil)
                                    "மின்னஞ்சல்"
                                else
                                    "Email"
                            )
                        },
                        leadingIcon = {

                            Icon(
                                Icons.Default.Email,
                                contentDescription = null
                            )
                        },
                        shape =
                            RoundedCornerShape(14.dp)
                    )


                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )


                    // -----------------------------------------
                    // PASSWORD
                    // -----------------------------------------

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = {

                            Text(
                                if (isTamil)
                                    "கடவுச்சொல்"
                                else
                                    "Password"
                            )
                        },
                        leadingIcon = {

                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {

                            IconButton(
                                onClick = {
                                    passwordVisible =
                                        !passwordVisible
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        if (passwordVisible)
                                            Icons.Default.VisibilityOff
                                        else
                                            Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation =
                            if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                        shape =
                            RoundedCornerShape(14.dp)
                    )


                    // -----------------------------------------
                    // CONFIRM PASSWORD
                    // -----------------------------------------

                    if (!isLoginMode) {

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )


                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                errorMessage = ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = {

                                Text(
                                    if (isTamil)
                                        "கடவுச்சொல்லை உறுதிப்படுத்தவும்"
                                    else
                                        "Confirm Password"
                                )
                            },
                            leadingIcon = {

                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {

                                IconButton(
                                    onClick = {

                                        confirmPasswordVisible =
                                            !confirmPasswordVisible
                                    }
                                ) {

                                    Icon(
                                        imageVector =
                                            if (confirmPasswordVisible)
                                                Icons.Default.VisibilityOff
                                            else
                                                Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation =
                                if (confirmPasswordVisible)
                                    VisualTransformation.None
                                else
                                    PasswordVisualTransformation(),
                            shape =
                                RoundedCornerShape(14.dp)
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    // -----------------------------------------
                    // REMEMBER ME
                    // -----------------------------------------

                    if (isLoginMode) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = {
                                    rememberMe = it
                                }
                            )

                            Text(
                                text =
                                    if (isTamil)
                                        "என்னை நினைவில் வைத்துக்கொள்"
                                    else
                                        "Remember me",
                                fontSize = 13.sp
                            )
                        }
                    }


                    // -----------------------------------------
                    // ERROR MESSAGE
                    // -----------------------------------------

                    if (errorMessage.isNotBlank()) {

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )


                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape =
                                RoundedCornerShape(12.dp),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        Color(0xFFFFE8E8)
                                )
                        ) {

                            Text(
                                text = errorMessage,
                                modifier =
                                    Modifier.padding(12.dp),
                                color =
                                    Color(0xFFB3261E),
                                fontSize = 13.sp
                            )
                        }
                    }


                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )


                    // -----------------------------------------
                    // MAIN LOGIN / REGISTER BUTTON
                    // -----------------------------------------

                    Button(
                        onClick = {

                            if (isLoginMode) {
                                loginUser()
                            } else {
                                registerUser()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = !isLoading,
                        shape =
                            RoundedCornerShape(15.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    Color(0xFF176B38)
                            )
                    ) {

                        if (isLoading) {

                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )

                        } else {

                            Text(
                                text =
                                    if (isLoginMode) {

                                        if (isTamil)
                                            "உள்நுழைக"
                                        else
                                            "Login"

                                    } else {

                                        if (isTamil)
                                            "கணக்கை உருவாக்கு"
                                        else
                                            "Create Account"
                                    },
                                fontSize = 16.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }


                    // -----------------------------------------
                    // BIOMETRIC LOGIN BUTTON
                    // -----------------------------------------

                    if (
                        isLoginMode &&
                        biometricEnabled &&
                        isBiometricAvailable()
                    ) {

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )


                        OutlinedButton(
                            onClick = {

                                errorMessage = ""

                                startBiometricLogin()
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                            shape =
                                RoundedCornerShape(15.dp)
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint =
                                    Color(0xFF176B38)
                            )


                            Spacer(
                                modifier =
                                    Modifier.width(8.dp)
                            )


                            Text(
                                text =
                                    if (isTamil) {
                                        "பயோமெட்ரிக் மூலம் உள்நுழைக"
                                    } else {
                                        "Login with Biometrics"
                                    },
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    Color(0xFF176B38)
                            )
                        }
                    }


                    // -----------------------------------------
                    // ENABLE BIOMETRIC
                    // -----------------------------------------

                    if (
                        isLoginMode &&
                        !biometricEnabled &&
                        isBiometricAvailable()
                    ) {

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )


                        OutlinedButton(
                            onClick = {

                                enableBiometric()
                            },
                            modifier =
                                Modifier.fillMaxWidth(),
                            shape =
                                RoundedCornerShape(14.dp)
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Fingerprint,
                                contentDescription = null
                            )


                            Spacer(
                                modifier =
                                    Modifier.width(8.dp)
                            )


                            Text(
                                text =
                                    if (isTamil) {
                                        "பயோமெட்ரிக் உள்நுழைவை இயக்கவும்"
                                    } else {
                                        "Enable Biometric Login"
                                    }
                            )
                        }
                    }
                }
            }


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // -------------------------------------------------
            // FOOTER
            // -------------------------------------------------

            Text(
                text =
                    if (isTamil) {
                        "உங்கள் கிராமத்தின் டிஜிட்டல் உதவியாளர்"
                    } else {
                        "Your digital assistant for village life"
                    },
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}