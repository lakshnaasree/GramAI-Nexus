package com.gramainexus.smartvillage.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.gramainexus.smartvillage.ProfileViewModel
import com.gramainexus.smartvillage.data.FarmerProfile
import java.io.File
import kotlin.math.roundToInt

private val EmploymentGreen = Color(0xFF176B3A)
private val EmploymentDarkGreen = Color(0xFF0D4525)
private val EmploymentLightGreen = Color(0xFFE8F5E9)
private val EmploymentBackground = Color(0xFFF4F7F5)
private val EmploymentBlue = Color(0xFF1976D2)
private val EmploymentTextDark = Color(0xFF183126)
private val EmploymentTextGray = Color(0xFF6B756F)
private val EmploymentBorder = Color(0xFFE0E7E2)
private val EmploymentSoftBlue = Color(0xFFEAF3FF)

private enum class EmploymentEditField {
    ABOUT,
    EXPERIENCE,
    EDUCATION,
    SKILLS,
    CERTIFICATIONS,
    LANGUAGES
}

@Composable
fun EmploymentScreen(
    selectedLanguage: String,
    villageName: String,
    onBack: () -> Unit,
    onFindJobs: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val profile by viewModel.profile.collectAsState()

    val isTamil = selectedLanguage.equals(
        "Tamil",
        ignoreCase = true
    )

    var openToWork by remember {
        mutableStateOf(profile.openToWork)
    }

    var editingField by remember {
        mutableStateOf<EmploymentEditField?>(null)
    }

    LaunchedEffect(profile.openToWork) {
        openToWork = profile.openToWork
    }

    /*
     * ---------------------------------------------------------
     * RESUME PICKER
     * ---------------------------------------------------------
     */

    val resumePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {
                viewModel.saveResume(
                    context = context,
                    resumeUri = uri
                )
            }
        }

    val launchResumePicker = {
        resumePickerLauncher.launch(
            arrayOf("application/pdf")
        )
    }

    /*
     * ---------------------------------------------------------
     * PROFILE COMPLETION
     * ---------------------------------------------------------
     */

    val profileCompletion = remember(profile) {

        val fields = listOf(
            profile.name,
            profile.about,
            profile.education,
            profile.skills,
            profile.experience,
            profile.certifications,
            profile.languages,
            profile.resumePath
        )

        val completed = fields.count {
            it.isNotBlank()
        }

        completed.toFloat() / fields.size.toFloat()
    }

    val completionPercentage =
        (profileCompletion * 100f)
            .roundToInt()
            .coerceIn(0, 100)

    /*
     * ---------------------------------------------------------
     * EDIT DIALOG
     * ---------------------------------------------------------
     */

    editingField?.let { field ->
        EditEmploymentFieldDialog(
            field = field,
            profile = profile,
            isTamil = isTamil,
            onDismiss = {
                editingField = null
            },
            onSave = { updatedProfile ->
                viewModel.saveProfile(
                    context = context,
                    farmerProfile = updatedProfile
                )
                editingField = null
            }
        )
    }

    /*
     * ---------------------------------------------------------
     * SCREEN
     * ---------------------------------------------------------
     */

    Scaffold(
        containerColor = EmploymentBackground,

        topBar = {

            EmploymentTopBar(
                isTamil = isTamil,
                onBack = onBack
            )
        }

    ) { paddingValues ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),

            contentPadding = PaddingValues(
                start = 16.dp,
                top = 12.dp,
                end = 16.dp,
                bottom = 32.dp
            ),

            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            /*
             * -------------------------------------------------
             * PROFILE HEADER
             * -------------------------------------------------
             */

            item {

                ProfessionalProfileCard(
                    name = profile.name,
                    headline = buildProfessionalHeadline(
                        profile = profile,
                        isTamil = isTamil
                    ),
                    village = profile.village.ifBlank {
                        villageName
                    },
                    district = profile.district,
                    profilePhotoUrl = profile.profilePhotoUrl,
                    openToWork = openToWork,
                    isTamil = isTamil
                )
            }

            /*
             * -------------------------------------------------
             * PROFILE STRENGTH
             * -------------------------------------------------
             */

            item {

                ProfileCompletionCard(
                    percentage = completionPercentage,
                    progress = profileCompletion,
                    isTamil = isTamil
                )
            }

            /*
             * -------------------------------------------------
             * OPEN TO WORK
             * -------------------------------------------------
             */

            item {

                OpenToWorkCard(
                    openToWork = openToWork,
                    isTamil = isTamil,
                    onOpenToWorkChanged = { enabled ->

                        openToWork = enabled

                        val updatedProfile =
                            profile.copy(
                                openToWork = enabled
                            )

                        viewModel.saveProfile(
                            context = context,
                            farmerProfile = updatedProfile
                        )
                    }
                )
            }

            /*
             * -------------------------------------------------
             * FIND JOBS
             * -------------------------------------------------
             */

            item {

                FindJobsCard(
                    isTamil = isTamil,
                    onFindJobs = onFindJobs
                )
            }

            /*
             * -------------------------------------------------
             * ABOUT
             * -------------------------------------------------
             */

            item {

                EmploymentSectionCard(
                    title = if (isTamil)
                        "என்னைப் பற்றி"
                    else
                        "About",

                    icon = Icons.Default.Person,

                    actionText = if (
                        profile.about.isBlank()
                    ) {
                        if (isTamil) "சேர்க்கவும்" else "Add"
                    } else {
                        if (isTamil) "திருத்து" else "Edit"
                    },

                    isEmpty = profile.about.isBlank(),

                    onAction = {
                        editingField =
                            EmploymentEditField.ABOUT
                    }
                ) {

                    Text(
                        text = profile.about.ifBlank {

                            if (isTamil) {
                                "உங்களைப் பற்றிய தொழில் அறிமுகத்தை சேர்க்கவும்."
                            } else {
                                "Add a short professional introduction about yourself."
                            }
                        },

                        color =
                            if (profile.about.isBlank())
                                EmploymentTextGray
                            else
                                EmploymentTextDark,

                        style =
                            MaterialTheme.typography.bodyMedium
                    )
                }
            }

            /*
             * -------------------------------------------------
             * EXPERIENCE
             * -------------------------------------------------
             */

            item {

                EmploymentSectionCard(
                    title = if (isTamil)
                        "பணி அனுபவம்"
                    else
                        "Experience",

                    icon = Icons.Default.WorkHistory,

                    actionText = if (
                        profile.experience.isBlank()
                    ) {
                        if (isTamil) "சேர்க்கவும்" else "Add"
                    } else {
                        if (isTamil) "திருத்து" else "Edit"
                    },

                    isEmpty = profile.experience.isBlank(),

                    onAction = {
                        editingField =
                            EmploymentEditField.EXPERIENCE
                    }
                ) {

                    ExperienceContent(
                        experience = profile.experience,
                        isTamil = isTamil
                    )
                }
            }

            /*
             * -------------------------------------------------
             * EDUCATION
             * -------------------------------------------------
             */

            item {

                EmploymentSectionCard(
                    title = if (isTamil)
                        "கல்வி"
                    else
                        "Education",

                    icon = Icons.Default.School,

                    actionText = if (
                        profile.education.isBlank()
                    ) {
                        if (isTamil) "சேர்க்கவும்" else "Add"
                    } else {
                        if (isTamil) "திருத்து" else "Edit"
                    },

                    isEmpty = profile.education.isBlank(),

                    onAction = {
                        editingField =
                            EmploymentEditField.EDUCATION
                    }
                ) {

                    Text(
                        text = profile.education.ifBlank {

                            if (isTamil)
                                "உங்கள் கல்வித் தகுதியை சேர்க்கவும்."
                            else
                                "Add your educational qualification."
                        },

                        color =
                            if (profile.education.isBlank())
                                EmploymentTextGray
                            else
                                EmploymentTextDark,

                        style =
                            MaterialTheme.typography.bodyMedium
                    )
                }
            }

            /*
             * -------------------------------------------------
             * SKILLS
             * -------------------------------------------------
             */

            item {

                EmploymentSectionCard(
                    title = if (isTamil)
                        "திறன்கள்"
                    else
                        "Skills",

                    icon = Icons.Default.Badge,

                    actionText = if (
                        profile.skills.isBlank()
                    ) {
                        if (isTamil) "சேர்க்கவும்" else "Add"
                    } else {
                        if (isTamil) "திருத்து" else "Edit"
                    },

                    isEmpty = profile.skills.isBlank(),

                    onAction = {
                        editingField =
                            EmploymentEditField.SKILLS
                    }
                ) {

                    SkillsContent(
                        skills = profile.skills,
                        isTamil = isTamil
                    )
                }
            }

            /*
             * -------------------------------------------------
             * CERTIFICATIONS
             * -------------------------------------------------
             */

            item {

                EmploymentSectionCard(
                    title = if (isTamil)
                        "சான்றிதழ்கள்"
                    else
                        "Certifications",

                    icon = Icons.Default.CheckCircle,

                    actionText = if (
                        profile.certifications.isBlank()
                    ) {
                        if (isTamil) "சேர்க்கவும்" else "Add"
                    } else {
                        if (isTamil) "திருத்து" else "Edit"
                    },

                    isEmpty =
                        profile.certifications.isBlank(),

                    onAction = {
                        editingField =
                            EmploymentEditField.CERTIFICATIONS
                    }
                ) {

                    Text(
                        text =
                            profile.certifications.ifBlank {

                                if (isTamil)
                                    "பயிற்சி மற்றும் தொழில் சான்றிதழ்களை சேர்க்கவும்."
                                else
                                    "Add your training and professional certifications."
                            },

                        color =
                            if (
                                profile.certifications.isBlank()
                            ) {
                                EmploymentTextGray
                            } else {
                                EmploymentTextDark
                            },

                        style =
                            MaterialTheme.typography.bodyMedium
                    )
                }
            }

            /*
             * -------------------------------------------------
             * LANGUAGES
             * -------------------------------------------------
             */

            item {

                EmploymentSectionCard(
                    title = if (isTamil)
                        "மொழிகள்"
                    else
                        "Languages",

                    icon = Icons.Default.Language,

                    actionText = if (
                        profile.languages.isBlank()
                    ) {
                        if (isTamil) "சேர்க்கவும்" else "Add"
                    } else {
                        if (isTamil) "திருத்து" else "Edit"
                    },

                    isEmpty =
                        profile.languages.isBlank(),

                    onAction = {
                        editingField =
                            EmploymentEditField.LANGUAGES
                    }
                ) {

                    LanguagesContent(
                        languages = profile.languages,
                        isTamil = isTamil
                    )
                }
            }

            /*
             * -------------------------------------------------
             * RESUME
             * -------------------------------------------------
             */

            item {

                ResumeCard(
                    resumePath = profile.resumePath,
                    isTamil = isTamil,
                    onResumeClick = launchResumePicker
                )
            }

            /*
             * -------------------------------------------------
             * JOB PREFERENCES
             * -------------------------------------------------
             */

            item {

                JobPreferencesCard(
                    profile = profile,
                    villageName = villageName,
                    isTamil = isTamil
                )
            }

            /*
             * -------------------------------------------------
             * FINAL FIND JOBS
             * -------------------------------------------------
             */

            item {

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Button(
                    onClick = onFindJobs,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),

                    shape = RoundedCornerShape(18.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmploymentGreen
                    )
                ) {

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = if (isTamil)
                            "வேலைகளை தேடுங்கள்"
                        else
                            "Find Jobs",

                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/*
 * ============================================================
 * TOP BAR
 * ============================================================
 */

@Composable
private fun EmploymentTopBar(
    isTamil: Boolean,
    onBack: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),

        color = EmploymentBackground,

        shadowElevation = 2.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    top = 10.dp,
                    end = 16.dp,
                    bottom = 10.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = if (isTamil)
                        "பின்"
                    else
                        "Back",

                    tint = EmploymentTextDark
                )
            }

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = if (isTamil)
                        "வேலைவாய்ப்பு"
                    else
                        "Employment",

                    color = EmploymentTextDark,

                    style =
                        MaterialTheme.typography.titleLarge,

                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = if (isTamil)
                        "உங்கள் தொழில் சுயவிவரம்"
                    else
                        "Your professional profile",

                    color = EmploymentTextGray,

                    style =
                        MaterialTheme.typography.labelSmall
                )
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(EmploymentLightGreen),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = null,
                    tint = EmploymentGreen
                )
            }
        }
    }
}

/*
 * ============================================================
 * PROFILE HEADER
 * ============================================================
 */

@Composable
private fun ProfessionalProfileCard(
    name: String,
    headline: String,
    village: String,
    district: String,
    profilePhotoUrl: String,
    openToWork: Boolean,
    isTamil: Boolean
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(28.dp),

        colors = CardDefaults.cardColors(
            containerColor = EmploymentDarkGreen
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Box {

            Box(
                modifier = Modifier
                    .size(170.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(alpha = 0.035f)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    ProfileImage(
                        profilePhotoUrl =
                            profilePhotoUrl
                    )

                    Spacer(
                        modifier = Modifier.width(16.dp)
                    )

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text = name.ifBlank {
                                if (isTamil)
                                    "உங்கள் பெயர்"
                                else
                                    "Your Name"
                            },

                            color = Color.White,

                            style =
                                MaterialTheme
                                    .typography
                                    .headlineSmall,

                            fontWeight =
                                FontWeight.ExtraBold,

                            maxLines = 1,

                            overflow =
                                TextOverflow.Ellipsis
                        )

                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )

                        Text(
                            text = headline,

                            color =
                                Color.White.copy(
                                    alpha = 0.85f
                                ),

                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,

                            maxLines = 2,

                            overflow =
                                TextOverflow.Ellipsis
                        )

                        Spacer(
                            modifier =
                                Modifier.height(7.dp)
                        )

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.LocationOn,

                                contentDescription =
                                    null,

                                tint =
                                    Color(0xFFA5D6A7),

                                modifier =
                                    Modifier.size(15.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(3.dp)
                            )

                            Text(
                                text = listOf(
                                    village,
                                    district
                                )
                                    .filter {
                                        it.isNotBlank()
                                    }
                                    .joinToString(", ")
                                    .ifBlank {

                                        if (isTamil)
                                            "இடம் சேர்க்கப்படவில்லை"
                                        else
                                            "Location not added"
                                    },

                                color =
                                    Color.White.copy(
                                        alpha = 0.72f
                                    ),

                                style =
                                    MaterialTheme
                                        .typography
                                        .labelSmall,

                                maxLines = 1,

                                overflow =
                                    TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Color.White.copy(
                                alpha = 0.12f
                            )
                        )
                )

                Spacer(
                    modifier =
                        Modifier.height(15.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(
                                if (openToWork)
                                    Color(0xFF69F0AE)
                                else
                                    Color.White.copy(
                                        alpha = 0.35f
                                    )
                            )
                    )

                    Spacer(
                        modifier =
                            Modifier.width(7.dp)
                    )

                    Text(
                        text = if (openToWork) {

                            if (isTamil)
                                "வேலை தேடுகிறேன்"
                            else
                                "Open to work"

                        } else {

                            if (isTamil)
                                "தற்போது வேலை தேடவில்லை"
                            else
                                "Not currently looking"
                        },

                        color =
                            Color.White.copy(
                                alpha = 0.85f
                            ),

                        style =
                            MaterialTheme
                                .typography
                                .labelMedium,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/*
 * ============================================================
 * PROFILE IMAGE
 * ============================================================
 */

@Composable
private fun ProfileImage(
    profilePhotoUrl: String
) {

    if (profilePhotoUrl.isNotBlank()) {

        AsyncImage(
            model = File(profilePhotoUrl),

            contentDescription =
                "Profile Photo",

            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .border(
                    width = 3.dp,
                    color =
                        Color.White.copy(
                            alpha = 0.75f
                        ),
                    shape = CircleShape
                ),

            contentScale =
                ContentScale.Crop
        )

    } else {

        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(
                    Color.White.copy(
                        alpha = 0.13f
                    )
                )
                .border(
                    width = 3.dp,
                    color =
                        Color.White.copy(
                            alpha = 0.45f
                        ),
                    shape = CircleShape
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector =
                    Icons.Default.Person,

                contentDescription =
                    "Profile",

                tint =
                    Color.White,

                modifier =
                    Modifier.size(42.dp)
            )
        }
    }
}

/*
 * ============================================================
 * PROFILE COMPLETION
 * ============================================================
 */

@Composable
private fun ProfileCompletionCard(
    percentage: Int,
    progress: Float,
    isTamil: Boolean
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(22.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        border =
            androidx.compose.foundation.BorderStroke(
                1.dp,
                EmploymentBorder
            )
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text = if (isTamil)
                            "சுயவிவர நிறைவு"
                        else
                            "Profile strength",

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            EmploymentTextDark
                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            if (percentage >= 80) {

                                if (isTamil)
                                    "உங்கள் சுயவிவரம் வேலைக்கு தயாராக உள்ளது."
                                else
                                    "Your profile is looking strong."

                            } else {

                                if (isTamil)
                                    "மேலும் தகவல்களை சேர்த்து சுயவிவரத்தை மேம்படுத்துங்கள்."
                                else
                                    "Add more details to improve your profile."
                            },

                        color =
                            EmploymentTextGray,

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }

                Text(
                    text = "$percentage%",

                    color =
                        EmploymentGreen,

                    fontWeight =
                        FontWeight.ExtraBold,

                    style =
                        MaterialTheme
                            .typography
                            .titleLarge
                )
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            LinearProgressIndicator(
                progress = {
                    progress
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(
                            RoundedCornerShape(20.dp)
                        ),

                color =
                    EmploymentGreen,

                trackColor =
                    EmploymentLightGreen
            )
        }
    }
}

/*
 * ============================================================
 * OPEN TO WORK
 * ============================================================
 */

@Composable
private fun OpenToWorkCard(
    openToWork: Boolean,
    isTamil: Boolean,
    onOpenToWorkChanged: (Boolean) -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(22.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (openToWork)
                        EmploymentLightGreen
                    else
                        Color.White
            ),

        border =
            androidx.compose.foundation.BorderStroke(
                1.dp,
                EmploymentBorder
            )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(17.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Color.White
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Work,

                    contentDescription =
                        null,

                    tint =
                        if (openToWork)
                            EmploymentGreen
                        else
                            EmploymentTextGray
                )
            }

            Spacer(
                modifier =
                    Modifier.width(13.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = if (isTamil)
                        "வேலைக்கு தயாராக உள்ளேன்"
                    else
                        "Open to Work",

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        EmploymentTextDark
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text = if (openToWork) {

                        if (isTamil)
                            "நீங்கள் வேலை தேடுகிறீர்கள் என்பதை தெரிவிக்கிறது."
                        else
                            "Let employers know you're available."

                    } else {

                        if (isTamil)
                            "வேலை தேடும்போது இதை இயக்கவும்."
                        else
                            "Turn this on when you're looking."
                    },

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    color =
                        EmploymentTextGray
                )
            }

            Switch(
                checked =
                    openToWork,

                onCheckedChange =
                    onOpenToWorkChanged
            )
        }
    }
}

/*
 * ============================================================
 * FIND JOBS
 * ============================================================
 */

@Composable
private fun FindJobsCard(
    isTamil: Boolean,
    onFindJobs: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onFindJobs()
                },

        shape =
            RoundedCornerShape(24.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    EmploymentGreen
            )
    ) {

        Row(
            modifier =
                Modifier.padding(19.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier =
                    Modifier
                        .size(52.dp)
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
                        .background(
                            Color.White.copy(
                                alpha = 0.12f
                            )
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Search,

                    contentDescription =
                        null,

                    tint =
                        Color.White,

                    modifier =
                        Modifier.size(28.dp)
                )
            }

            Spacer(
                modifier =
                    Modifier.width(14.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = if (isTamil)
                        "உங்களுக்கு ஏற்ற வேலை தேடுங்கள்"
                    else
                        "Find your next opportunity",

                    color =
                        Color.White,

                    fontWeight =
                        FontWeight.Bold,

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text = if (isTamil)
                        "அரசு, தனியார் மற்றும் பயிற்சி வாய்ப்புகள்"
                    else
                        "Government, private and apprenticeship opportunities",

                    color =
                        Color.White.copy(
                            alpha = 0.78f
                        ),

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall
                )
            }

            Icon(
                imageVector =
                    Icons.Default.ArrowForward,

                contentDescription =
                    null,

                tint =
                    Color.White
            )
        }
    }
}

/*
 * ============================================================
 * SECTION CARD
 * ============================================================
 */

@Composable
private fun EmploymentSectionCard(
    title: String,
    icon: ImageVector,
    actionText: String,
    isEmpty: Boolean,
    onAction: () -> Unit,
    content: @Composable () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(22.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        border =
            androidx.compose.foundation.BorderStroke(
                1.dp,
                EmploymentBorder
            )
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier =
                        Modifier
                            .size(42.dp)
                            .clip(
                                RoundedCornerShape(13.dp)
                            )
                            .background(
                                EmploymentLightGreen
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            icon,

                        contentDescription =
                            null,

                        tint =
                            EmploymentGreen,

                        modifier =
                            Modifier.size(21.dp)
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(11.dp)
                )

                Text(
                    text =
                        title,

                    modifier =
                        Modifier.weight(1f),

                    color =
                        EmploymentTextDark,

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.Bold
                )

                IconButton(
                    onClick =
                        onAction
                ) {

                    Icon(
                        imageVector =
                            if (isEmpty)
                                Icons.Default.Add
                            else
                                Icons.Default.Edit,

                        contentDescription =
                            actionText,

                        tint =
                            EmploymentGreen
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            content()
        }
    }
}

/*
 * ============================================================
 * EXPERIENCE
 * ============================================================
 */

@Composable
private fun ExperienceContent(
    experience: String,
    isTamil: Boolean
) {

    if (experience.isBlank()) {

        EmptySectionText(
            text = if (isTamil)
                "உங்கள் முந்தைய வேலை அனுபவத்தை சேர்க்கவும்."
            else
                "Add your previous work experience."
        )

        return
    }

    Row(
        verticalAlignment =
            Alignment.Top
    ) {

        Box(
            modifier =
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        EmploymentLightGreen
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector =
                    Icons.Default.Work,

                contentDescription =
                    null,

                tint =
                    EmploymentGreen
            )
        }

        Spacer(
            modifier =
                Modifier.width(12.dp)
        )

        Text(
            text =
                experience,

            color =
                EmploymentTextDark,

            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )
    }
}

/*
 * ============================================================
 * SKILLS
 * ============================================================
 */

@Composable
private fun SkillsContent(
    skills: String,
    isTamil: Boolean
) {

    if (skills.isBlank()) {

        EmptySectionText(
            text = if (isTamil)
                "உங்கள் முக்கிய திறன்களை சேர்க்கவும்."
            else
                "Add your key professional skills."
        )

        return
    }

    val skillList = remember(skills) {

        skills
            .split(",", "•", "\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    Column(
        verticalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {

        skillList
            .chunked(3)
            .take(2)
            .forEach { rowSkills ->

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    rowSkills.forEach { skill ->

                        SkillChip(
                            text = skill
                        )
                    }
                }
            }

        if (skillList.size > 6) {

            Text(
                text = "+${skillList.size - 6} more",

                color =
                    EmploymentTextGray,

                style =
                    MaterialTheme
                        .typography
                        .labelSmall
            )
        }
    }
}

/*
 * ============================================================
 * SKILL CHIP
 * ============================================================
 */

@Composable
private fun SkillChip(
    text: String
) {

    Box(
        modifier =
            Modifier
                .clip(
                    RoundedCornerShape(50.dp)
                )
                .background(
                    EmploymentLightGreen
                )
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                )
    ) {

        Text(
            text =
                text,

            color =
                EmploymentGreen,

            style =
                MaterialTheme
                    .typography
                    .labelMedium,

            fontWeight =
                FontWeight.SemiBold,

            maxLines =
                1,

            overflow =
                TextOverflow.Ellipsis
        )
    }
}

/*
 * ============================================================
 * LANGUAGES
 * ============================================================
 */

@Composable
private fun LanguagesContent(
    languages: String,
    isTamil: Boolean
) {

    if (languages.isBlank()) {

        EmptySectionText(
            text = if (isTamil)
                "உங்களுக்கு தெரிந்த மொழிகளை சேர்க்கவும்."
            else
                "Add the languages you can communicate in."
        )

        return
    }

    val languageList =
        remember(languages) {

            languages
                .split(",", "•", "\n")
                .map { it.trim() }
                .filter { it.isNotBlank() }
        }

    Row(
        horizontalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {

        languageList
            .take(5)
            .forEach { language ->

                SkillChip(
                    text =
                        language
                )
            }
    }
}

/*
 * ============================================================
 * RESUME
 * ============================================================
 */

@Composable
private fun ResumeCard(
    resumePath: String,
    isTamil: Boolean,
    onResumeClick: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(22.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    EmploymentDarkGreen
            )
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier =
                        Modifier
                            .size(50.dp)
                            .clip(
                                RoundedCornerShape(15.dp)
                            )
                            .background(
                                Color.White.copy(
                                    alpha = 0.12f
                                )
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Description,

                        contentDescription =
                            null,

                        tint =
                            Color.White,

                        modifier =
                            Modifier.size(27.dp)
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(13.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text = "Resume",

                        color =
                            Color.White,

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            if (resumePath.isNotBlank()) {

                                if (isTamil)
                                    "PDF சேமிக்கப்பட்டுள்ளது"
                                else
                                    "PDF resume added"

                            } else {

                                if (isTamil)
                                    "உங்கள் Resume-ஐ சேர்க்கவும்"
                                else
                                    "Add your professional PDF resume"
                            },

                        color =
                            Color.White.copy(
                                alpha = 0.72f
                            ),

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            Button(
                onClick =
                    onResumeClick,

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(15.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Color.White,

                        contentColor =
                            EmploymentDarkGreen
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Description,

                    contentDescription =
                        null
                )

                Spacer(
                    modifier =
                        Modifier.width(7.dp)
                )

                Text(
                    text =
                        if (resumePath.isNotBlank()) {

                            if (isTamil)
                                "Resume மாற்றவும்"
                            else
                                "Change Resume"

                        } else {

                            if (isTamil)
                                "Resume சேர்க்கவும்"
                            else
                                "Add Resume"
                        },

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}

/*
 * ============================================================
 * JOB PREFERENCES
 * ============================================================
 */

@Composable
private fun JobPreferencesCard(
    profile: FarmerProfile,
    villageName: String,
    isTamil: Boolean
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(22.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        border =
            androidx.compose.foundation.BorderStroke(
                1.dp,
                EmploymentBorder
            )
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier =
                        Modifier
                            .size(42.dp)
                            .clip(
                                RoundedCornerShape(13.dp)
                            )
                            .background(
                                EmploymentSoftBlue
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.BusinessCenter,

                        contentDescription =
                            null,

                        tint =
                            EmploymentBlue
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(11.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text = if (isTamil)
                            "வேலை விருப்பங்கள்"
                        else
                            "Job Preferences",

                        color =
                            EmploymentTextDark,

                        fontWeight =
                            FontWeight.Bold,

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    Text(
                        text = if (isTamil)
                            "உங்களுக்கு பொருத்தமான வேலைகளை கண்டறிய உதவும்"
                        else
                            "Your profile-based job preferences",

                        color =
                            EmploymentTextGray,

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(15.dp)
            )

            PreferenceRow(
                icon =
                    Icons.Default.LocationOn,

                title = if (isTamil)
                    "விருப்பமான இடம்"
                else
                    "Preferred Location",

                value = listOf(
                    profile.village,
                    profile.district,
                    villageName
                )
                    .firstOrNull {
                        it.isNotBlank()
                    }
                    ?: if (isTamil)
                        "இடம் அமைக்கப்படவில்லை"
                    else
                        "Location not set"
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            PreferenceRow(
                icon =
                    Icons.Default.Work,

                title = if (isTamil)
                    "வேலை நிலை"
                else
                    "Availability",

                value = if (profile.openToWork) {

                    if (isTamil)
                        "வேலை வாய்ப்புகளுக்கு தயாராக உள்ளேன்"
                    else
                        "Open to new opportunities"

                } else {

                    if (isTamil)
                        "தற்போது வேலை தேடவில்லை"
                    else
                        "Not currently looking"
                }
            )
        }
    }
}

/*
 * ============================================================
 * PREFERENCE ROW
 * ============================================================
 */

@Composable
private fun PreferenceRow(
    icon: ImageVector,
    title: String,
    value: String
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector =
                icon,

            contentDescription =
                null,

            tint =
                EmploymentGreen,

            modifier =
                Modifier.size(20.dp)
        )

        Spacer(
            modifier =
                Modifier.width(10.dp)
        )

        Column {

            Text(
                text =
                    title,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    EmploymentTextDark,

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )

            Text(
                text =
                    value,

                color =
                    EmploymentTextGray,

                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )
        }
    }
}

/*
 * ============================================================
 * EMPTY SECTION
 * ============================================================
 */

@Composable
private fun EmptySectionText(
    text: String
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector =
                Icons.Default.Add,

            contentDescription =
                null,

            tint =
                EmploymentGreen,

            modifier =
                Modifier.size(18.dp)
        )

        Spacer(
            modifier =
                Modifier.width(8.dp)
        )

        Text(
            text =
                text,

            color =
                EmploymentTextGray,

            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )
    }
}

/*
 * ============================================================
 * EDIT EMPLOYMENT FIELD DIALOG
 * ============================================================
 */

@Composable
private fun EditEmploymentFieldDialog(
    field: EmploymentEditField,
    profile: FarmerProfile,
    isTamil: Boolean,
    onDismiss: () -> Unit,
    onSave: (FarmerProfile) -> Unit
) {

    val initialValue = when (field) {

        EmploymentEditField.ABOUT ->
            profile.about

        EmploymentEditField.EXPERIENCE ->
            profile.experience

        EmploymentEditField.EDUCATION ->
            profile.education

        EmploymentEditField.SKILLS ->
            profile.skills

        EmploymentEditField.CERTIFICATIONS ->
            profile.certifications

        EmploymentEditField.LANGUAGES ->
            profile.languages
    }

    var value by remember(
        field,
        profile
    ) {
        mutableStateOf(initialValue)
    }

    val title = when (field) {

        EmploymentEditField.ABOUT ->
            if (isTamil) "என்னைப் பற்றி" else "About"

        EmploymentEditField.EXPERIENCE ->
            if (isTamil) "பணி அனுபவம்" else "Experience"

        EmploymentEditField.EDUCATION ->
            if (isTamil) "கல்வி" else "Education"

        EmploymentEditField.SKILLS ->
            if (isTamil) "திறன்கள்" else "Skills"

        EmploymentEditField.CERTIFICATIONS ->
            if (isTamil) "சான்றிதழ்கள்" else "Certifications"

        EmploymentEditField.LANGUAGES ->
            if (isTamil) "மொழிகள்" else "Languages"
    }

    val placeholder = when (field) {

        EmploymentEditField.ABOUT ->
            if (isTamil)
                "உங்கள் தொழில் அனுபவம் மற்றும் குறிக்கோள்"
            else
                "Tell employers about yourself"

        EmploymentEditField.EXPERIENCE ->
            if (isTamil)
                "நிறுவனம், பதவி, அனுபவம்..."
            else
                "Company, role, responsibilities, experience..."

        EmploymentEditField.EDUCATION ->
            if (isTamil)
                "பட்டம், கல்லூரி, ஆண்டு..."
            else
                "Degree, college, year..."

        EmploymentEditField.SKILLS ->
            if (isTamil)
                "எ.கா. Java, Farming, Sales"
            else
                "Example: Java, Farming, Sales"

        EmploymentEditField.CERTIFICATIONS ->
            if (isTamil)
                "பயிற்சி மற்றும் சான்றிதழ்கள்"
            else
                "Training and professional certifications"

        EmploymentEditField.LANGUAGES ->
            if (isTamil)
                "எ.கா. தமிழ், English"
            else
                "Example: Tamil, English"
    }

    val isLongText =
        field == EmploymentEditField.ABOUT ||
                field == EmploymentEditField.EXPERIENCE ||
                field == EmploymentEditField.CERTIFICATIONS

    AlertDialog(

        onDismissRequest =
            onDismiss,

        title = {

            Text(
                text =
                    title,

                fontWeight =
                    FontWeight.Bold
            )
        },

        text = {

            OutlinedTextField(
                value =
                    value,

                onValueChange = {
                    value = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                placeholder = {
                    Text(
                        text =
                            placeholder
                    )
                },

                minLines =
                    if (isLongText) 5 else 3,

                maxLines =
                    if (isLongText) 8 else 5,

                shape =
                    RoundedCornerShape(16.dp),

                supportingText = {

                    Text(
                        text =
                            if (isTamil)
                                "தகவலை சரியாக உள்ளிடவும்."
                            else
                                "Keep this information clear and professional."
                    )
                }
            )
        },

        confirmButton = {

            Button(
                onClick = {

                    val updatedProfile =
                        when (field) {

                            EmploymentEditField.ABOUT ->
                                profile.copy(
                                    about =
                                        value.trim()
                                )

                            EmploymentEditField.EXPERIENCE ->
                                profile.copy(
                                    experience =
                                        value.trim()
                                )

                            EmploymentEditField.EDUCATION ->
                                profile.copy(
                                    education =
                                        value.trim()
                                )

                            EmploymentEditField.SKILLS ->
                                profile.copy(
                                    skills =
                                        value.trim()
                                )

                            EmploymentEditField.CERTIFICATIONS ->
                                profile.copy(
                                    certifications =
                                        value.trim()
                                )

                            EmploymentEditField.LANGUAGES ->
                                profile.copy(
                                    languages =
                                        value.trim()
                                )
                        }

                    onSave(updatedProfile)
                },

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            EmploymentGreen
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Save,

                    contentDescription =
                        null
                )

                Spacer(
                    modifier =
                        Modifier.width(6.dp)
                )

                Text(
                    text =
                        if (isTamil)
                            "சேமிக்கவும்"
                        else
                            "Save"
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick =
                    onDismiss
            ) {

                Text(
                    text =
                        if (isTamil)
                            "ரத்து"
                        else
                            "Cancel"
                )
            }
        }
    )
}

/*
 * ============================================================
 * PROFESSIONAL HEADLINE
 * ============================================================
 */

private fun buildProfessionalHeadline(
    profile: FarmerProfile,
    isTamil: Boolean
): String {

    if (profile.skills.isNotBlank()) {

        val firstSkill =
            profile.skills
                .split(
                    ",",
                    "•",
                    "\n"
                )
                .firstOrNull {
                    it.isNotBlank()
                }
                ?.trim()

        if (!firstSkill.isNullOrBlank()) {

            return if (isTamil) {
                "$firstSkill | தொழில் நிபுணர்"
            } else {
                "$firstSkill | Professional"
            }
        }
    }

    if (profile.education.isNotBlank()) {

        return if (isTamil) {
            "${profile.education} | தொழில் வாய்ப்புகளை தேடுபவர்"
        } else {
            "${profile.education} | Career Professional"
        }
    }

    return if (isTamil) {
        "வேலை தேடும் தொழில் நிபுணர்"
    } else {
        "Professional looking for opportunities"
    }
}