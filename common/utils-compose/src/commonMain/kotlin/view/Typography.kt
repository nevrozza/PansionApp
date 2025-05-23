package view

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily


@Composable
fun scaledTypography(font: FontFamily, fontMultiply: Float) = MaterialTheme.typography.copy(
    displayLarge = MaterialTheme.typography.displayLarge.copy(
        fontFamily = font,
        fontSize = MaterialTheme.typography.displayLarge.fontSize * fontMultiply,
        lineHeight = MaterialTheme.typography.displayLarge.lineHeight * fontMultiply
    ),
    displayMedium = MaterialTheme.typography.displayMedium.copy(
        fontFamily = font,
        fontSize = MaterialTheme.typography.displayMedium.fontSize * fontMultiply,
        lineHeight = MaterialTheme.typography.displayMedium.lineHeight * fontMultiply
    ),
    displaySmall = MaterialTheme.typography.displaySmall.copy(
        fontFamily = font,
        fontSize = MaterialTheme.typography.displaySmall.fontSize * fontMultiply,
        lineHeight = MaterialTheme.typography.displaySmall.lineHeight * fontMultiply
    ),
    headlineLarge = MaterialTheme.typography.headlineLarge.copy(
        fontFamily = font,
        fontSize = MaterialTheme.typography.headlineLarge.fontSize * fontMultiply,
        lineHeight = MaterialTheme.typography.headlineLarge.lineHeight * fontMultiply
    ),
    headlineMedium = MaterialTheme.typography.headlineMedium.copy(
        fontFamily = font,
        fontSize = MaterialTheme.typography.headlineMedium.fontSize * fontMultiply,
        lineHeight = MaterialTheme.typography.headlineMedium.lineHeight * fontMultiply
    ),
    headlineSmall = MaterialTheme.typography.headlineSmall.copy(
        fontFamily = font,
        fontSize = MaterialTheme.typography.headlineSmall.fontSize * fontMultiply,
        lineHeight = MaterialTheme.typography.headlineSmall.lineHeight * fontMultiply
    ),
    titleLarge = MaterialTheme.typography.titleLarge.copy(
        fontFamily = font,
        fontSize = MaterialTheme.typography.titleLarge.fontSize * fontMultiply,
        lineHeight = MaterialTheme.typography.titleLarge.lineHeight * fontMultiply
    ),
    titleMedium = MaterialTheme.typography.titleMedium.copy(
        fontFamily = font,
        fontSize = MaterialTheme.typography.titleMedium.fontSize * fontMultiply,
        lineHeight = MaterialTheme.typography.titleMedium.lineHeight * fontMultiply
    ),
    titleSmall = MaterialTheme.typography.titleSmall.copy(
        fontFamily = font,
        fontSize = MaterialTheme.typography.titleSmall.fontSize * fontMultiply,
        lineHeight = MaterialTheme.typography.titleSmall.lineHeight * fontMultiply
    ),
    bodyLarge = MaterialTheme.typography.bodyLarge.copy(
        fontFamily = font,
        fontSize = MaterialTheme.typography.bodyLarge.fontSize * fontMultiply,
        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * fontMultiply
    ),
    bodyMedium = MaterialTheme.typography.bodyMedium.copy(
        fontFamily = font,
        fontSize = MaterialTheme.typography.bodyMedium.fontSize * fontMultiply,
        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * fontMultiply
    ),
    bodySmall = MaterialTheme.typography.bodySmall.copy(
        fontFamily = font,
        fontSize = MaterialTheme.typography.bodySmall.fontSize * fontMultiply,
        lineHeight = MaterialTheme.typography.bodySmall.lineHeight * fontMultiply
    ),
    labelLarge = MaterialTheme.typography.labelLarge.copy(
        fontFamily = font,
        fontSize = MaterialTheme.typography.labelLarge.fontSize * fontMultiply,
        lineHeight = MaterialTheme.typography.labelLarge.lineHeight * fontMultiply
    ),
    labelMedium = MaterialTheme.typography.labelMedium.copy(
        fontFamily = font,
        fontSize = MaterialTheme.typography.labelMedium.fontSize * fontMultiply,
        lineHeight = MaterialTheme.typography.labelMedium.lineHeight * fontMultiply
    ),
    labelSmall = MaterialTheme.typography.labelSmall.copy(
        fontFamily = font,
        fontSize = MaterialTheme.typography.labelSmall.fontSize * fontMultiply,
        lineHeight = MaterialTheme.typography.labelSmall.lineHeight * fontMultiply
    )
)