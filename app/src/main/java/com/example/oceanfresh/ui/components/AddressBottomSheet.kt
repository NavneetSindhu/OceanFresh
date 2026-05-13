package com.example.oceanfresh.ui.components // Adjust package name as needed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.oceanfresh.ui.theme.GreenPrimary
import com.example.oceanfresh.ui.theme.OnSurface
import com.example.oceanfresh.ui.theme.OnSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressBottomSheet(
    onDismiss: () -> Unit,
    isDark: Boolean,
    onAddressSaved: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var addressInput by remember { mutableStateOf("") }
    var houseDetail by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        // Using your specific dark/light surface colors
        containerColor = if (isDark) Color(0xFF1C1C1E) else Color.White,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = if (isDark) Color.White.copy(0.2f) else Color.Gray.copy(0.3f)
            )
        },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 40.dp)
        ) {
            Text(
                text = "Select Delivery Location",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = if (isDark) Color.White else OnSurface
            )

            Spacer(Modifier.height(20.dp))

            // ── USE CURRENT LOCATION BUTTON ────────────────────────────────
            Surface(
                onClick = { /* Mock Location Fetch Logic */ },
                shape = RoundedCornerShape(16.dp),
                color = GreenPrimary.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MyLocation,
                        contentDescription = "Location",
                        tint = GreenPrimary
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Use current location",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GreenPrimary
                        )
                        Text(
                            "Using GPS for high accuracy",
                            style = MaterialTheme.typography.bodySmall,
                            color = GreenPrimary.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── MANUAL INPUTS ─────────────────────────────────────────────
            Text(
                "Enter Address Manually",
                style = MaterialTheme.typography.labelLarge,
                color = if (isDark) Color.White.copy(0.6f) else OnSurfaceVariant
            )

            Spacer(Modifier.height(12.dp))

            AddressTextField(
                value = addressInput,
                onValueChange = { addressInput = it },
                label = "Area / Sector / Locality",
                icon = Icons.Rounded.Search,
                isDark = isDark
            )

            Spacer(Modifier.height(12.dp))

            AddressTextField(
                value = houseDetail,
                onValueChange = { houseDetail = it },
                label = "House No. / Floor / Landmark",
                icon = Icons.Rounded.Home,
                isDark = isDark
            )

            Spacer(Modifier.height(32.dp))

            // ── SAVE BUTTON ────────────────────────────────────────────────
            Button(
                onClick = {
                    if (addressInput.isNotBlank()) {
                        onAddressSaved("$houseDetail, $addressInput")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(
                    "Confirm Address",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun AddressTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDark: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenPrimary,
            unfocusedBorderColor = if (isDark) Color.White.copy(0.1f) else Color.Gray.copy(0.2f),
            focusedLabelColor = GreenPrimary,
            unfocusedTextColor = if (isDark) Color.White else Color.Black,
            focusedTextColor = if (isDark) Color.White else Color.Black
        )
    )
}