package com.gramainexus.smartvillage.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NaturePeople
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ======================================================
// COLORS
// ======================================================

private val SmartGreen = Color(0xFF2E7D32)
private val DarkGreen = Color(0xFF1B5E20)
private val DeepGreen = Color(0xFF103D20)

private val LightGreen = Color(0xFFE8F5E9)
private val VeryLightGreen = Color(0xFFF4FAF5)

private val BackgroundColor = Color(0xFFF7FAF7)
private val TextDark = Color(0xFF26352A)
private val TextGray = Color(0xFF6B756D)
private val BorderGreen = Color(0xFFC8E6C9)


// ======================================================
// VILLAGE MODEL
// ======================================================

private data class Village(
    val english: String,
    val tamil: String
)


// ======================================================
// VILLAGE LIST
// ======================================================

private val villages = listOf(

    // Original 6 villages
    Village(
        "Kaduvettankurichi",
        "காடுவெட்டாங்குறிச்சி"
    ),

    Village(
        "Melamaikelpatti",
        "மேலமைக்கல்பட்டி"
    ),

    Village(
        "Kottiyal",
        "கொட்டியல்"
    ),

    Village(
        "Solankurichi",
        "சோழன்குறிச்சி"
    ),

    Village(
        "Singarayapuram",
        "சிங்கராயபுரம்"
    ),

    Village(
        "Nayaganaipiriyal",
        "நாயகனைப்பிரியாள்"
    ),

    // Additional villages
    Village(
        "Alanganallur",
        "அலங்காநல்லூர்"
    ),

    Village(
        "Palamedu",
        "பாலமேடு"
    ),

    Village(
        "Paravai",
        "பரவை"
    ),

    Village(
        "Samayanallur",
        "சமயநல்லூர்"
    ),

    Village(
        "Othakadai",
        "ஒத்தக்கடை"
    ),

    Village(
        "Karuppayurani",
        "கருப்பாயூரணி"
    ),

    Village(
        "Thiruppalai",
        "திருப்பாலை"
    ),

    Village(
        "Kochadai",
        "கோச்சடை"
    ),

    Village(
        "Anaiyur",
        "ஆனையூர்"
    ),

    Village(
        "Avaniyapuram",
        "அவனியாபுரம்"
    ),

    Village(
        "Silaiman",
        "சிலைமான்"
    ),

    Village(
        "Vilangudi",
        "விளாங்குடி"
    ),

    Village(
        "Kalinjipatti",
        "களிஞ்சிப்பட்டி"
    ),

    Village(
        "Thirumangalam",
        "திருமங்கலம்"
    )
)


// ======================================================
// VILLAGE SELECTION SCREEN
// ======================================================

@Composable
fun VillageSelectionScreen(
    selectedLanguage: String,
    onContinue: (String) -> Unit
) {

    var selectedVillage by remember {
        mutableStateOf("")
    }

    var searchQuery by remember {
        mutableStateOf("")
    }

    // ==================================================
    // FILTER VILLAGES
    // ==================================================

    val filteredVillages = villages.filter { village ->

        if (searchQuery.isBlank()) {

            true

        } else {

            village.english.contains(
                searchQuery,
                ignoreCase = true
            ) ||
                    village.tamil.contains(
                        searchQuery,
                        ignoreCase = true
                    )
        }
    }

    // ==================================================
    // SELECTED VILLAGE OBJECT
    // ==================================================

    val selectedVillageObject = villages.find {

        it.english == selectedVillage ||
                it.tamil == selectedVillage
    }

    // ==================================================
    // MAIN SCREEN
    // ==================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 20.dp)
    ) {

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        // ==================================================
        // PREMIUM HEADER
        // ==================================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = DeepGreen
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 7.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 22.dp,
                        vertical = 23.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Header Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.13f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.NaturePeople,
                        contentDescription = "Village",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.height(13.dp)
                )

                // Title
                Text(
                    text = if (selectedLanguage == "Tamil") {
                        "உங்கள் கிராமத்தைத் தேர்ந்தெடுக்கவும்"
                    } else {
                        "Select Your Village"
                    },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                // Subtitle
                Text(
                    text = if (selectedLanguage == "Tamil") {
                        "உங்கள் உள்ளூர் சேவைகள் மற்றும் தகவல்களைப் பெறுங்கள்"
                    } else {
                        "Choose your village to access local services"
                    },
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.82f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        // ==================================================
        // SEARCH BAR
        // ==================================================

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            leadingIcon = {

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = SmartGreen
                )
            },
            trailingIcon = {

                if (searchQuery.isNotEmpty()) {

                    IconButton(
                        onClick = {
                            searchQuery = ""
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = TextGray
                        )
                    }
                }
            },
            placeholder = {

                Text(
                    text = if (selectedLanguage == "Tamil") {
                        "கிராமத்தைத் தேடுங்கள்..."
                    } else {
                        "Search village..."
                    },
                    color = TextGray,
                    fontSize = 14.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SmartGreen,
                unfocusedBorderColor = BorderGreen,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = SmartGreen
            )
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // ==================================================
        // SECTION HEADER
        // ==================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = if (selectedLanguage == "Tamil") {
                        "கிடைக்கக்கூடிய கிராமங்கள்"
                    } else {
                        "Available Villages"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = if (selectedLanguage == "Tamil") {
                        "உங்கள் கிராமத்தைத் தேர்ந்தெடுக்கவும்"
                    } else {
                        "Select one village to continue"
                    },
                    fontSize = 12.sp,
                    color = TextGray
                )
            }

            // ==================================================
            // VILLAGE COUNT
            // ==================================================

            Box(
                modifier = Modifier
                    .background(
                        color = LightGreen,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
            ) {

                Text(
                    text = "${filteredVillages.size}/20",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
            }
        }

        Spacer(
            modifier = Modifier.height(11.dp)
        )

        // ==================================================
        // VILLAGE LIST
        // ==================================================

        if (filteredVillages.isNotEmpty()) {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = filteredVillages,
                    key = {
                        it.english
                    }
                ) { village ->

                    val displayName =
                        if (selectedLanguage == "Tamil") {
                            village.tamil
                        } else {
                            village.english
                        }

                    VillageCard(
                        villageName = displayName,
                        selected =
                            selectedVillage == village.english ||
                                    selectedVillage == village.tamil,
                        onClick = {

                            selectedVillage =
                                if (selectedLanguage == "Tamil") {
                                    village.tamil
                                } else {
                                    village.english
                                }
                        }
                    )
                }
            }

        } else {

            // ==================================================
            // NO SEARCH RESULTS
            // ==================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(
                                color = LightGreen,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = SmartGreen,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Text(
                        text = if (selectedLanguage == "Tamil") {
                            "கிராமம் கிடைக்கவில்லை"
                        } else {
                            "Village not found"
                        },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = if (selectedLanguage == "Tamil") {
                            "வேறு பெயரைத் தேட முயற்சிக்கவும்"
                        } else {
                            "Try searching with another name"
                        },
                        fontSize = 13.sp,
                        color = TextGray
                    )
                }
            }
        }

        // ==================================================
        // SELECTED VILLAGE PREVIEW
        // ==================================================

        if (selectedVillageObject != null) {

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = LightGreen,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = SmartGreen,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(
                    modifier = Modifier.size(8.dp)
                )

                Column {

                    Text(
                        text = if (selectedLanguage == "Tamil") {
                            "தேர்ந்தெடுக்கப்பட்டது"
                        } else {
                            "Selected village"
                        },
                        fontSize = 11.sp,
                        color = TextGray
                    )

                    Text(
                        text = selectedVillage,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // ==================================================
        // CONTINUE BUTTON
        // ==================================================

        Button(
            onClick = {

                if (selectedVillage.isNotEmpty()) {
                    onContinue(selectedVillage)
                }
            },
            enabled = selectedVillage.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(57.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SmartGreen,
                disabledContainerColor = Color(0xFFD5DED6),
                contentColor = Color.White,
                disabledContentColor = Color(0xFF8A938B)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 1.dp
            )
        ) {

            Text(
                text = if (selectedLanguage == "Tamil") {
                    "தொடரவும்"
                } else {
                    "Continue"
                },
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(17.dp)
        )
    }
}


// ======================================================
// VILLAGE CARD
// ======================================================

@Composable
private fun VillageCard(
    villageName: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val cardColor = if (selected) {
        LightGreen
    } else {
        Color.White
    }

    val borderColor = if (selected) {
        SmartGreen
    } else {
        BorderGreen.copy(alpha = 0.55f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        border = BorderStroke(
            width = if (selected) {
                1.5.dp
            } else {
                1.dp
            },
            color = borderColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) {
                4.dp
            } else {
                1.dp
            }
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 13.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ==================================================
            // LOCATION ICON
            // ==================================================

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        if (selected) {
                            SmartGreen
                        } else {
                            VeryLightGreen
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Village",
                    tint = if (selected) {
                        Color.White
                    } else {
                        SmartGreen
                    },
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(
                modifier = Modifier.size(13.dp)
            )

            // ==================================================
            // VILLAGE NAME
            // ==================================================

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = villageName,
                    fontSize = 16.sp,
                    fontWeight = if (selected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    },
                    color = if (selected) {
                        DarkGreen
                    } else {
                        TextDark
                    }
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = if (selected) {
                        if (villageName.any { it.code > 127 }) {
                            "தேர்ந்தெடுக்கப்பட்டது"
                        } else {
                            "Selected village"
                        }
                    } else {
                        if (villageName.any { it.code > 127 }) {
                            "தேர்ந்தெடுக்க கிளிக் செய்யவும்"
                        } else {
                            "Tap to select"
                        }
                    },
                    fontSize = 11.sp,
                    color = if (selected) {
                        SmartGreen
                    } else {
                        TextGray
                    }
                )
            }

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            // ==================================================
            // CHECK INDICATOR
            // ==================================================

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = if (selected) {
                            SmartGreen
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                if (selected) {

                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}