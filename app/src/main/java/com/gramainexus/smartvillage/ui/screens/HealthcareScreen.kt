package com.gramainexus.smartvillage.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.widget.Toast

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

import com.gramainexus.smartvillage.data.HealthcareFacility
import com.gramainexus.smartvillage.data.HealthcarePreferences

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.util.Locale

private val HealthcareDarkGreen = Color(0xFF124C2E)
private val HealthcareGreen = Color(0xFF2E7D32)
private val HealthcareLightGreen = Color(0xFFE8F5E9)
private val HealthcareBackground = Color(0xFFF4F8F5)

private val EmergencyRed = Color(0xFFD32F2F)
private val EmergencyLightRed = Color(0xFFFFEBEE)

private val AmbulanceGreen = Color(0xFF2E7D32)
private val AmbulanceLightGreen = Color(0xFFE8F5E9)

private val PoliceBlue = Color(0xFF1976D2)
private val PoliceLightBlue = Color(0xFFE3F2FD)

private val PharmacyPurple = Color(0xFF7B1FA2)

private val MedicineWarningBackground = Color(0xFFFFF8E1)
private val MedicineWarningText = Color(0xFF6D4C41)

private enum class HealthcareFilter {
    ALL,
    HOSPITAL,
    CLINIC,
    DOCTOR,
    PHARMACY
}

/* =========================================================
   MAIN HEALTHCARE SCREEN
   ========================================================= */

@Composable
fun HealthcareScreen(
    selectedLanguage: String,
    villageName: String,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val healthcarePreferences = remember {
        HealthcarePreferences()
    }

    val isTamil =
        selectedLanguage.equals(
            "Tamil",
            ignoreCase = true
        )

    var facilities by remember {
        mutableStateOf<List<HealthcareFacility>>(
            emptyList()
        )
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var selectedFilter by remember {
        mutableStateOf(
            HealthcareFilter.ALL
        )
    }

    var refreshKey by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(
        villageName,
        refreshKey
    ) {

        isLoading = true
        errorMessage = ""

        try {

            val villageCoordinates =
                withContext(Dispatchers.IO) {

                    try {

                        @Suppress("DEPRECATION")
                        val geocoder =
                            Geocoder(
                                context,
                                Locale.getDefault()
                            )

                        @Suppress("DEPRECATION")
                        val addresses =
                            geocoder.getFromLocationName(
                                "$villageName, Tamil Nadu, India",
                                1
                            )

                        addresses
                            ?.firstOrNull()
                            ?.let { address ->
                                Pair(
                                    address.latitude,
                                    address.longitude
                                )
                            }

                    } catch (e: Exception) {

                        e.printStackTrace()

                        null
                    }
                }

            if (villageCoordinates == null) {

                facilities = emptyList()

                errorMessage =
                    if (isTamil) {
                        "கிராமத்தின் இருப்பிடத்தை கண்டறிய முடியவில்லை."
                    } else {
                        "Unable to find the village location."
                    }

                return@LaunchedEffect
            }

            facilities =
                healthcarePreferences
                    .getNearbyHealthcareFacilities(
                        latitude =
                            villageCoordinates.first,

                        longitude =
                            villageCoordinates.second,

                        radiusMeters = 10_000
                    )

        } catch (e: Exception) {

            e.printStackTrace()

            facilities = emptyList()

            errorMessage =
                if (isTamil) {
                    "சுகாதார சேவைகளை ஏற்ற முடியவில்லை."
                } else {
                    "Unable to load healthcare services."
                }

        } finally {

            isLoading = false
        }
    }

    val filteredFacilities =
        when (selectedFilter) {

            HealthcareFilter.ALL ->
                facilities

            HealthcareFilter.HOSPITAL ->
                facilities.filter {
                    it.type.equals(
                        "Hospital",
                        ignoreCase = true
                    )
                }

            HealthcareFilter.CLINIC ->
                facilities.filter {
                    it.type.equals(
                        "Clinic",
                        ignoreCase = true
                    )
                }

            HealthcareFilter.DOCTOR ->
                facilities.filter {
                    it.type.equals(
                        "Doctor",
                        ignoreCase = true
                    )
                }

            HealthcareFilter.PHARMACY ->
                facilities.filter {
                    it.type.equals(
                        "Pharmacy",
                        ignoreCase = true
                    )
                }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                HealthcareBackground
            )
    ) {

        HealthcareHeader(
            isTamil = isTamil,
            onBack = onBack,
            onRefresh = {
                refreshKey++
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),

            verticalArrangement =
                Arrangement.spacedBy(12.dp),

            contentPadding =
                PaddingValues(
                    top = 16.dp,
                    bottom = 28.dp
                )
        ) {

            /* INTRO */

            item {

                HealthcareIntroCard(
                    villageName = villageName,
                    isTamil = isTamil
                )
            }

            /* EMERGENCY */

            item {

                HealthcareEmergencySection(
                    isTamil = isTamil
                )
            }

            /* MEDICINE SCANNER */

            item {

                MedicineScannerCard(
                    isTamil = isTamil
                )
            }

            /* FILTERS */

            item {

                HealthcareFilterSection(
                    selectedFilter =
                        selectedFilter,

                    onFilterSelected = {
                        selectedFilter = it
                    },

                    isTamil = isTamil
                )
            }

            /* LOADING */

            if (isLoading) {

                item {

                    LoadingHealthcareCard(
                        isTamil = isTamil
                    )
                }
            }

            /* ERROR */

            if (
                !isLoading &&
                errorMessage.isNotBlank()
            ) {

                item {

                    HealthcareErrorCard(
                        message =
                            errorMessage,

                        isTamil =
                            isTamil,

                        onRetry = {
                            refreshKey++
                        }
                    )
                }
            }

            /* EMPTY */

            if (
                !isLoading &&
                errorMessage.isBlank() &&
                filteredFacilities.isEmpty()
            ) {

                item {

                    EmptyHealthcareCard(
                        isTamil = isTamil
                    )
                }
            }

            /* RESULTS */

            if (
                !isLoading &&
                filteredFacilities.isNotEmpty()
            ) {

                item {

                    ResultsHeader(
                        count =
                            filteredFacilities.size,

                        isTamil =
                            isTamil
                    )
                }

                items(
                    items = filteredFacilities,
                    key = {
                        it.id
                    }
                ) { facility ->

                    HealthcareFacilityCard(
                        facility = facility,
                        isTamil = isTamil
                    )
                }
            }
        }
    }
}

/* =========================================================
   HEADER
   ========================================================= */

@Composable
private fun HealthcareHeader(
    isTamil: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),

        color =
            HealthcareDarkGreen
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 12.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ArrowBack,

                    contentDescription =
                        if (isTamil)
                            "பின்செல்"
                        else
                            "Back",

                    tint =
                        Color.White
                )
            }

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        if (isTamil)
                            "சுகாதாரம்"
                        else
                            "Healthcare",

                    color =
                        Color.White,

                    fontSize =
                        21.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        if (isTamil)
                            "அருகிலுள்ள மருத்துவ சேவைகள்"
                        else
                            "Nearby healthcare services",

                    color =
                        Color.White.copy(
                            alpha = 0.78f
                        ),

                    fontSize =
                        12.sp
                )
            }

            IconButton(
                onClick = onRefresh
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Refresh,

                    contentDescription =
                        if (isTamil)
                            "புதுப்பிக்கவும்"
                        else
                            "Refresh",

                    tint =
                        Color.White
                )
            }
        }
    }
}

/* =========================================================
   INTRO
   ========================================================= */

@Composable
private fun HealthcareIntroCard(
    villageName: String,
    isTamil: Boolean
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp
            ),

        shape =
            RoundedCornerShape(22.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    HealthcareDarkGreen
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(
                            alpha = 0.14f
                        )
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.MedicalServices,

                    contentDescription = null,

                    tint =
                        Color.White,

                    modifier =
                        Modifier.size(29.dp)
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
                    text =
                        if (isTamil)
                            "சுகாதார சேவைகள்"
                        else
                            "Healthcare Services",

                    color =
                        Color.White,

                    fontSize =
                        21.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        if (isTamil)
                            "$villageName சுற்றி 10 km"
                        else
                            "Healthcare facilities within 10 km of $villageName",

                    color =
                        Color.White.copy(
                            alpha = 0.82f
                        ),

                    fontSize =
                        12.sp,

                    maxLines = 2,

                    overflow =
                        TextOverflow.Ellipsis
                )
            }
        }
    }
}

/* =========================================================
   EMERGENCY
   ========================================================= */

@Composable
private fun HealthcareEmergencySection(
    isTamil: Boolean
) {

    val context =
        LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp
            )
    ) {

        Text(
            text =
                if (isTamil)
                    "அவசர உதவி"
                else
                    "Emergency Help",

            fontSize =
                18.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                HealthcareDarkGreen
        )

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        Text(
            text =
                if (isTamil)
                    "அவசர நேரத்தில் விரைவாக அழைக்கவும்"
                else
                    "Quick access to emergency services",

            fontSize =
                12.sp,

            color =
                Color.Gray
        )

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            EmergencyMiniCard(
                modifier =
                    Modifier.weight(1f),

                icon =
                    Icons.Default.Warning,

                title =
                    "112",

                subtitle =
                    if (isTamil)
                        "அவசரம்"
                    else
                        "Emergency",

                background =
                    EmergencyLightRed,

                iconColor =
                    EmergencyRed,

                onClick = {
                    callNumber(
                        context,
                        "112"
                    )
                }
            )

            EmergencyMiniCard(
                modifier =
                    Modifier.weight(1f),

                icon =
                    Icons.Default.LocalHospital,

                title =
                    "108",

                subtitle =
                    if (isTamil)
                        "ஆம்புலன்ஸ்"
                    else
                        "Ambulance",

                background =
                    AmbulanceLightGreen,

                iconColor =
                    AmbulanceGreen,

                onClick = {
                    callNumber(
                        context,
                        "108"
                    )
                }
            )

            EmergencyMiniCard(
                modifier =
                    Modifier.weight(1f),

                icon =
                    Icons.Default.LocalPolice,

                title =
                    "100",

                subtitle =
                    if (isTamil)
                        "காவல்துறை"
                    else
                        "Police",

                background =
                    PoliceLightBlue,

                iconColor =
                    PoliceBlue,

                onClick = {
                    callNumber(
                        context,
                        "100"
                    )
                }
            )
        }
    }
}

@Composable
private fun EmergencyMiniCard(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    background: Color,
    iconColor: Color,
    onClick: () -> Unit
) {

    Card(
        modifier =
            modifier.clickable {
                onClick()
            },

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    background
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector =
                    icon,

                contentDescription =
                    null,

                tint =
                    iconColor,

                modifier =
                    Modifier.size(24.dp)
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    title,

                fontSize =
                    16.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    iconColor
            )

            Text(
                text =
                    subtitle,

                fontSize =
                    10.sp,

                color =
                    Color.Gray
            )
        }
    }
}

/* =========================================================
   MEDICINE SCANNER
   ========================================================= */

@Composable
private fun MedicineScannerCard(
    isTamil: Boolean
) {

    val context =
        LocalContext.current

    var selectedBitmap by remember {
        mutableStateOf<android.graphics.Bitmap?>(null)
    }

    var selectedUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var detectedText by remember {
        mutableStateOf("")
    }

    var isScanning by remember {
        mutableStateOf(false)
    }

    var scanError by remember {
        mutableStateOf(false)
    }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri == null) {
                return@rememberLauncherForActivityResult
            }

            selectedUri = uri
            detectedText = ""
            scanError = false

            selectedBitmap =
                try {

                    context.contentResolver
                        .openInputStream(uri)
                        ?.use { stream ->
                            BitmapFactory.decodeStream(
                                stream
                            )
                        }

                } catch (e: Exception) {

                    e.printStackTrace()
                    null
                }

            if (selectedBitmap == null) {

                scanError = true

                return@rememberLauncherForActivityResult
            }

            scanMedicineImage(
                context = context,
                uri = uri,

                onLoading = {
                    isScanning = true
                },

                onSuccess = { text ->

                    isScanning = false
                    detectedText = text
                    scanError = false
                },

                onError = {

                    isScanning = false
                    scanError = true
                }
            )
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp
            ),

        shape =
            RoundedCornerShape(22.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            /* TITLE */

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            HealthcareLightGreen
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Medication,

                        contentDescription =
                            null,

                        tint =
                            HealthcareGreen,

                        modifier =
                            Modifier.size(27.dp)
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(12.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            if (isTamil)
                                "மருந்து / மாத்திரை தகவல்"
                            else
                                "Medicine / Tablet Info",

                        fontSize =
                            18.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            HealthcareDarkGreen
                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            if (isTamil)
                                "மருந்து அட்டை அல்லது லேபிள் படத்தை பதிவேற்றவும்"
                            else
                                "Upload a medicine strip, box or label image",

                        fontSize =
                            12.sp,

                        color =
                            Color.Gray
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            /* IMAGE */

            selectedBitmap?.let { bitmap ->

                Image(
                    bitmap =
                        bitmap.asImageBitmap(),

                    contentDescription =
                        if (isTamil)
                            "மருந்து படம்"
                        else
                            "Medicine image",

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )
            }

            /* UPLOAD BUTTON */

            Button(
                onClick = {

                    imagePickerLauncher.launch(
                        "image/*"
                    )
                },

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(14.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            HealthcareGreen
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.PhotoLibrary,

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(18.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(7.dp)
                )

                Text(
                    text =
                        if (isTamil)
                            "மருந்து படம் பதிவேற்றவும்"
                        else
                            "Upload Medicine Image"
                )
            }

            /* SCANNING */

            if (isScanning) {

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(22.dp),

                        color =
                            HealthcareGreen,

                        strokeWidth =
                            2.5.dp
                    )

                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )

                    Text(
                        text =
                            if (isTamil)
                                "படத்தில் உள்ள மருந்து தகவலை படிக்கிறது..."
                            else
                                "Reading medicine information from image...",

                        fontSize =
                            12.sp,

                        color =
                            Color.Gray
                    )
                }
            }

            /* OCR ERROR */

            if (
                scanError &&
                !isScanning
            ) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(14.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                EmergencyLightRed
                        )
                ) {

                    Row(
                        modifier =
                            Modifier.padding(12.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Warning,

                            contentDescription =
                                null,

                            tint =
                                EmergencyRed,

                            modifier =
                                Modifier.size(20.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                if (isTamil)
                                    "படத்தில் உள்ள எழுத்துகளை படிக்க முடியவில்லை. தெளிவான மருந்து அட்டை அல்லது லேபிள் படத்தை பதிவேற்றவும்."
                                else
                                    "Could not read the medicine text. Upload a clearer strip, box or label image.",

                            fontSize =
                                12.sp,

                            color =
                                Color(0xFF5D3030)
                        )
                    }
                }
            }

            /* DETECTED TEXT */

            if (
                detectedText.isNotBlank() &&
                !isScanning
            ) {

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(16.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color(0xFFF4F8F5)
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(14.dp)
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Info,

                                contentDescription =
                                    null,

                                tint =
                                    HealthcareGreen,

                                modifier =
                                    Modifier.size(19.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(7.dp)
                            )

                            Text(
                                text =
                                    if (isTamil)
                                        "கண்டறியப்பட்ட மருந்து தகவல்"
                                    else
                                        "Detected Medicine Information",

                                fontSize =
                                    14.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    HealthcareDarkGreen
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                detectedText,

                            fontSize =
                                13.sp,

                            color =
                                Color(0xFF333333)
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                MedicineDescriptionCard(
                    detectedText =
                        detectedText,

                    isTamil =
                        isTamil
                )
            }

            /* SAFETY WARNING */

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(14.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MedicineWarningBackground
                    )
            ) {

                Row(
                    modifier =
                        Modifier.padding(12.dp),

                    verticalAlignment =
                        Alignment.Top
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Warning,

                        contentDescription =
                            null,

                        tint =
                            Color(0xFFF57C00),

                        modifier =
                            Modifier.size(20.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text =
                            if (isTamil)
                                "மாத்திரையின் நிறம் அல்லது வடிவத்தை மட்டும் வைத்து மருந்தை உறுதியாக அடையாளம் காண முடியாது. மருந்தின் பெயர், strength மற்றும் லேபிளை சரிபார்க்கவும். சந்தேகம் இருந்தால் மருத்துவர் அல்லது மருந்தாளரை அணுகவும்."
                            else
                                "A tablet cannot be reliably identified from its shape or colour alone. Verify the medicine name, strength and label. Ask a doctor or pharmacist if you are unsure.",

                        fontSize =
                            11.sp,

                        color =
                            MedicineWarningText
                    )
                }
            }
        }
    }
}

/* =========================================================
   OCR
   ========================================================= */

private fun scanMedicineImage(
    context: Context,
    uri: Uri,
    onLoading: () -> Unit,
    onSuccess: (String) -> Unit,
    onError: () -> Unit
) {

    onLoading()

    try {

        val image =
            InputImage.fromFilePath(
                context,
                uri
            )

        val recognizer =
            TextRecognition.getClient(
                TextRecognizerOptions.DEFAULT_OPTIONS
            )

        recognizer
            .process(image)
            .addOnSuccessListener { result ->

                val text =
                    result.text
                        .trim()

                recognizer.close()

                if (text.isBlank()) {
                    onError()
                } else {
                    onSuccess(text)
                }
            }
            .addOnFailureListener { exception ->

                exception.printStackTrace()

                recognizer.close()

                onError()
            }

    } catch (e: Exception) {

        e.printStackTrace()

        onError()
    }
}

/* =========================================================
   MEDICINE DESCRIPTION
   ========================================================= */

@Composable
private fun MedicineDescriptionCard(
    detectedText: String,
    isTamil: Boolean
) {

    val description =
        generateMedicineDescription(
            detectedText =
                detectedText,

            isTamil =
                isTamil
        )

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    HealthcareLightGreen
            )
    ) {

        Column(
            modifier =
                Modifier.padding(14.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.MedicalServices,

                    contentDescription =
                        null,

                    tint =
                        HealthcareGreen,

                    modifier =
                        Modifier.size(19.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(7.dp)
                )

                Text(
                    text =
                        if (isTamil)
                            "மருந்து விளக்கம்"
                        else
                            "Medicine Description",

                    fontSize =
                        14.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        HealthcareDarkGreen
                )
            }

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    description,

                fontSize =
                    12.sp,

                color =
                    Color(0xFF333333)
            )
        }
    }
}

private fun generateMedicineDescription(
    detectedText: String,
    isTamil: Boolean
): String {

    val text =
        detectedText.lowercase(
            Locale.getDefault()
        )

    return when {

        text.contains("paracetamol") ||
                text.contains("acetaminophen") -> {

            if (isTamil) {
                "Paracetamol பொதுவாக காய்ச்சல் மற்றும் லேசான முதல் மிதமான வலியை குறைக்க பயன்படுத்தப்படுகிறது. மருந்தின் strength மற்றும் label-ல் கொடுக்கப்பட்ட வழிமுறைகளை சரிபார்க்கவும்."
            } else {
                "Paracetamol is commonly used to reduce fever and relieve mild to moderate pain. Check the medicine strength and follow the prescribed or labelled instructions."
            }
        }

        text.contains("cetirizine") -> {

            if (isTamil) {
                "Cetirizine பொதுவாக allergy அறிகுறிகளான தும்மல், மூக்கு ஒழுகுதல் மற்றும் அரிப்பு போன்றவற்றிற்கு பயன்படுத்தப்படும் antihistamine மருந்தாகும். சிலருக்கு தூக்கத்தை ஏற்படுத்தலாம்."
            } else {
                "Cetirizine is an antihistamine commonly used for allergy symptoms such as sneezing, runny nose and itching. It can cause drowsiness in some people."
            }
        }

        text.contains("azithromycin") -> {

            if (isTamil) {
                "Azithromycin சில bacterial infections-க்கு பயன்படுத்தப்படும் antibiotic. மருத்துவர் பரிந்துரைத்தால் மட்டுமே பயன்படுத்த வேண்டும்."
            } else {
                "Azithromycin is an antibiotic used for certain bacterial infections. It should be used only according to a healthcare professional's prescription."
            }
        }

        text.contains("amoxicillin") -> {

            if (isTamil) {
                "Amoxicillin சில bacterial infections-க்கு பயன்படுத்தப்படும் antibiotic. மருத்துவர் பரிந்துரையின் படி மட்டுமே பயன்படுத்த வேண்டும்."
            } else {
                "Amoxicillin is an antibiotic used for certain bacterial infections. Use it only according to a healthcare professional's prescription."
            }
        }

        text.contains("ibuprofen") -> {

            if (isTamil) {
                "Ibuprofen பொதுவாக வலி, வீக்கம் மற்றும் காய்ச்சலை குறைக்க பயன்படுத்தப்படும் NSAID மருந்தாகும். அனைவருக்கும் இது பொருத்தமானதாக இருக்காது."
            } else {
                "Ibuprofen is an NSAID commonly used for pain, inflammation and fever. It may not be suitable for everyone, so follow medical or package instructions."
            }
        }

        text.contains("omeprazole") -> {

            if (isTamil) {
                "Omeprazole வயிற்றில் உருவாகும் acid-ஐ குறைக்க பயன்படுகிறது. மருத்துவர் அல்லது label வழங்கும் வழிமுறைகளை பின்பற்றவும்."
            } else {
                "Omeprazole reduces stomach acid and is commonly used for acid-related stomach conditions. Follow the prescribed or labelled instructions."
            }
        }

        text.contains("pantoprazole") -> {

            if (isTamil) {
                "Pantoprazole வயிற்றில் உருவாகும் acid-ஐ குறைக்க பயன்படும் மருந்தாகும். மருத்துவர் வழங்கிய வழிமுறைகளை பின்பற்றவும்."
            } else {
                "Pantoprazole reduces stomach acid and is commonly used for acid-related stomach conditions. Follow your healthcare professional's instructions."
            }
        }

        text.contains("metformin") -> {

            if (isTamil) {
                "Metformin பொதுவாக type 2 diabetes-ல் இரத்த சர்க்கரை அளவை கட்டுப்படுத்த பயன்படுத்தப்படுகிறது. மருத்துவர் வழங்கிய வழிமுறைகளை மட்டுமே பின்பற்றவும்."
            } else {
                "Metformin is commonly prescribed to help control blood glucose in type 2 diabetes. Use it according to your healthcare professional's instructions."
            }
        }

        text.contains("amlodipine") -> {

            if (isTamil) {
                "Amlodipine பொதுவாக உயர் இரத்த அழுத்தம் மற்றும் சில இதய சம்பந்தமான நிலைகளுக்கு பயன்படுத்தப்படுகிறது. மருத்துவர் ஆலோசனையை பின்பற்றவும்."
            } else {
                "Amlodipine is commonly used to treat high blood pressure and certain heart-related conditions. Follow your healthcare professional's instructions."
            }
        }

        text.contains("atorvastatin") -> {

            if (isTamil) {
                "Atorvastatin இரத்தத்தில் உள்ள cholesterol அளவை குறைக்க பயன்படுத்தப்படும் மருந்தாகும். மருத்துவர் பரிந்துரையின் படி பயன்படுத்தவும்."
            } else {
                "Atorvastatin is commonly used to lower cholesterol levels. Use it according to your healthcare professional's instructions."
            }
        }

        text.contains("losartan") -> {

            if (isTamil) {
                "Losartan பொதுவாக உயர் இரத்த அழுத்தத்தை கட்டுப்படுத்த பயன்படுத்தப்படுகிறது. மருத்துவர் பரிந்துரைத்த முறையில் பயன்படுத்தவும்."
            } else {
                "Losartan is commonly used to help control high blood pressure. Follow your healthcare professional's instructions."
            }
        }

        text.contains("aspirin") -> {

            if (isTamil) {
                "Aspirin பல்வேறு மருத்துவ பயன்பாடுகளைக் கொண்டுள்ளது. சில சூழ்நிலைகளில் bleeding அபாயத்தை அதிகரிக்கலாம். மருத்துவர் ஆலோசனையின்றி பயன்படுத்த வேண்டாம்."
            } else {
                "Aspirin has several medical uses and can increase bleeding risk in some situations. Do not use it without appropriate medical advice."
            }
        }

        else -> {

            if (isTamil) {
                "படத்தில் இருந்து மருந்தின் எழுத்துகளை படிக்க முடிந்தது. ஆனால் இந்த மருந்தின் அடையாளம் மற்றும் பயன்பாட்டை படம் மட்டும் வைத்து உறுதிப்படுத்த முடியாது. மருந்தின் பெயர், strength மற்றும் manufacturer தகவலை சரிபார்த்து, தேவையெனில் pharmacist அல்லது doctor-ஐ அணுகவும்."
            } else {
                "The app could read text from the image, but it cannot safely confirm the medicine's identity or use from the image alone. Verify the medicine name, strength and manufacturer information, and consult a pharmacist or doctor when needed."
            }
        }
    }
}

/* =========================================================
   FILTERS
   ========================================================= */

@Composable
private fun HealthcareFilterSection(
    selectedFilter: HealthcareFilter,
    onFilterSelected:
        (HealthcareFilter) -> Unit,
    isTamil: Boolean
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp
            )
    ) {

        Text(
            text =
                if (isTamil)
                    "சேவை வகை"
                else
                    "Service Type",

            fontSize =
                15.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                HealthcareDarkGreen
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(7.dp)
        ) {

            HealthcareFilterChip(
                text =
                    if (isTamil)
                        "அனைத்தும்"
                    else
                        "All",

                selected =
                    selectedFilter ==
                            HealthcareFilter.ALL,

                onClick = {
                    onFilterSelected(
                        HealthcareFilter.ALL
                    )
                }
            )

            HealthcareFilterChip(
                text =
                    if (isTamil)
                        "மருத்துவமனை"
                    else
                        "Hospitals",

                selected =
                    selectedFilter ==
                            HealthcareFilter.HOSPITAL,

                onClick = {
                    onFilterSelected(
                        HealthcareFilter.HOSPITAL
                    )
                }
            )

            HealthcareFilterChip(
                text =
                    if (isTamil)
                        "கிளினிக்"
                    else
                        "Clinics",

                selected =
                    selectedFilter ==
                            HealthcareFilter.CLINIC,

                onClick = {
                    onFilterSelected(
                        HealthcareFilter.CLINIC
                    )
                }
            )
        }

        Spacer(
            modifier =
                Modifier.height(7.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(7.dp)
        ) {

            HealthcareFilterChip(
                text =
                    if (isTamil)
                        "மருத்துவர்"
                    else
                        "Doctors",

                selected =
                    selectedFilter ==
                            HealthcareFilter.DOCTOR,

                onClick = {
                    onFilterSelected(
                        HealthcareFilter.DOCTOR
                    )
                }
            )

            HealthcareFilterChip(
                text =
                    if (isTamil)
                        "மருந்தகம்"
                    else
                        "Pharmacies",

                selected =
                    selectedFilter ==
                            HealthcareFilter.PHARMACY,

                onClick = {
                    onFilterSelected(
                        HealthcareFilter.PHARMACY
                    )
                }
            )
        }
    }
}

@Composable
private fun HealthcareFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Surface(
        modifier =
            Modifier.clickable {
                onClick()
            },

        shape =
            RoundedCornerShape(50.dp),

        color =
            if (selected)
                HealthcareGreen
            else
                Color.White
    ) {

        Text(
            text =
                text,

            modifier =
                Modifier.padding(
                    horizontal = 13.dp,
                    vertical = 8.dp
                ),

            color =
                if (selected)
                    Color.White
                else
                    HealthcareDarkGreen,

            fontSize =
                12.sp,

            fontWeight =
                if (selected)
                    FontWeight.Bold
                else
                    FontWeight.Medium
        )
    }
}

/* =========================================================
   RESULTS HEADER
   ========================================================= */

@Composable
private fun ResultsHeader(
    count: Int,
    isTamil: Boolean
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector =
                Icons.Default.LocationOn,

            contentDescription =
                null,

            tint =
                HealthcareGreen,

            modifier =
                Modifier.size(19.dp)
        )

        Spacer(
            modifier =
                Modifier.width(6.dp)
        )

        Text(
            text =
                if (isTamil)
                    "$count சேவைகள் அருகில் உள்ளன"
                else
                    "$count healthcare services nearby",

            fontSize =
                14.sp,

            fontWeight =
                FontWeight.SemiBold,

            color =
                HealthcareDarkGreen
        )
    }
}

/* =========================================================
   FACILITY CARD
   ========================================================= */

@Composable
private fun HealthcareFacilityCard(
    facility: HealthcareFacility,
    isTamil: Boolean
) {

    val context =
        LocalContext.current

    val facilityType =
        facility.type.lowercase(
            Locale.getDefault()
        )

    val icon =
        when (facilityType) {

            "hospital" ->
                Icons.Default.LocalHospital

            "pharmacy" ->
                Icons.Default.LocalPharmacy

            "doctor" ->
                Icons.Default.MedicalServices

            else ->
                Icons.Default.MedicalServices
        }

    val iconColor =
        when (facilityType) {

            "hospital" ->
                HealthcareGreen

            "pharmacy" ->
                PharmacyPurple

            else ->
                HealthcareGreen
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp
            ),

        shape =
            RoundedCornerShape(22.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.Top
            ) {

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            iconColor.copy(
                                alpha = 0.10f
                            )
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
                            iconColor,

                        modifier =
                            Modifier.size(27.dp)
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(12.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            facility.name,

                        fontSize =
                            17.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color(0xFF202020),

                        maxLines =
                            2,

                        overflow =
                            TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Surface(
                        shape =
                            RoundedCornerShape(50.dp),

                        color =
                            iconColor.copy(
                                alpha = 0.10f
                            )
                    ) {

                        Text(
                            text =
                                facility.type,

                            modifier =
                                Modifier.padding(
                                    horizontal = 9.dp,
                                    vertical = 4.dp
                                ),

                            fontSize =
                                11.sp,

                            color =
                                iconColor,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                DistanceBadge(
                    distanceKm =
                        facility.distanceKm
                )
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            FacilityLocationCard(
                facility =
                    facility,

                isTamil =
                    isTamil
            )

            if (
                facility.address.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.Top
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.LocationOn,

                        contentDescription =
                            null,

                        tint =
                            Color.Gray,

                        modifier =
                            Modifier.size(17.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(6.dp)
                    )

                    Text(
                        text =
                            facility.address,

                        fontSize =
                            12.sp,

                        color =
                            Color.Gray,

                        maxLines =
                            2,

                        overflow =
                            TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                if (
                    facility.phone.isNotBlank()
                ) {

                    Button(
                        onClick = {

                            openPhoneDialer(
                                context =
                                    context,

                                phone =
                                    facility.phone
                            )
                        },

                        modifier =
                            Modifier.weight(1f),

                        shape =
                            RoundedCornerShape(13.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    HealthcareGreen
                            )
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Call,

                            contentDescription =
                                null,

                            modifier =
                                Modifier.size(17.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(5.dp)
                        )

                        Text(
                            text =
                                if (isTamil)
                                    "அழைக்க"
                                else
                                    "Call"
                        )
                    }
                }

                Button(
                    onClick = {

                        openGoogleMapsDirections(
                            context =
                                context,

                            latitude =
                                facility.latitude,

                            longitude =
                                facility.longitude
                        )
                    },

                    modifier =
                        Modifier.weight(1f),

                    shape =
                        RoundedCornerShape(13.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                HealthcareDarkGreen
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Directions,

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(17.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(5.dp)
                    )

                    Text(
                        text =
                            if (isTamil)
                                "வழிகாட்டி"
                            else
                                "Directions"
                    )
                }
            }
        }
    }
}

/* =========================================================
   DISTANCE
   ========================================================= */

@Composable
private fun DistanceBadge(
    distanceKm: Double
) {

    Surface(
        shape =
            RoundedCornerShape(10.dp),

        color =
            HealthcareLightGreen
    ) {

        Row(
            modifier =
                Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 6.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    Icons.Default.LocationOn,

                contentDescription =
                    null,

                tint =
                    HealthcareGreen,

                modifier =
                    Modifier.size(14.dp)
            )

            Spacer(
                modifier =
                    Modifier.width(3.dp)
            )

            Text(
                text =
                    String.format(
                        Locale.US,
                        "%.1f km",
                        distanceKm
                    ),

                fontSize =
                    11.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    HealthcareGreen
            )
        }
    }
}

/* =========================================================
   GPS LOCATION CARD
   ========================================================= */

@Composable
private fun FacilityLocationCard(
    facility: HealthcareFacility,
    isTamil: Boolean
) {

    val context =
        LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

                openGoogleMapsLocation(
                    context =
                        context,

                    facility =
                        facility
                )
            },

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFF2F7F3)
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(43.dp)
                    .clip(CircleShape)
                    .background(
                        HealthcareGreen.copy(
                            alpha = 0.12f
                        )
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.MyLocation,

                    contentDescription =
                        null,

                    tint =
                        HealthcareGreen,

                    modifier =
                        Modifier.size(22.dp)
                )
            }

            Spacer(
                modifier =
                    Modifier.width(10.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        if (isTamil)
                            "GPS இருப்பிடம்"
                        else
                            "GPS Location",

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        HealthcareDarkGreen
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        String.format(
                            Locale.US,
                            "%.6f, %.6f",
                            facility.latitude,
                            facility.longitude
                        ),

                    fontSize =
                        10.sp,

                    color =
                        Color.Gray
                )
            }

            Surface(
                shape =
                    RoundedCornerShape(10.dp),

                color =
                    HealthcareGreen
            ) {

                Row(
                    modifier =
                        Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 7.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Map,

                        contentDescription =
                            null,

                        tint =
                            Color.White,

                        modifier =
                            Modifier.size(15.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(4.dp)
                    )

                    Text(
                        text =
                            if (isTamil)
                                "வரைபடம்"
                            else
                                "Map",

                        fontSize =
                            11.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color.White
                    )
                }
            }
        }
    }
}

/* =========================================================
   LOADING
   ========================================================= */

@Composable
private fun LoadingHealthcareCard(
    isTamil: Boolean
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp
            ),

        shape =
            RoundedCornerShape(20.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(26.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator(
                color =
                    HealthcareGreen,

                strokeWidth =
                    3.dp
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Text(
                text =
                    if (isTamil)
                        "அருகிலுள்ள மருத்துவ சேவைகளை தேடுகிறது..."
                    else
                        "Finding nearby healthcare services...",

                fontSize =
                    13.sp,

                color =
                    Color.Gray
            )
        }
    }
}

/* =========================================================
   ERROR
   ========================================================= */

@Composable
private fun HealthcareErrorCard(
    message: String,
    isTamil: Boolean,
    onRetry: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp
            ),

        shape =
            RoundedCornerShape(20.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
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

                Icon(
                    imageVector =
                        Icons.Default.Warning,

                    contentDescription =
                        null,

                    tint =
                        EmergencyRed
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text =
                        message,

                    fontSize =
                        13.sp,

                    color =
                        Color(0xFF444444)
                )
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Button(
                onClick =
                    onRetry,

                shape =
                    RoundedCornerShape(12.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            HealthcareGreen
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Refresh,

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(17.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(5.dp)
                )

                Text(
                    text =
                        if (isTamil)
                            "மீண்டும் முயற்சி"
                        else
                            "Try Again"
                )
            }
        }
    }
}

/* =========================================================
   EMPTY
   ========================================================= */

@Composable
private fun EmptyHealthcareCard(
    isTamil: Boolean
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp
            ),

        shape =
            RoundedCornerShape(20.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector =
                    Icons.Default.Search,

                contentDescription =
                    null,

                tint =
                    HealthcareGreen,

                modifier =
                    Modifier.size(42.dp)
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(
                text =
                    if (isTamil)
                        "சுகாதார சேவைகள் கிடைக்கவில்லை"
                    else
                        "No healthcare services found",

                fontSize =
                    16.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    HealthcareDarkGreen
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    if (isTamil)
                        "வேறு வகையை தேர்வு செய்து முயற்சிக்கவும்."
                    else
                        "Try another service category.",

                fontSize =
                    12.sp,

                color =
                    Color.Gray
            )
        }
    }
}

/* =========================================================
   PHONE
   ========================================================= */

private fun openPhoneDialer(
    context: Context,
    phone: String
) {

    try {

        val intent =
            Intent(
                Intent.ACTION_DIAL,
                Uri.parse(
                    "tel:${phone.trim()}"
                )
            )

        context.startActivity(
            intent
        )

    } catch (e: Exception) {

        Toast.makeText(
            context,
            "Unable to open phone dialer",
            Toast.LENGTH_SHORT
        ).show()
    }
}

/* =========================================================
   GOOGLE MAPS - DIRECTIONS
   ========================================================= */

private fun openGoogleMapsDirections(
    context: Context,
    latitude: Double,
    longitude: Double
) {

    try {

        val googleMapsUri =
            Uri.parse(
                "google.navigation:q=$latitude,$longitude"
            )

        val googleMapsIntent =
            Intent(
                Intent.ACTION_VIEW,
                googleMapsUri
            ).apply {

                setPackage(
                    "com.google.android.apps.maps"
                )
            }

        if (
            googleMapsIntent.resolveActivity(
                context.packageManager
            ) != null
        ) {

            context.startActivity(
                googleMapsIntent
            )

            return
        }

        val webMapsUri =
            Uri.parse(
                "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude"
            )

        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                webMapsUri
            )
        )

    } catch (e: Exception) {

        Toast.makeText(
            context,
            "Unable to open Google Maps",
            Toast.LENGTH_SHORT
        ).show()
    }
}

/* =========================================================
   GOOGLE MAPS - EXACT LOCATION
   ========================================================= */

private fun openGoogleMapsLocation(
    context: Context,
    facility: HealthcareFacility
) {

    try {

        val label =
            Uri.encode(
                facility.name
            )

        val googleMapsUri =
            Uri.parse(
                "geo:${facility.latitude},${facility.longitude}?q=${facility.latitude},${facility.longitude}($label)"
            )

        val googleMapsIntent =
            Intent(
                Intent.ACTION_VIEW,
                googleMapsUri
            ).apply {

                setPackage(
                    "com.google.android.apps.maps"
                )
            }

        if (
            googleMapsIntent.resolveActivity(
                context.packageManager
            ) != null
        ) {

            context.startActivity(
                googleMapsIntent
            )

            return
        }

        val webMapsUri =
            Uri.parse(
                "https://www.google.com/maps/search/?api=1&query=${facility.latitude},${facility.longitude}"
            )

        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                webMapsUri
            )
        )

    } catch (e: Exception) {

        Toast.makeText(
            context,
            "Unable to open Google Maps",
            Toast.LENGTH_SHORT
        ).show()
    }
}

/* =========================================================
   EMERGENCY CALL
   ========================================================= */

private fun callNumber(
    context: Context,
    number: String
) {

    try {

        val intent =
            Intent(
                Intent.ACTION_DIAL,
                Uri.parse(
                    "tel:$number"
                )
            )

        context.startActivity(
            intent
        )

    } catch (e: Exception) {

        Toast.makeText(
            context,
            "Unable to open phone dialer",
            Toast.LENGTH_SHORT
        ).show()
    }
}