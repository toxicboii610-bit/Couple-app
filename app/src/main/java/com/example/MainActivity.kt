package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UniverseViewModel
import com.example.ui.components.GalaxyBackground
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    private val viewModel: UniverseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Galaxy master layout
                GalaxyBackground {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent, // Let the starry background shine through!
                        bottomBar = {
                            CapsuleNavigationBar(
                                currentTab = viewModel.currentTab.value,
                                onTabSelected = { viewModel.currentTab.value = it }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            // Crossfade transitions between different space dimensions!
                            Crossfade(
                                targetState = viewModel.currentTab.value,
                                label = "screens"
                            ) { tab ->
                                when (tab) {
                                    "home" -> HomeScreenView(viewModel = viewModel)
                                    "constellation" -> MemoryConstellationView(viewModel = viewModel)
                                    "moons" -> BirthdayMoonView()
                                    "chat" -> PrivateUniverseChatView(viewModel = viewModel)
                                    "caret" -> CycleCaringView(viewModel = viewModel)
                                    "wrongs" -> WrongsLogView(viewModel = viewModel)
                                    "letters" -> SacredLettersView()
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
// AESTHETIC CAPSULE BOTTOM NAVIGATION BAR
// Styled with glassmorphism to look floating and premium!
// ----------------------------------------------------
@Composable
fun CapsuleNavigationBar(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    val items = remember {
        listOf(
            TabItem("home", "Our Space", Icons.Default.Home),
            TabItem("constellation", "Timeline", Icons.Default.Star),
            TabItem("moons", "Moons", Icons.Default.Bedtime),
            TabItem("chat", "Chat", Icons.Default.ChatBubble),
            TabItem("caret", "Caring", Icons.Default.Favorite),
            TabItem("wrongs", "Wrongs", Icons.Default.Warning),
            TabItem("letters", "Letters", Icons.Default.Mail)
        )
    }

    // Wrap in full navigation bars padding to avoid gesture blockages
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp))
                .background(
                    color = Color(0xD8091122), // Deep semi-transparent space blue background
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(SpaceDustPink.copy(0.35f), GlowPurple.copy(0.35f))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items.forEach { item ->
                val isSelected = currentTab == item.id
                
                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(item.id) }
                        .padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) GlowPurple.copy(alpha = 0.25f) else Color.Transparent
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) {
                                if (item.id == "wrongs") WrongRedGlow else SpaceDustPink
                            } else {
                                StarWhite.copy(alpha = 0.5f)
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.label,
                        fontSize = 8.sp,
                        fontFamily = StarNodeFont,
                        color = if (isSelected) StarWhite else StarWhite.copy(alpha = 0.4f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Light
                    )
                }
            }
        }
    }
}

data class TabItem(val id: String, val label: String, val icon: ImageVector)
