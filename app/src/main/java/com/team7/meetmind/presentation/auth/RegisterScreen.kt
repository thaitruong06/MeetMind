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
import com.team7.meetmind.presentation.theme.TextMuted
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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
            IconButton(onClick = onNavigateToLogin) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = TextMuted)
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
                loading = uiState.isLoading,
                enabled = !uiState.isLoading,
                onClick = { viewModel.onRegisterClick(name, email, password, confirmPassword) }
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
