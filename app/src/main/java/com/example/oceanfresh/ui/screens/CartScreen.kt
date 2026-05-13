package com.example.oceanfresh.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.oceanfresh.data.model.CartItem
import com.example.oceanfresh.ui.theme.*
import com.example.oceanfresh.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartVM: CartViewModel,
    onBack: () -> Unit,
    onCheckout: () -> Unit
) {
    val cartItems by cartVM.cartItems.collectAsStateWithLifecycle()
    val cartTotal by cartVM.cartTotal.collectAsStateWithLifecycle()
    val isDark = LocalThemeIsDark.current // Use our custom local provider

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Review Cart",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "${cartItems.sumOf { it.quantity }} Items · ₹${String.format("%.0f", cartVM.grandTotal)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 16.dp
                ) {
                    Button(
                        onClick = onCheckout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .navigationBarsPadding()
                            .height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "₹${String.format("%.0f", cartVM.grandTotal)}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                                Text(
                                    "TOTAL AMOUNT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Proceed to Pay",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Icon(
                                    Icons.Rounded.ChevronRight,
                                    null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            EmptyCartView(padding, onBack)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding() + 16.dp
                )
            ) {
                // ⚡ 10 MIN DELIVERY BADGE
                item {
                    DeliveryBadge(isDark)
                }

                // CART ITEMS
                items(cartItems, key = { it.product.id }) { item ->
                    CartItemRow(
                        item = item,
                        onAdd = { cartVM.addToCart(item.product) },
                        onRemove = { cartVM.removeOneFromCart(item.product) }
                    )
                }

                // BILL SUMMARY
                item {
                    BillSummaryCard(
                        subtotal = cartTotal,
                        deliveryFee = cartVM.deliveryFee,
                        platformFee = cartVM.platformFee,
                        grandTotal = cartVM.grandTotal
                    )
                }
            }
        }
    }
}

@Composable
fun DeliveryBadge(isDark: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    CircleShape
                ),
                contentAlignment = Alignment.Center
            ) {
                Text("⚡", fontSize = 20.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    "Delivery in 10 minutes",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Express shipping to your saved address",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem, onAdd: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(item.product.imageUrl, fontSize = 32.sp)
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.product.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    item.product.unit,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "₹${item.product.discountedPrice.toInt()}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                // STEPPER
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                        Icon(
                            if (item.quantity == 1) Icons.Rounded.Delete else Icons.Rounded.Remove,
                            null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        "${item.quantity}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = onAdd, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Rounded.Add, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "₹${(item.quantity * item.product.discountedPrice).toInt()}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun BillSummaryCard(subtotal: Double, deliveryFee: Double, platformFee: Double, grandTotal: Double) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Payment Details",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(16.dp))

            BillRow("Items Total", subtotal)
            BillRow("Delivery Fee", deliveryFee, isGreen = deliveryFee == 0.0)
            BillRow("Platform Fee", platformFee)

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)
            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "Total Amount",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "₹${grandTotal.toInt()}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer, // Premium Dark Green Container
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "You are saving ₹${(subtotal * 0.15).toInt()} on this order",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun BillRow(label: String, amount: Double, isGreen: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            if (amount == 0.0) "FREE" else "₹${amount.toInt()}",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isGreen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun EmptyCartView(padding: PaddingValues, onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🛒", fontSize = 80.sp)
            Spacer(Modifier.height(16.dp))
            Text("Your cart is empty", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Shop Now", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}