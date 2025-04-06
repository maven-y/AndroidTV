package com.example.androidtv.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.androidtv.ui.components.LeftMenu
import com.example.androidtv.ui.screens.MovieListScreen
import com.example.androidtv.ui.theme.AndroidTVTheme
import com.example.androidtv.viewmodel.MovieViewModel
import android.util.Log
import android.view.WindowManager
import android.util.DisplayMetrics
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * Main entry point of the Android TV application
 * Handles navigation between screens and manages the overall UI layout
 *
 */
class MainActivity : ComponentActivity() {
    private val viewModel: MovieViewModel by viewModels()
    private val vibrator by lazy {
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    private val MIN_SCREEN_HEIGHT = 720  // Minimum height for proper layout
    
    init {
        Log.d("MainActivity", "Initializing main activity")
        // Add proper initialization error handling
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "Starting main activity with savedInstanceState: ${savedInstanceState != null}")

        // Check screen size requirements
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        
        if (displayMetrics.widthPixels < Companion.MIN_SCREEN_WIDTH || displayMetrics.heightPixels < MIN_SCREEN_HEIGHT) {
            Log.w("MainActivity", "Screen size too small: ${displayMetrics.widthPixels}x${displayMetrics.heightPixels}")
            ///show error dialog for unsupported screen size
        }

        setContent {
            AndroidTVTheme {
                // Track selected screen state with persistence
                var selectedScreenId by rememberSaveable { mutableIntStateOf(1) }
                var isInitialized by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    if (!isInitialized) {
                        Log.d("MainActivity", "Initializing UI state")
                        //Add proper initialization error handling
                        isInitialized = true
                    }
                }

                Log.d("MainActivity", "Setting up main UI with initial screen: $selectedScreenId")

                Row(modifier = Modifier.fillMaxSize()) {
                    // Left navigation menu with proper focus handling
                    LeftMenu(
                        onItemSelected = { selectedId ->
                            Log.d("MainActivity", "Navigating to screen: $selectedId")
                            selectedScreenId = selectedId

                        }
                    )

                    // Main content area with proper padding
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 24.dp)
                    ) {
                        when (selectedScreenId) {
                            1 -> MovieListScreen(
                                viewModel = viewModel,
                                onMovieClick = { movie ->
                                    Log.d("MainActivity", "Starting player for movie: ${movie.title}")
                                    
                                    // Validate movie data before starting player
                                    if (movie.videoUrl.isNullOrEmpty()) {
                                        Log.e("MainActivity", "Invalid video URL for movie: ${movie.title}")
                                        // Sow error dialog
                                        return@MovieListScreen
                                    }

                                    val intent = Intent(this@MainActivity, ExoPlayerActivity::class.java).apply {
                                        putExtra("movie_url", movie.videoUrl)
                                        putExtra("movie_name", movie.title)
                                        putExtra("movie_des", movie.description)
                                    }

                                    Log.d("MainActivity", "Starting player activity")
                                    startActivity(intent)
                                    
                                      //Add proper error handling for activity start
                                }
                            )

                            else -> Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Coming Soon",
                                        style = MaterialTheme.typography.h6,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "This feature is currently under development",
                                        style = MaterialTheme.typography.body2,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        Log.d("MainActivity", "Destroying main activity")
        

        
        super.onDestroy()
    }

    companion object {
        private const val MIN_SCREEN_WIDTH = 1280  // Minimum width for proper layout
    }
}
