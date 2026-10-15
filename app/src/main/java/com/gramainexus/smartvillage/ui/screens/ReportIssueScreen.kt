package com.gramainexus.smartvillage.ui.screens

import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gramainexus.smartvillage.data.IssuePreferences
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.launch


// =========================================================
// SAVE ISSUE PHOTO LOCALLY
// =========================================================

private fun saveIssuePhotoLocally(
    context: Context,
    imageUri: Uri
): String {

    val issueImagesDirectory =
        File(
            context.filesDir,
            "issue_images"
        )

    if (!issueImagesDirectory.exists()) {
        issueImagesDirectory.mkdirs()
    }

    val imageFile =
        File(
            issueImagesDirectory,
            "issue_${System.currentTimeMillis()}.jpg"
        )

    val inputStream =
        context.contentResolver.openInputStream(imageUri)
            ?: throw Exception("Unable to open selected image")

    inputStream.use { input ->

        FileOutputStream(imageFile).use { output ->

            input.copyTo(output)
        }
    }

    return imageFile.absolutePath
}


// =========================================================
// REPORT ISSUE SCREEN
// =========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssueScreen(
    selectedLanguage: String,
    selectedVillage: String,
    onBack: () -> Unit,
    onIssueSubmitted: () -> Unit
) {

    val isTamil =
        selectedLanguage == "Tamil"

    val context =
        LocalContext.current

    val coroutineScope =
        rememberCoroutineScope()

    val issuePreferences =
        remember {
            IssuePreferences()
        }
    val fusedLocationClient =
        remember {
            LocationServices.getFusedLocationProviderClient(context)
        }


    // =====================================================
    // FORM STATE
    // =====================================================

    var category by remember {
        mutableStateOf("")
    }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    var description by remember {
        mutableStateOf("")
    }

    var location by remember {
        mutableStateOf("")
    }

    var gpsLocation by remember {
        mutableStateOf("")
    }

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var savedPhotoPath by remember {
        mutableStateOf("")
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf("")
    }


    // =====================================================
    // IMAGE PICKER
    // =====================================================

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                selectedImageUri = uri

                savedPhotoPath = ""

                message = ""
            }
        }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val fineLocationGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineLocationGranted || coarseLocationGranted) {

                try {

                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { locationResult ->

                            if (locationResult != null) {

                                gpsLocation =
                                    "${locationResult.latitude}, ${locationResult.longitude}"

                                message =
                                    if (isTamil)
                                        "GPS இருப்பிடம் பெறப்பட்டது"
                                    else
                                        "GPS location captured"

                            } else {

                                message =
                                    if (isTamil)
                                        "GPS இருப்பிடத்தைப் பெற முடியவில்லை"
                                    else
                                        "Unable to get GPS location"
                            }
                        }
                        .addOnFailureListener {

                            message =
                                if (isTamil)
                                    "GPS இருப்பிடத்தைப் பெற முடியவில்லை"
                                else
                                    "Unable to get GPS location"
                        }

                } catch (e: SecurityException) {

                    message =
                        if (isTamil)
                            "GPS அனுமதி தேவை"
                        else
                            "Location permission is required"
                }

            } else {

                message =
                    if (isTamil)
                        "GPS இருப்பிட அனுமதி தேவை"
                    else
                        "Location permission is required"
            }
        }
    // =====================================================
    // SCREEN
    // =====================================================

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {

        val locationRequest =
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                5000L
            ).setMaxUpdates(1)
                .build()

        val locationCallback =
            object : LocationCallback() {

                override fun onLocationResult(
                    result: LocationResult
                ) {

                    val locationResult =
                        result.lastLocation

                    if (locationResult != null) {

                        gpsLocation =
                            "${locationResult.latitude}, ${locationResult.longitude}"

                        message =
                            if (isTamil)
                                "GPS இருப்பிடம் பெறப்பட்டது"
                            else
                                "GPS location captured"

                    } else {

                        message =
                            if (isTamil)
                                "GPS இருப்பிடத்தைப் பெற முடியவில்லை"
                            else
                                "Unable to get GPS location"
                    }
                }
            }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            context.mainLooper
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(16.dp)
    ) {


        // =================================================
        // TITLE
        // =================================================

        Text(
            text =
                if (isTamil)
                    "பிரச்சினையைப் புகாரளிக்கவும்"
                else
                    "Report an Issue"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )


        // =================================================
        // VILLAGE
        // =================================================

        Text(
            text =
                if (isTamil)
                    "கிராமம்: $selectedVillage"
                else
                    "Village: $selectedVillage"
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )


        // =================================================
        // ISSUE CATEGORY
        // =================================================

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,

            onExpandedChange = {

                if (!isSaving) {

                    categoryExpanded =
                        !categoryExpanded
                }
            }
        ) {

            OutlinedTextField(
                value = category,

                onValueChange = {},

                readOnly = true,

                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),

                label = {

                    Text(
                        if (isTamil)
                            "பிரச்சினையின் வகை"
                        else
                            "Issue Category"
                    )
                },

                placeholder = {

                    Text(
                        if (isTamil)
                            "வகையைத் தேர்ந்தெடுக்கவும்"
                        else
                            "Select issue category"
                    )
                },

                trailingIcon = {

                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = categoryExpanded
                    )
                },

                enabled = !isSaving
            )


            ExposedDropdownMenu(
                expanded = categoryExpanded,

                onDismissRequest = {

                    categoryExpanded = false
                }
            ) {

                IssuePreferences.ISSUE_CATEGORIES
                    .forEach { issueCategory ->

                        DropdownMenuItem(

                            text = {

                                Text(
                                    issueCategory
                                )
                            },

                            onClick = {

                                category =
                                    issueCategory

                                categoryExpanded =
                                    false

                                message = ""
                            }
                        )
                    }
            }
        }


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        // =================================================
        // DESCRIPTION
        // =================================================

        OutlinedTextField(
            value = description,

            onValueChange = {

                description = it

                message = ""
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),

            label = {

                Text(
                    if (isTamil)
                        "பிரச்சினையின் விவரம்"
                    else
                        "Issue Description"
                )
            },

            enabled = !isSaving
        )


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        // =================================================
        // LOCATION
        // =================================================

        OutlinedTextField(
            value = location,

            onValueChange = {

                location = it

                message = ""
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {

                Text(
                    if (isTamil)
                        "இடம்"
                    else
                        "Location"
                )
            },

            placeholder = {

                Text(
                    if (isTamil)
                        "பிரச்சினை ஏற்பட்ட இடத்தை உள்ளிடவும்"
                    else
                        "Enter the problem location"
                )
            },

            enabled = !isSaving
        )


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        // =================================================
        // GPS LOCATION
        // =================================================

        OutlinedTextField(
            value = gpsLocation,

            onValueChange = {

                gpsLocation = it
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {

                Text(
                    if (isTamil)
                        "GPS இருப்பிடம்"
                    else
                        "GPS Location"
                )
            },

            placeholder = {

                Text(
                    if (isTamil)
                        "GPS இருப்பிடம் பெறப்படும்"
                    else
                        "GPS location will be captured"
                )
            },

            enabled = !isSaving,

            readOnly = true
        )


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // =================================================
        // GPS BUTTON
        // =================================================

        Button(


            onClick = {

                val fineGranted =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                val coarseGranted =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                if (fineGranted || coarseGranted) {

                    message =
                        if (isTamil)
                            "இருப்பிடத்தைப் பெறுகிறது..."
                        else
                            "Getting location..."

                    getCurrentLocation()

                } else {

                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },

            modifier =
                Modifier.fillMaxWidth(),

            enabled = !isSaving
        ) {

            Text(
                if (isTamil)
                    "GPS இருப்பிடத்தைப் பெறுக"
                else
                    "Get GPS Location"
            )
        }


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // =================================================
        // SELECT PHOTO
        // =================================================

        Button(

            onClick = {

                imagePickerLauncher.launch(
                    "image/*"
                )
            },

            modifier =
                Modifier.fillMaxWidth(),

            enabled = !isSaving
        ) {

            Text(
                if (isTamil)
                    "புகைப்படத்தைத் தேர்ந்தெடுக்கவும்"
                else
                    "Select Photo"
            )
        }


        Spacer(
            modifier = Modifier.height(15.dp)
        )


        // =================================================
        // PHOTO PREVIEW
        // =================================================

        val imageToDisplay =
            if (savedPhotoPath.isNotBlank()) {

                File(savedPhotoPath)

            } else {

                selectedImageUri
            }


        if (
            selectedImageUri != null ||
            savedPhotoPath.isNotBlank()
        ) {

            AsyncImage(

                model = imageToDisplay,

                contentDescription =
                    if (isTamil)
                        "தேர்ந்தெடுக்கப்பட்ட புகைப்படம்"
                    else
                        "Selected issue photo",

                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(
                        RoundedCornerShape(16.dp)
                    ),

                contentScale =
                    ContentScale.Crop
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )
        }


        // =================================================
        // MESSAGE
        // =================================================

        if (message.isNotBlank()) {

            Text(
                text = message
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }


        // =================================================
        // SAVING INDICATOR
        // =================================================

        if (isSaving) {

            CircularProgressIndicator()

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                if (isTamil)
                    "சேமிக்கப்படுகிறது..."
                else
                    "Saving..."
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )
        }


        // =================================================
        // BACK + SUBMIT
        // =================================================

        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {


            // =============================================
            // BACK
            // =============================================

            Button(

                onClick = {

                    onBack()
                },

                modifier =
                    Modifier.weight(1f),

                enabled = !isSaving
            ) {

                Text(
                    if (isTamil)
                        "பின்"
                    else
                        "Back"
                )
            }


            // =============================================
            // SUBMIT
            // =============================================

            Button(

                onClick = {

                    // -------------------------------------
                    // CATEGORY VALIDATION
                    // -------------------------------------

                    if (category.isBlank()) {

                        message =
                            if (isTamil)
                                "பிரச்சினையின் வகையைத் தேர்ந்தெடுக்கவும்"
                            else
                                "Please select the issue category"

                        return@Button
                    }


                    // -------------------------------------
                    // DESCRIPTION VALIDATION
                    // -------------------------------------

                    if (description.isBlank()) {

                        message =
                            if (isTamil)
                                "பிரச்சினையின் விவரத்தை உள்ளிடவும்"
                            else
                                "Please enter the issue description"

                        return@Button
                    }


                    // -------------------------------------
                    // LOCATION VALIDATION
                    // -------------------------------------

                    if (location.isBlank()) {

                        message =
                            if (isTamil)
                                "இடத்தை உள்ளிடவும்"
                            else
                                "Please enter the location"

                        return@Button
                    }


                    // -------------------------------------
                    // START SAVING
                    // -------------------------------------

                    isSaving = true

                    message = ""


                    coroutineScope.launch {

                        try {

                            // ---------------------------------
                            // SAVE PHOTO LOCALLY
                            // ---------------------------------

                            val photoPath =
                                if (
                                    selectedImageUri != null
                                ) {

                                    saveIssuePhotoLocally(
                                        context = context,
                                        imageUri =
                                            selectedImageUri!!
                                    )

                                } else {

                                    ""
                                }


                            // ---------------------------------
                            // KEEP PHOTO PATH FOR PREVIEW
                            // ---------------------------------

                            savedPhotoPath =
                                photoPath


                            // ---------------------------------
                            // SAVE ISSUE TO FIRESTORE
                            // ---------------------------------

                            issuePreferences.saveIssue(

                                village =
                                    selectedVillage,

                                category =
                                    category,

                                description =
                                    description,

                                location =
                                    location,

                                photoUri =
                                    photoPath,

                                gpsLocation =
                                    gpsLocation
                            )


                            // ---------------------------------
                            // SUCCESS
                            // ---------------------------------

                            isSaving = false

                            message =
                                if (isTamil)
                                    "பிரச்சினை வெற்றிகரமாக பதிவு செய்யப்பட்டது"
                                else
                                    "Issue submitted successfully"


                            onIssueSubmitted()


                        } catch (e: Exception) {

                            // ---------------------------------
                            // ERROR
                            // ---------------------------------

                            isSaving = false

                            message =
                                if (isTamil)
                                    "பிரச்சினையை சேமிக்க முடியவில்லை"
                                else
                                    "Failed to save issue: ${e.message}"
                        }
                    }
                },

                modifier =
                    Modifier.weight(1f),

                enabled =
                    !isSaving
            ) {

                Text(
                    if (isTamil)
                        "சமர்ப்பிக்கவும்"
                    else
                        "Submit"
                )
            }
        }
    }
}