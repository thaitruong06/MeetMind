package com.team7.meetmind.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.team7.meetmind.ui.auth.components.GradientAuthButton
import com.team7.meetmind.ui.auth.components.MeetMindTextField
import com.team7.meetmind.ui.theme.StateSuccess
import com.team7.meetmind.ui.theme.TextMuted

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AuthUiState.Success -> {
                emailSent = true
                viewModel.resetState()
            }
            is AuthUiState.Error -> snackbarHostState.showSnackbar(state.message)
            else -> Unit
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            IconButton(onClick = onBackToLogin) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại", tint = TextMuted)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Quên mật khẩu",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Nhập email đã đăng ký, MeetMind sẽ gửi liên kết để bạn đặt lại mật khẩu",
                style = MaterialTheme.typography.bodyLarge,
                color = TextMuted
            )
            Spacer(Modifier.height(32.dp))

            if (emailSent) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = StateSuccess)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Đã gửi email đặt lại mật khẩu tới $email. Vui lòng kiểm tra hộp thư (kể cả mục spam).",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(28.dp))
                GradientAuthButton(text = "Quay lại đăng nhập", onClick = onBackToLogin)
            } else {
                MeetMindTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    keyboardType = KeyboardType.Email,
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = TextMuted) }
                )
                Spacer(Modifier.height(28.dp))
                GradientAuthButton(
                    text = "Gửi liên kết đặt lại",
                    loading = uiState is AuthUiState.Loading,
                    enabled = uiState !is AuthUiState.Loading,
                    onClick = { viewModel.resetPassword(email) }
                )
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
