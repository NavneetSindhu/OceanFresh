package com.example.oceanfresh.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// CRITICAL: Import both items extensions to avoid scope conflicts
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.items

import com.example.oceanfresh.data.model.Product
import com.example.oceanfresh.ui.components.AddressBottomSheet
import com.example.oceanfresh.ui.theme.*
import com.example.oceanfresh.viewmodel.CartViewModel
import com.example.oceanfresh.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeVM: HomeViewModel,
    cartVM: CartViewModel,
    onNavigateToCart: () -> Unit
) {
    val isDark      by homeVM.isDarkMode.collectAsStateWithLifecycle()
    val products    by homeVM.filteredProducts.collectAsStateWithLifecycle()
    val selectedCat by homeVM.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by homeVM.searchQuery.collectAsStateWithLifecycle()
    val cartCount   by cartVM.cartCount.collectAsStateWithLifecycle()
    val cartTotal   by cartVM.cartTotal.collectAsStateWithLifecycle()
    val cartMap     by cartVM.cartMap.collectAsStateWithLifecycle()

    var showAddressSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF0F0F0F) else MaterialTheme.colorScheme.surface)
    ) {
        // 1. SCROLLABLE GRID
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 14.dp, end = 14.dp,
                top = 188.dp, // Matches fixed header height
                bottom = 160.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement   = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { GridItemSpan(2) }) {
                Column {
                    PromoBanner(isDark)
                    Spacer(Modifier.height(16.dp))

                    // CATEGORY ROW
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        // Using items(items = ...) forces String type resolution
                        items(items = homeVM.categories) { cat ->
                            OFCategoryChip(
                                label    = cat,
                                selected = cat == selectedCat,
                                isDark   = isDark,
                                onClick  = { homeVM.onCategorySelected(cat) }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // SECTION HEADER
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedCat == "All") "Fresh For You 🌿" else selectedCat,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                        AnimatedContent(targetState = products.size, label = "count") { count ->
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = GreenPrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    "$count items",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = GreenPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }

            // EMPTY STATE
            if (products.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🕵️", fontSize = 56.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("No results found",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDark) Color.White.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // PRODUCT CARDS
            items(products, key = { it.id }) { product ->
                val quantity = cartMap[product.id]?.quantity ?: 0
                OFProductCard(
                    product  = product,
                    quantity = quantity,
                    isDark   = isDark,
                    onAdd    = { cartVM.addToCart(product) },
                    onRemove = { cartVM.removeOneFromCart(product) }
                )
            }
        }

        // 2. FIXED HEADER (Glassmorphism Effect)
        Surface(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
            color = if (isDark) Color(0xFF0F0F0F).copy(alpha = 0.95f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ) {
            Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
                OFTopBar(isDark = isDark, onThemeToggle = homeVM::toggleDarkTheme,onAddressClick = { showAddressSheet = true })
                OFSearchBar(
                    query         = searchQuery,
                    onQueryChange = homeVM::onSearchQueryChange,
                    isDark        = isDark
                )
                Spacer(Modifier.height(6.dp))
            }
        }

        if (showAddressSheet) {
            AddressBottomSheet(
                isDark = isDark,
                onDismiss = { showAddressSheet = false },
                onAddressSaved = { newAddress ->
                    // Logic to update address in ViewModel can go here
                    showAddressSheet = false
                }
            )
        }

        // 3. FLOATING CART FAB
        AnimatedVisibility(
            visible = cartCount > 0,
            enter   = slideInVertically { it } + fadeIn(tween(300)),
            exit    = slideOutVertically { it } + fadeOut(tween(200)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 96.dp)
        ) {
            OFCartFab(
                itemCount = cartCount,
                total     = cartTotal,
                onClick   = onNavigateToCart
            )
        }
    }
}

@Composable
fun OFProductCard(
    product: Product,
    quantity: Int,
    isDark: Boolean,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var triggerPulse by remember { mutableStateOf(false) }
    val pulse by animateFloatAsState(
        targetValue = if (triggerPulse) 1.04f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        finishedListener = { triggerPulse = false },
        label = "pulse"
    )

    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White

    Card(
        modifier = Modifier.fillMaxWidth().scale(pulse),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 3.dp),
        border = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)) else null
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isDark) Color(0xFF2C2C2E)
                        else Color(0xFFF7F7F7)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(product.imageUrl, fontSize = 42.sp)

                if (product.discount > 0) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopStart).padding(6.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFF9800) // Orange discount
                    ) {
                        Text("${product.discount}% OFF",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(product.name, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = if (isDark) Color.White else Color.Black)
            Text(product.unit, style = MaterialTheme.typography.labelSmall, color = if (isDark) Color.White.copy(alpha = 0.45f) else Color.Gray)
            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("₹${product.discountedPrice.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = GreenPrimary)
                }

                AnimatedContent(targetState = quantity > 0, label = "stepper") { inCart ->
                    if (inCart) {
                        Row(modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(GreenPrimary).padding(2.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onRemove() }, modifier = Modifier.size(28.dp)) {
                                Icon(if (quantity == 1) Icons.Rounded.Delete else Icons.Rounded.Remove, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Text("$quantity", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                            IconButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); triggerPulse = true; onAdd() }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Rounded.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    } else {
                        Button(onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); triggerPulse = true; onAdd() }, modifier = Modifier.height(34.dp), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) {
                            Text("ADD", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OFTopBar(
    isDark: Boolean,
    onThemeToggle: () -> Unit,
    onAddressClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).clickable { onAddressClick() }.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(OrangePrimary.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Explore, null, tint = OrangePrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Delivery at", style = MaterialTheme.typography.labelMedium, color = if (isDark) Color.White.copy(0.6f) else Color.Gray)
                    Icon(Icons.Rounded.ExpandMore, null, modifier = Modifier.size(16.dp), tint = if (isDark) Color.White.copy(0.6f) else Color.Gray)
                }
                Text("Sector 18, Noida", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = if (isDark) Color.White else Color.Black)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HeaderIconButton(isDark, onThemeToggle) {
                Icon(if (isDark) Icons.Rounded.LightMode else Icons.Rounded.DarkMode, null, tint = if (isDark) OrangePrimary else GreenPrimary, modifier = Modifier.size(20.dp))
            }
            HeaderIconButton(isDark, onNotificationClick) {
                Icon(Icons.Rounded.NotificationsNone, null, tint = if (isDark) Color.White else Color.Black, modifier = Modifier.size(22.dp))
            }
            HeaderIconButton(isDark, onProfileClick) {
                Icon(Icons.Rounded.AccountCircle, null, tint = if (isDark) Color.White else Color.Black, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
fun HeaderIconButton(isDark: Boolean, onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.size(40.dp).background(if (isDark) Color(0xFF2C2C2E) else Color(0xFFF3F3F3), RoundedCornerShape(12.dp)).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
fun OFSearchBar(query: String, onQueryChange: (String) -> Unit, isDark: Boolean) {
    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth().height(52.dp).background(if (isDark) Color(0xFF1C1C1E) else Color.White, RoundedCornerShape(26.dp)).border(1.dp, Color.Gray.copy(0.1f), RoundedCornerShape(26.dp))) {
        TextField(
            value = query, onValueChange = onQueryChange,
            placeholder = { Text("Search groceries...") },
            leadingIcon = { Icon(Icons.Rounded.Search, null, tint = GreenPrimary) },
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
            modifier = Modifier.fillMaxSize(), singleLine = true
        )
    }
}

@Composable
fun PromoBanner(isDark: Boolean) {
    Box(modifier = Modifier.fillMaxWidth().height(108.dp).clip(RoundedCornerShape(22.dp)).background(GreenPrimary).padding(horizontal = 20.dp)) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text("Fresh Essentials", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Text("at your door", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
        }
        Text("🚛", fontSize = 52.sp, modifier = Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
fun OFCategoryChip(label: String, selected: Boolean, isDark: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected, onClick = onClick,
        label = { Text(label) },
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GreenPrimary, selectedLabelColor = Color.White)
    )
}

@Composable
fun OFCartFab(itemCount: Int, total: Double, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(58.dp).padding(horizontal = 16.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("$itemCount items", color = Color.White, fontWeight = FontWeight.Bold)
            Text("View Cart", color = Color.White)
            Text("₹${total.toInt()}", color = Color.White, fontWeight = FontWeight.ExtraBold)
        }
    }
}