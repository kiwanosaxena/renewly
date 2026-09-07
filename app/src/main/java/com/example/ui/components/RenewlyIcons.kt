package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RenewlyIcon(
  name: String,
  modifier: Modifier = Modifier,
  color: Color,
  size: Dp = 20.dp
) {
  Canvas(modifier = modifier.size(size)) {
    val strokeWidth = 2.dp.toPx()
    val stroke = Stroke(
      width = strokeWidth,
      cap = StrokeCap.Round,
      join = StrokeJoin.Round
    )

    // ViewBox is 24x24
    val scale = this.size.width / 24f

    when (name) {
      "arrow-right" -> {
        drawLine(color, Offset(5f * scale, 12f * scale), Offset(18f * scale, 12f * scale), strokeWidth = strokeWidth, cap = StrokeCap.Round)
        val p = Path().apply {
          moveTo(13f * scale, 6f * scale)
          lineTo(19f * scale, 12f * scale)
          lineTo(13f * scale, 18f * scale)
        }
        drawPath(p, color, style = stroke)
      }
      "check" -> {
        val p = Path().apply {
          moveTo(4f * scale, 12.5f * scale)
          lineTo(9f * scale, 17.5f * scale)
          lineTo(20f * scale, 6.5f * scale)
        }
        drawPath(p, color, style = stroke)
      }
      "chevron-left" -> {
        val p = Path().apply {
          moveTo(15f * scale, 6f * scale)
          lineTo(9f * scale, 12f * scale)
          lineTo(15f * scale, 18f * scale)
        }
        drawPath(p, color, style = stroke)
      }
      "chevron-right" -> {
        val p = Path().apply {
          moveTo(9f * scale, 6f * scale)
          lineTo(15f * scale, 12f * scale)
          lineTo(9f * scale, 18f * scale)
        }
        drawPath(p, color, style = stroke)
      }
      "close" -> {
        drawLine(color, Offset(6f * scale, 6f * scale), Offset(18f * scale, 18f * scale), strokeWidth = strokeWidth, cap = StrokeCap.Round)
        drawLine(color, Offset(18f * scale, 6f * scale), Offset(6f * scale, 18f * scale), strokeWidth = strokeWidth, cap = StrokeCap.Round)
      }
      "search" -> {
        drawCircle(color, radius = 6.5f * scale, center = Offset(10.5f * scale, 10.5f * scale), style = stroke)
        drawLine(color, Offset(15.5f * scale, 15.5f * scale), Offset(20.5f * scale, 20.5f * scale), strokeWidth = strokeWidth, cap = StrokeCap.Round)
      }
      "bell" -> {
        val p = Path().apply {
          moveTo(18f * scale, 8f * scale)
          cubicTo(18f * scale, 4.69f * scale, 15.31f * scale, 2f * scale, 12f * scale, 2f * scale)
          cubicTo(8.69f * scale, 2f * scale, 6f * scale, 4.69f * scale, 6f * scale, 8f * scale)
          cubicTo(6f * scale, 15f * scale, 3f * scale, 17f * scale, 3f * scale, 17f * scale)
          lineTo(21f * scale, 17f * scale)
          cubicTo(21f * scale, 17f * scale, 18f * scale, 15f * scale, 18f * scale, 8f * scale)
          close()
        }
        drawPath(p, color, style = stroke)
        val p2 = Path().apply {
          moveTo(10.3f * scale, 20f * scale)
          cubicTo(10.7f * scale, 21.2f * scale, 11.8f * scale, 22f * scale, 12f * scale, 22f * scale)
          cubicTo(12.2f * scale, 22f * scale, 13.3f * scale, 21.2f * scale, 13.7f * scale, 20f * scale)
        }
        drawPath(p2, color, style = stroke)
      }
      "pencil" -> {
        val p = Path().apply {
          moveTo(4f * scale, 20f * scale)
          lineTo(8f * scale, 20f * scale)
          lineTo(20f * scale, 8f * scale)
          lineTo(16f * scale, 4f * scale)
          lineTo(4f * scale, 16f * scale)
          close()
        }
        drawPath(p, color, style = stroke)
        drawLine(color, Offset(13.5f * scale, 6.5f * scale), Offset(17.5f * scale, 10.5f * scale), strokeWidth = strokeWidth, cap = StrokeCap.Round)
      }
      "home" -> {
        val p = Path().apply {
          moveTo(4f * scale, 11f * scale)
          lineTo(12f * scale, 5f * scale)
          lineTo(20f * scale, 11f * scale)
          lineTo(20f * scale, 20f * scale)
          lineTo(4f * scale, 20f * scale)
          close()
        }
        drawPath(p, color, style = stroke)
      }
      "calendar" -> {
        val p = Path().apply {
          moveTo(4f * scale, 6f * scale)
          lineTo(20f * scale, 6f * scale)
          lineTo(20f * scale, 20f * scale)
          lineTo(4f * scale, 20f * scale)
          close()
        }
        drawPath(p, color, style = stroke)
        drawLine(color, Offset(4f * scale, 10f * scale), Offset(20f * scale, 10f * scale), strokeWidth = strokeWidth)
        drawLine(color, Offset(8f * scale, 3f * scale), Offset(8f * scale, 7f * scale), strokeWidth = strokeWidth, cap = StrokeCap.Round)
        drawLine(color, Offset(16f * scale, 3f * scale), Offset(16f * scale, 7f * scale), strokeWidth = strokeWidth, cap = StrokeCap.Round)
      }
      "insights" -> {
        drawLine(color, Offset(6f * scale, 20f * scale), Offset(6f * scale, 12f * scale), strokeWidth = strokeWidth, cap = StrokeCap.Round)
        drawLine(color, Offset(12f * scale, 20f * scale), Offset(12f * scale, 7f * scale), strokeWidth = strokeWidth, cap = StrokeCap.Round)
        drawLine(color, Offset(18f * scale, 20f * scale), Offset(18f * scale, 4f * scale), strokeWidth = strokeWidth, cap = StrokeCap.Round)
      }
      "plus" -> {
        drawLine(color, Offset(12f * scale, 5f * scale), Offset(12f * scale, 19f * scale), strokeWidth = strokeWidth, cap = StrokeCap.Round)
        drawLine(color, Offset(5f * scale, 12f * scale), Offset(19f * scale, 12f * scale), strokeWidth = strokeWidth, cap = StrokeCap.Round)
      }
      "sun" -> {
        drawCircle(color, radius = 4f * scale, center = Offset(12f * scale, 12f * scale), style = stroke)
        drawLine(color, Offset(12f * scale, 2f * scale), Offset(12f * scale, 4f * scale), strokeWidth = strokeWidth, cap = StrokeCap.Round)
        drawLine(color, Offset(12f * scale, 20f * scale), Offset(12f * scale, 22f * scale), strokeWidth = strokeWidth, cap = StrokeCap.Round)
        drawLine(color, Offset(2f * scale, 12f * scale), Offset(4f * scale, 12f * scale), strokeWidth = strokeWidth, cap = StrokeCap.Round)
        drawLine(color, Offset(20f * scale, 12f * scale), Offset(22f * scale, 12f * scale), strokeWidth = strokeWidth, cap = StrokeCap.Round)
      }
    }
  }
}
