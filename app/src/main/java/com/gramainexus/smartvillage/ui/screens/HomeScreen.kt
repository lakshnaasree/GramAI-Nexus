@file:Suppress("DEPRECATION")

package com.gramainexus.smartvillage.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.gramainexus.smartvillage.ProfileViewModel
import com.gramainexus.smartvillage.data.FarmerProfile
import com.gramainexus.smartvillage.data.IssueData
import com.gramainexus.smartvillage.data.IssuePreferences
import java.io.File

private val SmartGreen = Color(0xFF2E7D32)
private val LightGreen = Color(0xFFE8F5E9)
private val DarkGreen = Color(0xFF174D2A)
private val SoftBackground = Color(0xFFF5F8F5)

data class VillageService(
    val routeKey: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun HomeScreen(
    villageName: String,
    selectedLanguage: String,
    onLogout: () -> Unit,
    onReportIssue: () -> Unit,
    onProfileClick: () -> Unit,
    onUpdatesClick: () -> Unit,
    onGovernmentSchemesClick: () -> Unit,
    onAgricultureClick: () -> Unit,
    onWaterClick: () -> Unit,
    onHealthcareClick: () -> Unit,
    onEmploymentClick: () -> Unit
) {

    val context =
        androidx.compose.ui.platform.LocalContext.current

    val profileViewModel: ProfileViewModel =
        viewModel()

    val profile by
    profileViewModel.profile.collectAsState()

    var savedIssue by remember {
        mutableStateOf<IssueData?>(null)
    }

    var showContent by remember {
        mutableStateOf(false)
    }

    val isTamil =
        selectedLanguage.equals(
            "Tamil",
            ignoreCase = true
        )

    // =====================================================
    // LOAD PROFILE
    // =====================================================

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile(context)
    }

    // =====================================================
    // LOAD CURRENT USER ISSUE
    // =====================================================

    LaunchedEffect(Unit) {

        try {

            val issuePreferences =
                IssuePreferences()

            savedIssue =
                issuePreferences
                    .getLatestIssueForCurrentUser()

        } catch (e: Exception) {

            savedIssue = null
        }

        showContent = true
    }

    // =====================================================
    // SERVICES
    // =====================================================

    val services = remember(isTamil) {

        listOf(

            VillageService(
                routeKey = "government",
                title =
                    if (isTamil)
                        "அரசு சேவைகள்"
                    else
                        "Government",
                subtitle =
                    if (isTamil)
                        "திட்டங்கள் & சேவைகள்"
                    else
                        "Schemes & Services",
                icon =
                    Icons.Default.AccountBalance,
                color =
                    Color(0xFF3F51B5)
            ),

            VillageService(
                routeKey = "agriculture",
                title =
                    if (isTamil)
                        "விவசாயம்"
                    else
                        "Agriculture",
                subtitle =
                    if (isTamil)
                        "விவசாய உதவி"
                    else
                        "Farm Support",
                icon =
                    Icons.Default.Agriculture,
                color =
                    Color(0xFF388E3C)
            ),

            VillageService(
                routeKey = "healthcare",
                title =
                    if (isTamil)
                        "சுகாதாரம்"
                    else
                        "Healthcare",
                subtitle =
                    if (isTamil)
                        "மருத்துவ சேவைகள்"
                    else
                        "Health Services",
                icon =
                    Icons.Default.HealthAndSafety,
                color =
                    Color(0xFFE53935)
            ),

            VillageService(
                routeKey = "employment",
                title =
                    if (isTamil)
                        "வேலைவாய்ப்பு"
                    else
                        "Employment",
                subtitle =
                    if (isTamil)
                        "வேலை வாய்ப்புகள்"
                    else
                        "Job Opportunities",
                icon =
                    Icons.Default.Work,
                color =
                    Color(0xFFFF8F00)
            ),

            VillageService(
                routeKey = "water",
                title =
                    if (isTamil)
                        "தண்ணீர்"
                    else
                        "Water",
                subtitle =
                    if (isTamil)
                        "நீர் சேவைகள்"
                    else
                        "Water Services",
                icon =
                    Icons.Default.WaterDrop,
                color =
                    Color(0xFF0288D1)
            ),

            VillageService(
                routeKey = "updates",
                title =
                    if (isTamil)
                        "செய்திகள்"
                    else
                        "Village News",
                subtitle =
                    if (isTamil)
                        "சமூக தகவல்கள்"
                    else
                        "Community Updates",
                icon =
                    Icons.Default.Newspaper,
                color =
                    Color(0xFF8E24AA)
            )
        )
    }

    // =====================================================
    // MAIN SCREEN
    // =====================================================

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(SoftBackground)
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
        ) {

            PremiumHeader(
                profile = profile,
                villageName = villageName,
                isTamil = isTamil,
                onProfileClick = onProfileClick,
                onLogout = onLogout
            )

            AnimatedVisibility(
                visible = showContent,
                enter =
                    fadeIn() +
                            slideInVertically()
            ) {

                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    // =================================================
                    // WELCOME
                    // =================================================

                    WelcomeHeroCard(
                        villageName = villageName,
                        isTamil = isTamil
                    )

                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )

                    // =================================================
                    // STATISTICS
                    // =================================================

                    StatisticsRow(
                        isTamil = isTamil
                    )

                    Spacer(
                        modifier =
                            Modifier.height(22.dp)
                    )

                    // =================================================
                    // QUICK ACTIONS
                    // =================================================

                    SectionTitle(
                        title =
                            if (isTamil)
                                "விரைவு செயல்கள்"
                            else
                                "Quick Actions"
                    )

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    QuickActions(
                        isTamil = isTamil,
                        onReportIssue = onReportIssue,
                        onUpdatesClick = onUpdatesClick
                    )

                    Spacer(
                        modifier =
                            Modifier.height(24.dp)
                    )

                    // =================================================
                    // VILLAGE SERVICES
                    // =================================================

                    SectionTitle(
                        title =
                            if (isTamil)
                                "கிராம சேவைகள்"
                            else
                                "Village Services"
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    LazyVerticalGrid(
                        columns =
                            GridCells.Fixed(2),

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(430.dp),

                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(12.dp),

                        userScrollEnabled =
                            false
                    ) {

                        items(
                            items = services,
                            key = {
                                it.routeKey
                            }
                        ) { service ->

                            ModernServiceCard(
                                service = service,

                                onClick = {

                                    when (service.routeKey) {

                                        "government" -> {
                                            onGovernmentSchemesClick()
                                        }

                                        "agriculture" -> {
                                            onAgricultureClick()
                                        }

                                        "healthcare" -> {
                                            onHealthcareClick()
                                        }

                                        "employment" -> {
                                            onEmploymentClick()
                                        }

                                        "water" -> {
                                            onWaterClick()
                                        }

                                        "updates" -> {
                                            onUpdatesClick()
                                        }
                                    }
                                }
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(24.dp)
                    )

                    // =================================================
                    // PANCHAYAT ANNOUNCEMENT
                    // =================================================

                    PanchayatAnnouncementCard(
                        isTamil = isTamil
                    )

                    Spacer(
                        modifier =
                            Modifier.height(24.dp)
                    )

                    // =================================================
                    // REPORT ISSUE
                    // =================================================

                    ReportIssuePremiumCard(
                        isTamil = isTamil,
                        onReportIssue = onReportIssue
                    )

                    Spacer(
                        modifier =
                            Modifier.height(24.dp)
                    )

                    // =================================================
                    // RECENT ISSUE
                    // =================================================

                    SectionTitle(
                        title =
                            if (isTamil)
                                "உங்கள் சமீபத்திய புகார்"
                            else
                                "Your Recent Issue"
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    if (savedIssue != null) {

                        IssueStatusCard(
                            issue = savedIssue!!
                        )

                    } else {

                        Card(
                            modifier =
                                Modifier.fillMaxWidth(),

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
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),

                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.CheckCircle,

                                    contentDescription =
                                        null,

                                    tint =
                                        SmartGreen,

                                    modifier =
                                        Modifier.size(38.dp)
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(8.dp)
                                )

                                Text(
                                    text =
                                        if (isTamil)
                                            "புதிய புகார்கள் இல்லை"
                                        else
                                            "No issues reported yet",

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        DarkGreen
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(4.dp)
                                )

                                Text(
                                    text =
                                        if (isTamil)
                                            "உங்கள் புகார்களை இங்கே பார்க்கலாம்."
                                        else
                                            "Your reported issues will appear here.",

                                    color =
                                        Color.Gray,

                                    style =
                                        MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(28.dp)
                    )

                    // =================================================
                    // FOOTER
                    // =================================================

                    Text(
                        text =
                            "🌾 SmartVillage",

                        style =
                            MaterialTheme.typography.titleMedium,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            SmartGreen,

                        modifier =
                            Modifier.align(
                                Alignment.CenterHorizontally
                            )
                    )

                    Text(
                        text =
                            if (isTamil)
                                "உங்கள் கிராமத்தின் டிஜிட்டல் துணை"
                            else
                                "Your Digital Rural Companion",

                        style =
                            MaterialTheme.typography.bodySmall,

                        color =
                            Color.Gray,

                        modifier =
                            Modifier
                                .align(
                                    Alignment.CenterHorizontally
                                )
                                .padding(top = 4.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(24.dp)
                    )
                }
            }
        }
    }
}


/* =========================================================
   PREMIUM HEADER
========================================================= */

@Composable
private fun PremiumHeader(
    profile: FarmerProfile,
    villageName: String,
    isTamil: Boolean,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape =
                        RoundedCornerShape(
                            bottomStart = 32.dp,
                            bottomEnd = 32.dp
                        )
                )
                .clip(
                    RoundedCornerShape(
                        bottomStart = 32.dp,
                        bottomEnd = 32.dp
                    )
                )
                .background(
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                Color(0xFF123F25),
                                Color(0xFF1F6B3A),
                                Color(0xFF2E7D32)
                            )
                    )
                )
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 18.dp,
                    bottom = 24.dp
                )
    ) {

        Box(
            modifier =
                Modifier
                    .size(140.dp)
                    .offset(
                        x = 230.dp,
                        y = (-55).dp
                    )
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(
                            alpha = 0.06f
                        )
                    )
        )

        Box(
            modifier =
                Modifier
                    .size(90.dp)
                    .offset(
                        x = (-40).dp,
                        y = 100.dp
                    )
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(
                            alpha = 0.05f
                        )
                    )
        )

        Column(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "🌾 SmartVillage",

                        color =
                            Color.White,

                        style =
                            MaterialTheme.typography.titleLarge,

                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Text(
                        text =
                            if (isTamil)
                                "உங்கள் கிராமத்தின் டிஜிட்டல் துணை"
                            else
                                "Your Smart Rural Companion",

                        color =
                            Color.White.copy(
                                alpha = 0.72f
                            ),

                        style =
                            MaterialTheme.typography.bodySmall
                    )
                }

                BadgedBox(
                    badge = {
                        Badge {
                            Text("3")
                        }
                    }
                ) {

                    IconButton(
                        onClick = {}
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Notifications,

                            contentDescription =
                                "Notifications",

                            tint =
                                Color.White
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(24.dp)
                        )
                        .background(
                            Color.White.copy(
                                alpha = 0.13f
                            )
                        )
                        .clickable {
                            onProfileClick()
                        }
                        .padding(14.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                ProfilePhoto(
                    photoPath =
                        profile.profilePhotoUrl,

                    modifier =
                        Modifier.size(72.dp)
                )

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
                            profile.name.ifBlank {
                                if (isTamil)
                                    "விவசாயி"
                                else
                                    "Farmer"
                            },

                        color =
                            Color.White,

                        style =
                            MaterialTheme.typography.titleMedium,

                        fontWeight =
                            FontWeight.Bold,

                        maxLines =
                            1,

                        overflow =
                            TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
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
                                Modifier.size(16.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(3.dp)
                        )

                        Text(
                            text =
                                villageName,

                            color =
                                Color.White.copy(
                                    alpha = 0.78f
                                ),

                            style =
                                MaterialTheme.typography.bodySmall,

                            maxLines =
                                1,

                            overflow =
                                TextOverflow.Ellipsis
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(5.dp)
                    )

                    Text(
                        text =
                            if (isTamil)
                                "சுயவிவரத்தை பார்க்க"
                            else
                                "View Profile",

                        color =
                            Color(0xFFA5D6A7),

                        style =
                            MaterialTheme.typography.labelSmall,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }

                Icon(
                    imageVector =
                        Icons.Default.ArrowForward,

                    contentDescription =
                        null,

                    tint =
                        Color.White.copy(
                            alpha = 0.7f
                        )
                )
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Row(
                    modifier =
                        Modifier.weight(1f),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier =
                            Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    Color(0xFF69F0AE)
                                )
                    )

                    Spacer(
                        modifier =
                            Modifier.width(7.dp)
                    )

                    Text(
                        text =
                            if (isTamil)
                                "சேவை செயல்பாட்டில் உள்ளது"
                            else
                                "Smart services active",

                        color =
                            Color.White.copy(
                                alpha = 0.75f
                            ),

                        style =
                            MaterialTheme.typography.labelSmall
                    )
                }

                Button(
                    onClick =
                        onLogout,

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color.White.copy(
                                    alpha = 0.14f
                                ),

                            contentColor =
                                Color.White
                        ),

                    shape =
                        RoundedCornerShape(14.dp),

                    contentPadding =
                        PaddingValues(
                            horizontal = 13.dp,
                            vertical = 7.dp
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Logout,

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
                                "வெளியேறு"
                            else
                                "Logout",

                        style =
                            MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}


/* =========================================================
   PROFILE PHOTO
========================================================= */

@Composable
private fun ProfilePhoto(
    photoPath: String,
    modifier: Modifier = Modifier
) {

    Box(
        modifier =
            modifier
                .shadow(
                    elevation = 8.dp,
                    shape =
                        RoundedCornerShape(20.dp)
                )
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .background(
                    Brush.linearGradient(
                        colors =
                            listOf(
                                Color(0xFFA5D6A7),
                                Color(0xFF43A047)
                            )
                    )
                )
                .padding(3.dp),

        contentAlignment =
            Alignment.Center
    ) {

        if (photoPath.isNotBlank()) {

            val bitmap =
                remember(photoPath) {
                    BitmapFactory.decodeFile(
                        photoPath
                    )
                }

            if (bitmap != null) {

                Image(
                    bitmap =
                        bitmap.asImageBitmap(),

                    contentDescription =
                        "Profile Photo",

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(
                                RoundedCornerShape(17.dp)
                            ),

                    contentScale =
                        ContentScale.Crop
                )

            } else {

                DefaultProfileIcon()
            }

        } else {

            DefaultProfileIcon()
        }
    }
}

@Composable
private fun DefaultProfileIcon() {

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Color.White.copy(
                        alpha = 0.9f
                    )
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
                SmartGreen,

            modifier =
                Modifier.size(38.dp)
        )
    }
}


/* =========================================================
   WELCOME
========================================================= */

@Composable
private fun WelcomeHeroCard(
    villageName: String,
    isTamil: Boolean
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(26.dp),

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

        Row(
            modifier =
                Modifier.padding(20.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier =
                    Modifier
                        .size(58.dp)
                        .clip(
                            RoundedCornerShape(18.dp)
                        )
                        .background(
                            LightGreen
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "🌾",

                    style =
                        MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(
                modifier =
                    Modifier.width(14.dp)
            )

            Column {

                Text(
                    text =
                        if (isTamil)
                            "வரவேற்கிறோம்!"
                        else
                            "Welcome back!",

                    style =
                        MaterialTheme.typography.titleMedium,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        DarkGreen
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        if (isTamil)
                            "$villageName கிராமத்திற்கு வரவேற்கிறோம்"
                        else
                            "Welcome to $villageName village",

                    style =
                        MaterialTheme.typography.bodyMedium,

                    color =
                        Color.Gray
                )
            }
        }
    }
}


/* =========================================================
   STATISTICS
========================================================= */

@Composable
private fun StatisticsRow(
    isTamil: Boolean
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {

        StatisticCard(
            value = "12",

            title =
                if (isTamil)
                    "புதுப்பிப்புகள்"
                else
                    "Updates",

            modifier =
                Modifier.weight(1f)
        )

        StatisticCard(
            value = "08",

            title =
                if (isTamil)
                    "தீர்வு"
                else
                    "Resolved",

            modifier =
                Modifier.weight(1f)
        )

        StatisticCard(
            value = "03",

            title =
                if (isTamil)
                    "நிலுவை"
                else
                    "Pending",

            modifier =
                Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatisticCard(
    value: String,
    title: String,
    modifier: Modifier
) {

    Card(
        modifier =
            modifier,

        shape =
            RoundedCornerShape(18.dp),

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
                Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 15.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    value,

                style =
                    MaterialTheme.typography.titleLarge,

                fontWeight =
                    FontWeight.ExtraBold,

                color =
                    SmartGreen
            )

            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )

            Text(
                text =
                    title,

                style =
                    MaterialTheme.typography.labelSmall,

                color =
                    Color.Gray
            )
        }
    }
}


/* =========================================================
   SECTION TITLE
========================================================= */

@Composable
private fun SectionTitle(
    title: String
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text =
                title,

            style =
                MaterialTheme.typography.titleMedium,

            fontWeight =
                FontWeight.ExtraBold,

            color =
                Color(0xFF26352A),

            modifier =
                Modifier.weight(1f)
        )

        Box(
            modifier =
                Modifier
                    .width(35.dp)
                    .height(4.dp)
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(
                        SmartGreen
                    )
        )
    }
}


/* =========================================================
   QUICK ACTIONS
========================================================= */

@Composable
private fun QuickActions(
    isTamil: Boolean,
    onReportIssue: () -> Unit,
    onUpdatesClick: () -> Unit
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        QuickActionCard(
            icon =
                Icons.Default.ReportProblem,

            title =
                if (isTamil)
                    "புகார்"
                else
                    "Report Issue",

            color =
                Color(0xFFE53935),

            modifier =
                Modifier.weight(1f),

            onClick =
                onReportIssue
        )

        QuickActionCard(
            icon =
                Icons.Default.Campaign,

            title =
                if (isTamil)
                    "புதுப்பிப்புகள்"
                else
                    "Updates",

            color =
                Color(0xFF1565C0),

            modifier =
                Modifier.weight(1f),

            onClick =
                onUpdatesClick
        )
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    title: String,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier =
            modifier
                .height(105.dp)
                .clickable {
                    onClick()
                },

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
                Modifier
                    .fillMaxSize()
                    .padding(14.dp),

            verticalArrangement =
                Arrangement.Center
        ) {

            Icon(
                imageVector =
                    icon,

                contentDescription =
                    null,

                tint =
                    color,

                modifier =
                    Modifier.size(30.dp)
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    title,

                fontWeight =
                    FontWeight.Bold,

                style =
                    MaterialTheme.typography.bodyMedium
            )
        }
    }
}


/* =========================================================
   SERVICE CARD
========================================================= */

@Composable
private fun ModernServiceCard(
    service: VillageService,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },

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
                Modifier.padding(15.dp)
        ) {

            Box(
                modifier =
                    Modifier
                        .size(46.dp)
                        .clip(
                            RoundedCornerShape(15.dp)
                        )
                        .background(
                            service.color.copy(
                                alpha = 0.12f
                            )
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        service.icon,

                    contentDescription =
                        service.title,

                    tint =
                        service.color,

                    modifier =
                        Modifier.size(25.dp)
                )
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Text(
                text =
                    service.title,

                fontWeight =
                    FontWeight.Bold,

                maxLines =
                    1,

                overflow =
                    TextOverflow.Ellipsis
            )

            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )

            Text(
                text =
                    service.subtitle,

                color =
                    Color.Gray,

                style =
                    MaterialTheme.typography.bodySmall,

                maxLines =
                    1,

                overflow =
                    TextOverflow.Ellipsis
            )
        }
    }
}


/* =========================================================
   PANCHAYAT ANNOUNCEMENT
========================================================= */

@Composable
private fun PanchayatAnnouncementCard(
    isTamil: Boolean
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(24.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFEAF5EC)
            )
    ) {

        Row(
            modifier =
                Modifier.padding(18.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(
                            RoundedCornerShape(15.dp)
                        )
                        .background(
                            Color.White
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Campaign,

                    contentDescription =
                        null,

                    tint =
                        SmartGreen
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
                    text =
                        if (isTamil)
                            "பஞ்சாயத்து அறிவிப்பு"
                        else
                            "Panchayat Announcement",

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
                            "நாளை காலை 10 மணிக்கு கிராம கூட்டம் நடைபெறும்."
                        else
                            "Village meeting will be held tomorrow at 10 AM.",

                    color =
                        Color.Gray,

                    style =
                        MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


/* =========================================================
   REPORT ISSUE
========================================================= */

@Composable
private fun ReportIssuePremiumCard(
    isTamil: Boolean,
    onReportIssue: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onReportIssue()
                },

        shape =
            RoundedCornerShape(26.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFF173F26)
            )
    ) {

        Row(
            modifier =
                Modifier.padding(20.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        if (isTamil)
                            "உங்கள் பிரச்சனையை தெரிவிக்கவும்"
                        else
                            "Have a problem?",

                    color =
                        Color.White,

                    style =
                        MaterialTheme.typography.titleMedium,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Text(
                    text =
                        if (isTamil)
                            "கிராமத்தில் உள்ள பிரச்சனையை எங்களுக்கு தெரிவிக்கவும்."
                        else
                            "Report a village issue and help improve your community.",

                    color =
                        Color.White.copy(
                            alpha = 0.72f
                        ),

                    style =
                        MaterialTheme.typography.bodySmall
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text =
                        if (isTamil)
                            "புகார் பதிவு செய்யவும்  →"
                        else
                            "Report now  →",

                    color =
                        Color(0xFFA5D6A7),

                    fontWeight =
                        FontWeight.Bold
                )
            }

            Text(
                text =
                    "📢",

                style =
                    MaterialTheme.typography.displaySmall
            )
        }
    }
}


/* =========================================================
   ISSUE STATUS
========================================================= */

@Composable
private fun IssueStatusCard(
    issue: IssueData
) {

    val status =
        issue.status

    val statusColor =
        when {

            status.contains(
                "Resolved",
                ignoreCase = true
            ) ||
                    status.contains(
                        "தீர்க்க",
                        ignoreCase = true
                    ) ->
                Color(0xFF2E7D32)

            status.contains(
                "Pending",
                ignoreCase = true
            ) ||
                    status.contains(
                        "நிலுவை",
                        ignoreCase = true
                    ) ->
                Color(0xFFEF6C00)

            else ->
                Color(0xFF1565C0)
        }

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(22.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )
    ) {

        Column(
            modifier =
                Modifier.padding(17.dp)
        ) {

            if (issue.photoUri.isNotBlank()) {

                LocalIssueImage(
                    imagePath =
                        issue.photoUri
                )

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )
            }

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ReportProblem,

                    contentDescription =
                        null,

                    tint =
                        statusColor
                )

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Text(
                    text =
                        issue.category,

                    fontWeight =
                        FontWeight.Bold,

                    modifier =
                        Modifier.weight(1f)
                )

                Text(
                    text =
                        status,

                    color =
                        statusColor,

                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(
                text =
                    issue.location,

                color =
                    Color.Gray,

                style =
                    MaterialTheme.typography.bodySmall
            )

            if (issue.gpsLocation.isNotBlank()) {

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        "GPS: ${issue.gpsLocation}",

                    color =
                        Color.Gray,

                    style =
                        MaterialTheme.typography.bodySmall
                )
            }

            if (issue.description.isNotBlank()) {

                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )

                Text(
                    text =
                        issue.description,

                    color =
                        Color.DarkGray,

                    style =
                        MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


/* =========================================================
   LOCAL ISSUE IMAGE
========================================================= */

@Composable
private fun LocalIssueImage(
    imagePath: String
) {

    val imageFile =
        remember(imagePath) {
            File(imagePath)
        }

    if (imageFile.exists()) {

        AsyncImage(
            model =
                imageFile,

            contentDescription =
                "Issue Photo",

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(
                        RoundedCornerShape(18.dp)
                    ),

            contentScale =
                ContentScale.Crop
        )
    }
}