package com.example.oceanfresh.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.oceanfresh.viewmodel.HomeViewModel

data class CategoryUIModel(
    val name: String,
    val emoji: String,
    val bgColor: Color
)

@Composable
fun CategoriesScreen(
    homeVM: HomeViewModel,
    onCategoryClick: (String) -> Unit
) {
    val isDark by homeVM.isDarkMode.collectAsStateWithLifecycle()

    // Placeholder data styled like Blinkit/Zepto
    val categoryList = listOf(
        CategoryUIModel("Fruits", "🍎", Color(0xFFFFE0E0)),
        CategoryUIModel("Vegetables", "🥦", Color(0xFFE0FFE0)),
        CategoryUIModel("Dairy", "🥛", Color(0xFFE0F4FF)),
        CategoryUIModel("Snacks", "🍿", Color(0xFFFFF4E0)),
        CategoryUIModel("Beverages", "🧃", Color(0xFFE0E0FF)),
        CategoryUIModel("Bakery", "🍞", Color(0xFFF5E0FF)),
        CategoryUIModel("Meat", "🥩", Color(0xFFFFE0F0)),
        CategoryUIModel("Personal Care", "🧼", Color(0xFFE0FFFF)),
        CategoryUIModel("Cleaning", "🧹", Color(0xFFF0F0F0)),
        CategoryUIModel("Pooja Needs", "🪔", Color(0xFFFFF9E0))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF0F0F0F) else MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Fixed Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDark) Color(0xFF0F0F0F).copy(alpha = 0.95f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ) {
                Column(modifier = Modifier.statusBarsPadding()) {
                    OFTopBar(isDark = isDark, onThemeToggle = homeVM::toggleDarkTheme)
                    Text(
                        "All Categories",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp),
                        color = if (isDark) Color.White else Color.Black
                    )
                }
            }

            // Grid of Categories
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3 columns for that catalog feel
                contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 120.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(categoryList) { category ->
                    CategoryCatalogItem(
                        category = category,
                        isDark = isDark,
                        onClick = { onCategoryClick(category.name) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCatalogItem(
    category: CategoryUIModel,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .background(
                    if (isDark) Color(0xFF1C1C1E) else category.bgColor,
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(category.emoji, fontSize = 40.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = if (isDark) Color.White.copy(alpha = 0.8f) else Color.Black,
            maxLines = 2,
            lineHeight = 16.sp
        )
    }
}