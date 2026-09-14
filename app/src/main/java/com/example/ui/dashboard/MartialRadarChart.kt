package com.example.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserMartialProfileEntity
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.WaterCyan
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MartialRadarChart(
    profile: UserMartialProfileEntity,
    modifier: Modifier = Modifier
) {
    val labels = listOf("Hip Mobility", "Centerline", "Thoracic", "Torque", "Recovery", "Breath")
    val rawValues = listOf(
        profile.hipMobility / 100f,
        profile.centerlineStability / 100f,
        profile.thoracicMobility / 100f,
        profile.kineticTorque / 100f,
        profile.recoveryReadiness / 100f,
        profile.breathControl / 100f
    )

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000),
        label = "RadarAnimation"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = ObsidianSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MARTIAL-YOGA RADAR",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                    )
                    Text(
                        text = "Biomechanical Balance Index",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                    )
                }
                Box(
                    modifier = Modifier
                        .background(WaterCyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    val overall = ((profile.hipMobility + profile.centerlineStability + profile.thoracicMobility +
                            profile.kineticTorque + profile.recoveryReadiness + profile.breathControl) / 6).toInt()
                    Text(
                        text = "$overall% READINESS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = WaterCyan
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Radar
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(220.dp)) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val radius = (size.minDimension / 2) * 0.82f
                    val angleStep = (2 * Math.PI / labels.size).toFloat()

                    // Draw concentric polygon rings (25%, 50%, 75%, 100%)
                    val levels = listOf(0.25f, 0.5f, 0.75f, 1.0f)
                    for (level in levels) {
                        val ringPath = Path()
                        for (i in labels.indices) {
                            val angle = i * angleStep - (Math.PI / 2).toFloat()
                            val x = centerX + radius * level * cos(angle)
                            val y = centerY + radius * level * sin(angle)
                            if (i == 0) ringPath.moveTo(x, y) else ringPath.lineTo(x, y)
                        }
                        ringPath.close()
                        drawPath(
                            path = ringPath,
                            color = Color(0xFF283449).copy(alpha = 0.6f),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }

                    // Draw radial spokes
                    for (i in labels.indices) {
                        val angle = i * angleStep - (Math.PI / 2).toFloat()
                        val x = centerX + radius * cos(angle)
                        val y = centerY + radius * sin(angle)
                        drawLine(
                            color = Color(0xFF334155).copy(alpha = 0.6f),
                            start = Offset(centerX, centerY),
                            end = Offset(x, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Draw active data polygon
                    val dataPath = Path()
                    val points = mutableListOf<Offset>()
                    for (i in rawValues.indices) {
                        val angle = i * angleStep - (Math.PI / 2).toFloat()
                        val v = rawValues[i] * animatedProgress
                        val x = centerX + radius * v * cos(angle)
                        val y = centerY + radius * v * sin(angle)
                        points.add(Offset(x, y))
                        if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
                    }
                    dataPath.close()

                    // Fill polygon with soft gold glow
                    drawPath(
                        path = dataPath,
                        color = GoldPrimary.copy(alpha = 0.28f),
                        style = Fill
                    )

                    // Stroke polygon outline
                    drawPath(
                        path = dataPath,
                        color = GoldPrimary,
                        style = Stroke(width = 2.5.dp.toPx())
                    )

                    // Draw glowing vertices
                    for (pt in points) {
                        drawCircle(
                            color = GoldLight,
                            radius = 4.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.dp.toPx(),
                            center = pt
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Attribute chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AttributePill("Hips", "${profile.hipMobility.toInt()}%", WaterCyan)
                AttributePill("Center", "${profile.centerlineStability.toInt()}%", GoldLight)
                AttributePill("Torque", "${profile.kineticTorque.toInt()}%", Color(0xFFF97316))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AttributePill("Thoracic", "${profile.thoracicMobility.toInt()}%", Color(0xFFA855F7))
                AttributePill("Recovery", "${profile.recoveryReadiness.toInt()}%", WaterCyan)
                AttributePill("Breath", "${profile.breathControl.toInt()}%", GoldLight)
            }
        }
    }
}

@Composable
private fun AttributePill(name: String, value: String, accentColor: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1A2230),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFF2E3D52))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(accentColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.sp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 11.sp
                )
            )
        }
    }
}
