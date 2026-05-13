package com.example.oceanfresh.ui.components

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.oceanfresh.navigation.Routes

@Composable
fun FloatingFitSyncNavBar(
    currentDestination: String?,
    accentColor: Color,
    isDarkTheme: Boolean,
    onNavigate: (String) -> Unit
) {
    val glassBackgroundColor = if (isDarkTheme) Color(0xFF1C1C1E).copy(alpha = 0.95f) else Color(0xFFF2F2F7).copy(alpha = 0.90f)
    val glassBorderColor = if (isDarkTheme) Color.White.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.90f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp) // Floating padding
            .navigationBarsPadding() // Space for system nav bar
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = Color.Black.copy(alpha = if (isDarkTheme) 0.6f else 0.15f),
                spotColor = Color.Black.copy(alpha = if (isDarkTheme) 0.5f else 0.25f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(glassBackgroundColor)
            .border(width = 1.dp, color = glassBorderColor, shape = RoundedCornerShape(32.dp))
    ) {
        // Blur Effect for Android 12+ (API 31)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        renderEffect = BlurEffect(radiusX = 25f, radiusY = 25f, edgeTreatment = TileMode.Clamp)
                    }
            )
        }

        // ... inside FloatingFitSyncNavBar ...
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Triple("Home", Routes.HOME, Icons.Outlined.Home to Icons.Filled.Home),
                // New Categories Tab
                Triple("Categories", Routes.CATEGORIES, Icons.Outlined.Category to Icons.Filled.Category),
                Triple("Cart", Routes.CART, Icons.Outlined.ShoppingCart to Icons.Filled.ShoppingCart)
            )

            tabs.forEach { (label, route, icons) ->
                val isSelected = currentDestination == route
                AnimatedNavItem(
                    label = label,
                    icon = if (isSelected) icons.second else icons.first,
                    isSelected = isSelected,
                    accentColor = accentColor,
                    onClick = { onNavigate(route) }
                )
            }
        }
    }
}

@Composable
fun AnimatedNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) accentColor.copy(alpha = 0.15f) else Color.Transparent
    val contentColor = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = if (isSelected) 20.dp else 12.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            AnimatedVisibility(visible = isSelected) {
                Text(
                    text = label,
                    color = contentColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}