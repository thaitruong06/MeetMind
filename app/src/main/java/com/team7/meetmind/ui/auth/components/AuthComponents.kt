package com.team7.meetmind.ui.auth.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team7.meetmind.ui.theme.SignalAmber
import com.team7.meetmind.ui.theme.SignalCoral
import com.team7.meetmind.ui.theme.SignalViolet
import com.team7.meetmind.ui.theme.TextFaint
import com.team7.meetmind.ui.theme.TextMuted
import kotlin.random.Random

/**
 * Chạm (click) không hiệu ứng ripple — dùng cho các liên kết dạng text ("Quên mật khẩu?",
 * "Đăng ký ngay"...) để trông nhẹ nhàng, không giống nút bấm.
 */
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}

/**
 * Sóng âm trang trí ở đầu màn hình đăng nhập/đăng ký — gợi hình ảnh app đang "lắng nghe"
 * cuộc họp. Mỗi thanh dao động biên độ ngẫu nhiên, lệch pha nhau để trông tự nhiên như audio thật,
 * tô theo dải gradient vàng-cam-tím là dải màu tín hiệu riêng của app.
 */
@Composable
fun VoiceWaveformHero(modifier: Modifier = Modifier, barCount: Int = 28) {
    val infinite = rememberInfiniteTransition(label = "waveform")
    val offsets = remember { List(barCount) { Random.nextInt(0, 400) } }
    val amplitudes = offsets.mapIndexed { index, offsetMs ->
        infinite.animateFloat(
            initialValue = 0.18f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600 + (index % 5) * 90, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(offsetMs)
            ),
            label = "bar$index"
        )
    }

    Canvas(modifier = modifier.fillMaxWidth().height(96.dp)) {
        val barWidth = size.width / (barCount * 1.6f)
        val gap = barWidth * 0.6f
        val totalWidth = barCount * (barWidth + gap)
        val startX = (size.width - totalWidth) / 2f
        val gradient = Brush.horizontalGradient(colors = listOf(SignalAmber, SignalCoral, SignalViolet))
        amplitudes.forEachIndexed { index, amp ->
            val h = size.height * amp.value
            val x = startX + index * (barWidth + gap)
            drawRoundRect(
                brush = gradient,
                topLeft = Offset(x, (size.height - h) / 2f),
                size = Size(barWidth, h),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}

/**
 * Ô nhập liệu dùng chung cho các màn hình xác thực — viền mảnh, không nền, để sự chú ý
 * dồn vào nút hành động chính (GradientAuthButton) thay vì lặp lại khối bo tròn khắp nơi.
 */
@Composable
fun MeetMindTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        leadingIcon = leadingIcon,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        imageVector = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (visible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                        tint = TextMuted
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = TextFaint,
            focusedBorderColor = SignalAmber,
            cursorColor = SignalAmber
        ),
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Nút hành động chính — nơi duy nhất dùng gradient đậm (vàng → cam) trên toàn màn hình,
 * để nó thực sự nổi bật thay vì bị pha loãng bởi những điểm nhấn khác.
 */
@Composable
fun GradientAuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    enabled: Boolean = true
) {
    val activeGradient = Brush.horizontalGradient(listOf(SignalAmber, SignalCoral))
    val disabledGradient = Brush.horizontalGradient(listOf(TextFaint, TextFaint))
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(27.dp))
            .background(if (enabled && !loading) activeGradient else disabledGradient)
            .clickable(enabled = enabled && !loading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
        } else {
            Text(text, color = Color(0xFF231405), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}
