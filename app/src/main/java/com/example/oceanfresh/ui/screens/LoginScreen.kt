// ui/screens/LoginScreen.kt
package com.example.oceanfresh.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.oceanfresh.ui.theme.GreenPrimary
import com.example.oceanfresh.ui.theme.OnSurface
import com.example.oceanfresh.ui.theme.OnSurfaceVariant
import com.freshexpress.viewmodel.AuthViewModel

/**
 * LOGIN SCREEN
 * Step 1: Enter 10-digit phone number → tap "Send OTP"
 * Step 2: Enter 4-digit OTP (hardcoded: 1234) → tap "Verify"
 * On success: navigate to Home
 *
 * Uses AnimatedContent to swap between the two steps smoothly.
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val phone by viewModel.phone.collectAsStateWithLifecycle()
    val otp by viewModel.otp.collectAsStateWithLifecycle()
    val otpSent by viewModel.otpSent.collectAsStateWithLifecycle()
    val otpError by viewModel.otpError.collectAsStateWithLifecycle()
    val loggedIn by viewModel.loginSuccess.collectAsStateWithLifecycle()

    LaunchedEffect(loggedIn) {
        if (loggedIn) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── 1. BACKGROUND DECORATION ──────────────────────────────────────
        // Large subtle organic circles for a premium "Blinkit" feel
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-50).dp)
                .background(GreenPrimary.copy(alpha = 0.08f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .navigationBarsPadding() // Ensures it stays above system nav bar
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))

            // ── 2. BRANDING ───────────────────────────────────────────────
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(28.dp),
                color = GreenPrimary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🥦", fontSize = 50.sp) // Larger, cleaner emoji
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "OceanFresh",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-1).sp
                ),
                color = OnSurface
            )
            Text(
                "Groceries delivered in 10 minutes",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant
            )

            Spacer(Modifier.height(56.dp))

            // ── 3. LOGIN CARD ─────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFF3F3F3)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(28.dp)) {
                    AnimatedContent(
                        targetState = otpSent,
                        transitionSpec = {
                            (fadeIn() + slideInHorizontally { it }).togetherWith(
                                fadeOut() + slideOutHorizontally { -it }
                            )
                        },
                        label = "step_transition"
                    ) { isOtpStep ->
                        if (!isOtpStep) {
                            PhoneInputStep(phone, viewModel)
                        } else {
                            OtpInputStep(otp, phone, otpError, viewModel)
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f)) // Push footer to bottom

            Text(
                "By continuing, you agree to our\nTerms of Service & Privacy Policy",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun PhoneInputStep(phone: String, viewModel: AuthViewModel) {
    Column {
        Text(
            "Welcome Back!",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = OnSurface
        )
        Text(
            "Login or Signup to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
        )

        Spacer(Modifier.height(28.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = viewModel::onPhoneChange,
            label = { Text("Mobile Number") },
            prefix = {
                Text(
                    "🇮🇳 +91 ",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenPrimary,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedLabelColor = GreenPrimary
            )
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = viewModel::sendOtp,
            enabled = phone.length == 10,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
            Text(
                "Get OTP",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun OtpInputStep(otp: String, phone: String, isError: Boolean, viewModel: AuthViewModel) {
    Column {
        Text(
            "Verify Phone",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            "Enter code sent to +91 $phone",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
        )

        Spacer(Modifier.height(28.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = viewModel::onOtpChange,
            placeholder = { Text("• • • •", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
            isError = isError,
            supportingText = if (isError) { { Text("Demo OTP is 1234", color = Color.Red) } } else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            // Styling it like a code input
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                textAlign = TextAlign.Center,
                letterSpacing = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isError) Color.Red else GreenPrimary
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) Color.Red else GreenPrimary,
                unfocusedBorderColor = Color(0xFFE0E0E0)
            )
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = viewModel::verifyOtp,
            enabled = otp.length == 4,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
            Text(
                "Verify & Continue",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        TextButton(
            onClick = { /* Reset logic if needed */ },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Edit Phone Number", color = GreenPrimary, style = MaterialTheme.typography.labelLarge)
        }
    }
}