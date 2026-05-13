// ui/screens/OrderSuccessScreen.kt
package com.example.oceanfresh.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.oceanfresh.ui.theme.GreenPrimary
import com.example.oceanfresh.ui.theme.OnSurface
import com.example.oceanfresh.ui.theme.OnSurfaceVariant
import com.example.oceanfresh.ui.theme.OutlineVariant
import com.example.oceanfresh.ui.theme.Surface
import com.example.oceanfresh.viewmodel.CartViewModel

/**
 * ORDER SUCCESS SCREEN
 * - Animated checkmark (scale + fade)
 * - Order ID
 * - Estimated delivery time
 * - Order summary (item count, total, address, payment)
 * - "Back to Home" button resets navigation stack
 *
 * WHY LaunchedEffect for animation?
 * LaunchedEffect(Unit) runs once when the composable first enters composition.
 * It's the Compose equivalent of onCreate(). Perfect for triggering
 * a one-shot animation when the success screen appears.
 */
@Composable
fun OrderSuccessScreen(
    cartVM: CartViewModel,
    onBackToHome: () -> Unit
) {
    val order by cartVM.lastOrder.collectAsStateWithLifecycle()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    // Pulse animation for the background circle
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // ── 1. CELEBRATORY ICON ─────────────────────────────────────────
            Box(contentAlignment = Alignment.Center) {
                // Pulsing background glow
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(pulseScale)
                        .background(GreenPrimary.copy(alpha = 0.1f), CircleShape)
                )

                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = GreenPrimary,
                    shadowElevation = 8.dp
                ) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(28.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── 2. SUCCESS MESSAGE ──────────────────────────────────────────
            Text(
                "Order Placed! 🎉",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = OnSurface,
                textAlign = TextAlign.Center
            )

            Text(
                "Order #${order?.orderId ?: "---"}",
                style = MaterialTheme.typography.labelLarge,
                color = GreenPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(32.dp))

            // ── 3. DELIVERY ETA SECTION ─────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = GreenPrimary.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(48.dp).background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🛵", fontSize = 24.sp)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            "Arriving in ${order?.estimatedDeliveryMinutes ?: "10-15"} mins",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GreenPrimary
                        )
                        Text(
                            "Delivery partner is on the way",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── 4. RECEIPT CARD ─────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Order Receipt", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(16.dp))

                    ReceiptRow("Total Items", "${order?.items?.sumOf { it.quantity }}")
                    ReceiptRow("Delivery Address", order?.deliveryAddress ?: "", isMultiLine = true)
                    ReceiptRow("Payment Method", order?.paymentMethod ?: "")

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp, color = OutlineVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Amount Paid", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("₹${order?.totalAmount?.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GreenPrimary)
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // ── 5. ACTION BUTTONS ───────────────────────────────────────────
            Button(
                onClick = onBackToHome,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text("Continue Shopping", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = { /* Handle Tracking */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Track My Order", color = OnSurfaceVariant, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String, isMultiLine: Boolean = false) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            maxLines = if (isMultiLine) 2 else 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}