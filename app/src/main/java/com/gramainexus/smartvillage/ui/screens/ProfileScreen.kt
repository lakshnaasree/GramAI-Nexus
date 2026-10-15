package com.gramainexus.smartvillage

import android.net.Uri
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import coil3.compose.AsyncImage

import com.gramainexus.smartvillage.data.FarmerProfile


@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {

    // =========================================================
    // VIEWMODEL STATE
    // =========================================================

    val profile by
    viewModel.profile.collectAsState()

    val isLoading by
    viewModel.isLoading.collectAsState()

    val message by
    viewModel.message.collectAsState()

    val context =
        androidx.compose.ui.platform.LocalContext.current


    // =========================================================
    // LOCAL FORM STATE
    // =========================================================

    var name by remember {
        mutableStateOf("")
    }

    var phone by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var village by remember {
        mutableStateOf("")
    }

    var district by remember {
        mutableStateOf("")
    }

    var state by remember {
        mutableStateOf("")
    }

    var landArea by remember {
        mutableStateOf("")
    }

    var crops by remember {
        mutableStateOf("")
    }


    // =========================================================
    // SNACKBAR
    // =========================================================

    val snackbarHostState =
        remember {
            SnackbarHostState()
        }


    // =========================================================
    // IMAGE PICKER
    // =========================================================

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            if (uri != null) {

                viewModel.saveProfileImage(
                    context = context,
                    imageUri = uri
                )
            }
        }


    // =========================================================
    // LOAD PROFILE
    // =========================================================

    LaunchedEffect(Unit) {

        viewModel.loadProfile(context)
    }


    // =========================================================
    // UPDATE FORM AFTER PROFILE LOAD
    // =========================================================

    LaunchedEffect(profile) {

        name = profile.name

        phone = profile.phone

        email = profile.email

        village = profile.village

        district = profile.district

        state = profile.state

        landArea = profile.landArea

        crops = profile.crops
    }


    // =========================================================
    // SHOW MESSAGE
    // =========================================================

    LaunchedEffect(message) {

        if (message.isNotEmpty()) {

            snackbarHostState.showSnackbar(
                message
            )

            viewModel.clearMessage()
        }
    }


    // =========================================================
    // SCREEN
    // =========================================================

    Scaffold(

        snackbarHost = {

            SnackbarHost(
                hostState =
                    snackbarHostState
            )
        }

    ) { paddingValues ->

        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

        ) {

            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal = 20.dp,
                        vertical = 16.dp
                    ),

                horizontalAlignment =
                    Alignment.CenterHorizontally

            ) {

                // =================================================
                // HEADER
                // =================================================

                Text(
                    text = "My Profile",
                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Text(
                    text =
                        "Manage your farmer information",
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium
                )

                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                // =================================================
                // PROFILE PHOTO
                // =================================================

                Box(
                    contentAlignment =
                        Alignment.BottomEnd
                ) {

                    if (
                        profile.profilePhotoUrl
                            .isNotBlank()
                    ) {

                        AsyncImage(
                            model = if (profile.profilePhotoUrl.isNotBlank()) {
                                File(profile.profilePhotoUrl)
                            } else {
                                null
                            },
                            contentDescription = "Farmer Profile Photo",
                            modifier = Modifier
                                .size(130.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                    } else {

                        Box(

                            modifier =
                                Modifier
                                    .size(130.dp)
                                    .clip(
                                        CircleShape
                                    )
                                    .background(
                                        MaterialTheme
                                            .colorScheme
                                            .primaryContainer
                                    ),

                            contentAlignment =
                                Alignment.Center

                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.Person,

                                contentDescription =
                                    "Profile",

                                modifier =
                                    Modifier.size(65.dp)
                            )
                        }
                    }


                    // =================================================
                    // CAMERA BUTTON
                    // =================================================

                    Surface(

                        modifier =
                            Modifier
                                .size(44.dp)
                                .clip(
                                    CircleShape
                                )
                                .clickable {

                                    imagePickerLauncher
                                        .launch(
                                            "image/*"
                                        )
                                },

                        color =
                            MaterialTheme
                                .colorScheme
                                .primary,

                        shape =
                            CircleShape

                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.CameraAlt,

                                contentDescription =
                                    "Change Profile Photo",

                                tint =
                                    MaterialTheme
                                        .colorScheme
                                        .onPrimary
                            )
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                Text(
                    text =
                        "Tap the camera to change your photo",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall
                )


                Spacer(
                    modifier =
                        Modifier.height(28.dp)
                )


                // =================================================
                // PERSONAL INFORMATION
                // =================================================

                SectionTitle(
                    title =
                        "Personal Information",
                    icon = {

                        Icon(
                            imageVector =
                                Icons.Default.Person,
                            contentDescription =
                                null
                        )
                    }
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                // =================================================
                // NAME
                // =================================================

                OutlinedTextField(

                    value = name,

                    onValueChange = {
                        name = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Full Name")
                    },

                    placeholder = {
                        Text(
                            "Enter your full name"
                        )
                    },

                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default.Person,
                            contentDescription =
                                null
                        )
                    },

                    singleLine = true,

                    shape =
                        RoundedCornerShape(14.dp)
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                // =================================================
                // PHONE
                // =================================================

                OutlinedTextField(

                    value = phone,

                    onValueChange = {
                        phone = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Phone Number")
                    },

                    placeholder = {
                        Text(
                            "Enter phone number"
                        )
                    },

                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default.Phone,
                            contentDescription =
                                null
                        )
                    },

                    singleLine = true,

                    shape =
                        RoundedCornerShape(14.dp)
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                // =================================================
                // EMAIL
                // =================================================

                OutlinedTextField(

                    value = email,

                    onValueChange = {
                        email = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Email")
                    },

                    placeholder = {
                        Text(
                            "Enter email address"
                        )
                    },

                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default.Email,
                            contentDescription =
                                null
                        )
                    },

                    singleLine = true,

                    shape =
                        RoundedCornerShape(14.dp)
                )


                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                // =================================================
                // FARM INFORMATION
                // =================================================

                SectionTitle(
                    title =
                        "Farm Information",
                    icon = {

                        Icon(
                            imageVector =
                                Icons.Default.Agriculture,
                            contentDescription =
                                null
                        )
                    }
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                // =================================================
                // VILLAGE
                // =================================================

                OutlinedTextField(

                    value = village,

                    onValueChange = {
                        village = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Village")
                    },

                    placeholder = {
                        Text(
                            "Enter village name"
                        )
                    },

                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default.LocationOn,
                            contentDescription =
                                null
                        )
                    },

                    singleLine = true,

                    shape =
                        RoundedCornerShape(14.dp)
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                // =================================================
                // DISTRICT
                // =================================================

                OutlinedTextField(

                    value = district,

                    onValueChange = {
                        district = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("District")
                    },

                    placeholder = {
                        Text(
                            "Enter district"
                        )
                    },

                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default.LocationOn,
                            contentDescription =
                                null
                        )
                    },

                    singleLine = true,

                    shape =
                        RoundedCornerShape(14.dp)
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                // =================================================
                // STATE
                // =================================================

                OutlinedTextField(

                    value = state,

                    onValueChange = {
                        state = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("State")
                    },

                    placeholder = {
                        Text(
                            "Enter state"
                        )
                    },

                    singleLine = true,

                    shape =
                        RoundedCornerShape(14.dp)
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                // =================================================
                // LAND AREA
                // =================================================

                OutlinedTextField(

                    value = landArea,

                    onValueChange = {
                        landArea = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Land Area (acres)")
                    },

                    placeholder = {
                        Text(
                            "Example: 2.5"
                        )
                    },

                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default.Agriculture,
                            contentDescription =
                                null
                        )
                    },

                    singleLine = true,

                    shape =
                        RoundedCornerShape(14.dp)
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                // =================================================
                // CROPS
                // =================================================

                OutlinedTextField(

                    value = crops,

                    onValueChange = {
                        crops = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            "Crops Cultivated"
                        )
                    },

                    placeholder = {

                        Text(
                            "Example: Rice, Tomato, Coconut"
                        )
                    },

                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default.Agriculture,
                            contentDescription =
                                null
                        )
                    },

                    minLines = 3,

                    shape =
                        RoundedCornerShape(14.dp)
                )


                Spacer(
                    modifier =
                        Modifier.height(28.dp)
                )


                // =================================================
                // SAVE BUTTON
                // =================================================

                Button(

                    onClick = {

                        val farmerProfile =
                            profile.copy(

                                name = name,

                                phone = phone,

                                email = email,

                                village = village,

                                district = district,

                                state = state,

                                landArea = landArea,

                                crops = crops
                            )

                        viewModel.saveProfile(

                            context = context,

                            farmerProfile =
                                farmerProfile
                        )
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                    shape =
                        RoundedCornerShape(16.dp),

                    enabled =
                        !isLoading

                ) {

                    Icon(

                        imageVector =
                            Icons.Default.Save,

                        contentDescription =
                            null
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text =
                            "Save Profile",
                        fontWeight =
                            FontWeight.Bold
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(30.dp)
                )
            }


            // =====================================================
            // LOADING OVERLAY
            // =====================================================

            if (isLoading) {

                Box(

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme
                                    .colorScheme
                                    .surface
                                    .copy(
                                        alpha = 0.6f
                                    )
                            ),

                    contentAlignment =
                        Alignment.Center

                ) {

                    CircularProgressIndicator()
                }
            }
        }
    }
}


// =============================================================
// SECTION TITLE
// =============================================================

@Composable
private fun SectionTitle(

    title: String,

    icon: @Composable () -> Unit

) {

    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            icon()

            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )

            Text(
                text = title,
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                fontWeight =
                    FontWeight.Bold
            )
        }

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        HorizontalDivider()
    }
}