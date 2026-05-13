// viewmodel/AuthViewModel.kt
package com.freshexpress.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages login state: phone number entry → OTP verification.
 *
 * HOW IT WORKS (explain in interview):
 * - We hold state in MutableStateFlow (Kotlin's reactive stream).
 * - The UI collects these flows via collectAsStateWithLifecycle().
 * - When user taps "Send OTP", we just flip a boolean (fake OTP = 1234).
 * - When user taps "Verify", we check if input == "1234".
 * - ViewModel survives screen rotation; Screen doesn't lose state.
 */
class AuthViewModel : ViewModel() {

    private val FAKE_OTP = "1234"

    // ── Phone entry state ────────────────────────────────────────────────────
    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    // ── OTP entry state ──────────────────────────────────────────────────────
    private val _otp = MutableStateFlow("")
    val otp: StateFlow<String> = _otp.asStateFlow()

    // ── UI flags ─────────────────────────────────────────────────────────────
    private val _otpSent = MutableStateFlow(false)
    val otpSent: StateFlow<Boolean> = _otpSent.asStateFlow()

    private val _otpError = MutableStateFlow(false)
    val otpError: StateFlow<Boolean> = _otpError.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    // ── Actions ───────────────────────────────────────────────────────────────

    fun onPhoneChange(value: String) {
        // Accept only digits, max 10
        if (value.all { it.isDigit() } && value.length <= 10) {
            _phone.value = value
        }
    }

    fun onOtpChange(value: String) {
        if (value.all { it.isDigit() } && value.length <= 4) {
            _otp.value = value
            _otpError.value = false   // clear error on new input
        }
    }

    /** Called when user taps "Send OTP" — in real app this hits your backend */
    fun sendOtp() {
        if (_phone.value.length == 10) {
            _otpSent.value = true
        }
    }

    /** Called when user taps "Verify OTP" */
    fun verifyOtp() {
        if (_otp.value == FAKE_OTP) {
            _loginSuccess.value = true
        } else {
            _otpError.value = true
        }
    }

    fun resetError() { _otpError.value = false }
}
