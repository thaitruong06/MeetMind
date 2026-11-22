package com.team7.meetmind.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.team7.meetmind.presentation.auth.components.GradientAuthButton
import com.team7.meetmind.presentation.auth.components.MeetMindTextField
import com.team7.meetmind.presentation.auth.components.clickableNoRipple
import com.team7.meetmind.presentation.theme.SignalAmber
import com.team7.meetmind.presentation.theme.StateSuccess
import com.team7.meetmind.presentation.theme.TextMuted
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    viewModel: ForgotPasswordViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var email by rememberSaveable { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onErrorShown()
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = TextMuted)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Quên mật khẩu",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(6.dp))

            val sentToEmail = uiState.sentToEmail
            if (sentToEmail == null) {
                Text(
                    "Nhập email đã đăng ký, MeetMind sẽ gửi liên kết để bạn đặt lại mật khẩu",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMuted
                )
                Spacer(Modifier.height(32.dp))
                MeetMindTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    keyboardType = KeyboardType.Email,
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = TextMuted) }
                )
                Spacer(Modifier.height(28.dp))
                GradientAuthButton(
                    text = "Gửi email đặt lại mật khẩu",
                    loading = uiState.isLoading,
                    enabled = !uiState.isLoading,
                    onClick = { viewModel.onSendEmail(email) }
                )
            } else {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = StateSuccess)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Nếu $sentToEmail đã đăng ký, bạn sẽ nhận được email đặt lại mật khẩu trong ít phút. " +
                        "Hãy bấm vào liên kết trong email (kiểm tra cả mục spam), đặt mật khẩu mới rồi quay lại đăng nhập.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(28.dp))
                GradientAuthButton(text = "Về trang đăng nhập", onClick = onBackToLogin)
                Spacer(Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "Gửi lại email",
                        color = if (uiState.isLoading) TextMuted else SignalAmber,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickableNoRipple { viewModel.onResend() }
                    )
                    Text(
                        "Đổi email",
                        color = SignalAmber,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickableNoRipple { viewModel.onChangeEmail() }
                    )
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
