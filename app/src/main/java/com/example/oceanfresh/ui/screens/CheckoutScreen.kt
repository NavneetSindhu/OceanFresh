package com.example.oceanfresh.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oceanfresh.ui.theme.*
import com.example.oceanfresh.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartVM: CartViewModel,
    onBack: () -> Unit,
    onOrderPlaced: () -> Unit
) {
    var address by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("COD") }
    var addressError by remember { mutableStateOf(false) }

    val cartTotal by cartVM.cartTotal.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Checkout", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Step 2 of 2", style = MaterialTheme.typography.labelSmall, color = GreenPrimary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back", modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Surface)
            )
        },
        containerColor = SurfaceContainerLow,
        bottomBar = {
            // STICKY PLACE ORDER FOOTER
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Button(
                    onClick = {
                        if (address.isBlank()) {
                            addressError = true
                        } else {
                            cartVM.placeOrder(address, paymentMethod)
                            onOrderPlaced()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("₹${String.format("%.0f", cartVM.grandTotal)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text("TOTAL PAYABLE", style = MaterialTheme.typography.labelSmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Place Order", style = MaterialTheme.typography.titleSmall)
                            Icon(Icons.Rounded.ChevronRight, null)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Delivery Address Section ───────────────────────────────────
            SectionCard(title = "Delivery Address", icon = Icons.Rounded.LocationOn) {
                OutlinedTextField(
                    value = address,
                    onValueChange = {
                        address = it
                        addressError = false
                    },
                    placeholder = { Text("House no, building name, street...", color = OnSurfaceVariant.copy(alpha = 0.6f)) },
                    isError = addressError,
                    supportingText = if (addressError) { { Text("Address is required to deliver", color = ErrorColor) } } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = OutlineVariant,
                        errorBorderColor = ErrorColor
                    )
                )

                Spacer(Modifier.height(12.dp))

                Text("Quick Select", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("🏠 Home", "🏢 Office", "📍 Other").forEach { label ->
                        FilterChip(
                            selected = false,
                            onClick = { address = "${label.substringAfter(" ")}, Sector 18, Noida" },
                            label = { Text(label) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(containerColor = SurfaceContainerLow)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Payment Method Section ─────────────────────────────────────
            SectionCard(title = "Payment Method", icon = Icons.Rounded.Payments) {
                PaymentOption(
                    label = "Cash on Delivery",
                    description = "Pay at your doorstep",
                    emoji = "💵",
                    selected = paymentMethod == "COD",
                    onClick = { paymentMethod = "COD" }
                )
                Spacer(Modifier.height(12.dp))
                PaymentOption(
                    label = "Online Payment",
                    description = "UPI, Cards, or NetBanking",
                    emoji = "📱",
                    selected = paymentMethod == "Online",
                    onClick = { paymentMethod = "Online" }
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Final Bill Breakdown ───────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Order Summary", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(12.dp))

                    SummaryRow("Items Total", cartTotal)
                    SummaryRow("Delivery Fee", cartVM.deliveryFee, isGreen = true)
                    SummaryRow("Platform Fee", cartVM.platformFee)

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = OutlineVariant)
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total to Pay", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold))
                        Text("₹${String.format("%.0f", cartVM.grandTotal)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold))
                    }
                }
            }

            Spacer(Modifier.height(32.dp)) // Extra space for scroll
        }
    }
}

@Composable
fun SectionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = OnSurface)
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun PaymentOption(label: String, description: String, emoji: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) GreenPrimary.copy(alpha = 0.08f) else Color.White,
        border = BorderStroke(1.dp, if (selected) GreenPrimary else OutlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(if(selected) GreenPrimary.copy(alpha = 0.1f) else SurfaceContainerLow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 20.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = if (selected) GreenPrimary else OnSurface)
                Text(description, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            }
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, amount: Double, isGreen: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
        Text(
            if (isGreen && amount == 25.0) "₹25" else "₹${amount.toInt()}",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isGreen && amount == 0.0) GreenPrimary else OnSurface
        )
    }
}