package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// MohitOS Black + Gold Palette (Permanent Brand System)
val MohitBackground = Color(0xFF080808)        // Matte Black
val MohitDarkCharcoal = Color(0xFF151515)      // Dark Charcoal (Secondary background)
val MohitSurfaceVariant = Color(0xFF202020)    // Elevated cards / containers
val MohitSurfaceElevated = Color(0xFF262626)   // Higher elevation
val MohitBorder = Color(0xFF332D1C)            // Subtle metallic gold border tint

// Gold Accents
val MohitGoldPrimary = Color(0xFFD4AF37)       // Metallic Gold
val MohitGoldHighlight = Color(0xFFF5D76E)     // Champagne Gold
val MohitGoldDark = Color(0xFF9A7B1C)          // Deep burnished gold
val MohitGoldGlow = Color(0xFFFFF2B2)          // Pale gold sparkle

// Backward-compatibility aliases for Cyber tokens -> mapped to MohitOS Black & Gold
val CyberBackground = MohitBackground
val CyberSurface = MohitDarkCharcoal
val CyberSurfaceVariant = MohitSurfaceVariant
val CyberSurfaceElevated = MohitSurfaceElevated
val CyberBorder = MohitBorder

val CyberCyan = MohitGoldPrimary               // Replaced with Metallic Gold
val CyberCyanDark = Color(0xFF2B230B)          // Deep gold container
val CyberCyanGlow = MohitGoldHighlight         // Champagne Gold

val CyberIndigo = MohitGoldHighlight
val CyberIndigoContainer = Color(0xFF261E0A)

// Status Accents
val StatusGreen = Color(0xFF10B981)
val StatusGreenContainer = Color(0xFF064E3B)
val StatusAmber = Color(0xFFF59E0B)
val StatusAmberContainer = Color(0xFF78350F)
val StatusRed = Color(0xFFEF4444)
val StatusRedContainer = Color(0xFF7F1D1D)
val StatusBlue = Color(0xFF38BDF8)
val StatusBlueContainer = Color(0xFF0C4A6E)

// Typography Palette
val TextPrimary = Color(0xFFF5F5F5)            // Soft White
val TextSecondary = Color(0xFFA3A3A3)          // Muted Gray
val TextMuted = Color(0xFF737373)

// M3 Color Scheme Palette
val DarkPrimary = MohitGoldPrimary
val DarkOnPrimary = Color(0xFF1A1400)
val DarkPrimaryContainer = Color(0xFF2D2409)
val DarkOnPrimaryContainer = MohitGoldHighlight

val DarkSecondary = MohitGoldHighlight
val DarkOnSecondary = Color(0xFF1A1400)
val DarkSecondaryContainer = Color(0xFF2B230A)
val DarkOnSecondaryContainer = Color(0xFFFFF6D1)

val DarkTertiary = StatusGreen
val DarkOnTertiary = Color(0xFFFFFFFF)
val DarkTertiaryContainer = StatusGreenContainer
val DarkOnTertiaryContainer = Color(0xFFA7F3D0)

val DarkBackground = MohitBackground
val DarkOnBackground = TextPrimary
val DarkSurface = MohitDarkCharcoal
val DarkOnSurface = TextPrimary
val DarkSurfaceVariant = MohitSurfaceVariant
val DarkOnSurfaceVariant = TextSecondary

val DarkError = StatusRed
val DarkOnError = Color(0xFFFFFFFF)
val DarkErrorContainer = StatusRedContainer
val DarkOnErrorContainer = Color(0xFFFECDD3)
