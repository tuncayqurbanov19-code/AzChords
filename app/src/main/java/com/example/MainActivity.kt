package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Song
import com.example.ui.components.TransposedSongContent
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkThemeEnabled by remember { mutableStateOf(true) } // default to sleek stage-ready dark theme
            
            MyApplicationTheme(darkTheme = darkThemeEnabled) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppMainLayout(
                        viewModel = viewModel,
                        isDarkTheme = darkThemeEnabled,
                        onThemeToggle = { darkThemeEnabled = !darkThemeEnabled }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppMainLayout(
    viewModel: MainViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val currentScreen = viewModel.currentScreen
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Listen to success message toasts safely
    LaunchedEffect(viewModel.successMessage) {
        viewModel.successMessage?.let { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(msg)
                viewModel.successMessage = null
            }
        }
    }

    // Listen to error message toasts safely
    LaunchedEffect(viewModel.authErrorMessage) {
        viewModel.authErrorMessage?.let { err ->
            scope.launch {
                snackbarHostState.showSnackbar(err)
                viewModel.authErrorMessage = null
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Sleek high-fidelity dark bottom navigation with smooth gold/amber accent active indicators
            NavigationBar(
                tonalElevation = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .testTag("app_navigation_bar")
                    .border(
                        BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
            ) {
                NavigationBarItem(
                    selected = currentScreen is Screen.Home,
                    onClick = { viewModel.navigateTo(Screen.Home) },
                    icon = { 
                        Icon(
                            imageVector = if (currentScreen is Screen.Home) Icons.Default.Home else Icons.Default.Home,
                            contentDescription = "Home"
                        ) 
                    },
                    label = { Text("Ana Səhifə", fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_home")
                )

                NavigationBarItem(
                    selected = currentScreen is Screen.AddSong,
                    onClick = { viewModel.navigateTo(Screen.AddSong) },
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.AddCircle, 
                            contentDescription = "Add Song"
                        ) 
                    },
                    label = { Text("Mahnı Əlavə Et", fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_add_song")
                )

                NavigationBarItem(
                    selected = currentScreen is Screen.Account,
                    onClick = { viewModel.navigateTo(Screen.Account) },
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.Person, 
                            contentDescription = "Profile"
                        ) 
                    },
                    label = { Text("Profil", fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_profile")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Elegant brand Header
            AppHeader(
                viewModel = viewModel,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )

            // Animated Screen Content Switcher with gorgeous spring transitions
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() + slideInHorizontally { it / 3 } togetherWith fadeOut() + slideOutHorizontally { -it / 3 }
                },
                modifier = Modifier.fillMaxSize()
            ) { screen ->
                when (screen) {
                    is Screen.Home -> HomeScreen(viewModel = viewModel)
                    is Screen.SongDetail -> SongDetailScreen(viewModel = viewModel, songId = screen.songId)
                    is Screen.AddSong -> AddSongScreen(viewModel = viewModel)
                    is Screen.AdminPanel -> AdminPanelScreen(viewModel = viewModel)
                    is Screen.Account -> AccountScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppHeader(
    viewModel: MainViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Return Arrow Back button safely
                if (viewModel.currentScreen !is Screen.Home) {
                    IconButton(
                        onClick = { viewModel.goBack() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                CircleShape
                            )
                            .testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = "AzChords",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Musiqi Akord Portalı",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        letterSpacing = 0.2.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Admin button
                if (viewModel.currentUser?.isAdmin == true) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.AdminPanel) },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("secret_admin_badge")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Admin Panel",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Theme switch button
                IconButton(
                    onClick = onThemeToggle,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                            CircleShape
                        )
                ) {
                    Text(
                        text = if (isDarkTheme) "☀️" else "🌙",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    var activeTab by remember { mutableStateOf(0) } // 0 = Bütün, 1 = Trendlər, 2 = Repertuarım
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    val approvedSongs by viewModel.approvedSongs.collectAsState()
    val trendingSongs by viewModel.trendingSongs.collectAsState()
    val repertoireSongs by viewModel.repertoireSongs.collectAsState()
    val allComments by viewModel.allComments.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // High fidelity elegant search component with golden accents
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { 
                Text(
                    "Mahnı və ya sənətçi axtar...", 
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ) 
            },
            leadingIcon = { 
                Icon(
                    Icons.Default.Search, 
                    contentDescription = "Search icon",
                    tint = MaterialTheme.colorScheme.primary
                ) 
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                        Icon(
                            Icons.Default.Close, 
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("search_bar")
        )

        // Statistics Deck
        val totalSongCount = approvedSongs.size
        val totalViewsCount = approvedSongs.sumOf { it.views }
        val totalCommentsCount = allComments.size

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Stat 1: Songs
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("stat_songs"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Mahnı Sayı",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mahnı Sayı",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$totalSongCount Akord",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Stat 2: Views
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("stat_views"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Ümumi Baxış",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ümumi Baxış",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$totalViewsCount Baxış",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Stat 3: Comments
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("stat_comments"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Rəylər",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mahnı Rəyləri",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$totalCommentsCount Rəy",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Tab selection pills for home content formatted in a gorgeous unified stadium dock bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf("Bütün Akordlar", "🔥 Trendlər", "⭐ Repertuar")
                tabs.forEachIndexed { index, title ->
                    val isActive = activeTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                            .clickable { activeTab = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Grid/Row view of list matching active tab
        val songList = when (activeTab) {
            0 -> approvedSongs
            1 -> trendingSongs
            else -> repertoireSongs
        }

        if (activeTab == 2 && viewModel.currentUser == null) {
            // Advise registration for protected Repertoire feature
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock icon",
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Repertuara Giriş Məhdaddur",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Öz şəxsi repertuarınızı yaradaraq mahnıları yadda saxlamaq üçün lütfən qeydiyyatdan keçin və ya sistemə daxil olun.",
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.navigateTo(Screen.Account) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("home_login_redirect")
                        ) {
                            Text("Qeydiyyat / Giriş", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        } else if (songList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Empty music note",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Mahnı tapılmadı",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp, top = 4.dp)
            ) {
                items(songList, key = { it.id }) { song ->
                    SongCardItem(
                        song = song,
                        isTrendingIndex = if (activeTab == 1) trendingSongs.indexOf(song) + 1 else null,
                        onClick = { viewModel.navigateTo(Screen.SongDetail(song.id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun SongCardItem(
    song: Song,
    isTrendingIndex: Int?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("song_item_${song.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (isTrendingIndex != null) {
                    val badgeColor = when (isTrendingIndex) {
                        1 -> GoldenAmber
                        2 -> Color(0xFFC0C0C0) // Silver
                        3 -> Color(0xFFCD7F32) // Bronze
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    val badgeTextColor = if (isTrendingIndex <= 3) Color.Black else MaterialTheme.colorScheme.onSurface
                    
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(badgeColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = isTrendingIndex.toString(),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = badgeTextColor
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play badge icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = song.artist,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "View icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = song.views.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun SongDetailScreen(viewModel: MainViewModel, songId: Int) {
    val song = viewModel.selectedSong ?: return
    val comments by viewModel.activeComments.collectAsState()
    var userCommentText by remember { mutableStateOf("") }
    val isFav = viewModel.isFavoriteSong(song.id)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            // Elegant Stage Poster Header
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                ),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Səhnə Rejimi",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = song.title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = song.artist,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        // Favorite star button with custom stage glow
                        IconButton(
                            onClick = { viewModel.toggleFavorite(song.id) },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (isFav) GoldenAmber.copy(alpha = 0.15f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                    CircleShape
                                )
                                .testTag("fav_toggle_btn")
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Default.Star else Icons.Default.Star,
                                contentDescription = "Sevimlilər",
                                tint = if (isFav) GoldenAmber else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Capo indicator styled like a fret badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🎸  Kapo: " + (if (song.capo > 0) song.capo.toString() else "Yoxdur"),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Added by system or user
                        Text(
                            text = "Yükləyən: @${song.submittedByUsername}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }

        // Transposition mixing board panel
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Transpozisiya",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Səs tonu pilləsi",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.activeTransposition-- },
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        ) {
                            Text("-1", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = (if (viewModel.activeTransposition >= 0) "+" else "") + viewModel.activeTransposition,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 13.sp
                            )
                        }

                        IconButton(
                            onClick = { viewModel.activeTransposition++ },
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        ) {
                            Text("+1", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        }

                        if (viewModel.activeTransposition != 0) {
                            TextButton(
                                onClick = { viewModel.activeTransposition = 0 },
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text("Sıfırla", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        // Live formatted scrollable lyrics with chords using custom premium styling
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Sözlər və Akordlar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    TransposedSongContent(
                        rawText = song.chordsAndLyrics,
                        transposition = viewModel.activeTransposition
                    )
                }
            }
        }

        // Comments header
        item {
            Text(
                text = "Rəylər (${comments.size})",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 10.dp)
            )
        }

        // Add review section
        item {
            if (viewModel.currentUser != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        OutlinedTextField(
                            value = userCommentText,
                            onValueChange = { userCommentText = it },
                            placeholder = { Text("Mahnı haqqında rəy yazın...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("comment_input"),
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                viewModel.postComment(song.id, userCommentText)
                                userCommentText = ""
                            },
                            modifier = Modifier
                                .align(Alignment.End)
                                .testTag("comment_submit_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Rəyini Göndər", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        TextButton(
                            onClick = { viewModel.navigateTo(Screen.Account) },
                            modifier = Modifier.testTag("prompt_login_comment")
                        ) {
                            Text("Rəy yazmaq üçün daxil olun 🔑", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        // List comments with visually distinguishable avatar indicators
        if (comments.isEmpty()) {
            item {
                Text(
                    text = "Hələ rəy yazılmayıb. İlk siz rəy bildirin! 📝",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(comments) { comment ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Determinstic beautiful background user letters
                        val firstChar = comment.username.firstOrNull()?.uppercaseChar() ?: 'U'
                        val colorHash = comment.username.hashCode()
                        val avatarColor = remember(comment.username) {
                            val hues = listOf(
                                Color(0xFFE57373), Color(0xFFF06292), Color(0xFFBA68C8),
                                Color(0xFF9575CD), Color(0xFF7986CB), Color(0xFF64B5F6),
                                Color(0xFF4FC3F7), Color(0xFF4DB6AC), Color(0xFF81C784),
                                Color(0xFFFFB74D), Color(0xFFFF8A65), Color(0xFFA1887F)
                            )
                            hues[Math.abs(colorHash) % hues.size]
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(avatarColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = firstChar.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "@${comment.username}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "az öncə",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = comment.commentText,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddSongScreen(viewModel: MainViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
            item {
                Text(
                    text = "Yeni Mahnı Göndər",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Text(
                    text = "Yazdığınız mahnılar professional musiqiçilərimizin (admin) yoxlamasından keçdikdən sonra portalda yayımlanacaqdır.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 12.dp),
                    lineHeight = 16.sp
                )
            }

            item {
                OutlinedTextField(
                    value = viewModel.inputSongTitle,
                    onValueChange = { viewModel.inputSongTitle = it },
                    label = { Text("Mahnı Adı (örn: Sarı Gəlin)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("input_title"),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = viewModel.inputSongArtist,
                    onValueChange = { viewModel.inputSongArtist = it },
                    label = { Text("Sənətçi / İfaçı (örn: Xalq Mahnısı)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("input_artist"),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = viewModel.inputSongCapo,
                    onValueChange = { viewModel.inputSongCapo = it },
                    label = { Text("Kapo nömrəsi (örn: 0, 1, 2, ...)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("input_capo"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            // Quick Chord Inserter Drawer formatted beautifully
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Akord Köməkçisi (Sürətli Əlavə Et)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val helperChords = listOf("Am", "C", "D", "Dm", "E", "Em", "F", "G", "A", "B", "A#", "G#")
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        helperChords.forEach { chord ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                    .border(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.inputSongChordsAndLyrics += "[$chord]"
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = chord,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = viewModel.inputSongChordsAndLyrics,
                    onValueChange = { viewModel.inputSongChordsAndLyrics = it },
                    label = { Text("Mahnının Sözləri və Akordları (örn: [Am]Saçın ucun)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 6.dp)
                        .testTag("input_lyrics"),
                    placeholder = {
                        Text(
                            "[Am]Saçın ucun [Dm]hörməzlər\n" +
                            "[G]Gülü sulu [C]dərməzlər"
                        )
                    }
                )
            }

            // Real-time Preview Section
            item {
                if (viewModel.inputSongChordsAndLyrics.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        ),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "✨ Canlı Önizləmə",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            TransposedSongContent(
                                rawText = viewModel.inputSongChordsAndLyrics,
                                transposition = 0
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        viewModel.submitSong(onCompleted = {
                            viewModel.navigateTo(Screen.Home)
                        })
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(vertical = 4.dp)
                        .testTag("submit_song_btn")
                ) {
                    Text("Mahnını Gönder", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
}

@Composable
fun AccountScreen(viewModel: MainViewModel) {
    val user = viewModel.currentUser

    var isRegistryTab by remember { mutableStateOf(false) }

    var usernameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    var showGoogleDialog by remember { mutableStateOf(false) }
    var googleEmailInput by remember { mutableStateOf("") }
    var googleUsernameInput by remember { mutableStateOf("") }

    if (showGoogleDialog) {
        AlertDialog(
            onDismissRequest = { showGoogleDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("G ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black, fontSize = 24.sp)
                    Text("Google ilə daxil ol", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Daxil olmaq istədiyiniz Google (Gmail) hesab məlumatlarını qeyd edin. Bütün istifadəçilər daxil ola bilər:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    OutlinedTextField(
                        value = googleEmailInput,
                        onValueChange = { googleEmailInput = it },
                        label = { Text("E-poçt (Gmail)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth().testTag("google_dialog_email")
                    )
                    OutlinedTextField(
                        value = googleUsernameInput,
                        onValueChange = { googleUsernameInput = it },
                        label = { Text("İstifadəçi adı (Username)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth().testTag("google_dialog_username")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (googleEmailInput.isNotBlank() && googleUsernameInput.isNotBlank()) {
                            viewModel.loginWithGoogleFirebase(googleEmailInput.trim(), googleUsernameInput.trim())
                            showGoogleDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Daxil Ol", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoogleDialog = false }) {
                    Text("İmtina Et")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        if (user != null) {
            // Logged in statistics/profile
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.username.take(1).uppercase(),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "@${user.username}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = user.email,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (user.isAdmin) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = GoldenAmber.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = GoldenAmber,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Sistem Administratoru",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldenAmber
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        if (user.isAdmin) {
                            Button(
                                onClick = { viewModel.navigateTo(Screen.AdminPanel) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("admin_redirect_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber, contentColor = Color.Black)
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = "Admin")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Admin Panelinə Daxil Ol", fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Button(
                            onClick = { viewModel.logout() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("logout_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Sistemdən Çıxış", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Authentication Forms formatted beautifully matching our palette
            item {
                Text(
                    text = if (isRegistryTab) "Qeydiyyatdan Keç" else "Sistemə Daxil Ol",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )

                Text(
                    text = if (isRegistryTab) "Yeni hesab yaradaraq öz akord repertuarını yarat!" else "Hesabına daxil olaraq mahnı və rəylər əlavə et.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Tab selectors matching the visual pill stadium dock style
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!isRegistryTab) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { isRegistryTab = false }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Giriş",
                                fontWeight = FontWeight.Bold,
                                color = if (!isRegistryTab) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isRegistryTab) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { isRegistryTab = true }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Qeydiyyat",
                                fontWeight = FontWeight.Bold,
                                color = if (isRegistryTab) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            if (isRegistryTab) {
                // Register fields
                item {
                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { usernameInput = it },
                        label = { Text("İstifadəçi adı") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("reg_username"),
                        singleLine = true
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Musiqiçi E-maili") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("reg_email"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Parol") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("reg_password"),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
            }

            item {
                Button(
                    onClick = {
                        if (isRegistryTab) {
                            viewModel.register(usernameInput, emailInput, passwordInput)
                        } else {
                            viewModel.login(emailInput, passwordInput)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(vertical = 8.dp)
                        .testTag("auth_submit_btn")
                ) {
                    Text(if (isRegistryTab) "Hesab Yarat" else "Daxil Ol", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    Text(
                        text = " və ya ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                }
            }

            item {
                Button(
                    onClick = {
                        showGoogleDialog = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .testTag("google_login_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "G ",
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Google ilə Daxil Ol (Firebase)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "💡 Qeyd",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Admin girişi üçün e-mail: admin@azchords.com və parol: admin2026 daxil edin. Bu xüsusi panel gizlidir.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPanelScreen(viewModel: MainViewModel) {
    if (viewModel.currentUser?.isAdmin != true) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Yetkiniz yoxdur.")
        }
        return
    }

    val pendingSongs by viewModel.pendingSongs.collectAsState()
    val editingId = viewModel.editingSongId

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(
                text = "🛡️ Admin Nəzarət Paneli",
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                color = GoldenAmber,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        if (editingId != null) {
            // Edit chords drawer
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.5.dp, GoldenAmber)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Mahnı Redaktə Edilir",
                                fontWeight = FontWeight.Black,
                                color = GoldenAmber,
                                fontSize = 16.sp
                            )
                            IconButton(onClick = { viewModel.editingSongId = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close edit", tint = GoldenAmber)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = viewModel.editSongTitle,
                            onValueChange = { viewModel.editSongTitle = it },
                            label = { Text("Mahnı Adı") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldenAmber),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("edit_title")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = viewModel.editSongArtist,
                            onValueChange = { viewModel.editSongArtist = it },
                            label = { Text("İfaçı/Artist") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldenAmber),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("edit_artist")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = viewModel.editSongCapo,
                            onValueChange = { viewModel.editSongCapo = it },
                            label = { Text("Kapo") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldenAmber),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("edit_capo"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val editingChords = listOf("Am", "C", "D", "Dm", "E", "Em", "F", "G")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            editingChords.forEach { c ->
                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(4.dp))
                                        .clickable { viewModel.editSongChordsAndLyrics += "[$c]" }
                                        .padding(6.dp)
                                ) {
                                    Text(c, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = viewModel.editSongChordsAndLyrics,
                            onValueChange = { viewModel.editSongChordsAndLyrics = it },
                            label = { Text("Sözlər və Akordlar") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldenAmber),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(140.dp).testTag("edit_lyrics")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Yazılma önizləməsi:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldenAmber)
                        TransposedSongContent(
                            rawText = viewModel.editSongChordsAndLyrics,
                            transposition = 0
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.saveEditedSong(onCompleted = {
                                    // Complete and clear
                                })
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_edit_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber, contentColor = Color.Black)
                        ) {
                            Text("Dəyişiklikləri və Təsdiqi Yadda Saxla", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Təsdiq gözləyən mahnılar (${pendingSongs.size})",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        if (pendingSongs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("Təbrik edirik! Yeni gözləyən mahnı yoxdur. 🎉", fontWeight = FontWeight.Medium, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    }
                }
            }
        } else {
            items(pendingSongs, key = { it.id }) { song ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("pending_song_item_${song.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = song.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = song.artist,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Göndərən: @${song.submittedByUsername}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { viewModel.startEditingSong(song) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit song", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Düzənlə", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.approveSong(song.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber, contentColor = Color.Black)
                                ) {
                                    Text("Təsdiqlə", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Brief raw preview of chords
                        Text(
                            text = "Gələn akord şəkli:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = song.chordsAndLyrics.take(120) + "...",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.deleteSong(song.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.align(Alignment.End),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Sil / Rədd et", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
