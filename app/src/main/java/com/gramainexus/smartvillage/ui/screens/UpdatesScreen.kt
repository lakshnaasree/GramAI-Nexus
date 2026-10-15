package com.gramainexus.smartvillage.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/* =========================================================
   COLORS
========================================================= */

private val DashboardGreen = Color(0xFF176B3A)
private val DashboardDarkGreen = Color(0xFF0D4525)
private val DashboardLightGreen = Color(0xFFE8F5E9)
private val DashboardBackground = Color(0xFFF4F7F5)
private val DashboardText = Color(0xFF183126)
private val DashboardGray = Color(0xFF6B756F)
private val DashboardBorder = Color(0xFFE1E8E3)

private val GoldColor = Color(0xFFFFA000)
private val GoldBackground = Color(0xFFFFF4D6)

private val BlueColor = Color(0xFF1976D2)
private val RedColor = Color(0xFFD32F2F)
private val RedBackground = Color(0xFFFFEBEE)

private val PurpleColor = Color(0xFF7B1FA2)

/* =========================================================
   NEWS CATEGORY
========================================================= */

private enum class NewsCategory(
    val english: String,
    val tamil: String
) {
    ALL(
        english = "All",
        tamil = "அனைத்தும்"
    ),

    AGRICULTURE(
        english = "Agriculture",
        tamil = "விவசாயம்"
    ),

    TAMIL_NADU(
        english = "Tamil Nadu",
        tamil = "தமிழ்நாடு"
    ),

    INDIA(
        english = "India",
        tamil = "இந்தியா"
    ),

    WORLD(
        english = "World",
        tamil = "உலகம்"
    ),

    BUSINESS(
        english = "Business",
        tamil = "வணிகம்"
    )
}

/* =========================================================
   NEWS MODEL
========================================================= */

private data class NewsArticle(
    val title: String,
    val description: String,
    val source: String,
    val publishedAt: String,
    val publishedAtMillis: Long,
    val link: String,
    val category: NewsCategory
)

/* =========================================================
   MARKET DATA
========================================================= */

private data class MarketData(
    val gold24k: String,
    val goldUnit: String,
    val petrol: String,
    val diesel: String,
    val fuelUnit: String
)

private val currentMarketData = MarketData(
    gold24k = "₹1,54,430",
    goldUnit = "/ 10g",
    petrol = "₹107.77",
    diesel = "₹99.55",
    fuelUnit = "/ litre"
)

/* =========================================================
   NEWS RESULT
========================================================= */

private data class NewsResult(
    val articles: List<NewsArticle>,
    val error: String? = null
)

/* =========================================================
   NEWS FEEDS
========================================================= */

private object NewsFeeds {

    private const val BASE =
        "https://news.google.com/rss/search"

    fun agriculture(): String =
        "$BASE?q=India+agriculture+farmers+crops&hl=en-IN&gl=IN&ceid=IN:en"

    fun tamilNadu(): String =
        "$BASE?q=Tamil+Nadu+agriculture+farmers+government&hl=en-IN&gl=IN&ceid=IN:en"

    fun india(): String =
        "$BASE?q=India+latest+news&hl=en-IN&gl=IN&ceid=IN:en"

    fun world(): String =
        "$BASE?q=world+latest+news&hl=en-IN&gl=IN&ceid=IN:en"

    fun business(): String =
        "$BASE?q=India+business+markets+economy&hl=en-IN&gl=IN&ceid=IN:en"
}

/* =========================================================
   UPDATE SCREEN
========================================================= */

@Composable
fun UpdatesScreen(
    selectedLanguage: String,
    villageName: String,
    onBack: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isTamil = selectedLanguage.equals(
        "Tamil",
        ignoreCase = true
    )

    var selectedCategory by remember {
        mutableStateOf(NewsCategory.ALL)
    }

    var news by remember {
        mutableStateOf(emptyList<NewsArticle>())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var lastUpdated by remember {
        mutableStateOf("")
    }

    /*
     * Network loading is kept outside the UI drawing logic.
     * This avoids calling suspend functions from normal UI code.
     */
    suspend fun refreshNews() {

        isLoading = true
        errorMessage = null

        val result = withContext(Dispatchers.IO) {
            loadAllNews()
        }

        news = result.articles
        errorMessage = result.error

        lastUpdated = SimpleDateFormat(
            "hh:mm a",
            Locale.getDefault()
        ).format(Date())

        isLoading = false
    }

    /*
     * Initial network load.
     */
    LaunchedEffect(Unit) {
        refreshNews()
    }

    val filteredNews = remember(
        news,
        selectedCategory
    ) {

        if (selectedCategory == NewsCategory.ALL) {
            news
        } else {
            news.filter {
                it.category == selectedCategory
            }
        }
    }

    Scaffold(
        containerColor = DashboardBackground,

        topBar = {
            DashboardTopBar(
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
                top = 14.dp,
                end = 16.dp,
                bottom = 32.dp
            ),

            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            /* =================================================
               HERO
            ================================================= */

            item {

                IntelligenceHeader(
                    villageName = villageName,
                    isTamil = isTamil
                )
            }

            /* =================================================
               MARKET TITLE
            ================================================= */

            item {

                SectionTitle(
                    title = if (isTamil) {
                        "இன்றைய சந்தை நிலவரம்"
                    } else {
                        "Today's Market Snapshot"
                    }
                )
            }

            /* =================================================
               MARKET CARDS
            ================================================= */

            item {

                MarketSnapshotRow(
                    isTamil = isTamil
                )
            }

            /* =================================================
               AGRICULTURE WATCH
            ================================================= */

            item {

                AgricultureAlertCard(
                    isTamil = isTamil
                )
            }

            /* =================================================
               NEWS CATEGORY
            ================================================= */

            item {

                SectionTitle(
                    title = if (isTamil) {
                        "செய்தி வகைகள்"
                    } else {
                        "News Categories"
                    }
                )
            }

            item {

                NewsCategoryRow(
                    selectedCategory = selectedCategory,
                    isTamil = isTamil,
                    onCategorySelected = {
                        selectedCategory = it
                    }
                )
            }

            /* =================================================
               STATUS
            ================================================= */

            item {

                NewsStatusRow(
                    isTamil = isTamil,
                    isLoading = isLoading,
                    lastUpdated = lastUpdated,
                    onRefresh = {

                        if (!isLoading) {

                            scope.launch {
                                refreshNews()
                            }
                        }
                    }
                )
            }

            /* =================================================
               ERROR
            ================================================= */

            if (!isLoading && errorMessage != null && news.isEmpty()) {

                item {

                    ErrorNewsCard(
                        isTamil = isTamil,
                        onRetry = {

                            scope.launch {
                                refreshNews()
                            }
                        }
                    )
                }
            }

            /* =================================================
               LOADING
            ================================================= */

            if (isLoading && news.isEmpty()) {

                item {

                    LoadingNewsCard(
                        isTamil = isTamil
                    )
                }
            }

            /* =================================================
               NEWS HEADER
            ================================================= */

            if (filteredNews.isNotEmpty()) {

                item {

                    NewsSectionHeader(
                        isTamil = isTamil,
                        count = filteredNews.size
                    )
                }
            }

            /* =================================================
               REAL NEWS
            ================================================= */

            items(
                items = filteredNews,
                key = {
                    "${it.category.name}_${it.link}_${it.title}"
                }
            ) { article ->

                RealNewsCard(
                    article = article,
                    isTamil = isTamil,
                    onClick = {
                        openExternalUrl(
                            context = context,
                            url = article.link
                        )
                    }
                )
            }

            /* =================================================
               EMPTY
            ================================================= */

            if (!isLoading && filteredNews.isEmpty()) {

                item {

                    EmptyNewsCard(
                        isTamil = isTamil
                    )
                }
            }

            /* =================================================
               FOOTER
            ================================================= */

            item {

                NewsDisclaimer(
                    isTamil = isTamil
                )
            }
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

    Text(
        text = title,

        style = MaterialTheme
            .typography
            .titleMedium,

        fontWeight = FontWeight.ExtraBold,

        color = DashboardText
    )
}

/* =========================================================
   TOP BAR
========================================================= */

@Composable
private fun DashboardTopBar(
    isTamil: Boolean,
    onBack: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DashboardDarkGreen,
        shadowElevation = 3.dp
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

            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector =
                        Icons.AutoMirrored.Filled.ArrowBack,

                    contentDescription =
                        if (isTamil) {
                            "பின்"
                        } else {
                            "Back"
                        },

                    tint = Color.White
                )
            }

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = if (isTamil) {
                        "கிராம தகவல் மையம்"
                    } else {
                        "Village Intelligence"
                    },

                    color = Color.White,

                    style = MaterialTheme
                        .typography
                        .titleLarge,

                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = if (isTamil) {
                        "செய்தி • சந்தை • விவசாயம்"
                    } else {
                        "News • Markets • Agriculture"
                    },

                    color = Color.White.copy(alpha = 0.72f),

                    style = MaterialTheme
                        .typography
                        .labelSmall
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(alpha = 0.12f)
                    ),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

/* =========================================================
   HERO HEADER
========================================================= */

@Composable
private fun IntelligenceHeader(
    villageName: String,
    isTamil: Boolean
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(26.dp),

        colors = CardDefaults.cardColors(
            containerColor = DashboardDarkGreen
        )
    ) {

        Column(
            modifier = Modifier.padding(21.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(
                            RoundedCornerShape(17.dp)
                        )
                        .background(
                            Color.White.copy(alpha = 0.11f)
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Newspaper,

                        contentDescription = null,

                        tint = Color.White,

                        modifier = Modifier.size(29.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(13.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = if (isTamil) {
                            "இன்றைய முக்கிய தகவல்கள்"
                        } else {
                            "Today's Intelligence"
                        },

                        color = Color.White,

                        style = MaterialTheme
                            .typography
                            .titleMedium,

                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = if (isTamil) {
                            "உங்கள் கிராமத்திற்கும் விவசாயத்திற்கும் தேவையான தகவல்கள்"
                        } else {
                            "Important information for your village and farming"
                        },

                        color = Color.White.copy(alpha = 0.72f),

                        style = MaterialTheme
                            .typography
                            .bodySmall
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFFA5D6A7),
                    modifier = Modifier.size(17.dp)
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                Text(
                    text = villageName.ifBlank {
                        if (isTamil) {
                            "உங்கள் கிராமம்"
                        } else {
                            "Your village"
                        }
                    },

                    color = Color.White,

                    fontWeight = FontWeight.SemiBold,

                    style = MaterialTheme
                        .typography
                        .bodyMedium
                )
            }
        }
    }
}

/* =========================================================
   MARKET SNAPSHOT
========================================================= */

@Composable
private fun MarketSnapshotRow(
    isTamil: Boolean
) {

    Row(
        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        MarketCard(
            modifier = Modifier.weight(1f),

            icon = Icons.Default.AttachMoney,

            iconColor = GoldColor,

            iconBackground = GoldBackground,

            title = if (isTamil) {
                "தங்கம் 24K"
            } else {
                "Gold 24K"
            },

            value = currentMarketData.gold24k,

            subtitle = if (isTamil) {
                currentMarketData.goldUnit
                    .replace("10g", "10 கிராம்")
            } else {
                currentMarketData.goldUnit
            }
        )

        MarketCard(
            modifier = Modifier.weight(1f),

            icon = Icons.Default.LocalGasStation,

            iconColor = RedColor,

            iconBackground = RedBackground,

            title = if (isTamil) {
                "சென்னை பெட்ரோல்"
            } else {
                "Chennai Petrol"
            },

            value = currentMarketData.petrol,

            subtitle = if (isTamil) {
                "/ லிட்டர்"
            } else {
                currentMarketData.fuelUnit
            }
        )
    }
}

/* =========================================================
   MARKET CARD
========================================================= */

@Composable
private fun MarketCard(
    modifier: Modifier,
    icon: ImageVector,
    iconColor: Color,
    iconBackground: Color,
    title: String,
    value: String,
    subtitle: String
) {

    Card(
        modifier = modifier,

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        border = BorderStroke(
            width = 1.dp,
            color = DashboardBorder
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(iconBackground),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,

                    contentDescription = null,

                    tint = iconColor,

                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = title,

                color = DashboardGray,

                style = MaterialTheme
                    .typography
                    .labelSmall,

                maxLines = 1,

                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = value,

                color = DashboardText,

                style = MaterialTheme
                    .typography
                    .titleMedium,

                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = subtitle,

                color = DashboardGray,

                style = MaterialTheme
                    .typography
                    .labelSmall
            )
        }
    }
}

/* =========================================================
   AGRICULTURE WATCH
========================================================= */

@Composable
private fun AgricultureAlertCard(
    isTamil: Boolean
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = DashboardLightGreen
        )
    ) {

        Row(
            modifier = Modifier.padding(17.dp),

            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color.White),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Agriculture,

                    contentDescription = null,

                    tint = DashboardGreen,

                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = if (isTamil) {
                        "விவசாய கவனம்"
                    } else {
                        "Agriculture Watch"
                    },

                    color = DashboardDarkGreen,

                    style = MaterialTheme
                        .typography
                        .titleSmall,

                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = if (isTamil) {
                        "மழைப்பொழிவு, நீர் இருப்பு மற்றும் பருவ பயிர் நிலவரங்களை தொடர்ந்து கவனியுங்கள்."
                    } else {
                        "Keep an eye on rainfall, water availability and seasonal crop conditions."
                    },

                    color = DashboardText,

                    style = MaterialTheme
                        .typography
                        .bodySmall
                )
            }
        }
    }
}

/* =========================================================
   CATEGORY ROW
========================================================= */

@Composable
private fun NewsCategoryRow(
    selectedCategory: NewsCategory,
    isTamil: Boolean,
    onCategorySelected: (NewsCategory) -> Unit
) {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(
            items = NewsCategory.values(),
            key = {
                it.name
            }
        ) { category ->

            FilterChip(
                selected = selectedCategory == category,

                onClick = {
                    onCategorySelected(category)
                },

                label = {

                    Text(
                        text = if (isTamil) {
                            category.tamil
                        } else {
                            category.english
                        }
                    )
                },

                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DashboardGreen,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

/* =========================================================
   STATUS ROW
========================================================= */

@Composable
private fun NewsStatusRow(
    isTamil: Boolean,
    isLoading: Boolean,
    lastUpdated: String,
    onRefresh: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically
    ) {

        if (isLoading) {

            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),

                strokeWidth = 2.dp,

                color = DashboardGreen
            )

            Spacer(
                modifier = Modifier.width(7.dp)
            )
        }

        Text(
            text = when {

                isLoading -> {
                    if (isTamil) {
                        "செய்திகளை புதுப்பிக்கிறது..."
                    } else {
                        "Refreshing news..."
                    }
                }

                lastUpdated.isNotBlank() -> {
                    if (isTamil) {
                        "கடைசி புதுப்பிப்பு $lastUpdated"
                    } else {
                        "Updated $lastUpdated"
                    }
                }

                else -> {
                    if (isTamil) {
                        "சமீபத்திய தகவல்கள்"
                    } else {
                        "Latest available"
                    }
                }
            },

            modifier = Modifier.weight(1f),

            color = DashboardGray,

            style = MaterialTheme
                .typography
                .labelSmall
        )

        IconButton(
            onClick = onRefresh,
            enabled = !isLoading
        ) {

            Icon(
                imageVector = Icons.Default.Refresh,

                contentDescription = if (isTamil) {
                    "புதுப்பிக்கவும்"
                } else {
                    "Refresh"
                },

                tint = if (isLoading) {
                    DashboardGray
                } else {
                    DashboardGreen
                }
            )
        }
    }
}

/* =========================================================
   NEWS SECTION HEADER
========================================================= */

@Composable
private fun NewsSectionHeader(
    isTamil: Boolean,
    count: Int
) {

    Row(
        modifier = Modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = if (isTamil) {
                "சமீபத்திய செய்திகள்"
            } else {
                "Latest News"
            },

            modifier = Modifier.weight(1f),

            style = MaterialTheme
                .typography
                .titleMedium,

            fontWeight = FontWeight.ExtraBold,

            color = DashboardText
        )

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(10.dp)
                )
                .background(DashboardLightGreen)
                .padding(
                    horizontal = 9.dp,
                    vertical = 5.dp
                )
        ) {

            Text(
                text = count.toString(),

                color = DashboardGreen,

                fontWeight = FontWeight.Bold,

                style = MaterialTheme
                    .typography
                    .labelSmall
            )
        }
    }
}

/* =========================================================
   LOADING CARD
========================================================= */

@Composable
private fun LoadingNewsCard(
    isTamil: Boolean
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = if (isTamil) {
                    "செய்திகளை ஏற்றுகிறது..."
                } else {
                    "Loading latest news..."
                },

                fontWeight = FontWeight.Bold,

                color = DashboardText
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),

                color = DashboardGreen
            )
        }
    }
}

/* =========================================================
   ERROR CARD
========================================================= */

@Composable
private fun ErrorNewsCard(
    isTamil: Boolean,
    onRetry: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.ErrorOutline,

                contentDescription = null,

                tint = RedColor,

                modifier = Modifier.size(36.dp)
            )

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            Text(
                text = if (isTamil) {
                    "செய்திகளை பெற முடியவில்லை"
                } else {
                    "Couldn't load live news"
                },

                fontWeight = FontWeight.Bold,

                color = DashboardText
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = if (isTamil) {
                    "இணைய இணைப்பை சரிபார்த்து மீண்டும் முயற்சிக்கவும்."
                } else {
                    "Check your internet connection and try again."
                },

                color = DashboardGray,

                style = MaterialTheme
                    .typography
                    .bodySmall
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedButton(
                onClick = onRetry
            ) {

                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = if (isTamil) {
                        "மீண்டும் முயற்சி"
                    } else {
                        "Retry"
                    }
                )
            }
        }
    }
}

/* =========================================================
   REAL NEWS CARD
========================================================= */

@Composable
private fun RealNewsCard(
    article: NewsArticle,
    isTamil: Boolean,
    onClick: () -> Unit
) {

    val categoryColor = when (article.category) {

        NewsCategory.AGRICULTURE ->
            DashboardGreen

        NewsCategory.TAMIL_NADU ->
            PurpleColor

        NewsCategory.INDIA ->
            BlueColor

        NewsCategory.WORLD ->
            Color(0xFF455A64)

        NewsCategory.BUSINESS ->
            GoldColor

        NewsCategory.ALL ->
            DashboardGreen
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),

        shape = RoundedCornerShape(23.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        border = BorderStroke(
            width = 1.dp,
            color = DashboardBorder
        )
    ) {

        Column(
            modifier = Modifier.padding(17.dp)
        ) {

            Row(
                verticalAlignment = Alignment.Top
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(
                            RoundedCornerShape(15.dp)
                        )
                        .background(
                            categoryColor.copy(
                                alpha = 0.11f
                            )
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = categoryIcon(
                            article.category
                        ),

                        contentDescription = null,

                        tint = categoryColor,

                        modifier = Modifier.size(25.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = if (isTamil) {
                            article.category.tamil
                        } else {
                            article.category.english
                        },

                        color = categoryColor,

                        style = MaterialTheme
                            .typography
                            .labelSmall,

                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = article.title,

                        color = DashboardText,

                        style = MaterialTheme
                            .typography
                            .titleMedium,

                        fontWeight = FontWeight.Bold,

                        maxLines = 3,

                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    imageVector =
                        Icons.AutoMirrored.Filled.OpenInNew,

                    contentDescription =
                        if (isTamil) {
                            "செய்தியை திறக்கவும்"
                        } else {
                            "Open article"
                        },

                    tint = DashboardGray,

                    modifier = Modifier.size(19.dp)
                )
            }

            val cleanedDescription =
                cleanDescription(
                    article.description
                )

            if (cleanedDescription.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = cleanedDescription,

                    color = DashboardGray,

                    style = MaterialTheme
                        .typography
                        .bodySmall,

                    maxLines = 3,

                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DashboardBorder)
            )

            Spacer(
                modifier = Modifier.height(11.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = article.source,

                    modifier = Modifier.weight(1f),

                    color = DashboardText,

                    style = MaterialTheme
                        .typography
                        .labelSmall,

                    fontWeight = FontWeight.SemiBold,

                    maxLines = 1,

                    overflow = TextOverflow.Ellipsis
                )

                Icon(
                    imageVector = Icons.Default.AccessTime,

                    contentDescription = null,

                    tint = DashboardGray,

                    modifier = Modifier.size(14.dp)
                )

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Text(
                    text = article.publishedAt,

                    color = DashboardGray,

                    style = MaterialTheme
                        .typography
                        .labelSmall
                )
            }
        }
    }
}

/* =========================================================
   EMPTY CARD
========================================================= */

@Composable
private fun EmptyNewsCard(
    isTamil: Boolean
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.Newspaper,

                contentDescription = null,

                tint = DashboardGray,

                modifier = Modifier.size(40.dp)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = if (isTamil) {
                    "இந்த வகையில் செய்திகள் இல்லை"
                } else {
                    "No news found in this category"
                },

                fontWeight = FontWeight.Bold,

                color = DashboardText
            )
        }
    }
}

/* =========================================================
   DISCLAIMER
========================================================= */

@Composable
private fun NewsDisclaimer(
    isTamil: Boolean
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F3F1)
        )
    ) {

        Row(
            modifier = Modifier.padding(14.dp),

            verticalAlignment = Alignment.Top
        ) {

            Icon(
                imageVector = Icons.Default.Newspaper,

                contentDescription = null,

                tint = DashboardGray,

                modifier = Modifier.size(18.dp)
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = if (isTamil) {
                    "செய்திகள் வெளிப்புற RSS மூலங்களிலிருந்து பெறப்படுகின்றன. முக்கியமான தகவல்களுக்கு மூலச் செய்தியை சரிபார்க்கவும்."
                } else {
                    "News is collected from external RSS sources. Verify important information with the original source."
                },

                color = DashboardGray,

                style = MaterialTheme
                    .typography
                    .labelSmall
            )
        }
    }
}

/* =========================================================
   CATEGORY ICON
========================================================= */

private fun categoryIcon(
    category: NewsCategory
): ImageVector {

    return when (category) {

        NewsCategory.AGRICULTURE ->
            Icons.Default.Agriculture

        NewsCategory.TAMIL_NADU ->
            Icons.Default.AccountBalance

        NewsCategory.INDIA ->
            Icons.Default.AccountBalance

        NewsCategory.WORLD ->
            Icons.Default.Language

        NewsCategory.BUSINESS ->
            Icons.AutoMirrored.Filled.ShowChart

        NewsCategory.ALL ->
            Icons.Default.Newspaper
    }
}

/* =========================================================
   LOAD ALL NEWS
========================================================= */

private suspend fun loadAllNews(): NewsResult {

    return try {

        val agriculture = withContext(Dispatchers.IO) {
            fetchRss(
                feedUrl = NewsFeeds.agriculture(),
                category = NewsCategory.AGRICULTURE
            )
        }

        val tamilNadu = withContext(Dispatchers.IO) {
            fetchRss(
                feedUrl = NewsFeeds.tamilNadu(),
                category = NewsCategory.TAMIL_NADU
            )
        }

        val india = withContext(Dispatchers.IO) {
            fetchRss(
                feedUrl = NewsFeeds.india(),
                category = NewsCategory.INDIA
            )
        }

        val world = withContext(Dispatchers.IO) {
            fetchRss(
                feedUrl = NewsFeeds.world(),
                category = NewsCategory.WORLD
            )
        }

        val business = withContext(Dispatchers.IO) {
            fetchRss(
                feedUrl = NewsFeeds.business(),
                category = NewsCategory.BUSINESS
            )
        }

        val combined = (
                agriculture +
                        tamilNadu +
                        india +
                        world +
                        business
                )
            .distinctBy { article ->
                article.link.ifBlank {
                    article.title
                }
            }
            .sortedByDescending {
                it.publishedAtMillis
            }
            .take(40)

        if (combined.isEmpty()) {

            NewsResult(
                articles = emptyList(),
                error = "No articles available"
            )

        } else {

            NewsResult(
                articles = combined
            )
        }

    } catch (exception: Exception) {

        NewsResult(
            articles = emptyList(),
            error = exception.message ?: "Unable to load news"
        )
    }
}

/* =========================================================
   FETCH RSS
========================================================= */

private fun fetchRss(
    feedUrl: String,
    category: NewsCategory
): List<NewsArticle> {

    var connection: HttpURLConnection? = null

    return try {

        connection =
            URL(feedUrl)
                .openConnection() as HttpURLConnection

        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        connection.requestMethod = "GET"

        connection.setRequestProperty(
            "User-Agent",
            "SmartVillage/1.0 Android"
        )

        connection.setRequestProperty(
            "Accept",
            "application/rss+xml, application/xml, text/xml"
        )

        connection.connect()

        if (connection.responseCode !in 200..299) {
            return emptyList()
        }

        connection.inputStream.use { inputStream ->

            val factory =
                XmlPullParserFactory.newInstance()

            factory.isNamespaceAware = false

            val parser =
                factory.newPullParser()

            parser.setInput(
                inputStream,
                null
            )

            val articles =
                mutableListOf<NewsArticle>()

            var eventType = parser.eventType

            var insideItem = false

            var currentTag = ""

            var title = ""
            var description = ""
            var link = ""
            var source = ""
            var pubDate = ""

            while (
                eventType !=
                XmlPullParser.END_DOCUMENT
            ) {

                when (eventType) {

                    XmlPullParser.START_TAG -> {

                        val tag =
                            parser.name.orEmpty()

                        if (
                            tag.equals(
                                "item",
                                ignoreCase = true
                            )
                        ) {

                            insideItem = true

                            title = ""
                            description = ""
                            link = ""
                            source = ""
                            pubDate = ""
                        }

                        currentTag = tag
                    }

                    XmlPullParser.TEXT -> {

                        if (insideItem) {

                            val text =
                                parser.text
                                    ?.trim()
                                    .orEmpty()

                            if (text.isNotBlank()) {

                                when {

                                    currentTag.equals(
                                        "title",
                                        ignoreCase = true
                                    ) -> {
                                        title += text
                                    }

                                    currentTag.equals(
                                        "description",
                                        ignoreCase = true
                                    ) -> {
                                        description += text
                                    }

                                    currentTag.equals(
                                        "link",
                                        ignoreCase = true
                                    ) -> {
                                        link += text
                                    }

                                    currentTag.equals(
                                        "pubDate",
                                        ignoreCase = true
                                    ) -> {
                                        pubDate += text
                                    }

                                    currentTag.equals(
                                        "source",
                                        ignoreCase = true
                                    ) -> {
                                        source += text
                                    }
                                }
                            }
                        }
                    }

                    XmlPullParser.END_TAG -> {

                        val tag =
                            parser.name.orEmpty()

                        if (
                            tag.equals(
                                "item",
                                ignoreCase = true
                            )
                        ) {

                            if (title.isNotBlank()) {

                                val dateInfo =
                                    parsePublishedDate(
                                        pubDate
                                    )

                                articles.add(
                                    NewsArticle(
                                        title = cleanDescription(
                                            title
                                        ),

                                        description =
                                            cleanDescription(
                                                description
                                            ),

                                        source =
                                            source
                                                .trim()
                                                .ifBlank {
                                                    extractSource(
                                                        link
                                                    )
                                                },

                                        publishedAt =
                                            dateInfo.first,

                                        publishedAtMillis =
                                            dateInfo.second,

                                        link =
                                            link.trim(),

                                        category =
                                            category
                                    )
                                )
                            }

                            insideItem = false
                        }

                        currentTag = ""
                    }
                }

                eventType = parser.next()
            }

            articles
                .distinctBy {
                    it.link.ifBlank {
                        it.title
                    }
                }
                .take(10)
        }

    } catch (
        exception: Exception
    ) {

        emptyList()

    } finally {

        connection?.disconnect()
    }
}

/* =========================================================
   PARSE DATE
========================================================= */

private fun parsePublishedDate(
    rawDate: String
): Pair<String, Long> {

    if (rawDate.isBlank()) {
        return "Recent" to 0L
    }

    return try {

        val parser =
            SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z",
                Locale.ENGLISH
            )

        val date =
            parser.parse(rawDate)

        if (date == null) {
            return "Recent" to 0L
        }

        val time =
            date.time

        val difference =
            System.currentTimeMillis() - time

        val minutes =
            difference / 60_000L

        val formatted =
            when {

                minutes < 60L ->
                    "${minutes.coerceAtLeast(1L)}m"

                minutes < 1_440L ->
                    "${minutes / 60L}h"

                else ->
                    "${minutes / 1_440L}d"
            }

        formatted to time

    } catch (
        _: Exception
    ) {

        "Recent" to 0L
    }
}

/* =========================================================
   SOURCE EXTRACTION
========================================================= */

private fun extractSource(
    link: String
): String {

    if (link.isBlank()) {
        return "News"
    }

    return try {

        val host =
            URI(link)
                .host
                .orEmpty()
                .removePrefix("www.")

        if (host.isBlank()) {
            return "News"
        }

        host
            .substringBefore(".com")
            .substringBefore(".in")
            .substringBefore(".org")
            .substringBefore(".net")
            .replaceFirstChar {
                it.uppercase()
            }

    } catch (
        _: Exception
    ) {

        "News"
    }
}

/* =========================================================
   CLEAN DESCRIPTION
========================================================= */

private fun cleanDescription(
    text: String
): String {

    return text
        .replace(
            Regex("<[^>]*>"),
            " "
        )
        .replace(
            "&nbsp;",
            " "
        )
        .replace(
            "&amp;",
            "&"
        )
        .replace(
            "&quot;",
            "\""
        )
        .replace(
            "&#39;",
            "'"
        )
        .replace(
            "&apos;",
            "'"
        )
        .replace(
            Regex("\\s+"),
            " "
        )
        .trim()
}

/* =========================================================
   OPEN ARTICLE
========================================================= */

private fun openExternalUrl(
    context: Context,
    url: String
) {

    if (url.isBlank()) {
        return
    }

    try {

        val intent =
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )

        context.startActivity(intent)

    } catch (
        _: Exception
    ) {
        // No compatible browser available.
    }
}