package com.team7.meetmind.ui.auth

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.team7.meetmind.ui.auth.components.GradientAuthButton
import com.team7.meetmind.ui.auth.components.MeetMindTextField
import com.team7.meetmind.ui.auth.components.clickableNoRipple
import com.team7.meetmind.ui.theme.SignalAmber
import com.team7.meetmind.ui.theme.TextMuted

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AuthUiState.Success -> {
                viewModel.resetState()
                onRegisterSuccess()
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
            IconButton(onClick = onNavigateToLogin) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại", tint = TextMuted)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Tạo tài khoản",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "MeetMind sẽ tự động ghi âm, phiên âm và tóm tắt cuộc họp giúp bạn",
                style = MaterialTheme.typography.bodyLarge,
                color = TextMuted
            )
            Spacer(Modifier.height(32.dp))
            MeetMindTextField(
                value = name,
                onValueChange = { name = it },
                label = "Họ và tên",
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = TextMuted) }
            )
            Spacer(Modifier.height(16.dp))
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
                label = "Mật khẩu (tối thiểu 6 ký tự)",
                isPassword = true,
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = TextMuted) }
            )
            Spacer(Modifier.height(16.dp))
            MeetMindTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Nhập lại mật khẩu",
                isPassword = true,
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = TextMuted) }
            )
            Spacer(Modifier.height(28.dp))
            GradientAuthButton(
                text = "Đăng ký",
                loading = uiState is AuthUiState.Loading,
                enabled = uiState !is AuthUiState.Loading,
                onClick = { viewModel.register(name, email, password, confirmPassword) }
            )
            Spacer(Modifier.height(28.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Đã có tài khoản? ", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Đăng nhập",
                    color = SignalAmber,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickableNoRipple { onNavigateToLogin() }
                )
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
