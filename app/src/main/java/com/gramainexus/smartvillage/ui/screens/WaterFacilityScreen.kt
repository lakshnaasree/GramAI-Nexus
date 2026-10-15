package com.gramainexus.smartvillage.ui.screens

import android.net.Uri
import android.view.ViewGroup
import android.widget.VideoView
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.gramainexus.smartvillage.R
import com.gramainexus.smartvillage.data.WaterPreferences

/* ============================================================
   COLORS
   ============================================================ */

private val WaterDarkGreen = Color(0xFF0B3D2E)
private val WaterGreen = Color(0xFF168A52)
private val WaterLightGreen = Color(0xFFE8F5E9)

private val WaterBlue = Color(0xFF0288D1)
private val WaterLightBlue = Color(0xFFE1F5FE)

private val WaterBackground = Color(0xFFF5F9F7)

private val SafeGreen = Color(0xFF2E7D32)
private val CautionOrange = Color(0xFFF57C00)
private val UnsafeRed = Color(0xFFD32F2F)

/* ============================================================
   DATA MODEL
   ============================================================ */

private data class WaterVillageFacility(
    val name: String,
    val tamilName: String,
    val type: String,
    val tamilType: String,
    val status: String,
    val tamilStatus: String,
    val icon: ImageVector
)

/* ============================================================
   MAIN SCREEN
   ============================================================ */

@Composable
fun WaterFacilityScreen(
    selectedLanguage: String,
    villageName: String,
    onBack: () -> Unit,
    onEmergencyWaterClick: () -> Unit = {},
    onBillClick: () -> Unit = {}
) {

    val isTamil = selectedLanguage == "Tamil"

    val waterPreferences = remember {
        WaterPreferences()
    }

    var waterCheck by remember {
        mutableStateOf<Map<String, Any>?>(null)
    }

    var waterSchedule by remember {
        mutableStateOf<Map<String, Any>?>(null)
    }

    var waterFacilities by remember {
        mutableStateOf<List<Map<String, Any>>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var loadError by remember {
        mutableStateOf(false)
    }

    var videoVisible by remember {
        mutableStateOf(false)
    }

    var showEmergencyDialog by remember {
        mutableStateOf(false)
    }

    var showBillDialog by remember {
        mutableStateOf(false)
    }

    /*
     * Load village water information only when village changes.
     */
    LaunchedEffect(villageName) {

        if (villageName.isBlank()) {
            isLoading = false
            loadError = false
            waterCheck = null
            waterSchedule = null
            waterFacilities = emptyList()
            return@LaunchedEffect
        }

        isLoading = true
        loadError = false

        try {

            val latestCheck =
                waterPreferences.getLatestWaterCheck(villageName)

            val schedule =
                waterPreferences.getWaterSchedule(villageName)

            val facilities =
                waterPreferences.getWaterFacilities(villageName)

            waterCheck = latestCheck
            waterSchedule = schedule
            waterFacilities = facilities

        } catch (_: Exception) {

            loadError = true

            waterCheck = null
            waterSchedule = null
            waterFacilities = emptyList()
        }

        isLoading = false
    }

    /*
     * Convert raw maps into UI models only when the data changes.
     */
    val facilities = remember(waterFacilities) {
        waterFacilities.map(::createFacilityModel)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WaterBackground),
        contentPadding = PaddingValues(
            bottom = 32.dp
        )
    ) {

        /* ====================================================
           HEADER
           ==================================================== */

        item {

            WaterHeader(
                villageName = villageName,
                isTamil = isTamil,
                onBack = onBack
            )
        }

        /* ====================================================
           STATUS
           ==================================================== */

        item {

            WaterStatusHero(
                isTamil = isTamil,
                waterCheck = waterCheck
            )
        }

        /* ====================================================
           MONITORING
           ==================================================== */

        item {

            SectionTitle(
                title = if (isTamil)
                    "இன்றைய நீர் கண்காணிப்பு"
                else
                    "Today's Water Monitoring",

                subtitle = if (isTamil)
                    "கிராம நீர் அமைப்பின் தற்போதைய நிலை"
                else
                    "Current status of the village water system"
            )
        }

        item {

            MonitoringCard(
                isTamil = isTamil,
                waterCheck = waterCheck
            )
        }

        /* ====================================================
           WATER QUALITY
           ==================================================== */

        item {

            SectionTitle(
                title = if (isTamil)
                    "நீர் தரம்"
                else
                    "Water Quality",

                subtitle = if (isTamil)
                    "குளோரினேஷன் மற்றும் பாதுகாப்பு நிலை"
                else
                    "Chlorination and safety status"
            )
        }

        item {

            WaterQualityCard(
                isTamil = isTamil,
                waterCheck = waterCheck
            )
        }

        /* ====================================================
           SUPPLY SCHEDULE
           ==================================================== */

        item {

            SectionTitle(
                title = if (isTamil)
                    "நீர் வழங்கும் நேரம்"
                else
                    "Water Supply Schedule",

                subtitle = if (isTamil)
                    "இன்றைய நீர் விநியோக நேரங்கள்"
                else
                    "Today's water supply timings"
            )
        }

        item {

            SupplyScheduleCard(
                isTamil = isTamil,
                waterSchedule = waterSchedule
            )
        }

        /* ====================================================
           WATER FLOW VIDEO
           ==================================================== */

        item {

            SectionTitle(
                title = if (isTamil)
                    "நீர் ஓட்டம் எப்படி வருகிறது?"
                else
                    "How Water Reaches Your Home",

                subtitle = if (isTamil)
                    "நீர் மூலத்திலிருந்து உங்கள் வீட்டிற்கு"
                else
                    "From the water source to your home"
            )
        }

        item {

            WaterFlowVideoCard(
                isTamil = isTamil,
                showVideo = videoVisible,
                onPlayClick = {
                    videoVisible = true
                }
            )
        }

        /* ====================================================
           VILLAGE FACILITIES
           ==================================================== */

        item {

            SectionTitle(
                title = if (isTamil)
                    "கிராம நீர் வசதிகள்"
                else
                    "Village Water Facilities",

                subtitle = if (isTamil)
                    "பொது நீர் வசதிகளின் தற்போதைய நிலை"
                else
                    "Current status of public water facilities"
            )
        }

        when {

            isLoading -> {

                item {

                    LoadingWaterCard(
                        isTamil = isTamil
                    )
                }
            }

            loadError -> {

                item {

                    WaterErrorCard(
                        isTamil = isTamil
                    )
                }
            }

            facilities.isEmpty() -> {

                item {

                    EmptyFacilitiesCard(
                        isTamil = isTamil
                    )
                }
            }

            else -> {

                items(
                    items = facilities,
                    key = { facility ->
                        "${facility.name}_${facility.type}"
                    }
                ) { facility ->

                    WaterFacilityCard(
                        facility = facility,
                        isTamil = isTamil
                    )
                }
            }
        }

        /* ====================================================
           GROUNDWATER
           ==================================================== */

        item {

            SectionTitle(
                title = if (isTamil)
                    "நிலத்தடி நீர்"
                else
                    "Groundwater",

                subtitle = if (isTamil)
                    "ஆழ்துளைக் கிணறு மற்றும் நீர்மட்ட கண்காணிப்பு"
                else
                    "Borewell and groundwater monitoring"
            )
        }

        item {

            GroundwaterCard(
                isTamil = isTamil
            )
        }

        /* ====================================================
           EMERGENCY WATER
           ==================================================== */

        item {

            EmergencyWaterCard(
                isTamil = isTamil,
                onClick = {

                    /*
                     * External navigation callback.
                     */
                    onEmergencyWaterClick()

                    /*
                     * Local dialog guarantees the button
                     * remains useful even when no navigation
                     * callback has been connected yet.
                     */
                    showEmergencyDialog = true
                }
            )
        }

        /* ====================================================
           WATER BILL & METER
           ==================================================== */

        item {

            WaterBillCard(
                isTamil = isTamil,
                onClick = {

                    onBillClick()

                    showBillDialog = true
                }
            )
        }

        /* ====================================================
           FOOTER
           ==================================================== */

        item {

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = if (isTamil)
                    "💧 SmartVillage • அனைவருக்கும் பாதுகாப்பான நீர்"
                else
                    "💧 SmartVillage • Safe Water for Everyone",

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),

                color = WaterDarkGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    /* ========================================================
       EMERGENCY WATER DIALOG
       ======================================================== */

    if (showEmergencyDialog) {

        AlertDialog(
            onDismissRequest = {
                showEmergencyDialog = false
            },

            title = {

                Text(
                    text = if (isTamil)
                        "🚛 அவசர நீர் வழங்கல்"
                    else
                        "🚛 Emergency Water Supply",
                    fontWeight = FontWeight.ExtraBold
                )
            },

            text = {

                Text(
                    text = if (isTamil)
                        "உங்கள் பகுதியில் நீர் பற்றாக்குறை ஏற்பட்டால், கிராம நீர் நிர்வாகத்திடம் டேங்கர் நீர் கோரிக்கையை பதிவு செய்யலாம்."
                    else
                        "If your village is facing a water shortage, you can request emergency tanker water from the village water administration."
                )
            },

            confirmButton = {

                Button(
                    onClick = {
                        showEmergencyDialog = false
                    }
                ) {

                    Text(
                        text = if (isTamil)
                            "சரி"
                        else
                            "OK"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showEmergencyDialog = false
                    }
                ) {

                    Text(
                        text = if (isTamil)
                            "மூடு"
                        else
                            "Close"
                    )
                }
            }
        )
    }

    /* ========================================================
       WATER BILL DIALOG
       ======================================================== */

    if (showBillDialog) {

        AlertDialog(
            onDismissRequest = {
                showBillDialog = false
            },

            title = {

                Text(
                    text = if (isTamil)
                        "💳 நீர் கட்டணம் & மீட்டர்"
                    else
                        "💳 Water Bill & Meter",

                    fontWeight = FontWeight.ExtraBold
                )
            },

            text = {

                Column {

                    Text(
                        text = if (isTamil)
                            "உங்கள் நீர் கட்டணம் மற்றும் மீட்டர் பதிவுகளை இங்கு பார்க்கலாம்."
                        else
                            "You can view your water bill and meter readings here."
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = if (isTamil)
                            "கிராமம்: $villageName"
                        else
                            "Village: $villageName",

                        fontWeight = FontWeight.Bold,
                        color = WaterDarkGreen
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = if (isTamil)
                            "மீட்டர் பதிவு மற்றும் கட்டண விவரங்களுக்கு உங்கள் உள்ளூர் நீர் சேவை அலுவலகத்தை தொடர்பு கொள்ளவும்."
                        else
                            "For meter readings and payment details, contact your local water service office."
                    )
                }
            },

            confirmButton = {

                Button(
                    onClick = {
                        showBillDialog = false
                    }
                ) {

                    Text(
                        text = if (isTamil)
                            "சரி"
                        else
                            "OK"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showBillDialog = false
                    }
                ) {

                    Text(
                        text = if (isTamil)
                            "மூடு"
                        else
                            "Close"
                    )
                }
            }
        )
    }
}

/* ============================================================
   FACILITY MODEL
   ============================================================ */

private fun createFacilityModel(
    facility: Map<String, Any>
): WaterVillageFacility {

    val name = facility
        .stringValue("name")
        .ifBlank {
            "Water Facility"
        }

    val type = facility
        .stringValue("type")
        .ifBlank {
            "Water Facility"
        }

    val status = facility
        .stringValue("status")
        .ifBlank {
            "Unknown"
        }

    val normalizedType = type.lowercase()

    val icon = when {

        normalizedType == "public tap" ||
                normalizedType == "tap" -> {
            Icons.Default.WaterDrop
        }

        normalizedType == "hand pump" ||
                normalizedType == "pump" -> {
            Icons.Default.WaterDrop
        }

        normalizedType == "oht" ||
                normalizedType == "overhead tank" ||
                normalizedType == "tank" -> {
            Icons.Default.AccountBalance
        }

        normalizedType == "borewell" ||
                normalizedType == "bore well" -> {
            Icons.Default.WaterDrop
        }

        else -> {
            Icons.Default.WaterDrop
        }
    }

    val tamilName = when (name.lowercase()) {

        "main public tap" ->
            "முக்கிய பொது குழாய்"

        "village hand pump" ->
            "கிராம கைப்பம்பு"

        "overhead tank" ->
            "மேல்நிலை நீர்த்தேக்கத் தொட்டி"

        "village borewell" ->
            "கிராம ஆழ்துளைக் கிணறு"

        else ->
            name
    }

    val tamilType = when (normalizedType) {

        "public tap",
        "tap" ->
            "பொது குழாய்"

        "hand pump",
        "pump" ->
            "கைப்பம்பு"

        "oht",
        "overhead tank",
        "tank" ->
            "OHT"

        "borewell",
        "bore well" ->
            "ஆழ்துளைக் கிணறு"

        else ->
            type
    }

    val tamilStatus = when (status.lowercase()) {

        "working" ->
            "செயல்பாட்டில்"

        "active" ->
            "செயலில்"

        "broken" ->
            "பழுதடைந்தது"

        "under repair" ->
            "பழுதுபார்க்கப்படுகிறது"

        "inactive" ->
            "செயலற்றது"

        else ->
            status
    }

    return WaterVillageFacility(
        name = name,
        tamilName = tamilName,
        type = type,
        tamilType = tamilType,
        status = status,
        tamilStatus = tamilStatus,
        icon = icon
    )
}

/* ============================================================
   HEADER
   ============================================================ */

@Composable
private fun WaterHeader(
    villageName: String,
    isTamil: Boolean,
    onBack: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(215.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        WaterDarkGreen,
                        WaterGreen
                    )
                )
            )
    ) {

        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(
                    Color.White.copy(alpha = 0.08f)
                )
        )

        Box(
            modifier = Modifier
                .size(85.dp)
                .align(Alignment.BottomStart)
                .clip(CircleShape)
                .background(
                    Color.White.copy(alpha = 0.07f)
                )
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(
                    start = 8.dp,
                    top = 6.dp
                )
            ) {

                Icon(
                    imageVector =
                        Icons.AutoMirrored.Filled.ArrowBack,

                    contentDescription =
                        if (isTamil)
                            "பின் செல்ல"
                        else
                            "Back",

                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier.padding(
                    horizontal = 22.dp
                )
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(
                                RoundedCornerShape(16.dp)
                            )
                            .background(
                                Color.White.copy(
                                    alpha = 0.16f
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.WaterDrop,

                            contentDescription = null,

                            tint = Color.White,

                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(14.dp)
                    )

                    Column {

                        Text(
                            text = if (isTamil)
                                "நீர் வசதி"
                            else
                                "Water Facility",

                            color = Color.White,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = villageName.ifBlank {

                                if (isTamil)
                                    "கிராமம்"
                                else
                                    "Village"
                            },

                            color = Color.White.copy(
                                alpha = 0.85f
                            ),

                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                Text(
                    text = if (isTamil)
                        "உங்கள் கிராமத்தின் நீர் சேவைகளை ஒரே இடத்தில் கண்காணிக்கவும்"
                    else
                        "Monitor your village water services in one place",

                    color = Color.White.copy(
                        alpha = 0.9f
                    ),

                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

/* ============================================================
   STATUS HERO
   ============================================================ */

@Composable
private fun WaterStatusHero(
    isTamil: Boolean,
    waterCheck: Map<String, Any>?
) {

    val safetyStatus = waterCheck
        .stringValue("safetyStatus")
        .lowercase()

    val isSafe = safetyStatus == "safe"

    val statusColor =
        if (isSafe) {
            SafeGreen
        } else {
            CautionOrange
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(WaterLightBlue),

                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.WaterDrop,

                        contentDescription = null,

                        tint = WaterBlue,

                        modifier = Modifier.size(35.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(15.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = if (isTamil)
                            "நீர் கிடைக்கும் நிலை"
                        else
                            "Water Availability",

                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = if (isTamil)
                            "கிடைக்கிறது"
                        else
                            "Available",

                        fontSize = 23.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SafeGreen
                    )
                }

                StatusBadge(
                    text = if (isTamil)
                        "செயலில்"
                    else
                        "ACTIVE",

                    color = statusColor
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                SmallStatus(
                    icon = "🚰",

                    title = if (isTamil)
                        "விநியோகம்"
                    else
                        "Supply",

                    value = if (isTamil)
                        "இயங்குகிறது"
                    else
                        "Running"
                )

                SmallStatus(
                    icon = "🏠",

                    title = if (isTamil)
                        "கிராமம்"
                    else
                        "Village",

                    value = if (isTamil)
                        "சாதாரணம்"
                    else
                        "Normal"
                )

                SmallStatus(
                    icon = "🧪",

                    title = if (isTamil)
                        "தரம்"
                    else
                        "Quality",

                    value = if (isSafe) {

                        if (isTamil)
                            "பாதுகாப்பானது"
                        else
                            "Safe"

                    } else {

                        if (isTamil)
                            "சரிபார்க்கவும்"
                        else
                            "Check"
                    }
                )
            }
        }
    }
}

/* ============================================================
   MONITORING
   ============================================================ */

@Composable
private fun MonitoringCard(
    isTamil: Boolean,
    waterCheck: Map<String, Any>?
) {

    val ohtLevel =
        waterCheck.intValue("ohtLevel")

    val reservoirLevel =
        waterCheck.intValue("reservoirLevel")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            MonitoringProgress(
                title = if (isTamil)
                    "மேல்நிலைத் தொட்டி (OHT)"
                else
                    "Overhead Tank (OHT)",

                percentage = ohtLevel,

                icon = "🏢"
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            MonitoringProgress(
                title = if (isTamil)
                    "நீர்த்தேக்கம்"
                else
                    "Reservoir",

                percentage = reservoirLevel,

                icon = "💧"
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                MonitoringMiniCard(
                    modifier = Modifier.weight(1f),

                    title = if (isTamil)
                        "தினசரி சோதனை"
                    else
                        "Daily Check",

                    value = if (waterCheck != null) {

                        if (isTamil)
                            "முடிந்தது"
                        else
                            "Completed"

                    } else {

                        if (isTamil)
                            "தகவல் இல்லை"
                        else
                            "Not available"
                    },

                    icon = Icons.Default.CheckCircle
                )

                MonitoringMiniCard(
                    modifier = Modifier.weight(1f),

                    title = if (isTamil)
                        "நிலை"
                    else
                        "System",

                    value = if (isTamil)
                        "சாதாரணம்"
                    else
                        "Normal",

                    icon = Icons.Default.WaterDrop
                )
            }
        }
    }
}

@Composable
private fun MonitoringProgress(
    title: String,
    percentage: Int,
    icon: String
) {

    val safePercentage =
        percentage.coerceIn(0, 100)

    Column {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = icon,
                fontSize = 22.sp
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = title,

                modifier = Modifier.weight(1f),

                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "$safePercentage%",

                fontSize = 16.sp,

                fontWeight =
                    FontWeight.ExtraBold,

                color = WaterGreen
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        LinearProgressIndicator(
            progress = {
                safePercentage / 100f
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .clip(
                    RoundedCornerShape(20.dp)
                ),

            color = WaterGreen,
            trackColor = WaterLightGreen
        )
    }
}

@Composable
private fun MonitoringMiniCard(
    modifier: Modifier,
    title: String,
    value: String,
    icon: ImageVector
) {

    Card(
        modifier = modifier,

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = WaterLightGreen
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Icon(
                imageVector = icon,

                contentDescription = null,

                tint = WaterGreen,

                modifier = Modifier.size(22.dp)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = title,

                fontSize = 12.sp,

                color = Color.Gray
            )

            Text(
                text = value,

                fontSize = 13.sp,

                fontWeight =
                    FontWeight.Bold,

                color = WaterDarkGreen
            )
        }
    }
}

/* ============================================================
   WATER QUALITY
   ============================================================ */

@Composable
private fun WaterQualityCard(
    isTamil: Boolean,
    waterCheck: Map<String, Any>?
) {

    val chlorineStatus =
        waterCheck
            .stringValue("chlorineStatus")
            .ifBlank {
                "Unknown"
            }

    val safetyStatus =
        waterCheck
            .stringValue("safetyStatus")
            .ifBlank {
                "Unknown"
            }

    val checkedAt =
        waterCheck
            .stringValue("checkedAt")
            .ifBlank {
                "Not available"
            }

    val normalizedSafety =
        safetyStatus.lowercase()

    val safetyColor = when (normalizedSafety) {

        "safe" ->
            SafeGreen

        "caution" ->
            CautionOrange

        "unsafe" ->
            UnsafeRed

        else ->
            Color.Gray
    }

    val safetyText = when (normalizedSafety) {

        "safe" -> {

            if (isTamil)
                "பாதுகாப்பானது"
            else
                "SAFE"
        }

        "caution" -> {

            if (isTamil)
                "எச்சரிக்கை"
            else
                "CAUTION"
        }

        "unsafe" -> {

            if (isTamil)
                "பாதுகாப்பற்றது"
            else
                "UNSAFE"
        }

        else -> {

            if (isTamil)
                "தகவல் இல்லை"
            else
                "UNKNOWN"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(WaterLightGreen),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "🧪",
                        fontSize = 27.sp
                    )
                }

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = if (isTamil)
                            "நீர் தர நிலை"
                        else
                            "Water Quality Status",

                        fontSize = 16.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text = if (isTamil)
                            "சமீபத்திய பாதுகாப்பு சோதனை"
                        else
                            "Latest safety check",

                        fontSize = 12.sp,

                        color = Color.Gray
                    )
                }

                StatusBadge(
                    text = safetyText,
                    color = safetyColor
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            QualityRow(
                label = if (isTamil)
                    "குளோரினேஷன்"
                else
                    "Chlorination",

                value = chlorineStatus,

                color = safetyColor
            )

            QualityRow(
                label = if (isTamil)
                    "பாதுகாப்பு சோதனை"
                else
                    "Safety Test",

                value = safetyText,

                color = safetyColor
            )

            QualityRow(
                label = if (isTamil)
                    "சோதனை நேரம்"
                else
                    "Last Checked",

                value = checkedAt,

                color = WaterBlue
            )
        }
    }
}

@Composable
private fun QualityRow(
    label: String,
    value: String,
    color: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Text(
            text = label,

            modifier = Modifier.weight(1f),

            fontSize = 13.sp
        )

        Text(
            text = value,

            fontSize = 13.sp,

            fontWeight =
                FontWeight.Bold,

            color = color,

            maxLines = 1,

            overflow =
                TextOverflow.Ellipsis
        )
    }
}

/* ============================================================
   SUPPLY SCHEDULE
   ============================================================ */

@Composable
private fun SupplyScheduleCard(
    isTamil: Boolean,
    waterSchedule: Map<String, Any>?
) {

    val morningStart =
        waterSchedule
            .stringValue("morningStart")
            .ifBlank {
                "--:--"
            }

    val morningEnd =
        waterSchedule
            .stringValue("morningEnd")
            .ifBlank {
                "--:--"
            }

    val eveningStart =
        waterSchedule
            .stringValue("eveningStart")
            .ifBlank {
                "--:--"
            }

    val eveningEnd =
        waterSchedule
            .stringValue("eveningEnd")
            .ifBlank {
                "--:--"
            }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            SupplyTimeRow(
                icon = "🌅",

                title = if (isTamil)
                    "காலை விநியோகம்"
                else
                    "Morning Supply",

                time =
                    "$morningStart - $morningEnd",

                status = if (isTamil)
                    "இன்று"
                else
                    "Today"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            SupplyTimeRow(
                icon = "🌇",

                title = if (isTamil)
                    "மாலை விநியோகம்"
                else
                    "Evening Supply",

                time =
                    "$eveningStart - $eveningEnd",

                status = if (isTamil)
                    "திட்டமிடப்பட்டது"
                else
                    "Scheduled"
            )
        }
    }
}

@Composable
private fun SupplyTimeRow(
    icon: String,
    title: String,
    time: String,
    status: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(16.dp)
            )
            .background(WaterLightBlue)
            .padding(14.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = icon,
            fontSize = 28.sp
        )

        Spacer(
            modifier = Modifier.width(13.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,

                fontWeight =
                    FontWeight.Bold,

                fontSize = 14.sp
            )

            Text(
                text = time,

                color = WaterBlue,

                fontWeight =
                    FontWeight.ExtraBold,

                fontSize = 15.sp
            )
        }

        Text(
            text = status,

            color = SafeGreen,

            fontSize = 11.sp,

            fontWeight =
                FontWeight.Bold
        )
    }
}

/* ============================================================
   WATER FLOW VIDEO
   ============================================================ */

@Composable
private fun WaterFlowVideoCard(
    isTamil: Boolean,
    showVideo: Boolean,
    onPlayClick: () -> Unit
) {

    val context = LocalContext.current

    var isPlaying by remember {
        mutableStateOf(false)
    }

    var videoError by remember {
        mutableStateOf(false)
    }

    val videoUri = remember(context) {

        Uri.parse(
            "android.resource://${context.packageName}/${R.raw.water_flow}"
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF092E25)
        )
    ) {

        Column {

            if (showVideo) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(Color.Black)
                ) {

                    AndroidView(
                        modifier = Modifier.fillMaxSize(),

                        factory = { ctx ->

                            VideoView(ctx).apply {

                                layoutParams =
                                    ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )

                                setVideoURI(videoUri)

                                setOnPreparedListener {
                                    videoError = false
                                }

                                setOnCompletionListener {
                                    isPlaying = false
                                }

                                setOnErrorListener { _, _, _ ->

                                    videoError = true
                                    isPlaying = false

                                    true
                                }
                            }
                        },

                        update = { videoView ->

                            if (videoError) {
                                return@AndroidView
                            }

                            if (isPlaying) {

                                if (!videoView.isPlaying) {
                                    videoView.start()
                                }

                            } else {

                                if (videoView.isPlaying) {
                                    videoView.pause()
                                }
                            }
                        }
                    )

                    if (videoError) {

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Color.Black.copy(
                                        alpha = 0.78f
                                    )
                                ),

                            horizontalAlignment =
                                Alignment.CenterHorizontally,

                            verticalArrangement =
                                Arrangement.Center
                        ) {

                            Text(
                                text = "⚠️",
                                fontSize = 32.sp
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = if (isTamil)
                                    "வீடியோவை இயக்க முடியவில்லை"
                                else
                                    "Unable to play video",

                                color = Color.White,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }

                    } else {

                        IconButton(
                            onClick = {
                                isPlaying = !isPlaying
                            },

                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .clip(CircleShape)
                                .background(
                                    Color.Black.copy(
                                        alpha = 0.65f
                                    )
                                )
                        ) {

                            Icon(
                                imageVector =
                                    if (isPlaying)
                                        Icons.Default.Pause
                                    else
                                        Icons.Default.PlayArrow,

                                contentDescription =
                                    if (isPlaying)
                                        "Pause"
                                    else
                                        "Play",

                                tint = Color.White
                            )
                        }
                    }
                }

            } else {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF126B56),
                                    Color(0xFF063A30)
                                )
                            )
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(
                                    Color.White.copy(
                                        alpha = 0.18f
                                    )
                                )
                                .clickable {
                                    onPlayClick()
                                },

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.PlayArrow,

                                contentDescription =
                                    if (isTamil)
                                        "நீர் வீடியோவை இயக்கவும்"
                                    else
                                        "Play water flow video",

                                tint = Color.White,

                                modifier =
                                    Modifier.size(42.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = if (isTamil)
                                "நீர் ஓட்ட வீடியோ"
                            else
                                "Water Flow Video",

                            color = Color.White,

                            fontWeight =
                                FontWeight.ExtraBold,

                            fontSize = 17.sp
                        )

                        Text(
                            text = if (isTamil)
                                "மூலம் → தொட்டி → குழாய் → வீடு"
                            else
                                "Source → Tank → Pipeline → Home",

                            color = Color.White.copy(
                                alpha = 0.8f
                            ),

                            fontSize = 12.sp
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(18.dp)
            ) {

                Text(
                    text = if (isTamil)
                        "உங்கள் வீட்டிற்கு நீர் எப்படி வருகிறது?"
                    else
                        "How does water reach your home?",

                    color = Color.White,

                    fontSize = 16.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Text(
                    text = if (isTamil)
                        "நீர் மூலத்திலிருந்து சேமிப்பு தொட்டி மற்றும் குழாய்கள் வழியாக உங்கள் வீட்டிற்கு வரும் செயல்முறையை அறிந்து கொள்ளுங்கள்."
                    else
                        "Learn how water travels from the source through storage tanks and pipelines to your home.",

                    color = Color.White.copy(
                        alpha = 0.72f
                    ),

                    fontSize = 13.sp,

                    lineHeight = 19.sp
                )

                if (showVideo) {

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = if (isTamil)
                            "🎬 வீடியோ இயக்கப்பட்டது"
                        else
                            "🎬 Video player ready",

                        color = Color(0xFF9FE3C7),

                        fontSize = 12.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

/* ============================================================
   FACILITY CARD
   ============================================================ */

@Composable
private fun WaterFacilityCard(
    facility: WaterVillageFacility,
    isTamil: Boolean
) {

    val normalizedStatus =
        facility.status.lowercase()

    val statusColor = when {

        normalizedStatus == "working" ||
                normalizedStatus == "active" ->
            SafeGreen

        normalizedStatus == "under repair" ||
                normalizedStatus == "repair" ->
            CautionOrange

        normalizedStatus == "broken" ||
                normalizedStatus == "inactive" ->
            UnsafeRed

        else ->
            Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 5.dp
            ),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(
                        RoundedCornerShape(15.dp)
                    )
                    .background(WaterLightBlue),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = facility.icon,

                    contentDescription = null,

                    tint = WaterBlue,

                    modifier = Modifier.size(27.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(13.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = if (isTamil)
                        facility.tamilName
                    else
                        facility.name,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize = 14.sp,

                    maxLines = 1,

                    overflow =
                        TextOverflow.Ellipsis
                )

                Text(
                    text = if (isTamil)
                        facility.tamilType
                    else
                        facility.type,

                    color = Color.Gray,

                    fontSize = 12.sp
                )
            }

            StatusBadge(
                text = if (isTamil)
                    facility.tamilStatus
                else
                    facility.status,

                color = statusColor
            )
        }
    }
}

/* ============================================================
   GROUNDWATER
   ============================================================ */

@Composable
private fun GroundwaterCard(
    isTamil: Boolean
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Row(
            modifier = Modifier.padding(18.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(WaterLightGreen),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "🌱",
                    fontSize = 29.sp
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = if (isTamil)
                        "ஆழ்துளைக் கிணறு"
                    else
                        "Village Borewell",

                    fontWeight =
                        FontWeight.Bold,

                    fontSize = 15.sp
                )

                Text(
                    text = if (isTamil)
                        "நீர்மட்ட நிலை"
                    else
                        "Water table status",

                    color = Color.Gray,

                    fontSize = 12.sp
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = if (isTamil)
                        "நிலை தகவல் இல்லை"
                    else
                        "Monitoring data unavailable",

                    color = Color.Gray,

                    fontSize = 13.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }

            Icon(
                imageVector =
                    Icons.Default.WaterDrop,

                contentDescription = null,

                tint = WaterBlue,

                modifier = Modifier.size(26.dp)
            )
        }
    }
}

/* ============================================================
   EMERGENCY WATER CARD
   ============================================================ */

@Composable
private fun EmergencyWaterCard(
    isTamil: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 8.dp
            ),

        shape = RoundedCornerShape(23.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1)
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            Color(0xFFFFECB3)
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "🚛",
                        fontSize = 27.sp
                    )
                }

                Spacer(
                    modifier = Modifier.width(13.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = if (isTamil)
                            "அவசர நீர் வழங்கல்"
                        else
                            "Emergency Water Supply",

                        fontWeight =
                            FontWeight.ExtraBold,

                        fontSize = 16.sp,

                        color = Color(0xFF6D4C00)
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = if (isTamil)
                            "நீர் பற்றாக்குறை ஏற்பட்டால் டேங்கர் கோரிக்கை"
                        else
                            "Request a tanker during water shortage",

                        fontSize = 12.sp,

                        color = Color(0xFF8A6A22)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            Button(
                onClick = onClick,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),

                shape = RoundedCornerShape(14.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        Color(0xFF8D6E00)
                )
            ) {

                Text(
                    text = if (isTamil)
                        "🚛 டேங்கர் கோரிக்கை"
                    else
                        "🚛 Request Water Tanker",

                    fontWeight =
                        FontWeight.Bold,

                    fontSize = 14.sp
                )
            }
        }
    }
}

/* ============================================================
   WATER BILL CARD
   ============================================================ */

@Composable
private fun WaterBillCard(
    isTamil: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 8.dp
            ),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
                        .background(
                            WaterLightGreen
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.CreditCard,

                        contentDescription = null,

                        tint = WaterGreen,

                        modifier = Modifier.size(29.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = if (isTamil)
                            "நீர் கட்டணம் & மீட்டர்"
                        else
                            "Water Bill & Meter",

                        fontWeight =
                            FontWeight.ExtraBold,

                        fontSize = 16.sp,

                        color = WaterDarkGreen
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = if (isTamil)
                            "மாதாந்திர கட்டணம் • மீட்டர் பதிவு"
                        else
                            "Monthly tariff • Meter reading",

                        color = Color.Gray,

                        fontSize = 12.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            OutlinedButton(
                onClick = onClick,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),

                shape = RoundedCornerShape(13.dp),

                contentPadding =
                    PaddingValues(
                        horizontal = 14.dp
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.CreditCard,

                    contentDescription = null,

                    modifier = Modifier.size(20.dp)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = if (isTamil)
                        "கட்டணம் & மீட்டர் பார்க்க"
                    else
                        "View Bill & Meter",

                    fontSize = 13.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}

/* ============================================================
   LOADING
   ============================================================ */

@Composable
private fun LoadingWaterCard(
    isTamil: Boolean
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator(
                color = WaterGreen
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = if (isTamil)
                    "நீர் தகவல்கள் ஏற்றப்படுகின்றன..."
                else
                    "Loading water information...",

                fontSize = 13.sp,

                color = Color.Gray
            )
        }
    }
}

/* ============================================================
   ERROR
   ============================================================ */

@Composable
private fun WaterErrorCard(
    isTamil: Boolean
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "⚠️",
                fontSize = 34.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = if (isTamil)
                    "நீர் தகவலை பெற முடியவில்லை"
                else
                    "Unable to load water information",

                fontWeight =
                    FontWeight.Bold,

                color = WaterDarkGreen
            )
        }
    }
}

/* ============================================================
   EMPTY
   ============================================================ */

@Composable
private fun EmptyFacilitiesCard(
    isTamil: Boolean
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "💧",
                fontSize = 38.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = if (isTamil)
                    "நீர் வசதிகள் இல்லை"
                else
                    "No water facilities available",

                fontSize = 15.sp,

                fontWeight =
                    FontWeight.Bold,

                color = WaterDarkGreen
            )

            Text(
                text = if (isTamil)
                    "இந்த கிராமத்திற்கான தகவல் விரைவில் சேர்க்கப்படும்."
                else
                    "Facility information will be added soon.",

                fontSize = 12.sp,

                color = Color.Gray
            )
        }
    }
}

/* ============================================================
   SECTION TITLE
   ============================================================ */

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String
) {

    Column(
        modifier = Modifier.padding(
            start = 18.dp,
            end = 18.dp,
            top = 18.dp,
            bottom = 10.dp
        )
    ) {

        Text(
            text = title,

            fontSize = 18.sp,

            fontWeight =
                FontWeight.ExtraBold,

            color = WaterDarkGreen
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = subtitle,

            fontSize = 12.sp,

            color = Color.Gray
        )
    }
}

/* ============================================================
   STATUS BADGE
   ============================================================ */

@Composable
private fun StatusBadge(
    text: String,
    color: Color
) {

    Surface(
        shape = RoundedCornerShape(50.dp),

        color = color.copy(
            alpha = 0.12f
        )
    ) {

        Text(
            text = text,

            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),

            color = color,

            fontSize = 10.sp,

            fontWeight =
                FontWeight.ExtraBold,

            maxLines = 1
        )
    }
}

/* ============================================================
   SMALL STATUS
   ============================================================ */

@Composable
private fun SmallStatus(
    icon: String,
    title: String,
    value: String
) {

    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = icon,
            fontSize = 20.sp
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = title,

            fontSize = 10.sp,

            color = Color.Gray
        )

        Text(
            text = value,

            fontSize = 11.sp,

            fontWeight =
                FontWeight.Bold,

            color = WaterDarkGreen,

            maxLines = 1,

            overflow =
                TextOverflow.Ellipsis
        )
    }
}

/* ============================================================
   SAFE MAP HELPERS
   ============================================================ */

private fun Map<String, Any>?.stringValue(
    key: String
): String {

    return this
        ?.get(key)
        ?.toString()
        ?.trim()
        .orEmpty()
}

private fun Map<String, Any>?.intValue(
    key: String
): Int {

    val value = this?.get(key)

    return when (value) {

        is Number ->
            value.toInt()

        is String ->
            value.toIntOrNull() ?: 0

        else ->
            0
    }
}


