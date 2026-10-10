package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.VyboraSurfaceGlass
import com.example.ui.viewmodel.NavigationItem

@Composable
fun VyboraBottomNav(
    currentScreen: NavigationItem,
    onNavigate: (NavigationItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(VyboraSurfaceGlass)
                .border(width = 1.dp, color = GlassBorder, shape = RoundedCornerShape(32.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItemView(
                title = "Home",
                selected = currentScreen == NavigationItem.HOME,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                tag = "nav_home",
                onClick = { onNavigate(NavigationItem.HOME) }
            )
            BottomNavItemView(
                title = "Search",
                selected = currentScreen == NavigationItem.SEARCH,
                selectedIcon = Icons.Filled.Search,
                unselectedIcon = Icons.Outlined.Search,
                tag = "nav_search",
                onClick = { onNavigate(NavigationItem.SEARCH) }
            )
            BottomNavItemView(
                title = "Library",
                selected = currentScreen == NavigationItem.LIBRARY,
                selectedIcon = Icons.Filled.LibraryMusic,
                unselectedIcon = Icons.Outlined.LibraryMusic,
                tag = "nav_library",
                onClick = { onNavigate(NavigationItem.LIBRARY) }
            )
            BottomNavItemView(
                title = "Profile",
                selected = currentScreen == NavigationItem.PROFILE,
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                tag = "nav_profile",
                onClick = { onNavigate(NavigationItem.PROFILE) }
            )
        }
    }
}

@Composable
private fun BottomNavItemView(
    title: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    tag: String,
    onClick: () -> Unit
) {
    val tintColor by animateColorAsState(
        targetValue = if (selected) EmeraldGreen else MutedGrey,
        animationSpec = tween(durationMillis = 200),
        label = "nav_tint"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .testTag(tag)
            .minimumInteractiveComponentSize()
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = if (selected) selectedIcon else unselectedIcon,
            contentDescription = title,
            tint = tintColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = tintColor,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
