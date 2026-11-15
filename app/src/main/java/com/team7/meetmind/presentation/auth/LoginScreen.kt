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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.team7.meetmind.presentation.auth.components.GradientAuthButton
import com.team7.meetmind.presentation.auth.components.MeetMindTextField
import com.team7.meetmind.presentation.auth.components.VoiceWaveformHero
import com.team7.meetmind.presentation.auth.components.clickableNoRipple
import com.team7.meetmind.presentation.theme.SignalAmber
import com.team7.meetmind.presentation.theme.TextMuted
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
            Spacer(Modifier.height(48.dp))
            VoiceWaveformHero()
            Spacer(Modifier.height(28.dp))
            Text(
                "Chào mừng trở lại",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Đăng nhập để tiếp tục lắng nghe và tóm tắt các cuộc họp của bạn",
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
            Spacer(Modifier.height(16.dp))
            MeetMindTextField(
                value = password,
                onValueChange = { password = it },
                label = "Mật khẩu",
                isPassword = true,
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = TextMuted) }
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Quên mật khẩu?",
                style = MaterialTheme.typography.labelLarge,
                color = SignalAmber,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickableNoRipple { onNavigateToForgotPassword() }
            )
            Spacer(Modifier.height(24.dp))
            GradientAuthButton(
                text = "Đăng nhập",
                loading = uiState.isLoading,
                enabled = !uiState.isLoading,
                onClick = { viewModel.onLoginClick(email, password) }
            )
            Spacer(Modifier.height(28.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Chưa có tài khoản? ", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Đăng ký ngay",
                    color = SignalAmber,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickableNoRipple { onNavigateToRegister() }
                )
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
