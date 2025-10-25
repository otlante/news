@file:Suppress("MagicNumber")
package com.otlante.news.presentation.ui.theme

import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.path

object CustomIcons {

    val OpenInNew: ImageVector
        get() {
            if (_Open_in_new != null) return _Open_in_new!!

            _Open_in_new = ImageVector.Builder(
                name = "Open_in_new",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000))
                ) {
                    moveTo(200f, 840f)
                    quadToRelative(-33f, 0f, -56.5f, -23.5f)
                    reflectiveQuadTo(120f, 760f)
                    verticalLineToRelative(-560f)
                    quadToRelative(0f, -33f, 23.5f, -56.5f)
                    reflectiveQuadTo(200f, 120f)
                    horizontalLineToRelative(240f)
                    quadToRelative(17f, 0f, 28.5f, 11.5f)
                    reflectiveQuadTo(480f, 160f)
                    reflectiveQuadToRelative(-11.5f, 28.5f)
                    reflectiveQuadTo(440f, 200f)
                    horizontalLineTo(200f)
                    verticalLineToRelative(560f)
                    horizontalLineToRelative(560f)
                    verticalLineToRelative(-240f)
                    quadToRelative(0f, -17f, 11.5f, -28.5f)
                    reflectiveQuadTo(800f, 480f)
                    reflectiveQuadToRelative(28.5f, 11.5f)
                    reflectiveQuadTo(840f, 520f)
                    verticalLineToRelative(240f)
                    quadToRelative(0f, 33f, -23.5f, 56.5f)
                    reflectiveQuadTo(760f, 840f)
                    close()
                    moveToRelative(560f, -584f)
                    lineTo(416f, 600f)
                    quadToRelative(-11f, 11f, -28f, 11f)
                    reflectiveQuadToRelative(-28f, -11f)
                    reflectiveQuadToRelative(-11f, -28f)
                    reflectiveQuadToRelative(11f, -28f)
                    lineToRelative(344f, -344f)
                    horizontalLineTo(600f)
                    quadToRelative(-17f, 0f, -28.5f, -11.5f)
                    reflectiveQuadTo(560f, 160f)
                    reflectiveQuadToRelative(11.5f, -28.5f)
                    reflectiveQuadTo(600f, 120f)
                    horizontalLineToRelative(200f)
                    quadToRelative(17f, 0f, 28.5f, 11.5f)
                    reflectiveQuadTo(840f, 160f)
                    verticalLineToRelative(200f)
                    quadToRelative(0f, 17f, -11.5f, 28.5f)
                    reflectiveQuadTo(800f, 400f)
                    reflectiveQuadToRelative(-28.5f, -11.5f)
                    reflectiveQuadTo(760f, 360f)
                    close()
                }
            }.build()

            return _Open_in_new!!
        }

    private var _Open_in_new: ImageVector? = null
}
