package com.example.ui.screens

import android.text.format.DateFormat
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// ----------------------------------------------------
// 1. HOME SCREEN SECTION
// ----------------------------------------------------
@Composable
fun HomeScreenView(viewModel: UniverseViewModel) {
    var dCount by remember { mutableStateOf<String>("00") }
    var hCount by remember { mutableStateOf<String>("00") }
    var mCount by remember { mutableStateOf<String>("00") }
    var sCount by remember { mutableStateOf<String>("00") }
    var anniversariesVal by remember { mutableStateOf(2) }

    LaunchedEffect(Unit) {
        viewModel.getCountdownToAnniversary { days, hours, minutes, seconds, completedYears ->
            dCount = days.toString().padStart(2, '0')
            hCount = hours.toString().padStart(2, '0')
            mCount = minutes.toString().padStart(2, '0')
            sCount = seconds.toString().padStart(2, '0')
            anniversariesVal = completedYears
        }
    }

    // Interactive floating quote
    val activeQuote by viewModel.currentShootingMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome Header & Active Profile Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Welcome Back,",
                    style = MaterialTheme.typography.bodyMedium,
                    color = StarWhite.copy(alpha = 0.6f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = viewModel.currentUser.value,
                        style = MaterialTheme.typography.titleLarge,
                        color = if (viewModel.currentUser.value == "Nikki") SpaceDustPink else Color(0xFF7CC8FF)
                    )
                    Text(
                        text = " ✦ Space",
                        style = MaterialTheme.typography.bodySmall,
                        color = StarWhite.copy(alpha = 0.4f),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // High Fidelity Toggle Switch between profiles
            Button(
                onClick = { viewModel.toggleUser() },
                colors = ButtonDefaults.buttonColors(containerColor = GlassBg),
                border = BorderStroke(1.dp, GlassBorder),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cached,
                    contentDescription = null,
                    tint = StarWhite.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Observe as ${if (viewModel.currentUser.value == "Nikki") "Suraj" else "Nikki"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = StarWhite,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Center orbiting planet animation
        PlanetOrbitAnimation(modifier = Modifier.padding(vertical = 12.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // Love title
        Text(
            text = "In this universe, it's always\nNikki & Suraj.",
            style = MaterialTheme.typography.displayMedium,
            color = StarWhite,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )

        Text(
            text = "Built from memories, art, late nights, and love.",
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = HandwrittenFont,
            fontSize = 17.sp,
            color = SpaceDustPink,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Live Shooting Star Quotes Bar
        AnimatedContent(
            targetState = activeQuote,
            transitionSpec = {
                fadeIn(animationSpec = tween(1200)) togetherWith fadeOut(animationSpec = tween(800))
            }, label = "quotes"
        ) { quote ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .drawBehind {
                        // Watercolor trace under the quote
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x0E9172EC), Color.Transparent),
                                radius = size.width / 2.5f
                            ),
                            radius = size.width / 2.5f
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "“$quote”",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftCosmicPurple,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Countdown Timer Card framed in Glowing Constellation Ring Animation
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = SpaceDustPink.copy(alpha = 0.2f),
            glowColor = GlowPurple
        ) {
            // Top Row for Next Anniversary with beautiful titles & completed years
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "NEXT ANNIVERSARY",
                        style = MaterialTheme.typography.labelMedium,
                        color = SpaceDustPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = dCount,
                            style = MaterialTheme.typography.headlineMedium,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Light,
                            color = StarWhite
                        )
                        Text(
                            text = "Days left",
                            style = MaterialTheme.typography.bodySmall,
                            color = StarWhite.copy(0.6f),
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "COMPLETED",
                        style = MaterialTheme.typography.labelMedium,
                        color = StarWhite.copy(alpha = 0.4f),
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$anniversariesVal Years",
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = ArtisticSerifFont,
                        fontStyle = FontStyle.Italic,
                        color = GlowPurple,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Time segment cells
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TimeCell(unit = "DAYS", value = dCount)
                TimeCell(unit = "HRS", value = hCount)
                TimeCell(unit = "MINS", value = mCount)
                TimeCell(unit = "SECS", value = sCount)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            val daysLeft = dCount.toLongOrNull() ?: 0L
            val progressPercent = remember(daysLeft) {
                if (daysLeft in 1..366) {
                    ((365f - daysLeft) / 365f).coerceIn(0f, 1f)
                } else {
                    0.65f // Beautiful fallback state matching the HTML mockup!
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.05f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressPercent)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(SpaceDustPink, NebulaViolet)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HandDrawnDivider(color = GlowPurple.copy(alpha = 0.2f))

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom controls / info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Together since 6 October 2024",
                        style = MaterialTheme.typography.labelMedium,
                        color = StarWhite.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Our Universe",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = HandwrittenFont,
                        color = SpaceDustPink,
                        fontSize = 14.sp
                    )
                }

                // Ambient Sound toggle button!
                IconButton(
                    onClick = { viewModel.toggleAmbientMusic() },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = if (viewModel.isMusicPlaying.value) GlowPurple.copy(alpha = 0.2f) else GlassBg,
                            shape = CircleShape
                        )
                        .border(1.dp, GlowPurple.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (viewModel.isMusicPlaying.value) Icons.Default.MusicNote else Icons.Default.MusicOff,
                        contentDescription = "Theme music",
                        tint = if (viewModel.isMusicPlaying.value) StarWhite else SpaceDustPink,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Interactive Fun Detail: tapping Nikki name triggers popups
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Tap Nikki's name to invoke magic sketches:",
                style = MaterialTheme.typography.bodySmall,
                color = StarWhite.copy(alpha = 0.4f),
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            InteractiveNikkiName(
                onClick = { /* Tap reaction already handled internally */ }
            )
        }
    }
}

@Composable
fun TimeCell(unit: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .background(Color(0x2E000000), RoundedCornerShape(12.dp))
                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontFamily = StarNodeFont,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = StarWhite
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = unit,
            fontFamily = StarNodeFont,
            fontSize = 9.sp,
            color = StarWhite.copy(alpha = 0.6f)
        )
    }
}


// ----------------------------------------------------
// 2. TIMELINE CONSTELLATION SECTION
// Every memory is a glowing star, connected like constellations
// ----------------------------------------------------
@Composable
fun MemoryConstellationView(viewModel: UniverseViewModel) {
    val memoryList by viewModel.memories.collectAsState()
    var selectedMemory by remember { mutableStateOf<Memory?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    // State for creating memory
    var newTitle by remember { mutableStateOf("") }
    var newNote by remember { mutableStateOf("") }
    var newDate by remember { mutableStateOf("Today") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Memory Constellations",
                style = MaterialTheme.typography.displayMedium,
                color = StarWhite
            )
            Text(
                text = "Timeline pathway starting 6 October 2024. Tap nodes to unlock letters of space-time.",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftCosmicPurple
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main star map representation
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                    .background(Color(0x0C000000))
                    .clip(RoundedCornerShape(24.dp))
            ) {
                // Interactive constellation Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {}
                ) {
                    val w = size.width
                    val h = size.height

                    if (memoryList.isNotEmpty()) {
                        // Plot coordinates dynamically based on index for scenic constellation lines
                        val points = mutableListOf<Offset>()
                        memoryList.forEachIndexed { i, _ ->
                            val tX = when (i % 3) {
                                0 -> w * 0.18f
                                1 -> w * 0.5f
                                else -> w * 0.82f
                            }
                            val tY = h * 0.12f + (h * 0.72f) * (i.toFloat() / (memoryList.size.coerceAtLeast(2) - 1))
                            points.add(Offset(tX, tY))
                        }

                        // Draw connection paths
                        for (i in 0 until points.size - 1) {
                            drawLine(
                                color = GlowPurple.copy(alpha = 0.35f),
                                start = points[i],
                                end = points[i + 1],
                                strokeWidth = 3f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                            )
                        }

                        // Draw extra faint constellation lines to other stars
                        for (i in points.indices) {
                            if (i > 1) {
                                drawLine(
                                    color = SpaceDustPink.copy(alpha = 0.15f),
                                    start = points[i],
                                    end = points[i - 2],
                                    strokeWidth = 1.5f
                                )
                            }
                        }
                    }
                }

                // Superimpose interactable Star elements
                if (memoryList.isNotEmpty()) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val w = maxWidth
                        val h = maxHeight

                        memoryList.forEachIndexed { index, memory ->
                            val tX = when (index % 3) {
                                0 -> w * 0.18f
                                1 -> w * 0.5f
                                else -> w * 0.82f
                            }
                            // Star distribute y mapping
                            val percentY = index.toFloat() / (memoryList.size.coerceAtLeast(2) - 1)
                            val tY = h * 0.12f + (h * 0.72f) * percentY

                            Box(
                                modifier = Modifier
                                    .absoluteOffset(x = tX - 35.dp, y = tY - 35.dp)
                                    .size(70.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val pulseScale = rememberInfiniteTransition(label = "pulse_star").animateFloat(
                                    initialValue = 0.85f,
                                    targetValue = 1.25f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(1500 + index * 300, easing = EaseInOutSine),
                                        repeatMode = RepeatMode.Reverse
                                    ), label = "pulse"
                                )

                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .drawBehind {
                                            // Soft watercolors radiant background
                                            drawCircle(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        (if (index == 0) SpaceDustPink else GlowPurple).copy(
                                                            alpha = 0.45f
                                                        ), Color.Transparent
                                                    )
                                                ),
                                                radius = 22.dp.toPx() * pulseScale.value
                                            )
                                        }
                                        .clip(CircleShape)
                                        .clickable {
                                            selectedMemory = memory
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = memory.title,
                                        tint = if (index == 0) SpaceDustPink else StarWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Quick caption labels
                                Box(
                                    modifier = Modifier
                                        .offset(y = 28.dp)
                                        .background(Color(0xD9090C15), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = memory.title,
                                        fontFamily = StarNodeFont,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Light,
                                        color = StarWhite.copy(alpha = 0.9f),
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add star button
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GlowPurple),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Ignite a New Star Memory",
                    fontFamily = ArtisticSerifFont,
                    color = SpaceMidnight,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Tapped Star Detailed Note view overlay! Styled like Polaroid Frames.
        AnimatedVisibility(
            visible = selectedMemory != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
            modifier = Modifier.align(Alignment.Center)
        ) {
            selectedMemory?.let { memory ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x99000000))
                        .clickable { selectedMemory = null },
                    contentAlignment = Alignment.Center
                ) {
                    // Polaroid Frame
                    Column(
                        modifier = Modifier
                            .width(300.dp)
                            .shadow(20.dp, RoundedCornerShape(12.dp))
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(4.dp, Color(0xFFF1F0E8), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                            .clickable(enabled = false) {}, // prevent closing inside click
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Drawing watercolor landscape inside polaroid photo placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(SpaceMidnight)
                                .drawBehind {
                                    // Custom visual galaxy sketches
                                    drawRect(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(SpaceMidnight, Color(0xFF231C3C), Color(0xFF381A2A))
                                        )
                                    )
                                    // Pencil-drawn constellations overlay inside polaroid
                                    drawCircle(SpaceDustPink.copy(alpha = 0.3f), radius = 30f, center = Offset(50f, 60f))
                                    drawCircle(Color(0xFFEAA250).copy(alpha = 0.25f), radius = 25f, center = Offset(240f, 130f))
                                    drawLine(
                                        StarWhite.copy(alpha = 0.3f),
                                        start = Offset(50f, 60f),
                                        end = Offset(240f, 130f),
                                        strokeWidth = 2f
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Brush,
                                    contentDescription = null,
                                    tint = SpaceDustPink,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = memory.date,
                                    fontSize = 11.sp,
                                    fontFamily = StarNodeFont,
                                    color = StarWhite.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Custom letters handwritten text details
                        Text(
                            text = memory.title,
                            fontFamily = ArtisticSerifFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF1E2022),
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = "Music Loop: ${memory.musicType} 🎧",
                            fontFamily = StarNodeFont,
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                        )

                        Text(
                            text = memory.note,
                            fontFamily = HandwrittenFont,
                            fontSize = 17.sp,
                            color = Color(0xFF3E4042),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Nikki & Suraj's Universe — Timeline Pathway",
                            fontFamily = StarNodeFont,
                            fontSize = 8.sp,
                            color = Color.DarkGray.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { selectedMemory = null },
                            colors = ButtonDefaults.buttonColors(containerColor = SpaceMidnight)
                        ) {
                            Text("Collapse Card", color = StarWhite)
                        }

                        // Super fun delete star feature ONLY for Nikki
                        if (viewModel.currentUser.value == "Nikki") {
                            TextButton(onClick = {
                                viewModel.deleteMemory(memory)
                                selectedMemory = null
                            }) {
                                Text("Delete Star", color = Color.Red.copy(0.7f), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // New memory creator Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                containerColor = SpaceMidnight,
                title = {
                    Text(
                        "Ignite a Star",
                        fontFamily = ArtisticSerifFont,
                        color = SpaceDustPink
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("Memory Title") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = StarWhite,
                                unfocusedTextColor = StarWhite
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newDate,
                            onValueChange = { newDate = it },
                            label = { Text("Date (e.g. 15 Oct 2024)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = StarWhite,
                                unfocusedTextColor = StarWhite
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newNote,
                            onValueChange = { newNote = it },
                            label = { Text("Emotional Star Note") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = StarWhite,
                                unfocusedTextColor = StarWhite
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTitle.isNotEmpty()) {
                                viewModel.createMemory(newTitle, newNote, newDate)
                                newTitle = ""
                                newNote = ""
                                newDate = "Today"
                                showDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GlowPurple)
                    ) {
                        Text("Add Star", color = SpaceMidnight)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel", color = StarWhite.copy(alpha = 0.6f))
                    }
                }
            )
        }
    }
}


// ----------------------------------------------------
// 3. MOON PHASE BIRTHDAY ALIGNMENT FEATURE SCREEN
// Show moon connection particles dynamically
// ----------------------------------------------------
@Composable
fun BirthdayMoonView() {
    val infiniteTransition = rememberInfiniteTransition(label = "moon_glow")

    // Slow pulsing glow of Nikki's feminine silver-pink moon
    val nikkiMoonPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "nikki"
    )

    // Slow pulsing glow of Suraj's warm golden-blue moon
    val surajMoonPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "suraj"
    )

    // Cosmic Alignment Flow particles
    val connectionFlowState by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "alignment"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sacred Birthday Moons",
            style = MaterialTheme.typography.displayMedium,
            color = StarWhite,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Cosmic alignment calculated by birthdays: Nikki's soft dreamy feminine lights & Suraj's warm solar glows.",
            style = MaterialTheme.typography.bodyMedium,
            color = SoftCosmicPurple,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // The Moons Alignment Visual Board
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                .background(Color(0x1F000000))
                .clip(RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Moon Centers
                val nikkiCenterX = w * 0.28f
                val nikkiCenterY = h * 0.45f
                val surajCenterX = w * 0.72f
                val surajCenterY = h * 0.45f

                // Distant planetary flow
                val numParticles = 8
                for (i in 0 until numParticles) {
                    val progress = (connectionFlowState + (i.toFloat() / numParticles)) % 1f
                    val currentX = nikkiCenterX + (surajCenterX - nikkiCenterX) * progress
                    // Floating sine wave path
                    val currentY = h * 0.45f + kotlin.math.sin(progress * Math.PI * 2f).toFloat() * 30.0f * density

                    drawCircle(
                        color = androidx.compose.ui.graphics.lerp(SpaceDustPink, Color(0xFF5AB6FF), progress).copy(alpha = 0.6f),
                        radius = 4f,
                        center = Offset(currentX, currentY)
                    )
                }

                // ----------------------------------------------------
                // 1. Nikki's Moon (基于 22 May 2007)
                // Waxing Gibbous style with dream aesthetic silver-pink coloring
                // ----------------------------------------------------
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(SpaceDustPink.copy(alpha = 0.4f * nikkiMoonPulse), Color.Transparent),
                        center = Offset(nikkiCenterX, nikkiCenterY),
                        radius = 65.dp.toPx()
                    ),
                    radius = 65.dp.toPx()
                )
                // Silver base moon sphere
                drawCircle(
                    color = Color(0xFFF9E8EB),
                    radius = 35.dp.toPx(),
                    center = Offset(nikkiCenterX, nikkiCenterY)
                )
                // Draw shadow overlap creating crescent/moon phases look
                drawCircle(
                    color = Color(0xFF090C15), // matches space background
                    radius = 35.dp.toPx(),
                    center = Offset(nikkiCenterX - 18.dp.toPx(), nikkiCenterY)
                )

                // ----------------------------------------------------
                // 2. Suraj's Moon (基于 17 June 2005)
                // Full light with warm golden-blue solar flare and crater textures
                // ----------------------------------------------------
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFEAA250).copy(alpha = 0.35f * surajMoonPulse), Color.Transparent),
                        center = Offset(surajCenterX, surajCenterY),
                        radius = 65.dp.toPx()
                    ),
                    radius = 65.dp.toPx()
                )
                // Golden base moon sphere
                drawCircle(
                    color = Color(0xFFFFF1DC),
                    radius = 35.dp.toPx(),
                    center = Offset(surajCenterX, surajCenterY)
                )
                // Subtle blue dark overlay craters
                drawCircle(
                    color = Color(0x3B5AB6FF),
                    radius = 12.dp.toPx(),
                    center = Offset(surajCenterX - 10f, surajCenterY - 15f)
                )
                drawCircle(
                    color = Color(0x3BEAA250),
                    radius = 8.dp.toPx(),
                    center = Offset(surajCenterX + 15f, surajCenterY + 10f)
                )
            }

            // Labels right underneath
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Nikki's Moon",
                        fontFamily = ArtisticSerifFont,
                        fontWeight = FontWeight.Bold,
                        color = SpaceDustPink,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Birthday: 22 May 2007\nSoft Dreamy Gibbous",
                        fontFamily = StarNodeFont,
                        fontSize = 10.sp,
                        color = StarWhite.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Suraj's Moon",
                        fontFamily = ArtisticSerifFont,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFF1DC),
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Birthday: 17 June 2005\nGolden Gibbous Horizon",
                        fontFamily = StarNodeFont,
                        fontSize = 10.sp,
                        color = StarWhite.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Emotional Analysis Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = GlowPurple.copy(alpha = 0.2f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = SpaceDustPink,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Cosmic Harmonizations",
                    fontFamily = ArtisticSerifFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = StarWhite
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Nikki's mystical pink-tinted Luna aligns seamlessly with Suraj's warm glowing solar sphere. Gravity forces stream a perpetual stream of beautiful hand-drawn sketches, watercolor paintings, and starry nighttime phone calls from Nikki to Suraj and back.\n\nIn this shared galaxy timeline pathway, moons never set. They orbit together, echoing 'always Nikki & Suraj'.",
                fontFamily = HandwrittenFont,
                fontSize = 18.sp,
                color = SoftCosmicPurple,
                lineHeight = 24.sp
            )
        }
    }
}


// ----------------------------------------------------
// 4. PRIVATE MINI CHAT SYSTEM
// Send text, cute sticker badges and drawings securely
// ----------------------------------------------------
@Composable
fun PrivateUniverseChatView(viewModel: UniverseViewModel) {
    val messagesList by viewModel.chatMessages.collectAsState()
    var inputMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Scroll to bottom on updates
    LaunchedEffect(messagesList.size) {
        if (messagesList.isNotEmpty()) {
            listState.animateScrollToItem(messagesList.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Chat Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Cosmic Private Chat",
                    style = MaterialTheme.typography.displayMedium,
                    color = StarWhite
                )
                Text(
                    text = "Couple-only secure nebula feed",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftCosmicPurple
                )
            }
            // Indicator of who you are chatting as
            Box(
                modifier = Modifier
                    .background(
                        if (viewModel.currentUser.value == "Nikki") SpaceDustPink.copy(alpha = 0.2f) else Color(0x3B5AB6FF),
                        RoundedCornerShape(8.dp)
                    )
                    .border(
                        1.dp,
                        if (viewModel.currentUser.value == "Nikki") SpaceDustPink else Color(0xFF5AB6FF),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${viewModel.currentUser.value} Messaging",
                    fontFamily = StarNodeFont,
                    fontSize = 10.sp,
                    color = StarWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chats Thread
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                .background(Color(0x0F000000))
                .clip(RoundedCornerShape(20.dp))
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messagesList) { msg ->
                    val isMe = msg.sender == viewModel.currentUser.value
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                    ) {
                        // Sender ID
                        Text(
                            text = if (isMe) "You" else msg.sender,
                            fontFamily = StarNodeFont,
                            fontSize = 9.sp,
                            color = if (msg.sender == "Nikki") SpaceDustPink else Color(0xFF7CC8FF)
                        )

                        // Message bubble
                        Box(
                            modifier = Modifier
                                .widthIn(max = 240.dp)
                                .shadow(2.dp, RoundedCornerShape(16.dp))
                                .background(
                                    color = if (isMe) {
                                        if (msg.sender == "Nikki") SpaceDustPink.copy(alpha = 0.25f) else Color(0xFF264C6F)
                                    } else {
                                        GlassBg
                                    },
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isMe) 16.dp else 4.dp,
                                        bottomEnd = if (isMe) 4.dp else 16.dp
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isMe) {
                                        if (msg.sender == "Nikki") SpaceDustPink.copy(alpha = 0.5f) else Color(0xFF7CC8FF).copy(0.4f)
                                    } else {
                                        GlassBorder
                                    },
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isMe) 16.dp else 4.dp,
                                        bottomEnd = if (isMe) 4.dp else 16.dp
                                    )
                                )
                                .padding(12.dp)
                        ) {
                            Column {
                                if (msg.stickerName.isNotEmpty()) {
                                    // Cute hand-drawn sticker sketch representation
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = when(msg.stickerName) {
                                                "Camera" -> Icons.Default.CameraAlt
                                                "Watercolor" -> Icons.Default.Brush
                                                "Pencil" -> Icons.Default.Create
                                                else -> Icons.Default.Favorite
                                            },
                                            contentDescription = null,
                                            tint = if (msg.sender == "Nikki") SpaceDustPink else Color(0xFFFFF1DC)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Sent Doodle Sticker: ${msg.stickerName}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 11.sp,
                                            color = StarWhite
                                        )
                                    }
                                } else {
                                    Text(
                                        text = msg.text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = StarWhite
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick sticker panel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Camera", "Watercolor", "Pencil").forEach { sticker ->
                Box(
                    modifier = Modifier
                        .clickable { viewModel.sendChatMessage("", sticker) }
                        .background(GlassBg, RoundedCornerShape(8.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "✦ $sticker 🎨", fontSize = 10.sp, color = SpaceDustPink)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Message input composer
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputMsg,
                onValueChange = { inputMsg = it },
                placeholder = { Text("Write to your favorite artist...", color = StarWhite.copy(0.4f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = StarWhite,
                    unfocusedTextColor = StarWhite,
                    focusedBorderColor = GlowPurple,
                    unfocusedBorderColor = GlassBorder
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            IconButton(
                onClick = {
                    if (inputMsg.trim().isNotEmpty()) {
                        viewModel.sendChatMessage(inputMsg)
                        inputMsg = ""
                    }
                },
                modifier = Modifier
                    .background(GlowPurple, CircleShape)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = SpaceMidnight
                )
            }
        }
    }
}


// ----------------------------------------------------
// 5. PERIOD REMINDER CARING FLOW
// Suraj trackers & floral elements
// ----------------------------------------------------
@Composable
fun CycleCaringView(viewModel: UniverseViewModel) {
    val logState by viewModel.latestPeriodLog.collectAsState()
    val scope = rememberCoroutineScope()
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Care & Reminders",
            style = MaterialTheme.typography.displayMedium,
            color = StarWhite
        )
        Text(
            text = "A soft garden of floral reminders and starry care timelines.",
            style = MaterialTheme.typography.bodyMedium,
            color = SoftCosmicPurple,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // The beautiful highlight taking care slogan
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF352631), Color(0xFF1E141D))
                    )
                )
                .border(1.dp, SpaceDustPink.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Flower motif logo
                Text(
                    text = "🌸 🌙 🤍",
                    fontSize = 28.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Take care of Nikki today 🤍",
                    fontFamily = ArtisticSerifFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 21.sp,
                    color = SpaceDustPink,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "“Be gentle, draw her starry sketches, and play calm lo-fi frequencies to ease her mind today.”",
                    fontFamily = HandwrittenFont,
                    fontStyle = FontStyle.Italic,
                    fontSize = 17.sp,
                    color = SoftCosmicPurple,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Last Period Status Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = SpaceDustPink.copy(alpha = 0.2f)
        ) {
            Text(
                text = "🌸 HISTORIC CYCLE DETAILS",
                fontFamily = StarNodeFont,
                color = SpaceDustPink,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            logState?.let { log ->
                val format = SimpleDateFormat("dd MMMM yyyy", Locale.US)
                val startForm = format.format(Date(log.startDate))
                val nextForm = format.format(Date(log.calculatedNextDate))

                // Calculate distance to next period
                val diff = log.calculatedNextDate - System.currentTimeMillis()
                val daysLeft = if (diff > 0) diff / (1000 * 60 * 60 * 24) else 0

                StatusRow(label = "Last Logged Start Date", value = startForm)
                StatusRow(label = "Estimated Next Flow", value = nextForm)
                StatusRow(
                    label = "Cycle Countdown Alert",
                    value = if (daysLeft > 0) "$daysLeft Days remaining" else "Flow is active now 🌸"
                )
                StatusRow(label = "Logged by Partner", value = log.loggedBy)
                StatusRow(label = "Care Protocol Notes", value = log.notes)
            } ?: run {
                Text(
                    text = "No period history currently logged. Tap below to log.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = StarWhite.copy(0.6f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Only Suraj (or active perspective in general) can log
            Button(
                onClick = { showDatePicker = true },
                colors = ButtonDefaults.buttonColors(containerColor = SpaceDustPink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Nikki's Period Start Date ➕", color = SpaceMidnight, fontWeight = FontWeight.Bold)
            }
        }

        // Beautiful Date logging dialogue mock representing highly interactive picker
        if (showDatePicker) {
            AlertDialog(
                onDismissRequest = { showDatePicker = false },
                containerColor = SpaceMidnight,
                title = { Text("Record Cycle Start", color = SpaceDustPink, fontFamily = ArtisticSerifFont) },
                text = {
                    Column {
                        Text(
                            "Confirm logging new start date. This will automatically calculate future monthly expectations based on regular average offsets.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = StarWhite
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Press 'Set Today' to stamp the timeline.",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlowPurple
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.savePeriodStart(System.currentTimeMillis())
                            showDatePicker = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SpaceDustPink)
                    ) {
                        Text("Set Today", color = SpaceMidnight)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel", color = StarWhite.copy(0.6f))
                    }
                }
            )
        }
    }
}

@Composable
fun StatusRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = StarWhite.copy(alpha = 0.6f))
        Text(
            text = value,
            fontFamily = StarNodeFont,
            fontSize = 12.sp,
            color = StarWhite,
            fontWeight = FontWeight.SemiBold
        )
    }
}


// ----------------------------------------------------
// 6. "WHAT SURAJ DID WRONG THIS TIME" FEATURE SCREEN
// Black aesthetic theme, soft red glows, only Nikki edits
// ----------------------------------------------------
@Composable
fun WrongsLogView(viewModel: UniverseViewModel) {
    val notesList by viewModel.wrongNotes.collectAsState()
    var inputWrong by remember { mutableStateOf("") }
    val isNikki = viewModel.currentUser.value == "Nikki"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WrongBlack)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Aesthetic Section Title
        Text(
            text = "What Suraj Did Wrong This Time",
            fontFamily = ArtisticSerifFont,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Light,
            fontSize = 24.sp,
            color = StarWhite,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "ONLY Nikki can record complaints. Suraj can only react 😭💀🫠❤️.",
            style = MaterialTheme.typography.bodySmall,
            color = WrongRedGlow.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Glassmorphism Note creator ONLY for Nikki
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = WrongRedGlow.copy(alpha = 0.35f)
        ) {
            Text(
                text = "LOG NEW STORY",
                fontFamily = StarNodeFont,
                color = WrongRedGlow,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (isNikki) {
                OutlinedTextField(
                    value = inputWrong,
                    onValueChange = { inputWrong = it },
                    placeholder = { Text("I can't believe Suraj did this...", color = StarWhite.copy(alpha = 0.4f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = StarWhite,
                        unfocusedTextColor = StarWhite,
                        focusedBorderColor = WrongRedGlow,
                        unfocusedBorderColor = GlassBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (inputWrong.trim().isNotEmpty()) {
                            viewModel.addWrong(inputWrong)
                            inputWrong = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WrongRedGlow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Record Disagreement", color = StarWhite, fontWeight = FontWeight.Bold)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x3BFF0000), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "🔒 VIEWING MODE - Suraj cannot insert new records! Be gentle & add a sweet reaction below.",
                        fontFamily = StarNodeFont,
                        fontSize = 11.sp,
                        color = WrongRedGlow,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // History of wrongs list
        Text(
            text = "ACTIVE ARCHIVE",
            style = MaterialTheme.typography.labelMedium,
            color = StarWhite.copy(0.6f),
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (notesList.isEmpty()) {
            Text(
                text = "Heavenly peace. Active archive is completely clean! ✦",
                style = MaterialTheme.typography.bodyMedium,
                color = StarWhite.copy(0.4f),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            notesList.forEach { note ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .background(Color(0x1F000000), RoundedCornerShape(16.dp))
                        .border(1.dp, WrongRedGlow.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        // Date formatted
                        val format = SimpleDateFormat("dd MMM hh:mm a", Locale.US)
                        Text(
                            text = format.format(Date(note.timestamp)),
                            fontFamily = StarNodeFont,
                            fontSize = 8.sp,
                            color = StarWhite.copy(alpha = 0.4f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = note.content,
                            fontFamily = HandwrittenFont,
                            fontSize = 18.sp,
                            color = StarWhite
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Heart-warming reactions row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("😭", "💀", "🫠", "❤️").forEach { char ->
                                    val isSelected = note.reaction == char
                                    Box(
                                        modifier = Modifier
                                            .clickable { viewModel.updateWrongReaction(note, char) }
                                            .background(
                                                if (isSelected) WrongRedGlow.copy(alpha = 0.35f) else GlassBg,
                                                CircleShape
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) WrongRedGlow else Color.Transparent,
                                                CircleShape
                                            )
                                            .padding(6.dp)
                                    ) {
                                        Text(text = char, fontSize = 13.sp)
                                    }
                                }
                            }

                            // Delete complaint ONLY for Nikki
                            if (isNikki) {
                                IconButton(
                                    onClick = { viewModel.deleteWrong(note) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete record",
                                        tint = StarWhite.copy(0.4f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// ----------------------------------------------------
// 7. OPEN WHEN LETTERS SECTION
// Interactive cozy letters opening with soft glows
// ----------------------------------------------------
@Composable
fun SacredLettersView() {
    var openedLetter by remember { mutableStateOf<LetterObj?>(null) }
    
    val letterOptions = remember {
        listOf(
            LetterObj(
                title = "Open when you miss me 🌌",
                note = "Suraj, whenever distances feel long or stars seem quiet, remember that my art holds you tight. I drew us under the infinite galaxy because somewhere between the late nights of talking, we found home. Look at the orbiting planets, they always return to each other, just like us.\n\nAlways with you,\nNikki 🤍"
            ),
            LetterObj(
                title = "Open when life feels heavy 🎨",
                note = "Nikki/Suraj, whenever the watercolor of our days turns messy or paint spills, know that we are co-creators of our life canvas. Storms make the stars glow brighter. Close your eyes, listen to this lo-fi melody loop, and breathe. You are my absolute art and safe space.\n\nLove,\nUs."
            ),
            LetterObj(
                title = "Open when you need comfort ✏️",
                note = "To my celestial partner, this is a soft charcoal pencil doodle of a warm embrace wrapped in space dust. Breathe in, breathe out. Gravity is holding us perfectly. Nothing in this universe can change the fact that it is always Nikki & Suraj. Sleep cozy!\n\nForever & Always."
            ),
            LetterObj(
                title = "Open when you can't sleep 🌙",
                note = "Hey sleepless star, I'm watching the cosmos too. Put on your headphones, play the ambient tone, and picture us floating on a glowing lavender nebula together sketching funny doodles of orbiting potatoes. You are safe. Love you to the edge of space and back."
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Sacred Letters",
                style = MaterialTheme.typography.displayMedium,
                color = StarWhite
            )
            Text(
                text = "Handwritten cosmic notes sealed for important space-time coordinates.",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftCosmicPurple
            )

            Spacer(modifier = Modifier.height(24.dp))

            letterOptions.forEach { letter ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { openedLetter = letter }
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .background(GlassBg, RoundedCornerShape(16.dp))
                        .border(1.dp, SpaceDustPink.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mail,
                                contentDescription = null,
                                tint = SpaceDustPink,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = letter.title,
                                fontFamily = ArtisticSerifFont,
                                fontWeight = FontWeight.SemiBold,
                                color = StarWhite,
                                fontSize = 15.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "Expand",
                            tint = StarWhite.copy(0.4f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Expanded handwritten letter view modal with glowing animations
        AnimatedVisibility(
            visible = openedLetter != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            openedLetter?.let { letter ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xE6090C15))
                        .clickable { openedLetter = null },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .width(320.dp)
                            .shadow(16.dp, RoundedCornerShape(20.dp))
                            .background(Color(0xFFFCF8F2), RoundedCornerShape(20.dp)) // warm paper aesthetic
                            .border(2.dp, SpaceDustPink.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(24.dp)
                            .clickable(enabled = false) {},
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Envelope stamp decoration
                        Text(
                            text = "✦ Nikki & Suraj Galaxy Stamp ✦",
                            fontFamily = StarNodeFont,
                            fontSize = 8.sp,
                            color = Color.DarkGray.copy(0.5f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            text = letter.title,
                            fontFamily = ArtisticSerifFont,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E2022),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = letter.note,
                            fontFamily = HandwrittenFont,
                            fontSize = 19.sp,
                            color = Color(0xFF333333),
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { openedLetter = null },
                            colors = ButtonDefaults.buttonColors(containerColor = SpaceMidnight)
                        ) {
                            Text("Fold Letter Back", color = StarWhite)
                        }
                    }
                }
            }
        }
    }
}

data class LetterObj(val title: String, val note: String)
