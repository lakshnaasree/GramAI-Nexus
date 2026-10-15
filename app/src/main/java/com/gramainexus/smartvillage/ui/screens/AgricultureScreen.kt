package com.gramainexus.smartvillage.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.gramainexus.smartvillage.viewmodel.PlantDoctorViewModel

private val AgricultureGreen = Color(0xFF2E7D32)
private val AgricultureDarkGreen = Color(0xFF1B5E20)
private val AgricultureLightGreen = Color(0xFFE8F5E9)
private val AgricultureBackground = Color(0xFFF7FBF7)

private val DoctorBlue = Color(0xFF1565C0)
private val DoctorLightBlue = Color(0xFFE3F2FD)

private val DangerRed = Color(0xFFD32F2F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgricultureScreen(
    selectedLanguage: String,
    villageName: String,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    // =====================================================
    // PLANT DOCTOR VIEWMODEL
    // =====================================================

    val plantDoctorViewModel: PlantDoctorViewModel = viewModel()

    val diagnosis by plantDoctorViewModel.diagnosis.collectAsState()
    val isAnalyzing by plantDoctorViewModel.isAnalyzing.collectAsState()
    val errorMessage by plantDoctorViewModel.errorMessage.collectAsState()

    val isTamil = selectedLanguage == "Tamil"

    // =====================================================
    // CROP ADVISOR STATE
    // =====================================================

    var soilType by remember {
        mutableStateOf("")
    }

    var waterAvailability by remember {
        mutableStateOf("")
    }

    var season by remember {
        mutableStateOf("")
    }

    var temperature by remember {
        mutableStateOf("")
    }

    var rainfall by remember {
        mutableStateOf("")
    }

    var landArea by remember {
        mutableStateOf("")
    }

    var previousCrop by remember {
        mutableStateOf("")
    }

    var recommendationShown by remember {
        mutableStateOf(false)
    }

    // =====================================================
    // PLANT DOCTOR STATE
    // =====================================================

    var plantImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var plantImageBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    // =====================================================
    // GALLERY
    // =====================================================

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                plantImageUri = uri
                plantDoctorViewModel.clearDiagnosis()

                try {

                    context.contentResolver
                        .openInputStream(uri)
                        ?.use { inputStream ->

                            plantImageBitmap =
                                BitmapFactory.decodeStream(inputStream)
                        }

                } catch (e: Exception) {

                    plantImageBitmap = null
                }
            }
        }

    // =====================================================
    // CAMERA
    // =====================================================

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->

            if (bitmap != null) {

                plantImageBitmap = bitmap
                plantImageUri = null

                plantDoctorViewModel.clearDiagnosis()
            }
        }

    // =====================================================
    // MAIN SCREEN
    // =====================================================

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AgricultureBackground),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // =================================================
        // HEADER
        // =================================================

        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AgricultureDarkGreen)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 42.dp,
                        bottom = 22.dp
                    )
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Column {

                        Text(
                            text = if (isTamil)
                                "விவசாயம்"
                            else
                                "Agriculture",
                            color = Color.White,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (isTamil)
                                "ஸ்மார்ட் விவசாய உதவியாளர்"
                            else
                                "Smart Farming Assistant",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // =================================================
        // VILLAGE CARD
        // =================================================

        item {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AgricultureLightGreen
                ),
                shape = RoundedCornerShape(18.dp)
            ) {

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = AgricultureGreen,
                        modifier = Modifier.size(30.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Column {

                        Text(
                            text = if (isTamil)
                                "உங்கள் கிராமம்"
                            else
                                "Your Village",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )

                        Text(
                            text = villageName.ifBlank {

                                if (isTamil)
                                    "கிராமம் தேர்ந்தெடுக்கப்படவில்லை"
                                else
                                    "Village not selected"
                            },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgricultureDarkGreen
                        )
                    }
                }
            }
        }

        // =================================================
        // CROP ADVISOR TITLE
        // =================================================

        item {

            Text(
                text = if (isTamil)
                    "🌾 பயிர் ஆலோசகர்"
                else
                    "🌾 Crop Advisor",
                modifier = Modifier.padding(
                    start = 18.dp,
                    top = 6.dp
                ),
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = AgricultureDarkGreen
            )
        }

        // =================================================
        // SOIL
        // =================================================

        item {

            AgricultureDropdown(
                label = if (isTamil)
                    "மண் வகை"
                else
                    "Soil Type",
                value = soilType,
                options = listOf(
                    "Red Soil",
                    "Black Soil",
                    "Sandy Soil",
                    "Clay Soil",
                    "Loamy Soil"
                ),
                onValueChange = {
                    soilType = it
                    recommendationShown = false
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // =================================================
        // WATER
        // =================================================

        item {

            AgricultureDropdown(
                label = if (isTamil)
                    "நீர் கிடைக்கும் அளவு"
                else
                    "Water Availability",
                value = waterAvailability,
                options = listOf(
                    "Low",
                    "Medium",
                    "High"
                ),
                onValueChange = {
                    waterAvailability = it
                    recommendationShown = false
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // =================================================
        // SEASON
        // =================================================

        item {

            AgricultureDropdown(
                label = if (isTamil)
                    "பருவம்"
                else
                    "Season",
                value = season,
                options = listOf(
                    "Kharif",
                    "Rabi",
                    "Summer"
                ),
                onValueChange = {
                    season = it
                    recommendationShown = false
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // =================================================
        // TEMPERATURE
        // =================================================

        item {

            OutlinedTextField(
                value = temperature,
                onValueChange = {
                    temperature = it
                    recommendationShown = false
                },
                label = {
                    Text(
                        if (isTamil)
                            "வெப்பநிலை (°C)"
                        else
                            "Temperature (°C)"
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true
            )
        }

        // =================================================
        // RAINFALL
        // =================================================

        item {

            OutlinedTextField(
                value = rainfall,
                onValueChange = {
                    rainfall = it
                    recommendationShown = false
                },
                label = {
                    Text(
                        if (isTamil)
                            "மழைப்பொழிவு (mm)"
                        else
                            "Rainfall (mm)"
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true
            )
        }

        // =================================================
        // LAND AREA
        // =================================================

        item {

            OutlinedTextField(
                value = landArea,
                onValueChange = {
                    landArea = it
                    recommendationShown = false
                },
                label = {
                    Text(
                        if (isTamil)
                            "நிலப்பரப்பு (ஏக்கர்)"
                        else
                            "Land Area (acres)"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true
            )
        }

        // =================================================
        // PREVIOUS CROP
        // =================================================

        item {

            AgricultureDropdown(
                label = if (isTamil)
                    "முந்தைய பயிர்"
                else
                    "Previous Crop",
                value = previousCrop,
                options = listOf(
                    "Paddy",
                    "Groundnut",
                    "Cotton",
                    "Sugarcane",
                    "Maize",
                    "Vegetables",
                    "None"
                ),
                onValueChange = {
                    previousCrop = it
                    recommendationShown = false
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // =================================================
        // FIND CROP
        // =================================================

        item {

            Button(
                onClick = {
                    recommendationShown = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AgricultureGreen
                ),
                shape = RoundedCornerShape(14.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Agriculture,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = if (isTamil)
                        "சிறந்த பயிரை கண்டறியவும்"
                    else
                        "Find Suitable Crop",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // =================================================
        // CROP RESULT
        // =================================================

        if (recommendationShown) {

            item {

                CropRecommendationCard(
                    isTamil = isTamil,
                    soilType = soilType,
                    waterAvailability = waterAvailability,
                    season = season,
                    temperature = temperature,
                    rainfall = rainfall,
                    landArea = landArea,
                    previousCrop = previousCrop
                )
            }
        }

        // =================================================
        // PLANT DOCTOR TITLE
        // =================================================

        item {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = if (isTamil)
                    "🩺 தாவர மருத்துவர்"
                else
                    "🩺 Plant Doctor",
                modifier = Modifier.padding(horizontal = 18.dp),
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = AgricultureDarkGreen
            )
        }

        // =================================================
        // PLANT DOCTOR CARD
        // =================================================

        item {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = if (isTamil)
                            "தாவரத்தின் புகைப்படத்தை பதிவேற்றவும்"
                        else
                            "Upload a photo of your plant",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    // =====================================
                    // IMAGE PREVIEW
                    // =====================================

                    if (
                        plantImageUri != null ||
                        plantImageBitmap != null
                    ) {

                        if (plantImageUri != null) {

                            AsyncImage(
                                model = plantImageUri,
                                contentDescription = "Plant Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .clip(
                                        RoundedCornerShape(16.dp)
                                    ),
                                contentScale = ContentScale.Crop
                            )

                        } else if (plantImageBitmap != null) {

                            Image(
                                bitmap = plantImageBitmap!!.asImageBitmap(),
                                contentDescription = "Plant Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .clip(
                                        RoundedCornerShape(16.dp)
                                    ),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        // =================================
                        // REMOVE PHOTO
                        // =================================

                        Button(
                            onClick = {

                                plantImageUri = null
                                plantImageBitmap = null

                                plantDoctorViewModel.clearDiagnosis()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DangerRed
                            )
                        ) {

                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text(
                                text = if (isTamil)
                                    "புகைப்படத்தை நீக்கவும்"
                                else
                                    "Remove Photo"
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        // =================================
                        // ANALYZE PLANT
                        // =================================

                        Button(
                            onClick = {

                                plantImageBitmap?.let { bitmap ->

                                    plantDoctorViewModel.analyzePlant(
                                        bitmap
                                    )
                                }
                            },
                            enabled = !isAnalyzing &&
                                    plantImageBitmap != null,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DoctorBlue
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {

                            if (isAnalyzing) {

                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Text(
                                    text = if (isTamil)
                                        "ஆய்வு செய்யப்படுகிறது..."
                                    else
                                        "Analyzing..."
                                )

                            } else {

                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null
                                )

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Text(
                                    text = if (isTamil)
                                        "தாவரத்தை ஆய்வு செய்யவும்"
                                    else
                                        "Analyze Plant",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                    } else {

                        // =================================
                        // CAMERA + GALLERY
                        // =================================

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {

                            Button(
                                onClick = {
                                    cameraLauncher.launch(null)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AgricultureGreen
                                )
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.CameraAlt,
                                    contentDescription = null
                                )

                                Spacer(
                                    modifier = Modifier.width(5.dp)
                                )

                                Text(
                                    text = if (isTamil)
                                        "கேமரா"
                                    else
                                        "Camera"
                                )
                            }

                            Button(
                                onClick = {
                                    galleryLauncher.launch("image/*")
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor =
                                        AgricultureDarkGreen
                                )
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Image,
                                    contentDescription = null
                                )

                                Spacer(
                                    modifier = Modifier.width(5.dp)
                                )

                                Text(
                                    text = if (isTamil)
                                        "கேலரி"
                                    else
                                        "Gallery"
                                )
                            }
                        }
                    }
                }
            }
        }

        // =================================================
        // ERROR RESULT
        // =================================================

        if (errorMessage != null) {

            item {

                PlantDoctorError(
                    isTamil = isTamil,
                    message = errorMessage ?: ""
                )
            }
        }

        // =================================================
        // AI RESULT
        // =================================================

        if (diagnosis != null) {

            item {

                PlantDoctorResult(
                    isTamil = isTamil,
                    diagnosis = diagnosis!!
                )
            }
        }

        item {

            Spacer(
                modifier = Modifier.height(30.dp)
            )
        }
    }
}


// =========================================================
// AGRICULTURE DROPDOWN
// =========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgricultureDropdown(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = modifier
    ) {

        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(label)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {

            options.forEach { option ->

                DropdownMenuItem(
                    text = {
                        Text(option)
                    },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


// =========================================================
// CROP RECOMMENDATION
// =========================================================

@Composable
private fun CropRecommendationCard(
    isTamil: Boolean,
    soilType: String,
    waterAvailability: String,
    season: String,
    temperature: String,
    rainfall: String,
    landArea: String,
    previousCrop: String
) {

    val recommendedCrop =
        when {

            soilType == "Black Soil" &&
                    waterAvailability == "Low" ->
                "Groundnut"

            soilType == "Clay Soil" &&
                    waterAvailability == "High" ->
                "Paddy"

            season == "Summer" &&
                    waterAvailability != "Low" ->
                "Vegetables"

            soilType == "Red Soil" ->
                "Groundnut"

            previousCrop == "Paddy" &&
                    waterAvailability == "Low" ->
                "Maize"

            soilType == "Sandy Soil" ->
                "Groundnut"

            soilType == "Loamy Soil" &&
                    waterAvailability == "High" ->
                "Maize"

            else ->
                "Maize"
        }

    val waterRequirement =
        when (recommendedCrop) {

            "Paddy" ->
                "High"

            "Groundnut" ->
                "Low – Medium"

            "Vegetables" ->
                "Medium"

            else ->
                "Medium"
        }

    val growingPeriod =
        when (recommendedCrop) {

            "Paddy" ->
                "110–140 days"

            "Groundnut" ->
                "100–120 days"

            "Vegetables" ->
                "60–100 days"

            else ->
                "90–120 days"
        }

    val advice =
        when (recommendedCrop) {

            "Paddy" ->

                if (isTamil)
                    "நீர் தேங்காமல் கட்டுப்படுத்தப்பட்ட பாசனத்தைப் பயன்படுத்தவும்."
                else
                    "Use controlled irrigation and avoid unnecessary water stagnation."

            "Groundnut" ->

                if (isTamil)
                    "நன்கு வடிகால் வசதியுள்ள மண்ணைப் பயன்படுத்தவும்."
                else
                    "Use well-drained soil and avoid excessive irrigation."

            "Vegetables" ->

                if (isTamil)
                    "தொடர்ச்சியான நீர்ப்பாசனம் மற்றும் பூச்சி கண்காணிப்பு அவசியம்."
                else
                    "Maintain regular irrigation and monitor crops for pests."

            else ->

                if (isTamil)
                    "சரியான நீர்ப்பாசனம் மற்றும் மண் ஈரப்பதத்தை கண்காணிக்கவும்."
                else
                    "Maintain proper irrigation and monitor soil moisture."
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = AgricultureLightGreen
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = if (isTamil)
                    "🌱 பரிந்துரைக்கப்பட்ட பயிர்"
                else
                    "🌱 Recommended Crop",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AgricultureDarkGreen
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = recommendedCrop,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = AgricultureGreen
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            RecommendationRow(
                label = if (isTamil)
                    "💧 நீர் தேவை"
                else
                    "💧 Water Requirement",
                value = waterRequirement
            )

            RecommendationRow(
                label = if (isTamil)
                    "⏱ வளர்ச்சி காலம்"
                else
                    "⏱ Growing Period",
                value = growingPeriod
            )

            if (landArea.isNotBlank()) {

                RecommendationRow(
                    label = if (isTamil)
                        "📐 நிலப்பரப்பு"
                    else
                        "📐 Land Area",
                    value = "$landArea acres"
                )
            }

            if (temperature.isNotBlank()) {

                RecommendationRow(
                    label = if (isTamil)
                        "🌡 வெப்பநிலை"
                    else
                        "🌡 Temperature",
                    value = "$temperature °C"
                )
            }

            if (rainfall.isNotBlank()) {

                RecommendationRow(
                    label = if (isTamil)
                        "🌧 மழைப்பொழிவு"
                    else
                        "🌧 Rainfall",
                    value = "$rainfall mm"
                )
            }

            if (previousCrop.isNotBlank()) {

                RecommendationRow(
                    label = if (isTamil)
                        "🌾 முந்தைய பயிர்"
                    else
                        "🌾 Previous Crop",
                    value = previousCrop
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = if (isTamil)
                    "💡 விவசாய ஆலோசனை"
                else
                    "💡 Farming Advice",
                fontWeight = FontWeight.Bold,
                color = AgricultureDarkGreen
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = advice,
                fontSize = 14.sp
            )
        }
    }
}


// =========================================================
// RECOMMENDATION ROW
// =========================================================

@Composable
private fun RecommendationRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.DarkGray
        )

        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AgricultureDarkGreen
        )
    }
}


// =========================================================
// PLANT DOCTOR RESULT
// =========================================================

@Composable
private fun PlantDoctorResult(
    isTamil: Boolean,
    diagnosis: com.gramainexus.smartvillage.data.PlantDiagnosis
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = DoctorLightBlue
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = if (isTamil)
                    "🩺 தாவர மருத்துவர் முடிவு"
                else
                    "🩺 Plant Doctor Result",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = DoctorBlue
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            if (diagnosis.plantName.isNotBlank()) {

                Text(
                    text = if (isTamil)
                        "🌿 தாவரம்: ${diagnosis.plantName}"
                    else
                        "🌿 Plant: ${diagnosis.plantName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }

            if (diagnosis.status.isNotBlank()) {

                Text(
                    text = if (isTamil)
                        "📋 நிலை: ${diagnosis.status}"
                    else
                        "📋 Status: ${diagnosis.status}",
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }

            if (diagnosis.diseaseName.isNotBlank()) {

                Text(
                    text = if (isTamil)
                        "🔍 நோய்: ${diagnosis.diseaseName}"
                    else
                        "🔍 Disease: ${diagnosis.diseaseName}",
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }

            if (diagnosis.confidence > 0f) {

                Text(
                    text = if (isTamil)
                        "🎯 நம்பகத்தன்மை: ${
                            String.format(
                                "%.1f",
                                diagnosis.confidence
                            )
                        }%"
                    else
                        "🎯 Confidence: ${
                            String.format(
                                "%.1f",
                                diagnosis.confidence
                            )
                        }%",
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            if (diagnosis.symptoms.isNotEmpty()) {

                Text(
                    text = if (isTamil)
                        "⚠️ அறிகுறிகள்"
                    else
                        "⚠️ Symptoms",
                    fontWeight = FontWeight.Bold,
                    color = DoctorBlue
                )

                diagnosis.symptoms.forEach { symptom ->

                    Text(
                        text = "• $symptom",
                        modifier = Modifier.padding(top = 4.dp),
                        fontSize = 14.sp
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            if (diagnosis.treatment.isNotEmpty()) {

                Text(
                    text = if (isTamil)
                        "💊 சிகிச்சை / பராமரிப்பு"
                    else
                        "💊 Treatment / Care",
                    fontWeight = FontWeight.Bold,
                    color = DoctorBlue
                )

                diagnosis.treatment.forEach { treatment ->

                    Text(
                        text = "• $treatment",
                        modifier = Modifier.padding(top = 4.dp),
                        fontSize = 14.sp
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            if (diagnosis.prevention.isNotEmpty()) {

                Text(
                    text = if (isTamil)
                        "🛡️ தடுப்பு குறிப்புகள்"
                    else
                        "🛡️ Prevention Tips",
                    fontWeight = FontWeight.Bold,
                    color = DoctorBlue
                )

                diagnosis.prevention.forEach { prevention ->

                    Text(
                        text = "• $prevention",
                        modifier = Modifier.padding(top = 4.dp),
                        fontSize = 14.sp
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            if (diagnosis.advice.isNotBlank()) {

                Text(
                    text = if (isTamil)
                        "💡 ஆலோசனை"
                    else
                        "💡 Advice",
                    fontWeight = FontWeight.Bold,
                    color = DoctorBlue
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = diagnosis.advice,
                    fontSize = 14.sp
                )
            }
        }
    }
}


// =========================================================
// PLANT DOCTOR ERROR
// =========================================================

@Composable
private fun PlantDoctorError(
    isTamil: Boolean,
    message: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = if (isTamil)
                    "⚠️ பிழை"
                else
                    "⚠️ Error",
                fontWeight = FontWeight.Bold,
                color = DangerRed
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = message,
                fontSize = 14.sp
            )
        }
    }
}