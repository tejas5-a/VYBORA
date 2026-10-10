package com.example.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.VyboraSurfaceGlass

/**
 * Applies iOS-inspired glassmorphism style to a composable:
 * Translucent frosted background, gradient sheen, subtle border, rounded corners,
 * and optional hardware blur when supported.
 */
fun Modifier.glassmorphism(
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = VyboraSurfaceGlass,
    borderColor: Color = GlassBorder,
    borderWidth: Dp = 1.dp,
    blurRadius: Dp = 16.dp
): Modifier = this
    .clip(shape)
    .then(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && blurRadius > 0.dp) {
            Modifier.blur(blurRadius)
        } else {
            Modifier
        }
    )
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                backgroundColor.copy(alpha = backgroundColor.alpha),
                backgroundColor.copy(alpha = (backgroundColor.alpha * 0.85f).coerceIn(0f, 1f))
            )
        ),
        shape = shape
    )
    .border(width = borderWidth, color = borderColor, shape = shape)

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = Color(0xBF161822),
    borderColor: Color = GlassBorder,
    borderWidth: Dp = 1.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.75f)
                    )
                ),
                shape = shape
            )
            .border(width = borderWidth, color = borderColor, shape = shape)
    ) {
        content()
    }
}
