package com.example.androidtv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.androidtv.R
import com.example.androidtv.data.Movie

@Composable
fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
    onFocused: (Boolean) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .onFocusChanged { focusState ->
                if (isFocused != focusState.isFocused) {
                    isFocused = focusState.isFocused
                    onFocused(focusState.isFocused)
                }
            }
            .then(
                if (isFocused) Modifier.border(2.dp, Color.White, RoundedCornerShape(12.dp))
                else Modifier
            )
            .clickable { onClick() }
            .focusable(),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.cardImageUrl ?: R.drawable.placeholder_image)
                    .crossfade(true)
                    .error(R.drawable.default_image)       // Shown if error
                    .placeholder(R.drawable.placeholder_image) // while loading
                    .build(),
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = movie.title ?: "",
                    color = Color.White,
                    style = MaterialTheme.typography.h6

                )
            }
        }
    }
}