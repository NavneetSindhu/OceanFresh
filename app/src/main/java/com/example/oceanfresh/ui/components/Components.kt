// ui/components/Components.kt
package com.example.oceanfresh.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.oceanfresh.data.model.Product
import com.example.oceanfresh.ui.theme.GreenContainer
import com.example.oceanfresh.ui.theme.GreenFixed
import com.example.oceanfresh.ui.theme.GreenPrimary
import com.example.oceanfresh.ui.theme.OnGreenContainer
import com.example.oceanfresh.ui.theme.OnSurface
import com.example.oceanfresh.ui.theme.OnSurfaceVariant
import com.example.oceanfresh.ui.theme.OrangeContainer
import com.example.oceanfresh.ui.theme.SurfaceContainerLow


// ─────────────────────────────────────────────────────────────────────────────
// SEARCH BAR
// A "Search Dock" — full width, rounded, Level-1 elevation card.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun FreshSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceContainerLow,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                Icons.Rounded.Search,
                contentDescription = null,
                tint = OnSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = OnSurface),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text(
                            "Search groceries...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = OnSurfaceVariant
                        )
                    }
                    inner()
                }
            )
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = OnSurfaceVariant)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PRODUCT CARD
// 2-column grid card with emoji image, name, weight, price, +/- stepper
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ProductCard(
    product: Product,
    quantity: Int,                  // from CartViewModel
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // ── Discount badge + image area ────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerLow),
                contentAlignment = Alignment.Center
            ) {
                Text(product.imageUrl, style = MaterialTheme.typography.displayLarge.copy(fontSize = TextUnit(40f, TextUnitType.Sp)))

                // Discount badge top-left
                if (product.discount > 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = OrangeContainer
                    ) {
                        Text(
                            "${product.discount}% OFF",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Organic badge top-right
                if (product.isOrganic) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = GreenFixed
                    ) {
                        Text(
                            "🌿",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Product info ───────────────────────────────────────────────
            Text(
                product.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = OnSurface
            )
            Text(
                product.unit,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            // ── Price row ──────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "₹${String.format("%.0f", product.discountedPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = GreenPrimary
                )
                if (product.discount > 0) {
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "₹${String.format("%.0f", product.originalPrice)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            textDecoration = TextDecoration.LineThrough
                        ),
                        color = OnSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // ── Add / Stepper button ───────────────────────────────────────
            // Shows "+" when qty=0; shows "- qty +" when qty>0
            AnimatedContent(
                targetState = quantity > 0,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "cart_anim"
            ) { hasItems ->
                if (!hasItems) {
                    // Simple ADD button
                    OutlinedButton(
                        onClick = onAdd,
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.5.dp, GreenPrimary),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "Add", tint = GreenPrimary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add", color = GreenPrimary, style = MaterialTheme.typography.labelLarge)
                    }
                } else {
                    // Stepper: [-] [qty] [+]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .clip(RoundedCornerShape(50))
                            .background(GreenPrimary),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Rounded.Remove, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            "$quantity",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        IconButton(onClick = onAdd, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Rounded.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CART FAB  — sticky bottom floating button showing item count + total
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun CartFab(
    itemCount: Int,
    total: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = itemCount > 0,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GreenContainer
                ) {
                    Text(
                        "$itemCount item${if (itemCount > 1) "s" else ""}",
                        style = MaterialTheme.typography.labelLarge,
                        color = OnGreenContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    "View Cart",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    "₹${String.format("%.0f", total)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}
