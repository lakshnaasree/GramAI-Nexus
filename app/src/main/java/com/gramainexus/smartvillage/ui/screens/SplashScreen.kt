package com.gramainexus.smartvillage.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gramainexus.smartvillage.ui.theme.VillageGreen
import kotlinx.coroutines.delay
import kotlin.math.min

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {

    var showContent by remember {
        mutableStateOf(false)
    }

    val infiniteTransition = rememberInfiniteTransition(
        label = "splash_animation"
    )

    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1600,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    LaunchedEffect(Unit) {

        showContent = true

        delay(3000)

        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFBDE8D0),
                        Color(0xFFFFFBEA),
                        Color(0xFFFFF1C7)
                    )
                )
            )
    ) {

        // Complete village illustration
        VillageScene(
            modifier = Modifier.fillMaxSize()
        )

        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(
                tween(1000)
            ) + scaleIn(
                tween(1000)
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 45.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                // --------------------------------
                // LOGO
                // --------------------------------

                Box(
                    modifier = Modifier
                        .size(125.dp)
                        .scale(logoScale)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF075E42),
                                    Color(0xFF2E8B57),
                                    Color(0xFF4CAF50)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "G",
                            color = Color.White,
                            fontSize = 62.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "🌱",
                            fontSize = 21.sp,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                // --------------------------------
                // APP NAME
                // --------------------------------

                Text(
                    text = "GramAI Nexus",
                    color = Color(0xFF075E42),
                    fontSize = 33.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "Smart Solutions for Stronger Villages",
                    color = Color(0xFF245F49),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                // Decorative leaf line
                Text(
                    text = "━━━━  🌿  ━━━━",
                    color = Color(0xFF3A8C4A),
                    fontSize = 16.sp
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                // --------------------------------
                // LOADING
                // --------------------------------

                CircularProgressIndicator(
                    modifier = Modifier.size(35.dp),
                    color = Color.White,
                    strokeWidth = 4.dp
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "Connecting Villages...",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.height(15.dp)
                )
            }
        }
    }
}


// ============================================================
// COMPLETE VILLAGE SCENE
// ============================================================

@Composable
private fun VillageScene(
    modifier: Modifier = Modifier
) {

    Canvas(
        modifier = modifier
    ) {

        val w = size.width
        val h = size.height

        // ----------------------------------------------------
        // SKY
        // ----------------------------------------------------

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFA9DCC0),
                    Color(0xFFE8F5E9),
                    Color(0xFFFFF5D5)
                )
            )
        )

        // ----------------------------------------------------
        // SUN
        // ----------------------------------------------------

        drawCircle(
            color = Color(0xFFFFD95A),
            radius = min(w, h) * 0.065f,
            center = Offset(
                w * 0.50f,
                h * 0.63f
            )
        )

        // ----------------------------------------------------
        // CLOUDS
        // ----------------------------------------------------

        drawCloud(
            x = w * 0.14f,
            y = h * 0.27f,
            scale = 0.8f
        )

        drawCloud(
            x = w * 0.82f,
            y = h * 0.31f,
            scale = 0.65f
        )

        // ----------------------------------------------------
        // DISTANT MOUNTAINS
        // ----------------------------------------------------

        drawMountains(
            w = w,
            h = h
        )

        // ----------------------------------------------------
        // FAR GREENERY
        // ----------------------------------------------------

        drawTree(
            x = w * 0.08f,
            y = h * 0.68f,
            scale = 0.7f
        )

        drawTree(
            x = w * 0.91f,
            y = h * 0.68f,
            scale = 0.8f
        )

        // ----------------------------------------------------
        // VILLAGE HOUSES
        // ----------------------------------------------------

        drawHouse(
            x = w * 0.25f,
            y = h * 0.73f,
            scale = 0.75f
        )

        drawHouse(
            x = w * 0.40f,
            y = h * 0.70f,
            scale = 0.9f
        )

        drawHouse(
            x = w * 0.55f,
            y = h * 0.73f,
            scale = 0.72f
        )

        drawHouse(
            x = w * 0.69f,
            y = h * 0.71f,
            scale = 0.78f
        )

        // ----------------------------------------------------
        // POND
        // ----------------------------------------------------

        drawOval(
            color = Color(0xFF72C7D0),
            topLeft = Offset(
                w * 0.65f,
                h * 0.77f
            ),
            size = Size(
                w * 0.33f,
                h * 0.105f
            )
        )

        // Water reflections

        drawLine(
            color = Color.White.copy(alpha = 0.55f),
            start = Offset(
                w * 0.70f,
                h * 0.805f
            ),
            end = Offset(
                w * 0.84f,
                h * 0.805f
            ),
            strokeWidth = 4f
        )

        drawLine(
            color = Color.White.copy(alpha = 0.40f),
            start = Offset(
                w * 0.74f,
                h * 0.84f
            ),
            end = Offset(
                w * 0.90f,
                h * 0.84f
            ),
            strokeWidth = 3f
        )

        // ----------------------------------------------------
        // FARM LAND
        // ----------------------------------------------------

        drawRect(
            color = Color(0xFF76A942),
            topLeft = Offset(
                0f,
                h * 0.81f
            ),
            size = Size(
                w,
                h * 0.14f
            )
        )

        // Crop rows

        for (i in 0..7) {

            val x = w * (0.03f + i * 0.075f)

            drawLine(
                color = Color(0xFF4F8737),
                start = Offset(
                    x,
                    h * 0.82f
                ),
                end = Offset(
                    x + 25f,
                    h * 0.94f
                ),
                strokeWidth = 3f
            )
        }

        // ----------------------------------------------------
        // LARGE TREES
        // ----------------------------------------------------

        drawTree(
            x = w * 0.035f,
            y = h * 0.80f,
            scale = 1.35f
        )

        drawTree(
            x = w * 0.965f,
            y = h * 0.80f,
            scale = 1.35f
        )

        // ----------------------------------------------------
        // FARMER
        // ----------------------------------------------------

        drawFarmer(
            x = w * 0.18f,
            y = h * 0.72f,
            scale = 1.15f
        )

        // ----------------------------------------------------
        // FRONT GREEN WAVES
        // ----------------------------------------------------

        val firstWave = Path().apply {

            moveTo(
                0f,
                h * 0.89f
            )

            cubicTo(
                w * 0.20f,
                h * 0.84f,
                w * 0.38f,
                h * 0.94f,
                w * 0.56f,
                h * 0.89f
            )

            cubicTo(
                w * 0.76f,
                h * 0.84f,
                w * 0.88f,
                h * 0.94f,
                w,
                h * 0.88f
            )

            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(
            path = firstWave,
            color = Color(0xFF4CAF50)
        )

        val secondWave = Path().apply {

            moveTo(
                0f,
                h * 0.94f
            )

            cubicTo(
                w * 0.22f,
                h * 0.89f,
                w * 0.43f,
                h * 0.99f,
                w * 0.62f,
                h * 0.93f
            )

            cubicTo(
                w * 0.80f,
                h * 0.88f,
                w * 0.91f,
                h * 0.97f,
                w,
                h * 0.92f
            )

            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(
            path = secondWave,
            color = Color(0xFF075E42)
        )
    }
}


// ============================================================
// FARMER
// ============================================================

private fun DrawScope.drawFarmer(
    x: Float,
    y: Float,
    scale: Float
) {

    // --------------------------------------------------------
    // BODY / WHITE SHIRT
    // --------------------------------------------------------

    val bodyPath = Path().apply {

        moveTo(
            x - 32f * scale,
            y + 25f * scale
        )

        cubicTo(
            x - 48f * scale,
            y + 55f * scale,
            x - 43f * scale,
            y + 100f * scale,
            x - 40f * scale,
            y + 140f * scale
        )

        lineTo(
            x + 40f * scale,
            y + 140f * scale
        )

        cubicTo(
            x + 43f * scale,
            y + 100f * scale,
            x + 48f * scale,
            y + 55f * scale,
            x + 30f * scale,
            y + 25f * scale
        )

        close()
    }

    drawPath(
        path = bodyPath,
        color = Color(0xFFF5F2E9)
    )

    // --------------------------------------------------------
    // SHOULDER / SHIRT FOLDS
    // --------------------------------------------------------

    drawLine(
        color = Color(0xFFD5D0C4),
        start = Offset(
            x - 27f * scale,
            y + 38f * scale
        ),
        end = Offset(
            x - 35f * scale,
            y + 105f * scale
        ),
        strokeWidth = 4f * scale
    )

    drawLine(
        color = Color(0xFFD5D0C4),
        start = Offset(
            x + 27f * scale,
            y + 38f * scale
        ),
        end = Offset(
            x + 35f * scale,
            y + 105f * scale
        ),
        strokeWidth = 4f * scale
    )

    // --------------------------------------------------------
    // HEAD
    // --------------------------------------------------------

    drawCircle(
        color = Color(0xFF9B633E),
        radius = 20f * scale,
        center = Offset(
            x,
            y
        )
    )

    // --------------------------------------------------------
    // HAIR
    // --------------------------------------------------------

    drawCircle(
        color = Color(0xFF35251D),
        radius = 18f * scale,
        center = Offset(
            x,
            y - 8f * scale
        )
    )

    // Face overlay
    drawCircle(
        color = Color(0xFF9B633E),
        radius = 14f * scale,
        center = Offset(
            x + 2f * scale,
            y + 3f * scale
        )
    )

    // --------------------------------------------------------
    // EAR
    // --------------------------------------------------------

    drawCircle(
        color = Color(0xFF875331),
        radius = 5f * scale,
        center = Offset(
            x + 15f * scale,
            y + 5f * scale
        )
    )

    // --------------------------------------------------------
    // EYE
    // --------------------------------------------------------

    drawCircle(
        color = Color(0xFF241C18),
        radius = 1.8f * scale,
        center = Offset(
            x + 7f * scale,
            y + 1f * scale
        )
    )

    // --------------------------------------------------------
    // NECK
    // --------------------------------------------------------

    drawRect(
        color = Color(0xFF8B5636),
        topLeft = Offset(
            x - 8f * scale,
            y + 15f * scale
        ),
        size = Size(
            16f * scale,
            18f * scale
        )
    )

    // --------------------------------------------------------
    // BLUE/GREEN TOWEL
    // --------------------------------------------------------

    val towel = Path().apply {

        moveTo(
            x - 30f * scale,
            y + 30f * scale
        )

        lineTo(
            x - 48f * scale,
            y + 120f * scale
        )

        lineTo(
            x - 25f * scale,
            y + 135f * scale
        )

        lineTo(
            x - 10f * scale,
            y + 40f * scale
        )

        close()
    }

    drawPath(
        path = towel,
        color = Color(0xFF4F8C82)
    )

    // --------------------------------------------------------
    // ARM HOLDING PHONE
    // --------------------------------------------------------

    val armPath = Path().apply {

        moveTo(
            x + 25f * scale,
            y + 45f * scale
        )

        lineTo(
            x + 60f * scale,
            y + 80f * scale
        )

        lineTo(
            x + 50f * scale,
            y + 92f * scale
        )

        lineTo(
            x + 15f * scale,
            y + 60f * scale
        )

        close()
    }

    drawPath(
        path = armPath,
        color = Color(0xFF9B633E)
    )

    // --------------------------------------------------------
    // HAND
    // --------------------------------------------------------

    drawCircle(
        color = Color(0xFF9B633E),
        radius = 9f * scale,
        center = Offset(
            x + 55f * scale,
            y + 85f * scale
        )
    )

    // --------------------------------------------------------
    // SMARTPHONE
    // --------------------------------------------------------

    drawRoundRect(
        color = Color(0xFF17242A),
        topLeft = Offset(
            x + 48f * scale,
            y + 63f * scale
        ),
        size = Size(
            20f * scale,
            35f * scale
        ),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(
            4f * scale,
            4f * scale
        )
    )

    drawRect(
        color = Color(0xFF66C7D0),
        topLeft = Offset(
            x + 51f * scale,
            y + 68f * scale
        ),
        size = Size(
            14f * scale,
            25f * scale
        )
    )
}


// ============================================================
// MOUNTAINS
// ============================================================

private fun DrawScope.drawMountains(
    w: Float,
    h: Float
) {

    val path = Path().apply {

        moveTo(
            0f,
            h * 0.68f
        )

        lineTo(
            w * 0.18f,
            h * 0.56f
        )

        lineTo(
            w * 0.29f,
            h * 0.67f
        )

        lineTo(
            w * 0.45f,
            h * 0.53f
        )

        lineTo(
            w * 0.58f,
            h * 0.67f
        )

        lineTo(
            w * 0.72f,
            h * 0.57f
        )

        lineTo(
            w * 0.86f,
            h * 0.68f
        )

        lineTo(
            w,
            h * 0.59f
        )

        lineTo(
            w,
            h * 0.77f
        )

        lineTo(
            0f,
            h * 0.77f
        )

        close()
    }

    drawPath(
        path = path,
        color = Color(0xFF76A993)
    )

    // Mountain highlight

    val highlight = Path().apply {

        moveTo(
            w * 0.18f,
            h * 0.56f
        )

        lineTo(
            w * 0.22f,
            h * 0.62f
        )

        lineTo(
            w * 0.27f,
            h * 0.65f
        )

        lineTo(
            w * 0.18f,
            h * 0.56f
        )

        close()
    }

    drawPath(
        path = highlight,
        color = Color(0xFFB4D1B9)
    )
}


// ============================================================
// CLOUD
// ============================================================

private fun DrawScope.drawCloud(
    x: Float,
    y: Float,
    scale: Float
) {

    drawCircle(
        color = Color.White.copy(alpha = 0.70f),
        radius = 25f * scale,
        center = Offset(x, y)
    )

    drawCircle(
        color = Color.White.copy(alpha = 0.70f),
        radius = 32f * scale,
        center = Offset(
            x + 25f * scale,
            y + 4f * scale
        )
    )

    drawCircle(
        color = Color.White.copy(alpha = 0.70f),
        radius = 22f * scale,
        center = Offset(
            x + 50f * scale,
            y + 8f * scale
        )
    )

    drawRect(
        color = Color.White.copy(alpha = 0.70f),
        topLeft = Offset(
            x - 5f * scale,
            y + 5f * scale
        ),
        size = Size(
            60f * scale,
            25f * scale
        )
    )
}


// ============================================================
// TREE
// ============================================================

private fun DrawScope.drawTree(
    x: Float,
    y: Float,
    scale: Float
) {

    drawRect(
        color = Color(0xFF795548),
        topLeft = Offset(
            x - 6f * scale,
            y
        ),
        size = Size(
            12f * scale,
            50f * scale
        )
    )

    drawCircle(
        color = Color(0xFF1B5E20),
        radius = 30f * scale,
        center = Offset(
            x,
            y - 25f * scale
        )
    )

    drawCircle(
        color = Color(0xFF2E7D32),
        radius = 24f * scale,
        center = Offset(
            x - 20f * scale,
            y - 5f * scale
        )
    )

    drawCircle(
        color = Color(0xFF43A047),
        radius = 22f * scale,
        center = Offset(
            x + 20f * scale,
            y - 5f * scale
        )
    )
}


// ============================================================
// HOUSE
// ============================================================

private fun DrawScope.drawHouse(
    x: Float,
    y: Float,
    scale: Float
) {

    drawRect(
        color = Color(0xFFFFE0B2),
        topLeft = Offset(
            x - 35f * scale,
            y
        ),
        size = Size(
            70f * scale,
            45f * scale
        )
    )

    val roof = Path().apply {

        moveTo(
            x - 46f * scale,
            y
        )

        lineTo(
            x,
            y - 35f * scale
        )

        lineTo(
            x + 46f * scale,
            y
        )

        close()
    }

    drawPath(
        path = roof,
        color = Color(0xFFC85A32)
    )

    drawRect(
        color = Color(0xFF795548),
        topLeft = Offset(
            x - 9f * scale,
            y + 20f * scale
        ),
        size = Size(
            18f * scale,
            25f * scale
        )
    )

    // Window

    drawRect(
        color = Color(0xFF6BA7A3),
        topLeft = Offset(
            x + 18f * scale,
            y + 12f * scale
        ),
        size = Size(
            13f * scale,
            13f * scale
        )
    )
}