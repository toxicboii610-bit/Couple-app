package com.example.ui.components

import android.graphics.BlurMaskFilter
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// ----------------------------------------------------
// FULL SCREEN ANIMATED GALAXY BACKGROUND WITH STARS & NEBULA
// ----------------------------------------------------
@Composable
fun GalaxyBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    
    // Slow breathing core light of the nebula
    val nebulaScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "nebula"
    )

    val nebulaRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    // Pulsing of distant stars
    val starsAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "stars"
    )

    // Shooting star trigger
    var shootingStarX by remember { mutableStateOf(-200f) }
    var shootingStarY by remember { mutableStateOf(-200f) }
    var triggerShootingStar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000 + Random.nextLong(6000))
            shootingStarX = Random.nextFloat() * 600f
            shootingStarY = Random.nextFloat() * 400f
            triggerShootingStar = true
            delay(1200)
            triggerShootingStar = false
        }
    }

    val shootingProgress by animateFloatAsState(
        targetValue = if (triggerShootingStar) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
        label = "shooting_star"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceMidnight)
    ) {
        // Draw starry nebula map
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Purple glow top-left (top -10%, left -10%, bg-purple-900/20)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x3D4C1D95), Color.Transparent),
                    center = Offset(width * -0.1f, height * -0.1f),
                    radius = width * 0.75f * nebulaScale
                ),
                radius = width * 0.75f * nebulaScale
            )

            // 2. Pink glow bottom-right (bottom -10%, right -10%, bg-pink-900/10)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1A831843), Color.Transparent),
                    center = Offset(width * 1.1f, height * 1.1f),
                    radius = width * 0.75f * nebulaScale
                ),
                radius = width * 0.75f * nebulaScale
            )

            // 3. Blue glow (top 40%, left 30%, bg-blue-600/10)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1B2563EB), Color.Transparent),
                    center = Offset(width * 0.3f, height * 0.4f),
                    radius = width * 0.65f * nebulaScale
                ),
                radius = width * 0.65f * nebulaScale
            )

            // 4. Draw Stars
            val random = Random(42) // Constant seed so stars stay in place
            for (i in 0..65) {
                val x = random.nextFloat() * width
                val y = random.nextFloat() * height
                val sizeVal = random.nextFloat() * 3f + 1f
                val alphaMultiplier = random.nextFloat()
                
                drawCircle(
                    color = StarWhite.copy(alpha = starsAlpha * alphaMultiplier),
                    radius = sizeVal,
                    center = Offset(x, y)
                )
            }

            // 4. Shooting Star line
            if (shootingProgress > 0f && shootingProgress < 1f) {
                val startX = shootingStarX
                val startY = shootingStarY
                val endX = startX + 350f * shootingProgress
                val endY = startY + 200f * shootingProgress

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Transparent, StarWhite.copy(alpha = 0.8f), Color.Transparent),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY)
                    ),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
        }

        // Child content layers
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}


// ----------------------------------------------------
// TWO GLOWING PLANETS (NIKKI & SURAJ) ORBITING
// ----------------------------------------------------
@Composable
fun PlanetOrbitAnimation(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbit")
    
    // Orbit rotation state
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "orbit_angle"
    )

    // Breathing float of space center
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "float"
    )

    Box(
        modifier = modifier
            .size(240.dp)
            .offset(y = floatOffset.dp),
        contentAlignment = Alignment.Center
    ) {
        // Constellation orbit circle
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2.5f

            // Sketched-looking dashed path
            drawCircle(
                color = GlowPurple.copy(alpha = 0.15f),
                radius = radius,
                style = Stroke(
                    width = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 12f), 0f)
                )
            )

            // Spark of connection line between planets
            val radianNikki = Math.toRadians(angle.toDouble())
            val radianSuraj = Math.toRadians((angle + 180f).toDouble())

            val nikkiX = center.x + radius * Math.cos(radianNikki).toFloat()
            val nikkiY = center.y + radius * Math.sin(radianNikki).toFloat()

            val surajX = center.x + radius * Math.cos(radianSuraj).toFloat()
            val surajY = center.y + radius * Math.sin(radianSuraj).toFloat()

            // Drawing connecting stardust gradient
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(SpaceDustPink.copy(alpha = 0.5f), GlowPurple.copy(alpha = 0.5f)),
                    start = Offset(nikkiX, nikkiY),
                    end = Offset(surajX, surajY)
                ),
                start = Offset(nikkiX, nikkiY),
                end = Offset(surajX, surajY),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 6f), 0f)
            )

            // Draw a subtle watercolor halo around both planets
            // Planet 1: Nikki (Dreamy silver-pink feminine glow)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SpaceDustPink.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(nikkiX, nikkiY),
                    radius = 35.dp.toPx()
                ),
                radius = 35.dp.toPx()
            )
            // Core Nikki
            drawCircle(
                color = StarWhite,
                radius = 11.dp.toPx(),
                center = Offset(nikkiX, nikkiY)
            )
            drawCircle(
                color = SpaceDustPink,
                radius = 8.dp.toPx(),
                center = Offset(nikkiX, nikkiY)
            )

            // Planet 2: Suraj (Warm golden-blue glow)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFEAA250).copy(alpha = 0.35f), Color.Transparent),
                    center = Offset(surajX, surajY),
                    radius = 35.dp.toPx()
                ),
                radius = 35.dp.toPx()
            )
            // Core Suraj
            drawCircle(
                color = StarWhite,
                radius = 11.dp.toPx(),
                center = Offset(surajX, surajY)
            )
            drawCircle(
                color = Color(0xFF5AB6FF),
                radius = 8.dp.toPx(),
                center = Offset(surajX, surajY)
            )
        }

        // Tiny floating text labels orbiting
        Box(modifier = Modifier.fillMaxSize()) {
            val radianNikki = Math.toRadians(angle.toDouble())
            val radianSuraj = Math.toRadians((angle + 180f).toDouble())
            val radius = 120f // matches circle px

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = (radius * Math.cos(radianNikki).toFloat() / 1.5f).dp,
                        y = (radius * Math.sin(radianNikki).toFloat() / 1.5f - 24).dp
                    )
            ) {
                Text(
                    text = "Nikki ♀",
                    fontFamily = ArtisticSerifFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = SpaceDustPink,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.drawBehind {
                        drawCircle(Color(0x3FFFB3C6), radius = 14f)
                    }
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = (radius * Math.cos(radianSuraj).toFloat() / 1.5f).dp,
                        y = (radius * Math.sin(radianSuraj).toFloat() / 1.5f - 24).dp
                    )
            ) {
                Text(
                    text = "Suraj ♂",
                    fontFamily = ArtisticSerifFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Color(0xFF7CC8FF),
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.drawBehind {
                        drawCircle(Color(0x2F7CC8FF), radius = 14f)
                    }
                )
            }
        }
    }
}


// ----------------------------------------------------
// ENHANCED NAME INTERACTION LAYER
// When name "Nikki" is clicked:
// Playful camera, brushes, stars, and polaroids pop out smoothly
// ----------------------------------------------------
@Composable
fun InteractiveNikkiName(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var triggerPop by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Smooth trigger handle
    fun handleTap() {
        onClick()
        scope.launch {
            triggerPop = true
            delay(2800) // Stay alive then disappear
            triggerPop = false
        }
    }

    Box(
        modifier = modifier.wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {
        // Special Visual Detail: subtle blurred camera icon as background of the text Nikki!
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 8.dp)
                .drawBehind {
                    // Soft blurry pink aesthetic base circle
                    drawCircle(
                        color = SpaceDustPink.copy(alpha = 0.08f),
                        radius = size.maxDimension / 1.5f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = SpaceDustPink.copy(alpha = 0.04f),
                modifier = Modifier
                    .size(90.dp)
                    .offset(y = (-4).dp)
            )

            Text(
                text = "Nikki",
                color = SpaceDustPink,
                fontFamily = HandwrittenFont,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { handleTap() }
            )
        }

        // Animated popouts floating around Nikki
        if (triggerPop) {
            PopoutParticles()
        }
    }
}


@Composable
fun PopoutParticles() {
    val items = remember {
        listOf(
            PopEntity(Icons.Default.CameraAlt, "Camera", -80f, -50f, Color(0xFFFFB3C6)),
            PopEntity(Icons.Default.Brush, "Brush", -10f, -90f, Color(0xFFD4C7FE)),
            PopEntity(Icons.Default.Create, "Pencil", 70f, -60f, Color(0xFFA6E3E9)),
            PopEntity(Icons.Default.Star, "Star", -60f, 60f, Color(0xFFFFF6B7)),
            PopEntity(Icons.Default.PhotoLibrary, "Polaroid", 60f, 50f, Color(0xFFF9F9F9))
        )
    }

    Box(modifier = Modifier.size(1.dp)) {
        items.forEachIndexed { index, item ->
            val animState = rememberInfiniteTransition(label = "popout_dist$index")
            
            // Slow hover float during display
            val deltaX by animState.animateFloat(
                initialValue = 0f,
                targetValue = Random.nextInt(-10, 10).toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ), label = "dx"
            )
            val deltaY by animState.animateFloat(
                initialValue = 0f,
                targetValue = Random.nextInt(-10, 10).toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(1800, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ), label = "dy"
            )

            // Spring expansion scale
            val entryScale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ), label = "scale"
            )

            Box(
                modifier = Modifier
                    .offset(x = (item.targetY + deltaX).dp, y = (item.targetX + deltaY).dp)
                    .scale(entryScale)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
                    .border(1.dp, item.color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .background(SpaceMidnight.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = item.vector,
                        contentDescription = null,
                        tint = item.color,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.label,
                        fontFamily = StarNodeFont,
                        fontSize = 9.sp,
                        color = StarWhite.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

data class PopEntity(
    val vector: ImageVector,
    val label: String,
    val targetX: Float,
    val targetY: Float,
    val color: Color
)


// ----------------------------------------------------
// GLASSMORPHISM PREMIUM CARD VISUALS
// ----------------------------------------------------
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = GlassBorder,
    glowColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardShape = RoundedCornerShape(24.dp)
    val actualGlowColor = glowColor ?: SpaceDustPink.copy(alpha = 0.25f)

    Box(
        modifier = modifier
            .clip(cardShape)
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, borderColor.copy(alpha = 0.3f), cardShape)
            .drawBehind {
                // Pinkish-purple ambient glow inside card top right
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(actualGlowColor.copy(alpha = 0.16f), Color.Transparent),
                        center = Offset(size.width, 0f),
                        radius = size.width * 0.45f
                    ),
                    radius = size.width * 0.45f
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            content()
        }
    }
}


// ----------------------------------------------------
// HAND-DRAWN SKETCHED LINE DIVIDER (ARTISTIC TOUCH)
// ----------------------------------------------------
@Composable
fun HandDrawnDivider(
    modifier: Modifier = Modifier,
    color: Color = SpaceDustPink.copy(alpha = 0.4f)
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
    ) {
        val width = size.width
        val height = size.height
        val midY = height / 2f

        // Draw an organic watercolor/sketch divider containing slight human hand jitter
        val path = Path().apply {
            moveTo(0f, midY - 1f)
            // Multi-segment bezier curve imitating a dry charcoal pencil line
            quadraticTo(width * 0.25f, midY + 2f, width * 0.5f, midY - 2f)
            quadraticTo(width * 0.75f, midY + 3f, width, midY)
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
        )

        // Draw a tiny star in the middle!
        drawCircle(
            color = StarWhite,
            radius = 3f,
            center = Offset(width / 2f, midY - 1f)
        )
    }
}



