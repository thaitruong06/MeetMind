package com.team7.meetmind.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.team7.meetmind.navigation.Routes
import com.team7.meetmind.R
import com.team7.meetmind.ui.theme.ThemeMode
import com.team7.meetmind.ui.auth.components.GradientAuthButton
import com.team7.meetmind.ui.common.MeetMindBottomBar
import com.team7.meetmind.ui.common.MeetMindTopBar
import com.team7.meetmind.ui.theme.InkSurface
import com.team7.meetmind.ui.theme.TextFaint
import com.team7.meetmind.ui.theme.TextMuted
import com.team7.meetmind.ui.theme.TextPrimary
import com.team7.meetmind.ui.theme.SignalAmber

@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    userLabel: String,
    onNavigate: (String) -> Unit,
    onRecordClick: () -> Unit,
    onLogout: () -> Unit
) {
    var cloudSync by remember { mutableStateOf(true) }

    Scaffold(
        topBar = { MeetMindTopBar(title = "Cài đặt") },
        bottomBar = { MeetMindBottomBar(currentRoute = Routes.SETTINGS, onNavigate = onNavigate, onRecordClick = onRecordClick) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            SettingsSectionLabel("Tài khoản")
            SettingsRow(icon = Icons.Filled.Person, title = userLabel, subtitle = "Quản lý tài khoản")
            Spacer(Modifier.height(20.dp))
            SettingsSectionLabel("Ứng dụng")
            ThemeModeSelector(themeMode = themeMode, onThemeModeChange = onThemeModeChange)
            Spacer(Modifier.height(10.dp))
            SettingsToggleRow(
                icon = Icons.Filled.CloudSync,
                title = "Đồng bộ Cloud",
                checked = cloudSync,
                onCheckedChange = { cloudSync = it }
            )
            SettingsRow(icon = Icons.Filled.Language, title = "Ngôn ngữ nhận diện", subtitle = "Tiếng Việt")
            SettingsRow(icon = Icons.Filled.CalendarMonth, title = "Kết nối Google Calendar", subtitle = "Chưa kết nối")
            Spacer(Modifier.height(28.dp))
            GradientAuthButton(text = "Đăng xuất", onClick = onLogout)
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
internal fun ThemeModeSelector(themeMode: ThemeMode, onThemeModeChange: (ThemeMode) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(InkSurface)
            .padding(vertical = 12.dp)
            .selectableGroup()
    ) {
        Text(
            text = stringResource(R.string.appearance_title),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = stringResource(R.string.appearance_description),
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        ThemeMode.entries.forEach { mode ->
            val label = when (mode) {
                ThemeMode.SYSTEM -> R.string.theme_system
                ThemeMode.LIGHT -> R.string.theme_light
                ThemeMode.DARK -> R.string.theme_dark
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .selectable(
                        selected = themeMode == mode,
                        role = Role.RadioButton,
                        onClick = { onThemeModeChange(mode) }
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = themeMode == mode, onClick = null)
                Spacer(Modifier.width(12.dp))
                Text(stringResource(label), color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun SettingsSectionLabel(text: String) {
    Text(text, color = TextFaint, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun SettingsRow(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(InkSurface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextMuted)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
        }
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(InkSurface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextMuted)
        Spacer(Modifier.width(12.dp))
        Text(title, color = TextPrimary, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = SignalAmber,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
    Spacer(Modifier.height(10.dp))
}
