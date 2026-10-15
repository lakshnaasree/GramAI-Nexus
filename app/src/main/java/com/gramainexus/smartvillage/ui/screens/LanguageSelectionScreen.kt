package com.gramainexus.smartvillage.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
private val TextGray = Color(0xFF68736B)
private val BorderGreen = Color(0xFFC8E6C9)


// ======================================================
// LANGUAGE SELECTION SCREEN
// ======================================================

@Composable
fun LanguageSelectionScreen(
    onContinue: (String) -> Unit
) {

    var selectedLanguage by remember {
        mutableStateOf("English")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 20.dp)
    ) {

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // ==================================================
        // PREMIUM TOP HEADER
        // ==================================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(
                containerColor = DeepGreen
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 22.dp,
                        vertical = 25.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Logo
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.13f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(66.dp)
                            .background(
                                color = Color.White,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = SmartGreen,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // App Name
                Text(
                    text = "GramAI Nexus",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "Smart Village AI Platform",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.78f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        // ==================================================
        // WELCOME TEXT
        // ==================================================

        Text(
            text = "Welcome!",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Choose your preferred language",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TextDark,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = "உங்கள் விருப்பமான மொழியைத் தேர்ந்தெடுக்கவும்",
            fontSize = 13.sp,
            color = TextGray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        // ==================================================
        // LANGUAGE SECTION LABEL
        // ==================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        color = LightGreen,
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = "Choose language",
                    tint = SmartGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(
                modifier = Modifier.size(10.dp)
            )

            Text(
                text = "Select Language",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        Spacer(
            modifier = Modifier.height(13.dp)
        )

        // ==================================================
        // TAMIL LANGUAGE
        // ==================================================

        LanguageCard(
            title = "தமிழ்",
            subtitle = "தமிழில் தொடரவும்",
            languageDescription = "Tamil",
            selected = selectedLanguage == "Tamil",
            onClick = {
                selectedLanguage = "Tamil"
            }
        )

        Spacer(
            modifier = Modifier.height(13.dp)
        )

        // ==================================================
        // ENGLISH LANGUAGE
        // ==================================================

        LanguageCard(
            title = "English",
            subtitle = "Continue in English",
            languageDescription = "English",
            selected = selectedLanguage == "English",
            onClick = {
                selectedLanguage = "English"
            }
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        // ==================================================
        // SELECTED LANGUAGE STATUS
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = LightGreen,
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(
                    horizontal = 14.dp,
                    vertical = 11.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = SmartGreen,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(
                modifier = Modifier.size(10.dp)
            )

            Column {

                Text(
                    text = "Selected language",
                    fontSize = 11.sp,
                    color = TextGray
                )

                Text(
                    text = if (selectedLanguage == "Tamil") {
                        "தமிழ்"
                    } else {
                        "English"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // ==================================================
        // CONTINUE BUTTON
        // ==================================================

        Button(
            onClick = {
                onContinue(selectedLanguage)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SmartGreen
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 5.dp,
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
            modifier = Modifier.height(13.dp)
        )

        // ==================================================
        // FOOTER
        // ==================================================

        Text(
            text = "🌱  GramAI Nexus  •  Smart Village",
            fontSize = 12.sp,
            color = TextGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )
    }
}


// ======================================================
// LANGUAGE CARD
// ======================================================

@Composable
private fun LanguageCard(
    title: String,
    subtitle: String,
    languageDescription: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor = if (selected) {
        LightGreen
    } else {
        Color.White
    }

    val borderColor = if (selected) {
        SmartGreen
    } else {
        BorderGreen.copy(alpha = 0.65f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) {
                1.7.dp
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
                    horizontal = 16.dp,
                    vertical = 15.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ==================================================
            // LANGUAGE ICON
            // ==================================================

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(
                        if (selected) {
                            SmartGreen
                        } else {
                            VeryLightGreen
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {

                if (languageDescription == "Tamil") {

                    Text(
                        text = "த",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) {
                            Color.White
                        } else {
                            SmartGreen
                        }
                    )

                } else {

                    Text(
                        text = "A",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) {
                            Color.White
                        } else {
                            SmartGreen
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.size(14.dp)
            )

            // ==================================================
            // LANGUAGE TEXT
            // ==================================================

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
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
                    text = subtitle,
                    fontSize = 13.sp,
                    color = if (selected) {
                        SmartGreen
                    } else {
                        TextGray
                    }
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = if (selected) {
                        "Selected"
                    } else {
                        "Tap to select"
                    },
                    fontSize = 10.sp,
                    fontWeight = if (selected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },
                    color = if (selected) {
                        SmartGreen
                    } else {
                        TextGray.copy(alpha = 0.8f)
                    }
                )
            }

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            // ==================================================
            // SELECTION CHECK
            // ==================================================

            Box(
                modifier = Modifier
                    .size(31.dp)
                    .background(
                        color = if (selected) {
                            SmartGreen
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape
                    )
                    .border(
                        width = if (selected) {
                            0.dp
                        } else {
                            1.7.dp
                        },
                        color = if (selected) {
                            Color.Transparent
                        } else {
                            Color(0xFFB7C2B8)
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
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}