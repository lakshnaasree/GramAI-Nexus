package com.gramainexus.smartvillage.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// =========================================================
// COLORS
// =========================================================

private val GovernmentDarkGreen = Color(0xFF073B2A)
private val GovernmentGreen = Color(0xFF168A52)
private val GovernmentMediumGreen = Color(0xFF2E9D68)
private val GovernmentLightGreen = Color(0xFFE8F5E9)
private val GovernmentSoftGreen = Color(0xFFF4FAF6)

private val GovernmentBackground = Color(0xFFF5F8F6)
private val GovernmentGold = Color(0xFFF4A825)
private val GovernmentGoldLight = Color(0xFFFFF7E1)

private val GovernmentBlue = Color(0xFF1976D2)
private val GovernmentBlueLight = Color(0xFFE8F1FC)

private val TextDark = Color(0xFF17352A)
private val TextGray = Color(0xFF64736C)


// =========================================================
// DATA MODEL
// =========================================================

private data class GovernmentScheme(
    val id: String,

    val name: String,
    val tamilName: String,

    val category: SchemeCategory,

    val description: String,
    val tamilDescription: String,

    val eligibility: String,
    val tamilEligibility: String,

    val benefits: String,
    val tamilBenefits: String,

    val documents: List<String>,
    val tamilDocuments: List<String>,

    val icon: ImageVector,

    val officialUrl: String,

    val verified: Boolean = true
) {

    val officialDomain: String
        get() = Uri.parse(officialUrl)
            .host
            ?.removePrefix("www.")
            ?: officialUrl
}


// =========================================================
// CATEGORIES
// =========================================================

private enum class SchemeCategory(
    val english: String,
    val tamil: String
) {

    ALL(
        "All",
        "அனைத்தும்"
    ),

    AGRICULTURE(
        "Agriculture",
        "விவசாயம்"
    ),

    FINANCIAL(
        "Financial",
        "நிதி உதவி"
    ),

    HOUSING(
        "Housing",
        "வீட்டுவசதி"
    ),

    EDUCATION(
        "Education",
        "கல்வி"
    ),

    HEALTH(
        "Health",
        "சுகாதாரம்"
    ),

    EMPLOYMENT(
        "Employment",
        "வேலைவாய்ப்பு"
    ),

    WOMEN(
        "Women & Children",
        "பெண்கள் மற்றும் குழந்தைகள்"
    ),

    SENIOR(
        "Senior Citizens",
        "மூத்த குடிமக்கள்"
    ),

    DISABILITY(
        "Disability",
        "மாற்றுத்திறனாளிகள்"
    )
}


// =========================================================
// MAIN SCREEN
// =========================================================

@Composable
fun GovernmentSchemesScreen(
    selectedLanguage: String,
    villageName: String,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val isTamil =
        selectedLanguage.equals(
            "Tamil",
            ignoreCase = true
        )

    // -----------------------------------------------------
    // UI STATE
    // -----------------------------------------------------

    var searchText by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableStateOf(
            SchemeCategory.ALL
        )
    }

    val bookmarkedSchemes =
        remember {
            mutableStateListOf<String>()
        }

    // -----------------------------------------------------
    // SCHEME DATA
    // -----------------------------------------------------

    val schemes = remember {
        sampleGovernmentSchemes()
    }

    // -----------------------------------------------------
    // FILTERED DATA
    // -----------------------------------------------------

    val filteredSchemes by remember(
        schemes,
        searchText,
        selectedCategory
    ) {

        derivedStateOf {

            val query =
                searchText
                    .trim()
                    .lowercase()

            schemes.filter { scheme ->

                val categoryMatches =
                    selectedCategory ==
                            SchemeCategory.ALL ||
                            scheme.category ==
                            selectedCategory

                val searchMatches =
                    query.isEmpty() ||
                            scheme.name
                                .lowercase()
                                .contains(query) ||
                            scheme.tamilName
                                .contains(query) ||
                            scheme.category
                                .english
                                .lowercase()
                                .contains(query) ||
                            scheme.category
                                .tamil
                                .contains(query) ||
                            scheme.description
                                .lowercase()
                                .contains(query) ||
                            scheme.tamilDescription
                                .contains(query)

                categoryMatches &&
                        searchMatches
            }
        }
    }


    // =====================================================
    // SCREEN
    // =====================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                GovernmentBackground
            )
    ) {

        // -------------------------------------------------
        // HEADER
        // -------------------------------------------------

        GovernmentHeader(
            isTamil = isTamil,
            villageName = villageName,
            onBack = onBack
        )


        // -------------------------------------------------
        // CONTENT
        // -------------------------------------------------

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),

            contentPadding = PaddingValues(
                top = 12.dp,
                bottom = 28.dp
            ),

            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            // =================================================
            // INTRO
            // =================================================

            item {

                GovernmentIntroCard(
                    isTamil = isTamil,
                    schemeCount = schemes.size
                )
            }


            // =================================================
            // SEARCH
            // =================================================

            item {

                GovernmentSearchBar(
                    searchText = searchText,
                    isTamil = isTamil,
                    onSearchChange = {
                        searchText = it
                    },
                    onClear = {
                        searchText = ""
                    }
                )
            }


            // =================================================
            // CATEGORIES
            // =================================================

            item {

                GovernmentCategorySection(
                    selectedCategory =
                        selectedCategory,

                    isTamil = isTamil,

                    onCategorySelected = {
                        selectedCategory = it
                    }
                )
            }


            // =================================================
            // RESULT HEADER
            // =================================================

            item {

                GovernmentResultHeader(
                    isTamil = isTamil,
                    resultCount =
                        filteredSchemes.size
                )
            }


            // =================================================
            // EMPTY STATE
            // =================================================

            if (filteredSchemes.isEmpty()) {

                item {

                    EmptySchemeState(
                        isTamil = isTamil,

                        onClear = {

                            searchText = ""

                            selectedCategory =
                                SchemeCategory.ALL
                        }
                    )
                }

            } else {

                // =================================================
                // SCHEMES
                // =================================================

                items(
                    items = filteredSchemes,
                    key = {
                        it.id
                    }
                ) { scheme ->

                    GovernmentSchemeCard(
                        scheme = scheme,
                        isTamil = isTamil,

                        isBookmarked =
                            bookmarkedSchemes
                                .contains(
                                    scheme.id
                                ),

                        onBookmarkClick = {

                            if (
                                bookmarkedSchemes
                                    .contains(
                                        scheme.id
                                    )
                            ) {

                                bookmarkedSchemes
                                    .remove(
                                        scheme.id
                                    )

                            } else {

                                bookmarkedSchemes
                                    .add(
                                        scheme.id
                                    )
                            }
                        },

                        onOpenWebsite = {

                            openOfficialWebsite(
                                context = context,
                                url = scheme.officialUrl
                            )
                        }
                    )
                }
            }


            // =================================================
            // FOOTER
            // =================================================

            item {

                GovernmentFooter(
                    isTamil = isTamil
                )
            }
        }
    }
}


// =========================================================
// HEADER
// =========================================================

@Composable
private fun GovernmentHeader(
    isTamil: Boolean,
    villageName: String,
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                GovernmentDarkGreen
            )
            .padding(
                start = 8.dp,
                end = 16.dp,
                top = 10.dp,
                bottom = 18.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
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

                    tint = Color.White
                )
            }


            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        if (isTamil)
                            "அரசு திட்டங்கள்"
                        else
                            "Government Schemes",

                    color = Color.White,

                    fontSize = 23.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        if (isTamil)
                            "உங்களுக்கு கிடைக்கக்கூடிய அரசு நலத்திட்டங்கள்"
                        else
                            "Explore government welfare services",

                    color =
                        Color.White.copy(
                            alpha = 0.76f
                        ),

                    fontSize = 12.sp
                )
            }


            Surface(
                shape =
                    RoundedCornerShape(15.dp),

                color =
                    Color.White.copy(
                        alpha = 0.12f
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.AccountBalance,

                    contentDescription = null,

                    tint = Color.White,

                    modifier = Modifier
                        .padding(11.dp)
                        .size(26.dp)
                )
            }
        }


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // -----------------------------------------------------
        // VILLAGE BADGE
        // -----------------------------------------------------

        Surface(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(13.dp),

            color =
                Color.White.copy(
                    alpha = 0.10f
                )
        ) {

            Row(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 9.dp
                ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.People,

                    contentDescription = null,

                    tint = Color.White,

                    modifier =
                        Modifier.size(18.dp)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text =
                        if (isTamil)
                            "கிராமம்: $villageName"
                        else
                            "Village: $villageName",

                    color = Color.White,

                    fontSize = 13.sp,

                    fontWeight =
                        FontWeight.Medium
                )
            }
        }
    }
}


// =========================================================
// INTRO CARD
// =========================================================

@Composable
private fun GovernmentIntroCard(
    isTamil: Boolean,
    schemeCount: Int
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

        shape =
            RoundedCornerShape(22.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    GovernmentLightGreen
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
    ) {

        Row(
            modifier =
                Modifier.padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Surface(
                shape =
                    RoundedCornerShape(17.dp),

                color =
                    GovernmentGreen
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Verified,

                    contentDescription = null,

                    tint = Color.White,

                    modifier = Modifier
                        .padding(13.dp)
                        .size(29.dp)
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
                            "அரசு உதவிகளை கண்டறியுங்கள்"
                        else
                            "Discover Government Benefits",

                    fontSize = 16.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        GovernmentDarkGreen
                )


                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )


                Text(
                    text =
                        if (isTamil)
                            "$schemeCount திட்டங்கள் மற்றும் சேவைகள் இங்கே உள்ளன."
                        else
                            "$schemeCount schemes and services are available here.",

                    fontSize = 12.sp,

                    color =
                        TextGray,

                    lineHeight = 18.sp
                )
            }
        }
    }
}


// =========================================================
// SEARCH BAR
// =========================================================

@Composable
private fun GovernmentSearchBar(
    searchText: String,
    isTamil: Boolean,
    onSearchChange: (String) -> Unit,
    onClear: () -> Unit
) {

    OutlinedTextField(

        value = searchText,

        onValueChange =
            onSearchChange,

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),

        singleLine = true,

        shape =
            RoundedCornerShape(18.dp),

        leadingIcon = {

            Icon(
                imageVector =
                    Icons.Default.Search,

                contentDescription = null,

                tint =
                    GovernmentGreen
            )
        },

        trailingIcon = {

            if (searchText.isNotEmpty()) {

                IconButton(
                    onClick = onClear
                ) {

                    Text(
                        text = "×",
                        fontSize = 24.sp,
                        color = TextGray
                    )
                }
            }
        },

        placeholder = {

            Text(
                text =
                    if (isTamil)
                        "திட்டங்களைத் தேடுங்கள்..."
                    else
                        "Search schemes..."
            )
        }
    )
}


// =========================================================
// CATEGORY SECTION
// =========================================================

@Composable
private fun GovernmentCategorySection(
    selectedCategory: SchemeCategory,
    isTamil: Boolean,
    onCategorySelected:
        (SchemeCategory) -> Unit
) {

    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Text(
            text =
                if (isTamil)
                    "திட்ட வகைகள்"
                else
                    "Categories",

            modifier =
                Modifier.padding(
                    horizontal = 16.dp
                ),

            fontSize = 16.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                GovernmentDarkGreen
        )


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        LazyRow(
            contentPadding =
                PaddingValues(
                    horizontal = 16.dp
                ),

            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            items(
                items =
                    SchemeCategory.entries,

                key = {
                    it.name
                }
            ) { category ->

                FilterChip(

                    selected =
                        selectedCategory ==
                                category,

                    onClick = {
                        onCategorySelected(
                            category
                        )
                    },

                    label = {

                        Text(
                            text =
                                if (isTamil)
                                    category.tamil
                                else
                                    category.english
                        )
                    },

                    leadingIcon = {

                        if (
                            selectedCategory ==
                            category
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.CheckCircle,

                                contentDescription =
                                    null,

                                modifier =
                                    Modifier.size(
                                        17.dp
                                    )
                            )
                        }
                    },

                    colors =
                        FilterChipDefaults
                            .filterChipColors(

                                selectedContainerColor =
                                    GovernmentGreen,

                                selectedLabelColor =
                                    Color.White,

                                selectedLeadingIconColor =
                                    Color.White
                            )
                )
            }
        }
    }
}


// =========================================================
// RESULT HEADER
// =========================================================

@Composable
private fun GovernmentResultHeader(
    isTamil: Boolean,
    resultCount: Int
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp
                ),

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
                        "கிடைக்கும் திட்டங்கள்"
                    else
                        "Available Schemes",

                fontSize = 18.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    GovernmentDarkGreen
            )

            Text(
                text =
                    if (isTamil)
                        "$resultCount திட்டங்கள்"
                    else
                        "$resultCount schemes found",

                fontSize = 12.sp,

                color = TextGray
            )
        }


        Surface(
            shape =
                RoundedCornerShape(12.dp),

            color =
                GovernmentLightGreen
        ) {

            Row(
                modifier =
                    Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 7.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Language,

                    contentDescription = null,

                    tint =
                        GovernmentGreen,

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
                            "தமிழ்"
                        else
                            "English",

                    color =
                        GovernmentGreen,

                    fontWeight =
                        FontWeight.SemiBold,

                    fontSize = 12.sp
                )
            }
        }
    }
}


// =========================================================
// SCHEME CARD
// =========================================================

@Composable
private fun GovernmentSchemeCard(
    scheme: GovernmentScheme,
    isTamil: Boolean,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onOpenWebsite: () -> Unit
) {

    var expanded by remember(
        scheme.id
    ) {
        mutableStateOf(false)
    }


    val schemeName =
        if (isTamil)
            scheme.tamilName
        else
            scheme.name

    val category =
        if (isTamil)
            scheme.category.tamil
        else
            scheme.category.english

    val description =
        if (isTamil)
            scheme.tamilDescription
        else
            scheme.description

    val eligibility =
        if (isTamil)
            scheme.tamilEligibility
        else
            scheme.eligibility

    val benefits =
        if (isTamil)
            scheme.tamilBenefits
        else
            scheme.benefits

    val documents =
        if (isTamil)
            scheme.tamilDocuments
        else
            scheme.documents


    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp
                ),

        shape =
            RoundedCornerShape(23.dp),

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
                Modifier.fillMaxWidth()
        ) {

            // =================================================
            // MAIN CARD
            // =================================================

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),

                verticalAlignment =
                    Alignment.Top
            ) {

                Surface(
                    shape =
                        RoundedCornerShape(16.dp),

                    color =
                        GovernmentLightGreen
                ) {

                    Icon(
                        imageVector =
                            scheme.icon,

                        contentDescription =
                            null,

                        tint =
                            GovernmentGreen,

                        modifier =
                            Modifier
                                .padding(12.dp)
                                .size(28.dp)
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

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                schemeName,

                            modifier =
                                Modifier.weight(1f),

                            fontSize = 16.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextDark,

                            maxLines = 2,

                            overflow =
                                TextOverflow.Ellipsis
                        )


                        IconButton(
                            onClick =
                                onBookmarkClick,

                            modifier =
                                Modifier.size(36.dp)
                        ) {

                            Icon(
                                imageVector =
                                    if (isBookmarked)
                                        Icons.Default.Bookmark
                                    else
                                        Icons.Default.BookmarkBorder,

                                contentDescription =
                                    if (isTamil)
                                        "சேமி"
                                    else
                                        "Bookmark",

                                tint =
                                    if (isBookmarked)
                                        GovernmentGreen
                                    else
                                        Color.Gray
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )


                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Surface(
                            shape =
                                RoundedCornerShape(8.dp),

                            color =
                                GovernmentLightGreen
                        ) {

                            Text(
                                text =
                                    category,

                                modifier =
                                    Modifier.padding(
                                        horizontal = 9.dp,
                                        vertical = 4.dp
                                    ),

                                fontSize = 11.sp,

                                fontWeight =
                                    FontWeight.SemiBold,

                                color =
                                    GovernmentGreen
                            )
                        }


                        if (scheme.verified) {

                            Spacer(
                                modifier =
                                    Modifier.width(7.dp)
                            )

                            Icon(
                                imageVector =
                                    Icons.Default.Verified,

                                contentDescription =
                                    null,

                                tint =
                                    GovernmentBlue,

                                modifier =
                                    Modifier.size(16.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(3.dp)
                            )

                            Text(
                                text =
                                    if (isTamil)
                                        "அதிகாரப்பூர்வம்"
                                    else
                                        "Official",

                                fontSize = 10.sp,

                                fontWeight =
                                    FontWeight.SemiBold,

                                color =
                                    GovernmentBlue
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(9.dp)
                    )


                    Text(
                        text =
                            description,

                        fontSize = 13.sp,

                        color =
                            TextGray,

                        lineHeight = 19.sp,

                        maxLines =
                            if (expanded)
                                Int.MAX_VALUE
                            else
                                3,

                        overflow =
                            TextOverflow.Ellipsis
                    )
                }
            }


            // =================================================
            // EXPANDED DETAILS
            // =================================================

            AnimatedVisibility(

                visible = expanded,

                enter =
                    fadeIn() +
                            expandVertically(),

                exit =
                    fadeOut() +
                            shrinkVertically()
            ) {

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 14.dp
                            )
                ) {

                    HorizontalDivider(
                        color =
                            Color(0xFFE6ECE8)
                    )


                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )


                    SchemeDetailSection(
                        icon =
                            Icons.Default.People,

                        title =
                            if (isTamil)
                                "தகுதி"
                            else
                                "Eligibility",

                        text =
                            eligibility
                    )


                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )


                    SchemeDetailSection(
                        icon =
                            Icons.Default.CheckCircle,

                        title =
                            if (isTamil)
                                "நன்மைகள்"
                            else
                                "Benefits",

                        text =
                            benefits
                    )


                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )


                    Text(
                        text =
                            if (isTamil)
                                "தேவையான ஆவணங்கள்"
                            else
                                "Required Documents",

                        fontSize = 14.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            GovernmentDarkGreen
                    )


                    Spacer(
                        modifier =
                            Modifier.height(7.dp)
                    )


                    documents.forEach { document ->

                        Row(
                            modifier =
                                Modifier.padding(
                                    vertical = 3.dp
                                ),

                            verticalAlignment =
                                Alignment.Top
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Description,

                                contentDescription =
                                    null,

                                tint =
                                    GovernmentGreen,

                                modifier =
                                    Modifier.size(17.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(7.dp)
                            )

                            Text(
                                text =
                                    document,

                                fontSize = 12.sp,

                                color =
                                    TextGray,

                                lineHeight = 17.sp
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(15.dp)
                    )


                    // =================================================
                    // OFFICIAL WEBSITE CARD
                    // =================================================

                    OfficialWebsiteCard(
                        scheme = scheme,
                        isTamil = isTamil,
                        onOpenWebsite =
                            onOpenWebsite
                    )
                }
            }


            // =================================================
            // EXPAND BUTTON
            // =================================================

            Surface(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            expanded = !expanded
                        },

                color =
                    GovernmentSoftGreen
            ) {

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text =
                            if (expanded) {

                                if (isTamil)
                                    "விவரங்களை மறைக்கவும்"
                                else
                                    "Hide details"

                            } else {

                                if (isTamil)
                                    "முழு விவரங்கள் மற்றும் இணையதளம்"
                                else
                                    "View details & official website"
                            },

                        modifier =
                            Modifier.weight(1f),

                        fontSize = 12.sp,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            GovernmentGreen
                    )


                    Icon(
                        imageVector =
                            if (expanded)
                                Icons.Default.ExpandLess
                            else
                                Icons.Default.ExpandMore,

                        contentDescription =
                            null,

                        tint =
                            GovernmentGreen
                    )
                }
            }
        }
    }
}


// =========================================================
// OFFICIAL WEBSITE CARD
// =========================================================

@Composable
private fun OfficialWebsiteCard(
    scheme: GovernmentScheme,
    isTamil: Boolean,
    onOpenWebsite: () -> Unit
) {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(17.dp),

        color =
            GovernmentBlueLight
    ) {

        Column(
            modifier =
                Modifier.padding(14.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    shape =
                        RoundedCornerShape(11.dp),

                    color =
                        GovernmentBlue
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Language,

                        contentDescription =
                            null,

                        tint = Color.White,

                        modifier =
                            Modifier
                                .padding(8.dp)
                                .size(20.dp)
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(9.dp)
                )


                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            if (isTamil)
                                "அதிகாரப்பூர்வ அரசு இணையதளம்"
                            else
                                "Official Government Website",

                        fontSize = 13.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color(0xFF174A7A)
                    )


                    Spacer(
                        modifier =
                            Modifier.height(2.dp)
                    )


                    Text(
                        text =
                            scheme.officialDomain,

                        fontSize = 11.sp,

                        color =
                            GovernmentBlue,

                        maxLines = 1,

                        overflow =
                            TextOverflow.Ellipsis
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(11.dp)
            )


            // -------------------------------------------------
            // CLEAR OPEN WEBSITE BUTTON
            // -------------------------------------------------

            Button(
                onClick =
                    onOpenWebsite,

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(13.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            GovernmentBlue
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.OpenInNew,

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
                            "அதிகாரப்பூர்வ இணையதளத்தைத் திறக்கவும்"
                        else
                            "Open Official Website",

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}


// =========================================================
// DETAIL SECTION
// =========================================================

@Composable
private fun SchemeDetailSection(
    icon: ImageVector,
    title: String,
    text: String
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.Top
    ) {

        Surface(
            shape =
                RoundedCornerShape(9.dp),

            color =
                GovernmentLightGreen
        ) {

            Icon(
                imageVector =
                    icon,

                contentDescription =
                    null,

                tint =
                    GovernmentGreen,

                modifier =
                    Modifier
                        .padding(7.dp)
                        .size(17.dp)
            )
        }


        Spacer(
            modifier =
                Modifier.width(9.dp)
        )


        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text =
                    title,

                fontSize = 14.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    GovernmentDarkGreen
            )


            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )


            Text(
                text =
                    text,

                fontSize = 12.sp,

                color =
                    TextGray,

                lineHeight = 18.sp
            )
        }
    }
}


// =========================================================
// EMPTY STATE
// =========================================================

@Composable
private fun EmptySchemeState(
    isTamil: Boolean,
    onClear: () -> Unit
) {

    Card(
        modifier =
            Modifier
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
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(30.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Surface(
                shape =
                    RoundedCornerShape(18.dp),

                color =
                    GovernmentLightGreen
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Search,

                    contentDescription =
                        null,

                    tint =
                        GovernmentGreen,

                    modifier =
                        Modifier
                            .padding(15.dp)
                            .size(30.dp)
                )
            }


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            Text(
                text =
                    if (isTamil)
                        "திட்டங்கள் கிடைக்கவில்லை"
                    else
                        "No schemes found",

                fontSize = 17.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    GovernmentDarkGreen
            )


            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )


            Text(
                text =
                    if (isTamil)
                        "வேறு தேடல் அல்லது வகையை முயற்சிக்கவும்."
                    else
                        "Try another search or category.",

                fontSize = 12.sp,

                color = TextGray
            )


            Spacer(
                modifier =
                    Modifier.height(15.dp)
            )


            Button(
                onClick = onClear,

                shape =
                    RoundedCornerShape(12.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            GovernmentGreen
                    )
            ) {

                Text(
                    text =
                        if (isTamil)
                            "மீட்டமை"
                        else
                            "Reset"
                )
            }
        }
    }
}


// =========================================================
// FOOTER
// =========================================================

@Composable
private fun GovernmentFooter(
    isTamil: Boolean
) {

    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp
                ),

        shape =
            RoundedCornerShape(18.dp),

        color =
            GovernmentGoldLight
    ) {

        Row(
            modifier =
                Modifier.padding(14.dp),

            verticalAlignment =
                Alignment.Top
        ) {

            Icon(
                imageVector =
                    Icons.Default.Verified,

                contentDescription =
                    null,

                tint =
                    GovernmentGold,

                modifier =
                    Modifier.size(22.dp)
            )


            Spacer(
                modifier =
                    Modifier.width(9.dp)
            )


            Text(
                text =
                    if (isTamil)
                        "விண்ணப்பிக்கும் முன் அதிகாரப்பூர்வ அரசு இணையதளத்தில் தகுதி, ஆவணங்கள் மற்றும் தற்போதைய விதிமுறைகளை சரிபார்க்கவும்."
                    else
                        "Before applying, verify eligibility, documents and current requirements on the official government website.",

                fontSize = 11.sp,

                color =
                    Color(0xFF6D5A20),

                lineHeight = 17.sp
            )
        }
    }
}


// =========================================================
// OPEN OFFICIAL WEBSITE
// =========================================================

private fun openOfficialWebsite(
    context: android.content.Context,
    url: String
) {

    val uri =
        Uri.parse(url)

    val browserIntent =
        Intent(
            Intent.ACTION_VIEW,
            uri
        )

    try {

        context.startActivity(
            Intent.createChooser(
                browserIntent,
                "Open official government website"
            )
        )

    } catch (
        _: ActivityNotFoundException
    ) {

        // No browser available.
        // Nothing else is required here.
    }
}


// =========================================================
// SAMPLE GOVERNMENT SCHEMES
// =========================================================

private fun sampleGovernmentSchemes():
        List<GovernmentScheme> {

    return listOf(

        // =====================================================
        // 1. PM KISAN
        // =====================================================

        GovernmentScheme(
            id = "pm-kisan",
            name = "PM-KISAN",
            tamilName = "பிரதமர் கிசான்",
            category = SchemeCategory.AGRICULTURE,

            description =
                "Income support scheme for eligible landholding farmer families.",

            tamilDescription =
                "தகுதியான நிலம் வைத்திருக்கும் விவசாயக் குடும்பங்களுக்கு வருமான ஆதரவு வழங்கும் திட்டம்.",

            eligibility =
                "Eligible landholding farmer families subject to applicable scheme rules and exclusions.",

            tamilEligibility =
                "திட்ட விதிகள் மற்றும் விலக்கு நிபந்தனைகளுக்கு உட்பட்ட தகுதியான நிலம் வைத்திருக்கும் விவசாயக் குடும்பங்கள்.",

            benefits =
                "Income support is provided according to applicable government guidelines.",

            tamilBenefits =
                "நடைமுறையில் உள்ள அரசு வழிகாட்டுதல்களின்படி வருமான ஆதரவு வழங்கப்படுகிறது.",

            documents =
                listOf(
                    "Aadhaar",
                    "Bank account details",
                    "Land-related records"
                ),

            tamilDocuments =
                listOf(
                    "ஆதார்",
                    "வங்கி கணக்கு விவரங்கள்",
                    "நில ஆவணங்கள்"
                ),

            icon = Icons.Default.Agriculture,
            officialUrl = "https://pmkisan.gov.in/"
        ),


        // =====================================================
        // 2. PMAY-G
        // =====================================================

        GovernmentScheme(
            id = "pmay-g",
            name = "PMAY - Gramin",
            tamilName = "பிரதமர் வீட்டு வசதி - கிராமப்புறம்",
            category = SchemeCategory.HOUSING,

            description =
                "Rural housing assistance programme for eligible households.",

            tamilDescription =
                "தகுதியான கிராமப்புற குடும்பங்களுக்கு வீட்டு வசதி உதவி வழங்கும் திட்டம்.",

            eligibility =
                "Eligibility is determined using applicable rural housing criteria and government verification.",

            tamilEligibility =
                "கிராமப்புற வீட்டு வசதி தகுதி விதிகள் மற்றும் அரசு சரிபார்ப்பின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Eligible households may receive housing assistance according to programme rules.",

            tamilBenefits =
                "திட்ட விதிகளின்படி தகுதியான குடும்பங்களுக்கு வீட்டு வசதி உதவி கிடைக்கலாம்.",

            documents =
                listOf(
                    "Aadhaar",
                    "Bank account",
                    "Household details"
                ),

            tamilDocuments =
                listOf(
                    "ஆதார்",
                    "வங்கி கணக்கு",
                    "குடும்ப விவரங்கள்"
                ),

            icon = Icons.Default.Home,
            officialUrl = "https://pmayg.nic.in/"
        ),


        // =====================================================
        // 3. JAN DHAN
        // =====================================================

        GovernmentScheme(
            id = "pmjdy",
            name = "Pradhan Mantri Jan-Dhan Yojana",
            tamilName = "பிரதமர் ஜன்-தன் யோஜனா",
            category = SchemeCategory.FINANCIAL,

            description =
                "Financial inclusion programme providing access to basic banking services.",

            tamilDescription =
                "அடிப்படை வங்கி சேவைகளுக்கான அணுகலை வழங்கும் நிதி உள்ளடக்கத் திட்டம்.",

            eligibility =
                "Individuals who meet applicable account-opening and KYC requirements.",

            tamilEligibility =
                "பொருந்தும் கணக்கு திறப்பு மற்றும் KYC விதிமுறைகளை பூர்த்தி செய்யும் நபர்கள்.",

            benefits =
                "Access to applicable basic banking and financial inclusion services.",

            tamilBenefits =
                "தகுதியான அடிப்படை வங்கி மற்றும் நிதி உள்ளடக்க சேவைகளுக்கான அணுகல்.",

            documents =
                listOf(
                    "Aadhaar or accepted KYC document",
                    "Address details"
                ),

            tamilDocuments =
                listOf(
                    "ஆதார் அல்லது ஏற்றுக்கொள்ளப்படும் KYC ஆவணம்",
                    "முகவரி விவரங்கள்"
                ),

            icon = Icons.Default.AccountBalance,
            officialUrl = "https://www.pmjdy.gov.in/"
        ),


        // =====================================================
        // 4. MUDRA
        // =====================================================

        GovernmentScheme(
            id = "mudra",
            name = "Pradhan Mantri MUDRA Yojana",
            tamilName = "பிரதமர் முத்ரா யோஜனா",
            category = SchemeCategory.FINANCIAL,

            description =
                "Credit support programme for eligible micro and small business activities.",

            tamilDescription =
                "தகுதியான குறு மற்றும் சிறு தொழில் நடவடிக்கைகளுக்கான கடன் ஆதரவு திட்டம்.",

            eligibility =
                "Eligibility is subject to applicable lending and scheme criteria.",

            tamilEligibility =
                "திட்ட மற்றும் கடன் வழங்குநரின் நடைமுறை தகுதி விதிகளுக்கு உட்பட்டது.",

            benefits =
                "Eligible applicants can seek business credit through participating lending institutions.",

            tamilBenefits =
                "பங்கேற்கும் கடன் நிறுவனங்கள் மூலம் தகுதியானவர்கள் தொழில் கடன் பெற விண்ணப்பிக்கலாம்.",

            documents =
                listOf(
                    "Identity proof",
                    "Address proof",
                    "Business-related documents"
                ),

            tamilDocuments =
                listOf(
                    "அடையாளச் சான்று",
                    "முகவரிச் சான்று",
                    "தொழில் தொடர்பான ஆவணங்கள்"
                ),

            icon = Icons.Default.Work,
            officialUrl = "https://www.mudra.org.in/"
        ),


        // =====================================================
        // 5. PM-JAY
        // =====================================================

        GovernmentScheme(
            id = "pmjay",
            name = "Ayushman Bharat PM-JAY",
            tamilName = "ஆயுஷ்மான் பாரத் PM-JAY",
            category = SchemeCategory.HEALTH,

            description =
                "Government health protection programme for eligible beneficiary families.",

            tamilDescription =
                "தகுதியான பயனாளி குடும்பங்களுக்கான அரசு சுகாதார பாதுகாப்புத் திட்டம்.",

            eligibility =
                "Eligibility depends on the beneficiary database and applicable scheme rules.",

            tamilEligibility =
                "பயனாளி தரவுத்தளம் மற்றும் நடைமுறையில் உள்ள திட்ட விதிகளின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Eligible beneficiaries can access covered healthcare services according to scheme rules.",

            tamilBenefits =
                "திட்ட விதிகளின்படி தகுதியான பயனாளிகள் உள்ளடக்கப்பட்ட சுகாதார சேவைகளைப் பெறலாம்.",

            documents =
                listOf(
                    "Aadhaar or accepted identification",
                    "Beneficiary verification details"
                ),

            tamilDocuments =
                listOf(
                    "ஆதார் அல்லது ஏற்றுக்கொள்ளப்படும் அடையாள ஆவணம்",
                    "பயனாளி சரிபார்ப்பு விவரங்கள்"
                ),

            icon = Icons.Default.HealthAndSafety,
            officialUrl = "https://pmjay.gov.in/"
        ),


        // =====================================================
        // 6. KISAN MAAN DHAN
        // =====================================================

        GovernmentScheme(
            id = "kisan-maan-dhan",
            name = "Pradhan Mantri Kisan Maan Dhan Yojana",
            tamilName = "பிரதமர் கிசான் மான் தன் யோஜனா",
            category = SchemeCategory.AGRICULTURE,

            description =
                "Pension-related social security scheme for eligible small and marginal farmers.",

            tamilDescription =
                "தகுதியான சிறு மற்றும் குறு விவசாயிகளுக்கான ஓய்வூதிய சமூக பாதுகாப்புத் திட்டம்.",

            eligibility =
                "Eligibility depends on age, landholding and other applicable scheme conditions.",

            tamilEligibility =
                "வயது, நில அளவு மற்றும் பிற பொருந்தும் திட்ட நிபந்தனைகளின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Provides pension-related social security according to applicable scheme rules.",

            tamilBenefits =
                "திட்ட விதிகளின்படி ஓய்வூதிய சமூக பாதுகாப்பு நன்மைகள் வழங்கப்படுகின்றன.",

            documents =
                listOf(
                    "Aadhaar",
                    "Bank account details",
                    "Landholding details"
                ),

            tamilDocuments =
                listOf(
                    "ஆதார்",
                    "வங்கி கணக்கு விவரங்கள்",
                    "நில விவரங்கள்"
                ),

            icon = Icons.Default.Agriculture,
            officialUrl = "https://maandhan.in/"
        ),


        // =====================================================
        // STUDENT / EDUCATION SCHEMES
        // =====================================================

        // 7
        GovernmentScheme(
            id = "nsp",
            name = "National Scholarship Portal",
            tamilName = "தேசிய கல்வி உதவித்தொகை தளம்",
            category = SchemeCategory.EDUCATION,

            description =
                "National online platform for multiple government scholarship schemes.",

            tamilDescription =
                "பல அரசு கல்வி உதவித்தொகை திட்டங்களுக்கான தேசிய ஆன்லைன் தளம்.",

            eligibility =
                "Eligibility varies according to the individual scholarship scheme.",

            tamilEligibility =
                "ஒவ்வொரு கல்வி உதவித்தொகை திட்டத்திற்கும் தகுதி மாறுபடும்.",

            benefits =
                "Students can discover and apply for eligible scholarship schemes through the portal.",

            tamilBenefits =
                "மாணவர்கள் தங்களுக்கு பொருந்தும் கல்வி உதவித்தொகை திட்டங்களை கண்டறிந்து விண்ணப்பிக்கலாம்.",

            documents =
                listOf(
                    "Student identity details",
                    "Educational records",
                    "Bank account details"
                ),

            tamilDocuments =
                listOf(
                    "மாணவர் அடையாள விவரங்கள்",
                    "கல்வி ஆவணங்கள்",
                    "வங்கி கணக்கு விவரங்கள்"
                ),

            icon = Icons.Default.School,
            officialUrl = "https://scholarships.gov.in/"
        ),


        // 8
        GovernmentScheme(
            id = "pm-usp-csss",
            name = "PM-USP Central Sector Scholarship",
            tamilName = "PM-USP மத்திய துறை கல்வி உதவித்தொகை",
            category = SchemeCategory.EDUCATION,

            description =
                "Merit-cum-means scholarship support for eligible college and university students.",

            tamilDescription =
                "தகுதியான கல்லூரி மற்றும் பல்கலைக்கழக மாணவர்களுக்கான திறமை மற்றும் பொருளாதார அடிப்படையிலான உதவித்தொகை.",

            eligibility =
                "Eligibility includes academic performance, income and other applicable scheme conditions.",

            tamilEligibility =
                "கல்வி செயல்திறன், குடும்ப வருமானம் மற்றும் பிற திட்ட நிபந்தனைகளின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Financial assistance toward educational expenses according to scheme guidelines.",

            tamilBenefits =
                "திட்ட வழிகாட்டுதல்களின்படி கல்விச் செலவுகளுக்கு நிதி உதவி வழங்கப்படுகிறது.",

            documents =
                listOf(
                    "Class 12 marks",
                    "Income certificate",
                    "Bank account",
                    "Aadhaar"
                ),

            tamilDocuments =
                listOf(
                    "12ஆம் வகுப்பு மதிப்பெண்",
                    "வருமானச் சான்று",
                    "வங்கி கணக்கு",
                    "ஆதார்"
                ),

            icon = Icons.Default.School,
            officialUrl = "https://scholarships.gov.in/"
        ),


        // 9
        GovernmentScheme(
            id = "nmmss",
            name = "National Means-cum-Merit Scholarship Scheme",
            tamilName = "தேசிய வருமானம் மற்றும் திறமை கல்வி உதவித்தொகை",
            category = SchemeCategory.EDUCATION,

            description =
                "Scholarship scheme supporting eligible students to continue secondary education.",

            tamilDescription =
                "தகுதியான மாணவர்கள் மேல்நிலை கல்வியைத் தொடர உதவும் கல்வி உதவித்தொகை திட்டம்.",

            eligibility =
                "Eligibility depends on the applicable examination, income and scheme criteria.",

            tamilEligibility =
                "தேர்வு, குடும்ப வருமானம் மற்றும் திட்ட தகுதி விதிகளின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Scholarship support for eligible students according to current guidelines.",

            tamilBenefits =
                "நடைமுறையில் உள்ள வழிகாட்டுதல்களின்படி தகுதியான மாணவர்களுக்கு கல்வி உதவி.",

            documents =
                listOf(
                    "Student records",
                    "Income certificate",
                    "Bank details"
                ),

            tamilDocuments =
                listOf(
                    "மாணவர் ஆவணங்கள்",
                    "வருமானச் சான்று",
                    "வங்கி விவரங்கள்"
                ),

            icon = Icons.Default.School,
            officialUrl = "https://scholarships.gov.in/"
        ),


        // 10
        GovernmentScheme(
            id = "pm-yasasvi-school",
            name = "PM YASASVI - Top Class Education in Schools",
            tamilName = "PM YASASVI - பள்ளி மாணவர்களுக்கான கல்வி உதவி",
            category = SchemeCategory.EDUCATION,

            description =
                "Education support scheme for eligible OBC, EBC and DNT students.",

            tamilDescription =
                "தகுதியான OBC, EBC மற்றும் DNT மாணவர்களுக்கான கல்வி உதவி திட்டம்.",

            eligibility =
                "Eligibility is subject to the applicable category, income and educational criteria.",

            tamilEligibility =
                "பொருந்தும் சமூகப் பிரிவு, வருமானம் மற்றும் கல்வித் தகுதி விதிகளுக்கு உட்பட்டது.",

            benefits =
                "Educational financial support according to the scheme guidelines.",

            tamilBenefits =
                "திட்ட வழிகாட்டுதல்களின்படி கல்விக்கான நிதி உதவி.",

            documents =
                listOf(
                    "Caste/category certificate",
                    "Income certificate",
                    "Student records",
                    "Bank details"
                ),

            tamilDocuments =
                listOf(
                    "சாதி/பிரிவு சான்று",
                    "வருமானச் சான்று",
                    "மாணவர் ஆவணங்கள்",
                    "வங்கி விவரங்கள்"
                ),

            icon = Icons.Default.School,
            officialUrl = "https://scholarships.gov.in/"
        ),


        // 11
        GovernmentScheme(
            id = "pm-yasasvi-college",
            name = "PM YASASVI - Top Class Education in College",
            tamilName = "PM YASASVI - கல்லூரி மாணவர்களுக்கான கல்வி உதவி",
            category = SchemeCategory.EDUCATION,

            description =
                "Higher education support for eligible OBC, EBC and DNT students.",

            tamilDescription =
                "தகுதியான OBC, EBC மற்றும் DNT மாணவர்களின் உயர்கல்விக்கான உதவி.",

            eligibility =
                "Eligibility depends on category, income, institution and other applicable criteria.",

            tamilEligibility =
                "சமூகப் பிரிவு, வருமானம், கல்வி நிறுவனம் மற்றும் பிற தகுதி விதிகளின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Financial support for eligible students pursuing higher education.",

            tamilBenefits =
                "உயர்கல்வி பயிலும் தகுதியான மாணவர்களுக்கு நிதி உதவி.",

            documents =
                listOf(
                    "Category certificate",
                    "Income certificate",
                    "Admission proof",
                    "Bank account"
                ),

            tamilDocuments =
                listOf(
                    "பிரிவு சான்று",
                    "வருமானச் சான்று",
                    "சேர்க்கைச் சான்று",
                    "வங்கி கணக்கு"
                ),

            icon = Icons.Default.School,
            officialUrl = "https://scholarships.gov.in/"
        ),


        // 12
        GovernmentScheme(
            id = "top-class-sc",
            name = "Top Class Education for SC Students",
            tamilName = "SC மாணவர்களுக்கான உயர்தர கல்வி உதவி",
            category = SchemeCategory.EDUCATION,

            description =
                "Higher education scholarship support for eligible SC students.",

            tamilDescription =
                "தகுதியான SC மாணவர்களுக்கான உயர்கல்வி உதவித்தொகை திட்டம்.",

            eligibility =
                "Eligibility depends on category, income, institution and scheme requirements.",

            tamilEligibility =
                "சமூகப் பிரிவு, வருமானம், கல்வி நிறுவனம் மற்றும் திட்ட விதிகளின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Scholarship support according to current scheme guidelines.",

            tamilBenefits =
                "நடைமுறையில் உள்ள திட்ட விதிகளின்படி கல்வி உதவி.",

            documents =
                listOf(
                    "SC certificate",
                    "Income certificate",
                    "Admission documents",
                    "Bank details"
                ),

            tamilDocuments =
                listOf(
                    "SC சான்றிதழ்",
                    "வருமானச் சான்று",
                    "சேர்க்கை ஆவணங்கள்",
                    "வங்கி விவரங்கள்"
                ),

            icon = Icons.Default.School,
            officialUrl = "https://scholarships.gov.in/"
        ),


        // 13
        GovernmentScheme(
            id = "top-class-st",
            name = "Top Class Education for ST Students",
            tamilName = "ST மாணவர்களுக்கான உயர்கல்வி உதவி",
            category = SchemeCategory.EDUCATION,

            description =
                "Higher education scholarship support for eligible ST students.",

            tamilDescription =
                "தகுதியான ST மாணவர்களுக்கான உயர்கல்வி கல்வி உதவி.",

            eligibility =
                "Eligibility is subject to applicable ST scholarship rules and institutional criteria.",

            tamilEligibility =
                "பொருந்தும் ST கல்வி உதவித்தொகை விதிகள் மற்றும் கல்வி நிறுவனத் தகுதிகளுக்கு உட்பட்டது.",

            benefits =
                "Financial scholarship support for eligible higher education students.",

            tamilBenefits =
                "தகுதியான உயர்கல்வி மாணவர்களுக்கு நிதி உதவி.",

            documents =
                listOf(
                    "ST certificate",
                    "Income certificate",
                    "Admission proof",
                    "Bank details"
                ),

            tamilDocuments =
                listOf(
                    "ST சான்றிதழ்",
                    "வருமானச் சான்று",
                    "சேர்க்கைச் சான்று",
                    "வங்கி விவரங்கள்"
                ),

            icon = Icons.Default.School,
            officialUrl = "https://scholarships.gov.in/"
        ),


        // 14
        GovernmentScheme(
            id = "disability-pre-matric",
            name = "Pre-Matric Scholarship for Students with Disabilities",
            tamilName = "மாற்றுத்திறனாளி மாணவர்களுக்கான முன்-மெட்ரிக் உதவித்தொகை",
            category = SchemeCategory.DISABILITY,

            description =
                "Scholarship support for eligible school students with disabilities.",

            tamilDescription =
                "தகுதியான மாற்றுத்திறனாளி பள்ளி மாணவர்களுக்கான கல்வி உதவித்தொகை.",

            eligibility =
                "Eligibility depends on disability certification and applicable scholarship criteria.",

            tamilEligibility =
                "மாற்றுத்திறனாளி சான்றிதழ் மற்றும் பொருந்தும் கல்வி உதவித்தொகை விதிகளின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Educational scholarship support according to applicable guidelines.",

            tamilBenefits =
                "பொருந்தும் வழிகாட்டுதல்களின்படி கல்வி உதவி.",

            documents =
                listOf(
                    "UDID/disability certificate",
                    "Student records",
                    "Bank details"
                ),

            tamilDocuments =
                listOf(
                    "UDID/மாற்றுத்திறனாளி சான்றிதழ்",
                    "மாணவர் ஆவணங்கள்",
                    "வங்கி விவரங்கள்"
                ),

            icon = Icons.Default.Accessibility,
            officialUrl = "https://scholarships.gov.in/"
        ),


        // 15
        GovernmentScheme(
            id = "disability-post-matric",
            name = "Post-Matric Scholarship for Students with Disabilities",
            tamilName = "மாற்றுத்திறனாளி மாணவர்களுக்கான பிந்தைய மெட்ரிக் உதவித்தொகை",
            category = SchemeCategory.DISABILITY,

            description =
                "Post-secondary scholarship support for eligible students with disabilities.",

            tamilDescription =
                "தகுதியான மாற்றுத்திறனாளி மாணவர்களின் உயர்கல்விக்கான கல்வி உதவி.",

            eligibility =
                "Eligibility is subject to disability certification and applicable education criteria.",

            tamilEligibility =
                "மாற்றுத்திறனாளி சான்றிதழ் மற்றும் கல்வித் தகுதி விதிகளுக்கு உட்பட்டது.",

            benefits =
                "Scholarship assistance according to current scheme rules.",

            tamilBenefits =
                "நடைமுறையில் உள்ள திட்ட விதிகளின்படி கல்வி உதவி.",

            documents =
                listOf(
                    "UDID/disability certificate",
                    "Admission proof",
                    "Income certificate",
                    "Bank account"
                ),

            tamilDocuments =
                listOf(
                    "UDID/மாற்றுத்திறனாளி சான்றிதழ்",
                    "சேர்க்கைச் சான்று",
                    "வருமானச் சான்று",
                    "வங்கி கணக்கு"
                ),

            icon = Icons.Default.Accessibility,
            officialUrl = "https://scholarships.gov.in/"
        ),


        // 16
        GovernmentScheme(
            id = "aicte-swanath",
            name = "AICTE Swanath Scholarship",
            tamilName = "AICTE ஸ்வநாத் கல்வி உதவித்தொகை",
            category = SchemeCategory.EDUCATION,

            description =
                "Scholarship support for eligible students pursuing technical education under the scheme criteria.",

            tamilDescription =
                "திட்ட தகுதி விதிகளின்படி தொழில்நுட்ப கல்வி பயிலும் தகுதியான மாணவர்களுக்கான உதவித்தொகை.",

            eligibility =
                "Eligibility depends on the applicable AICTE scheme conditions.",

            tamilEligibility =
                "பொருந்தும் AICTE திட்ட நிபந்தனைகளின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Financial support toward technical education according to scheme rules.",

            tamilBenefits =
                "திட்ட விதிகளின்படி தொழில்நுட்ப கல்விக்கான நிதி உதவி.",

            documents =
                listOf(
                    "Admission proof",
                    "Student identity",
                    "Bank details",
                    "Supporting documents"
                ),

            tamilDocuments =
                listOf(
                    "சேர்க்கைச் சான்று",
                    "மாணவர் அடையாளம்",
                    "வங்கி விவரங்கள்",
                    "ஆதார ஆவணங்கள்"
                ),

            icon = Icons.Default.School,
            officialUrl = "https://scholarships.gov.in/"
        ),


        // 17
        GovernmentScheme(
            id = "pm-scholarship-capf",
            name = "PM Scholarship Scheme for CAPF & Assam Rifles",
            tamilName = "CAPF மற்றும் Assam Rifles வாரிசுகளுக்கான PM கல்வி உதவித்தொகை",
            category = SchemeCategory.EDUCATION,

            description =
                "Merit-based scholarship scheme for eligible wards under the applicable CAPF and Assam Rifles criteria.",

            tamilDescription =
                "பொருந்தும் CAPF மற்றும் Assam Rifles விதிகளின் கீழ் தகுதியான வாரிசுகளுக்கான திறமை அடிப்படையிலான கல்வி உதவித்தொகை.",

            eligibility =
                "Eligibility is restricted to categories specified under the scheme.",

            tamilEligibility =
                "திட்டத்தில் குறிப்பிடப்பட்ட தகுதி பிரிவுகளுக்கு உட்பட்ட மாணவர்களுக்கு மட்டுமே.",

            benefits =
                "Scholarship assistance according to current scheme guidelines.",

            tamilBenefits =
                "நடைமுறையில் உள்ள திட்ட விதிகளின்படி கல்வி உதவி.",

            documents =
                listOf(
                    "Service-related documents",
                    "Student records",
                    "Bank details"
                ),

            tamilDocuments =
                listOf(
                    "பணி தொடர்பான ஆவணங்கள்",
                    "மாணவர் ஆவணங்கள்",
                    "வங்கி விவரங்கள்"
                ),

            icon = Icons.Default.School,
            officialUrl = "https://scholarships.gov.in/"
        ),


        // 18
        GovernmentScheme(
            id = "pm-scholarship-police",
            name = "PM Scholarship for Wards of Police Personnel",
            tamilName = "காவல்துறை பணியாளர்களின் வாரிசுகளுக்கான PM கல்வி உதவித்தொகை",
            category = SchemeCategory.EDUCATION,

            description =
                "Scholarship support for eligible wards under the applicable police personnel scheme.",

            tamilDescription =
                "பொருந்தும் காவல்துறை பணியாளர் திட்டத்தின் கீழ் தகுதியான வாரிசுகளுக்கான கல்வி உதவி.",

            eligibility =
                "Eligibility depends on the specific police personnel category and scheme rules.",

            tamilEligibility =
                "காவல்துறை பணியாளர் வகை மற்றும் திட்ட விதிகளின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Merit-based scholarship support according to scheme guidelines.",

            tamilBenefits =
                "திட்ட வழிகாட்டுதல்களின்படி திறமை அடிப்படையிலான கல்வி உதவி.",

            documents =
                listOf(
                    "Service documents",
                    "Student records",
                    "Bank details"
                ),

            tamilDocuments =
                listOf(
                    "பணி ஆவணங்கள்",
                    "மாணவர் ஆவணங்கள்",
                    "வங்கி விவரங்கள்"
                ),

            icon = Icons.Default.School,
            officialUrl = "https://scholarships.gov.in/"
        ),


        // 19
        GovernmentScheme(
            id = "pm-railway-scholarship",
            name = "PM Scholarship Scheme for Ministry of Railways",
            tamilName = "ரயில்வே பணியாளர்களின் வாரிசுகளுக்கான கல்வி உதவித்தொகை",
            category = SchemeCategory.EDUCATION,

            description =
                "Scholarship scheme for eligible wards under Ministry of Railways criteria.",

            tamilDescription =
                "ரயில்வே அமைச்சகத்தின் தகுதி விதிகளின் கீழ் தகுதியான வாரிசுகளுக்கான கல்வி உதவித்தொகை.",

            eligibility =
                "Eligibility depends on the applicable railway employee category and scheme criteria.",

            tamilEligibility =
                "பொருந்தும் ரயில்வே பணியாளர் வகை மற்றும் திட்ட தகுதி விதிகளின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Scholarship support according to current scheme guidelines.",

            tamilBenefits =
                "நடைமுறையில் உள்ள திட்ட விதிகளின்படி கல்வி உதவி.",

            documents =
                listOf(
                    "Railway service documents",
                    "Student records",
                    "Bank details"
                ),

            tamilDocuments =
                listOf(
                    "ரயில்வே பணி ஆவணங்கள்",
                    "மாணவர் ஆவணங்கள்",
                    "வங்கி விவரங்கள்"
                ),

            icon = Icons.Default.School,
            officialUrl = "https://scholarships.gov.in/"
        ),


        // 20
        GovernmentScheme(
            id = "national-fellowship-st",
            name = "National Fellowship for ST Students",
            tamilName = "ST மாணவர்களுக்கான தேசிய பெல்லோஷிப்",
            category = SchemeCategory.EDUCATION,

            description =
                "Higher education fellowship support for eligible ST research scholars.",

            tamilDescription =
                "தகுதியான ST ஆராய்ச்சி மாணவர்களுக்கான உயர்கல்வி பெல்லோஷிப் உதவி.",

            eligibility =
                "Eligibility is subject to the applicable fellowship and academic criteria.",

            tamilEligibility =
                "பொருந்தும் பெல்லோஷிப் மற்றும் கல்வித் தகுதி விதிகளுக்கு உட்பட்டது.",

            benefits =
                "Fellowship support for eligible higher education and research.",

            tamilBenefits =
                "தகுதியான உயர்கல்வி மற்றும் ஆராய்ச்சிக்கான பெல்லோஷிப் உதவி.",

            documents =
                listOf(
                    "ST certificate",
                    "Academic records",
                    "Admission/research documents"
                ),

            tamilDocuments =
                listOf(
                    "ST சான்றிதழ்",
                    "கல்வி ஆவணங்கள்",
                    "சேர்க்கை/ஆராய்ச்சி ஆவணங்கள்"
                ),

            icon = Icons.Default.School,
            officialUrl = "https://scholarships.gov.in/"
        ),


        // 21
        GovernmentScheme(
            id = "free-coaching",
            name = "Free Coaching Scheme",
            tamilName = "இலவச பயிற்சி திட்டம்",
            category = SchemeCategory.EDUCATION,

            description =
                "Coaching support for eligible students and beneficiaries preparing for competitive examinations.",

            tamilDescription =
                "போட்டித் தேர்வுகளுக்குத் தயாராகும் தகுதியான மாணவர்கள் மற்றும் பயனாளிகளுக்கான பயிற்சி உதவி.",

            eligibility =
                "Eligibility depends on the applicable category and coaching scheme rules.",

            tamilEligibility =
                "பொருந்தும் சமூகப் பிரிவு மற்றும் பயிற்சி திட்ட விதிகளின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Access to eligible coaching support under the applicable programme.",

            tamilBenefits =
                "பொருந்தும் திட்டத்தின் கீழ் தகுதியான பயிற்சி உதவி.",

            documents =
                listOf(
                    "Category certificate where applicable",
                    "Student identity",
                    "Academic records"
                ),

            tamilDocuments =
                listOf(
                    "தேவைப்பட்டால் பிரிவு சான்றிதழ்",
                    "மாணவர் அடையாளம்",
                    "கல்வி ஆவணங்கள்"
                ),

            icon = Icons.Default.School,
            officialUrl = "https://scholarships.gov.in/"
        ),


        // =====================================================
        // 22. PMEGP
        // =====================================================

        GovernmentScheme(
            id = "pmegp",
            name = "Prime Minister's Employment Generation Programme",
            tamilName = "பிரதமர் வேலைவாய்ப்பு உருவாக்கத் திட்டம்",
            category = SchemeCategory.EMPLOYMENT,

            description =
                "Credit-linked support programme for eligible new micro enterprises.",

            tamilDescription =
                "தகுதியான புதிய குறு தொழில் நிறுவனங்களுக்கு கடன் இணைந்த ஆதரவு வழங்கும் திட்டம்.",

            eligibility =
                "Eligibility depends on programme rules, applicant category and project requirements.",

            tamilEligibility =
                "திட்ட விதிகள், விண்ணப்பதாரர் வகை மற்றும் திட்டத் தேவைகளின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Eligible entrepreneurs can seek support for qualifying new micro enterprises.",

            tamilBenefits =
                "தகுதியான தொழில்முனைவோர் குறு தொழில் தொடங்குவதற்கான ஆதரவைப் பெற விண்ணப்பிக்கலாம்.",

            documents =
                listOf(
                    "Identity proof",
                    "Address proof",
                    "Project/business details"
                ),

            tamilDocuments =
                listOf(
                    "அடையாளச் சான்று",
                    "முகவரிச் சான்று",
                    "திட்டம்/தொழில் விவரங்கள்"
                ),

            icon = Icons.Default.Work,
            officialUrl =
                "https://www.kviconline.gov.in/pmegpeportal/"
        ),


        // =====================================================
        // 23. MISSION SHAKTI
        // =====================================================

        GovernmentScheme(
            id = "mission-shakti",
            name = "Mission Shakti",
            tamilName = "மிஷன் சக்தி",
            category = SchemeCategory.WOMEN,

            description =
                "Government framework supporting women's safety, security and empowerment.",

            tamilDescription =
                "பெண்களின் பாதுகாப்பு, நலன் மற்றும் அதிகாரமளிப்பை ஆதரிக்கும் அரசு திட்ட அமைப்பு.",

            eligibility =
                "Eligibility depends on the individual service or programme component.",

            tamilEligibility =
                "குறிப்பிட்ட சேவை அல்லது திட்டப் பகுதியைப் பொறுத்து தகுதி மாறுபடும்.",

            benefits =
                "Access to applicable support services under relevant programme components.",

            tamilBenefits =
                "தொடர்புடைய திட்டப் பகுதிகளின் கீழ் கிடைக்கும் ஆதரவு சேவைகளுக்கான அணுகல்.",

            documents =
                listOf(
                    "Identity details",
                    "Address/contact details"
                ),

            tamilDocuments =
                listOf(
                    "அடையாள விவரங்கள்",
                    "முகவரி/தொடர்பு விவரங்கள்"
                ),

            icon = Icons.Default.ChildCare,
            officialUrl = "https://missionshakti.wcd.gov.in/"
        ),


        // =====================================================
        // 24. SENIOR CITIZEN HEALTH
        // =====================================================

        GovernmentScheme(
            id = "nphce",
            name = "National Programme for Health Care of Elderly",
            tamilName = "மூத்த குடிமக்களுக்கான தேசிய சுகாதாரத் திட்டம்",
            category = SchemeCategory.SENIOR,

            description =
                "Government health programme focused on healthcare services for elderly persons.",

            tamilDescription =
                "மூத்த குடிமக்களுக்கான சுகாதார சேவைகளை ஆதரிக்கும் அரசு திட்டம்.",

            eligibility =
                "Services depend on programme availability and applicable healthcare facility guidelines.",

            tamilEligibility =
                "திட்டம் கிடைக்கும் இடம் மற்றும் நடைமுறையில் உள்ள சுகாதார விதிமுறைகளின் அடிப்படையில் சேவை கிடைக்கும்.",

            benefits =
                "Access to applicable elderly healthcare services.",

            tamilBenefits =
                "மூத்த குடிமக்களுக்கான பொருந்தும் சுகாதார சேவைகளுக்கான அணுகல்.",

            documents =
                listOf(
                    "Age/identity proof",
                    "Health records where required"
                ),

            tamilDocuments =
                listOf(
                    "வயது/அடையாளச் சான்று",
                    "தேவைப்பட்டால் மருத்துவ ஆவணங்கள்"
                ),

            icon = Icons.Default.People,
            officialUrl =
                "https://dghs.mohfw.gov.in/national-programme-for-the-health-care-of-the-elderly.php"
        ),


        // =====================================================
        // 25. UDID
        // =====================================================

        GovernmentScheme(
            id = "udid",
            name = "UDID - Unique Disability ID",
            tamilName = "UDID - மாற்றுத்திறனாளி அடையாள அட்டை",
            category = SchemeCategory.DISABILITY,

            description =
                "National system for disability identity documentation and related services.",

            tamilDescription =
                "மாற்றுத்திறனாளிகளுக்கான அடையாள ஆவணம் மற்றும் தொடர்புடைய சேவைகளுக்கான தேசிய அமைப்பு.",

            eligibility =
                "Persons with disabilities who meet applicable certification requirements.",

            tamilEligibility =
                "பொருந்தும் மாற்றுத்திறனாளி சான்றிதழ் விதிமுறைகளை பூர்த்தி செய்யும் நபர்கள்.",

            benefits =
                "Provides a standardized disability identity document for eligible persons.",

            tamilBenefits =
                "தகுதியான நபர்களுக்கு ஒருங்கிணைந்த மாற்றுத்திறனாளி அடையாள ஆவணம் வழங்கப்படுகிறது.",

            documents =
                listOf(
                    "Identity proof",
                    "Address details",
                    "Disability certification"
                ),

            tamilDocuments =
                listOf(
                    "அடையாளச் சான்று",
                    "முகவரி விவரங்கள்",
                    "மாற்றுத்திறன் சான்றிதழ்"
                ),

            icon = Icons.Default.Accessibility,
            officialUrl =
                "https://www.swavlambancard.gov.in/"
        ),


        // =====================================================
        // ADDITIONAL LIVELIHOOD / RURAL SCHEMES
        // =====================================================

        // 26
        GovernmentScheme(
            id = "pm-vishwakarma",
            name = "PM Vishwakarma",
            tamilName = "PM விஸ்வகர்மா",
            category = SchemeCategory.EMPLOYMENT,

            description =
                "Support programme for eligible traditional artisans and craftspeople.",

            tamilDescription =
                "தகுதியான பாரம்பரிய கைவினைஞர்கள் மற்றும் கைவினைத் தொழிலாளர்களுக்கான ஆதரவு திட்டம்.",

            eligibility =
                "Eligibility depends on the covered trade and applicable scheme conditions.",

            tamilEligibility =
                "திட்டத்தில் உள்ள தொழில் வகை மற்றும் பொருந்தும் விதிகளின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Support can include training, toolkit assistance, credit and other applicable benefits.",

            tamilBenefits =
                "பயிற்சி, கருவி உதவி, கடன் மற்றும் பிற பொருந்தும் நன்மைகள் வழங்கப்படலாம்.",

            documents =
                listOf(
                    "Aadhaar",
                    "Mobile number",
                    "Trade-related details"
                ),

            tamilDocuments =
                listOf(
                    "ஆதார்",
                    "மொபைல் எண்",
                    "தொழில் தொடர்பான விவரங்கள்"
                ),

            icon = Icons.Default.Work,
            officialUrl =
                "https://pmvishwakarma.gov.in/"
        ),


        // 27
        GovernmentScheme(
            id = "pm-svanidhi",
            name = "PM SVANidhi",
            tamilName = "PM ஸ்வநிதி",
            category = SchemeCategory.FINANCIAL,

            description =
                "Credit and support programme for eligible street vendors.",

            tamilDescription =
                "தகுதியான தெருவோர வியாபாரிகளுக்கான கடன் மற்றும் ஆதரவு திட்டம்.",

            eligibility =
                "Eligibility depends on vendor status and applicable scheme criteria.",

            tamilEligibility =
                "வியாபாரி தகுதி மற்றும் பொருந்தும் திட்ட விதிகளின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Eligible vendors can access scheme-linked credit and other support.",

            tamilBenefits =
                "தகுதியான வியாபாரிகள் திட்டத்துடன் இணைந்த கடன் மற்றும் பிற ஆதரவுகளைப் பெறலாம்.",

            documents =
                listOf(
                    "Vendor identification",
                    "Aadhaar",
                    "Bank account"
                ),

            tamilDocuments =
                listOf(
                    "வியாபாரி அடையாளம்",
                    "ஆதார்",
                    "வங்கி கணக்கு"
                ),

            icon = Icons.Default.Work,
            officialUrl =
                "https://www.myscheme.gov.in/schemes/pm-svanidhi"
        ),


        // 28
        GovernmentScheme(
            id = "stand-up-india",
            name = "Stand-Up India",
            tamilName = "ஸ்டாண்ட்-அப் இந்தியா",
            category = SchemeCategory.FINANCIAL,

            description =
                "Credit support framework for eligible SC/ST and women entrepreneurs.",

            tamilDescription =
                "தகுதியான SC/ST மற்றும் பெண்கள் தொழில்முனைவோருக்கான கடன் ஆதரவு திட்டம்.",

            eligibility =
                "Eligibility is subject to applicable borrower and enterprise conditions.",

            tamilEligibility =
                "பொருந்தும் கடனாளி மற்றும் தொழில் நிறுவன தகுதி விதிகளுக்கு உட்பட்டது.",

            benefits =
                "Eligible entrepreneurs can seek institutional credit for qualifying enterprises.",

            tamilBenefits =
                "தகுதியான தொழில்முனைவோர் பொருந்தும் தொழில்களுக்கு நிறுவன கடன் பெற விண்ணப்பிக்கலாம்.",

            documents =
                listOf(
                    "Identity proof",
                    "Business/project details",
                    "Bank-related documents"
                ),

            tamilDocuments =
                listOf(
                    "அடையாளச் சான்று",
                    "தொழில்/திட்ட விவரங்கள்",
                    "வங்கி ஆவணங்கள்"
                ),

            icon = Icons.Default.Work,
            officialUrl =
                "https://www.standupmitra.in/"
        ),


        // 29
        GovernmentScheme(
            id = "day-nrlm",
            name = "DAY-NRLM",
            tamilName = "தீன்தயாள் அந்த்யோதயா தேசிய கிராமப்புற வாழ்வாதார திட்டம்",
            category = SchemeCategory.EMPLOYMENT,

            description =
                "Rural livelihoods programme supporting eligible rural households and self-help groups.",

            tamilDescription =
                "தகுதியான கிராமப்புற குடும்பங்கள் மற்றும் சுயஉதவி குழுக்களின் வாழ்வாதாரத்தை ஆதரிக்கும் திட்டம்.",

            eligibility =
                "Eligibility and participation depend on applicable rural livelihood programme conditions.",

            tamilEligibility =
                "பொருந்தும் கிராமப்புற வாழ்வாதார திட்ட நிபந்தனைகளின் அடிப்படையில் பங்கேற்பு மற்றும் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Livelihood, self-help group and enterprise-related support may be available.",

            tamilBenefits =
                "வாழ்வாதாரம், சுயஉதவி குழு மற்றும் தொழில் தொடர்பான ஆதரவுகள் கிடைக்கலாம்.",

            documents =
                listOf(
                    "Identity details",
                    "Household details",
                    "SHG records where applicable"
                ),

            tamilDocuments =
                listOf(
                    "அடையாள விவரங்கள்",
                    "குடும்ப விவரங்கள்",
                    "தேவைப்பட்டால் SHG ஆவணங்கள்"
                ),

            icon = Icons.Default.People,
            officialUrl =
                "https://aajeevika.gov.in/"
        ),


        // 30
        GovernmentScheme(
            id = "pm-fme",
            name = "PM Formalisation of Micro Food Processing Enterprises",
            tamilName = "PM குறு உணவு பதப்படுத்தும் தொழில் திட்டம்",
            category = SchemeCategory.EMPLOYMENT,

            description =
                "Support programme for eligible micro food-processing enterprises.",

            tamilDescription =
                "தகுதியான குறு உணவு பதப்படுத்தும் தொழில் நிறுவனங்களுக்கான ஆதரவு திட்டம்.",

            eligibility =
                "Eligibility depends on enterprise type, project and applicable programme conditions.",

            tamilEligibility =
                "தொழில் வகை, திட்டம் மற்றும் பொருந்தும் திட்ட நிபந்தனைகளின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Eligible enterprises may receive applicable financial and business support.",

            tamilBenefits =
                "தகுதியான தொழில் நிறுவனங்களுக்கு பொருந்தும் நிதி மற்றும் தொழில் ஆதரவு கிடைக்கலாம்.",

            documents =
                listOf(
                    "Identity proof",
                    "Business details",
                    "Project documents"
                ),

            tamilDocuments =
                listOf(
                    "அடையாளச் சான்று",
                    "தொழில் விவரங்கள்",
                    "திட்ட ஆவணங்கள்"
                ),

            icon = Icons.Default.Work,
            officialUrl =
                "https://pmfme.mofpi.gov.in/"
        ),


        // 31
        GovernmentScheme(
            id = "skill-india",
            name = "Skill India",
            tamilName = "ஸ்கில் இந்தியா",
            category = SchemeCategory.EMPLOYMENT,

            description =
                "National skilling ecosystem providing access to skill development opportunities.",

            tamilDescription =
                "திறன் மேம்பாட்டு வாய்ப்புகளுக்கான தேசிய திறன் வளர்ப்பு அமைப்பு.",

            eligibility =
                "Eligibility varies by training programme and participating institution.",

            tamilEligibility =
                "பயிற்சி திட்டம் மற்றும் பங்கேற்கும் நிறுவனத்தைப் பொறுத்து தகுதி மாறுபடும்.",

            benefits =
                "Eligible learners can access relevant skill training opportunities.",

            tamilBenefits =
                "தகுதியானவர்கள் பொருந்தும் திறன் பயிற்சி வாய்ப்புகளைப் பெறலாம்.",

            documents =
                listOf(
                    "Identity proof",
                    "Educational details",
                    "Contact details"
                ),

            tamilDocuments =
                listOf(
                    "அடையாளச் சான்று",
                    "கல்வி விவரங்கள்",
                    "தொடர்பு விவரங்கள்"
                ),

            icon = Icons.Default.Work,
            officialUrl =
                "https://www.skillindiadigital.gov.in/"
        ),


        // 32
        GovernmentScheme(
            id = "national-apprenticeship",
            name = "National Apprenticeship Promotion Scheme",
            tamilName = "தேசிய தொழில்பயிற்சி ஊக்குவிப்பு திட்டம்",
            category = SchemeCategory.EMPLOYMENT,

            description =
                "Apprenticeship support programme connecting eligible learners with practical workplace training.",

            tamilDescription =
                "தகுதியான பயிற்சியாளர்களை நடைமுறை பணியிடப் பயிற்சியுடன் இணைக்கும் தொழில்பயிற்சி திட்டம்.",

            eligibility =
                "Eligibility depends on apprenticeship trade, age and programme requirements.",

            tamilEligibility =
                "தொழில்பயிற்சி வகை, வயது மற்றும் திட்டத் தேவைகளின் அடிப்படையில் தகுதி நிர்ணயிக்கப்படுகிறது.",

            benefits =
                "Eligible apprentices can receive structured workplace training under applicable rules.",

            tamilBenefits =
                "தகுதியான பயிற்சியாளர்கள் பொருந்தும் விதிகளின்படி பணியிடப் பயிற்சி பெறலாம்.",

            documents =
                listOf(
                    "Identity proof",
                    "Educational qualification",
                    "Bank details"
                ),

            tamilDocuments =
                listOf(
                    "அடையாளச் சான்று",
                    "கல்வித் தகுதி",
                    "வங்கி விவரங்கள்"
                ),

            icon = Icons.Default.Work,
            officialUrl =
                "https://www.apprenticeshipindia.gov.in/"
        )
    )
}