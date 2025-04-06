package com.example.androidtv.ui.screens

import android.os.Vibrator
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.androidtv.R
import com.example.androidtv.data.Movie
import com.example.androidtv.ui.components.MovieCard
import com.example.androidtv.ui.theme.LocalVibrator
import com.example.androidtv.viewmodel.MovieViewModel
import kotlinx.coroutines.launch

/**
 * Main screen for displaying the movie list
 * Handles movie loading, selection, and TV navigation
 */
@Composable
fun MovieListScreen(
    viewModel: MovieViewModel,
    onMovieClick: (Movie) -> Unit,
    vibrator: Vibrator = LocalVibrator.current
) {
    val movies by viewModel.movies.collectAsState()
    val selectedMovie by viewModel.selectedMovie.collectAsState(initial = null)
    val backgroundImageUrl = remember { mutableStateOf<String?>(null) }
    val lastLoadedImageUrl = remember { mutableStateOf<String?>(null) }
    
    Log.d("MovieListScreen", "Loading movies, total count: ${movies.size}")

    // Track the last loaded image URL
    LaunchedEffect(selectedMovie) {
        val newImageUrl = selectedMovie?.backgroundImageUrl
        if (newImageUrl != null) {
            backgroundImageUrl.value = newImageUrl
            lastLoadedImageUrl.value = newImageUrl
            Log.d("MovieListScreen", "Updated background image to: $newImageUrl")
        } else if (lastLoadedImageUrl.value != null) {
            backgroundImageUrl.value = lastLoadedImageUrl.value
            Log.d("MovieListScreen", "Using last loaded background image")
        }
    }

    // Select first movie when movies list is loaded
    LaunchedEffect(movies) {
        if (movies.isNotEmpty() && selectedMovie == null) {
            Log.d("MovieListScreen", "Selecting first movie from list")
            viewModel.onMovieSelected(movies[0])
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        // Background Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val context = LocalContext.current

            var imageRequest = ImageRequest.Builder(context)
                .data(backgroundImageUrl.value ?: R.drawable.default_background)
                .crossfade(true)
                .error(R.drawable.default_image)
                .placeholder(R.drawable.default_background)
                .build()

            var painter = rememberAsyncImagePainter(model = imageRequest)

            Image(
                painter = painter,
                contentDescription = "Movie Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            )

            // Description text with background
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = selectedMovie?.title ?: "Select a movie",
                        style = MaterialTheme.typography.h5,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedMovie?.description ?: "No description available",
                        style = MaterialTheme.typography.body1,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedMovie?.studio ?: "No studio available",
                        style = MaterialTheme.typography.overline,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Movie Grid
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(movies) { movie ->



                    MovieCard(
                        movie = movie,
                        onClick = { onMovieClick(movie) },
                        onFocused = { focused ->
                            if (focused) {
                                Log.d("MovieListScreen", "Movie ${movie.title} focused")
                                viewModel.onMovieSelected(movie)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun rememberIsNavigationFocused(): Boolean {
    var isNavigationFocused by remember { mutableStateOf(false) }
    
    /* navigation focus checking logic*/
    // This could be based on navigation system's focus state
    return isNavigationFocused
}