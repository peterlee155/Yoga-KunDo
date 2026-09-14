package com.example.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TrainingProgressEntity
import com.example.data.model.UserMartialProfileEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WaterCyan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val progressHistory by viewModel.allProgress.collectAsStateWithLifecycle()
    var showCheckInDialog by remember { mutableStateOf(false) }

    val user = profile ?: UserMartialProfileEntity()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MARTIAL PROGRESS",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = TextWhite
                        )
                    )
                    Text(
                        text = "Tracking Fluidity, Stance & Combat Readiness",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }

                Button(
                    onClick = { showCheckInDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WaterCyan.copy(alpha = 0.2f),
                        contentColor = WaterCyan
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("check_in_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "Daily Check-in",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Check-In", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Top Martial Stats Summary
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("martial_stats_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    Brush.linearGradient(listOf(GoldPrimary, GoldDark)),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = "Martial Artist",
                                tint = ObsidianBlack,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(GoldPrimary.copy(alpha = 0.18f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = user.experienceLevel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = GoldLight,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "Discipline: ${user.primaryArt}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3 Metric Tiles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.LocalFireDepartment,
                            label = "Streak",
                            value = "${user.currentStreakDays} Days",
                            color = Color(0xFFF97316)
                        )
                        MetricTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Schedule,
                            label = "Mat Time",
                            value = "${user.totalMinutesTrained}m",
                            color = WaterCyan
                        )
                        MetricTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.FitnessCenter,
                            label = "Sessions",
                            value = "${user.sessionsCompleted}",
                            color = GoldLight
                        )
                    }
                }
            }
        }

        // Radar Attribute Chart
        item {
            MartialRadarChart(
                profile = user,
                modifier = Modifier.testTag("martial_radar_chart")
            )
        }

        // JKD Principle Quote
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "JKD PRINCIPLE OF THE DAY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = GoldLight
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"You must be shapeless, formless, like water. When you pour water in a cup, it becomes the cup. When you pour water in a bottle, it becomes the bottle. Water can drip and it can crash. Become like water, my friend.\"",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextWhite,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "— Bruce Lee, Founder of Jeet Kune Do",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = WaterCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        // Training Sessions History
        item {
            Text(
                text = "RECENT TRAINING SESSIONS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = TextMuted
                )
            )
        }

        if (progressHistory.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = ObsidianSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "No sessions yet",
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No completed sessions yet",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        )
                        Text(
                            text = "Start a yoga flow from the Training tab to record progress!",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }
                }
            }
        } else {
            items(progressHistory.take(5)) { record ->
                HistoryItemRow(record)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showCheckInDialog) {
        DailyCheckInDialog(
            user = user,
            onDismiss = { showCheckInDialog = false },
            onSave = { hip, center, thor, torq, recov, breath ->
                viewModel.updateAssessment(hip, center, thor, torq, recov, breath)
                showCheckInDialog = false
            }
        )
    }
}

@Composable
private fun MetricTile(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF19202C),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF263244))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 15.sp))
            Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp))
        }
    }
}

@Composable
private fun HistoryItemRow(record: TrainingProgressEntity) {
    val dateStr = remember(record.timestamp) {
        SimpleDateFormat("MMM d • h:mm a", Locale.getDefault()).format(Date(record.timestamp))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${record.id}"),
        shape = RoundedCornerShape(14.dp),
        color = ObsidianSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(WaterCyan.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = WaterCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = record.moduleTitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    )
                    Text(
                        text = "$dateStr • ${record.focusArea}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${record.durationMinutes}m",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                )
                Text(
                    text = "+${record.mobilityScoreAwarded}% Mobility",
                    style = MaterialTheme.typography.labelSmall.copy(color = WaterCyan, fontSize = 10.sp)
                )
            }
        }
    }
}

@Composable
private fun DailyCheckInDialog(
    user: UserMartialProfileEntity,
    onDismiss: () -> Unit,
    onSave: (Float, Float, Float, Float, Float, Float) -> Unit
) {
    var hip by remember { mutableFloatStateOf(user.hipMobility) }
    var centerline by remember { mutableFloatStateOf(user.centerlineStability) }
    var thoracic by remember { mutableFloatStateOf(user.thoracicMobility) }
    var torque by remember { mutableFloatStateOf(user.kineticTorque) }
    var recovery by remember { mutableFloatStateOf(user.recoveryReadiness) }
    var breath by remember { mutableFloatStateOf(user.breathControl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        title = {
            Text(
                text = "Daily Martial Assessment",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextWhite)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Calibrate your biomechanical readiness after today's training or recovery.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                )
                Spacer(modifier = Modifier.height(12.dp))

                AssessmentSlider("Hip Mobility & Flexor Opening", hip, WaterCyan) { hip = it }
                AssessmentSlider("Centerline Bai Jong Stability", centerline, GoldLight) { centerline = it }
                AssessmentSlider("Thoracic Spine Rotational Torque", thoracic, Color(0xFFA855F7)) { thoracic = it }
                AssessmentSlider("Recovery Readiness & Soreness", recovery, WaterCyan) { recovery = it }
                AssessmentSlider("Pranayama Breath Control", breath, GoldLight) { breath = it }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(hip, centerline, thoracic, torque, recovery, breath) },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("Save Assessment", color = ObsidianBlack, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
private fun AssessmentSlider(
    label: String,
    value: Float,
    accent: Color,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0), fontSize = 11.sp))
            Text(text = "${value.toInt()}%", style = MaterialTheme.typography.bodySmall.copy(color = accent, fontWeight = FontWeight.Bold, fontSize = 11.sp))
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 40f..100f,
            colors = SliderDefaults.colors(
                thumbColor = accent,
                activeTrackColor = accent,
                inactiveTrackColor = Color(0xFF263244)
            )
        )
    }
}
