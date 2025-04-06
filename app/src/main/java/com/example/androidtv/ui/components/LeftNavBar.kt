package com.example.androidtv.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidtv.R
import android.util.Log

/**
 * Navigation destination for the left menu
 * Each destination represents a screen in the app
 */
sealed class NavDestination(val id: Int, val label: String, val icon: Int) {
    data object Home : NavDestination(1, "Home", R.drawable.ic_home)
    data object Search : NavDestination(2, "Search", R.drawable.ic_search)
    data object Movies : NavDestination(3, "Movies", R.drawable.ic_movie)
    data object TVShows : NavDestination(4, "TV", R.drawable.ic_tv)
    data object Favorites : NavDestination(5, "Favorites", R.drawable.ic_favorite)
    data object Menu : NavDestination(6, "Menu", R.drawable.ic_menu)
    data object Settings : NavDestination(7, "Settings", R.drawable.ic_settings)
}

/**
 * List of all navigation items in the menu
 */
var menuItems = listOf(
    NavDestination.Home,
    NavDestination.Search,
    NavDestination.Movies,
    NavDestination.TVShows,
    NavDestination.Favorites,
    NavDestination.Menu,
    NavDestination.Settings
)


/**
 * Left navigation menu component
 * Handles TV-specific focus and navigation
 * 
 * @param onItemSelected Callback when a menu item is selected
 */
@Composable
fun LeftMenu(
    onItemSelected: (Int) -> Unit
) {
    var selectedItemId by remember { mutableIntStateOf(1) }
    
    Log.d("LeftMenu", "Setting up navigation menu with ${menuItems.size} items")

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(72.dp)
            .background(Color.Black.copy(alpha = 0.85f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            menuItems.forEach { item ->
                var focusRequester = remember { FocusRequester() }
                
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = if (item.id == selectedItemId) Color.White.copy(alpha = 0.2f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            Log.d("LeftMenu", "Menu item ${item.label} focus changed to: ${focusState.isFocused}")
                            if (focusState.isFocused) {
                                selectedItemId = item.id
                                onItemSelected(item.id)
                            }
                        }
                        .focusable(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(item.icon),
                            contentDescription = item.label,
                            tint = if (item.id == selectedItemId) Color.White else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            maxLines = 1,
                            color = if (item.id == selectedItemId) Color.White else Color.Gray,
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }
        }
    }
}
