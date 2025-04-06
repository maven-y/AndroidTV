package com.example.androidtv.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.URLUtil
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.androidtv.R
import com.example.androidtv.ui.theme.AndroidTVTheme
import kotlinx.coroutines.flow.collectLatest

// Constants for better readability
private const val DEFAULT_PLAYER_TIMEOUT =
    2000L  // 2 seconds - might need to ajust based on testing
private const val HEADER_ANIMATION_DURATION = 300  // 300ms - feels a bit fast, might need tweaking
private const val HEADER_SLIDE_OFFSET =
    -40f  // Slide in from above - arbitrary value, might need adjustment

/**
 * Main activity for video playback
 * Handles video streaming and TV-specific UI controls
 *
 */
class ExoPlayerActivity : ComponentActivity() {
    private lateinit var exoPlayer: ExoPlayer
    private var exoPlayerView: PlayerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get video URL from intent - this is our main source of truth
        // ccheck validation for URL format
        val streamUrl = intent.getStringExtra("movie_url")

        // Fallback to default URL if none provided
        // dummyy url Show error message instead of using default Discuss with team - should we show an error or play default? Add analytics tracking for failed URLs
        val defaultUrl = getString(R.string.default_url)

        if (streamUrl == null) {
            Log.e("ExoPlayer", "No stream URL provided")
            // Show error dialog to user
            finish()
            return
        }

        exoPlayer = ExoPlayer.Builder(this).build().apply {
            var videoSource = if (URLUtil.isValidUrl(streamUrl)) streamUrl else defaultUrl
            setMediaItem(MediaItem.fromUri(videoSource))
            prepare()
            playWhenReady = true

            Log.d("ExoPlayer", "Player initialized with source: $videoSource")
        }

        // Set up UI with Compose
        setContent {
            AndroidTVTheme {
                // Ad proper error handling for UI rendering
                PlayerScreen(
                    player = exoPlayer,
                    title = intent.getStringExtra("movie_name") ?: "Unknown Title",
                    description = intent.getStringExtra("movie_des") ?: "No details available.",
                    onBack = {
//                          Add analytics tracking for back button
                        finish()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
         Log.d("ExoPlayer", "Releasing player resources")
      exoPlayer.release()
      exoPlayerView?.player = null
      exoPlayerView = null
    }

    @OptIn(UnstableApi::class)
    @Composable
    fun PlayerScreen(
        player: ExoPlayer,
        title: String,
        description: String,
        onBack: () -> Unit
    ) {
        // Add loading states
        var isControlsVisible by remember { mutableStateOf(true) }
        var lastInteraction by remember { mutableLongStateOf(System.currentTimeMillis()) }
        var isBuffering by remember { mutableStateOf(true) }
        var focusRequester = remember { FocusRequester() }

        Log.d("ExoPlayer", "sting up player screen with title: $title")

        DisposableEffect(Unit) {
            onDispose {
                Log.d("ExoPlayer", "Releasing player resources")
                player.release()
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
        ) {
         // Player view with controller
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        useController = true
                       setShowPreviousButton(false)
                        setShowNextButton(false)
                        setControllerShowTimeoutMs(DEFAULT_PLAYER_TIMEOUT.toInt())
                        this.player = player

                           //  controller visibility
                        setControllerVisibilityListener(PlayerView.ControllerVisibilityListener { visibility ->
                            isControlsVisible = visibility == View.VISIBLE
                            lastInteraction = System.currentTimeMillis()
                            Log.d(
                                "ExoPlayer",
                                "Controller visibility changed to: ${visibility == View.VISIBLE}"
                            )
                        })

                          //  buffering state
                        player.addListener(object : Player.Listener {
                            override fun onPlaybackStateChanged(playbackState: Int) {
                                isBuffering = playbackState == Player.STATE_BUFFERING
                                exoPlayerView?.showController()
                             Log.d("ExoPlayer", "Playback state changed to: $playbackState")
                            }
                        })

                        exoPlayerView = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Progressbar during buffering
            if (isBuffering) {
                CircularProgressIndicator(
                    modifier = Modifier
                      .align(Alignment.Center)
                        .size(48.dp),
                    color = Color.White,
                    strokeWidth = 4.dp
                )
            }

            //animtion overlay with title and description
            AnimatedVisibility(
                visible = isControlsVisible,
                enter = fadeIn(tween(HEADER_ANIMATION_DURATION, easing = FastOutSlowInEasing)),
                exit = fadeOut(tween(HEADER_ANIMATION_DURATION, easing = FastOutSlowInEasing)),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                PlayerOverlay(
                    title = title,
                    description = description,
                    onBackPressed = onBack
                )
            }

            // Autojhide overlay after inactivity
            LaunchedEffect(lastInteraction) {
                snapshotFlow { System.currentTimeMillis() - lastInteraction }
                    .collectLatest { elapsed ->
                        if (elapsed > DEFAULT_PLAYER_TIMEOUT) {
                            Log.d("ExoPlayer", "Auto-hiding controls after $elapsed ms")
                            isControlsVisible = false
                            exoPlayerView?.hideController()
                        }
                    }
            }
        }
    }

    @OptIn(UnstableApi::class)
    @Composable
    private fun PlayerOverlay(
        title: String,
        description: String,
        onBackPressed: () -> Unit
    ) {
        var screenWidth = LocalConfiguration.current.screenWidthDp
        var leftPadding = if (screenWidth > 1920) 72.dp else 38.dp

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        Log.d("ExoPlayer", "Back button clicked")
                        onBackPressed()
                    },
                    modifier = Modifier
                        .focusable()
                        .onFocusChanged { focusState ->
                            Log.d(
                                "ExoPlayer",
                                "Back button focus changed to: ${focusState.isFocused}"
                            )
                            if (focusState.isFocused) {
                                exoPlayerView?.showController()
                            }
                        }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back",
                        tint = Color.White
                    )
                }

                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = description,
                color = Color.White,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(start = leftPadding, top = 4.dp)
            )
        }
    }

    @OptIn(UnstableApi::class)
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
      Log.d("ExoPlayer", "Key pressed: $keyCode")
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                Log.d("ExoPlayer", "DPAD center pressed - showing controller")
                exoPlayerView?.showController()
                true
            }

            KeyEvent.KEYCODE_HOME -> {
                Log.d("ExoPlayer", "HOME key pressed - " +
                        "pausing and finishing")
                  exoPlayer.pause()
                  finish()
                  true
            }

            else ->
                super.onKeyDown(keyCode, event)
        }
    }
}
