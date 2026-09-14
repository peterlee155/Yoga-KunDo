package com.example.ui.training

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

@Composable
fun WorkoutPlayerScreen(
    viewModel: MainViewModel,
    onFinishWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val module by viewModel.activeModule.collectAsStateWithLifecycle()
    val poses by viewModel.activePoses.collectAsStateWithLifecycle()
    val poseIndex by viewModel.currentPoseIndex.collectAsStateWithLifecycle()
    val secondsRemaining by viewModel.secondsRemaining.collectAsStateWithLifecycle()
    val isPaused by viewModel.isWorkoutPaused.collectAsStateWithLifecycle()
    val isFinished by viewModel.isWorkoutFinished.collectAsStateWithLifecycle()

    if (module == null || poses.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBlack),
            contentAlignment = Alignment.Center
        ) {
            Text("No active flow.", color = TextMuted)
        }
        return
    }

    val currentPose = poses.getOrNull(poseIndex) ?: poses[0]
    val totalPoseDuration = currentPose.durationSeconds.toFloat().coerceAtLeast(1f)
    val progressFraction = (secondsRemaining.toFloat() / totalPoseDuration).coerceIn(0f, 1f)

    val animatedTimerProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "TimerProgress"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    viewModel.exitWorkout()
                    onFinishWorkout()
                },
                modifier = Modifier.testTag("exit_workout_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Exit Flow",
                    tint = TextWhite
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = module?.title ?: "Martial Yoga Flow",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        fontSize = 15.sp
                    )
                )
                Text(
                    text = "Pose ${poseIndex + 1} of ${poses.size}",
                    style = MaterialTheme.typography.bodySmall.copy(color = GoldLight, fontSize = 11.sp)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = WaterCyan.copy(alpha = 0.18f),
                border = androidx.compose.foundation.BorderStroke(0.6.dp, WaterCyan)
            ) {
                Text(
                    text = "OFFLINE ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = WaterCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }

        // Overall flow progress bar
        Spacer(modifier = Modifier.height(10.dp))
        val overallProgress = (poseIndex + 1).toFloat() / poses.size.toFloat()
        LinearProgressIndicator(
            progress = { overallProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = GoldPrimary,
            trackColor = Color(0xFF1E2633),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable content area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circular Countdown Timer
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .testTag("workout_timer_circle"),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF1A2230),
                    strokeWidth = 8.dp
                )
                CircularProgressIndicator(
                    progress = { animatedTimerProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = if (isPaused) TextMuted else GoldPrimary,
                    strokeWidth = 8.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$secondsRemaining",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                    )
                    Text(
                        text = if (isPaused) "PAUSED" else "SECONDS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isPaused) Color(0xFFF87171) else GoldLight,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Current Pose Titles
            Text(
                text = currentPose.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = TextWhite,
                    textAlign = TextAlign.Center
                )
            )
            Text(
                text = currentPose.sanskritOrMartialName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = WaterCyan,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // JKD Martial Principle Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("jkd_martial_cue_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SportsKabaddi,
                            contentDescription = "Combat Principle",
                            tint = GoldLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "JKD COMBAT BIOMECHANICS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.1.sp,
                                color = GoldLight
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentPose.jkdPrincipleCue,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFE2E8F0),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Instructions & Breathing Cues
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = currentPose.instructions,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextWhite,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Air,
                            contentDescription = "Breathing",
                            tint = WaterCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentPose.breathingCue,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = WaterCyan,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = "Target",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Target: ${currentPose.targetMuscles}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom Playback Controls
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = ObsidianSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prev pose
                IconButton(
                    onClick = { viewModel.prevPose() },
                    enabled = poseIndex > 0,
                    modifier = Modifier.testTag("prev_pose_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Pose",
                        tint = if (poseIndex > 0) TextWhite else TextMuted,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Pause / Play Main Button
                Button(
                    onClick = { viewModel.togglePauseWorkout() },
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("pause_resume_button"),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isPaused) "Resume" else "Pause",
                        tint = ObsidianBlack,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Next pose
                IconButton(
                    onClick = { viewModel.nextPose() },
                    modifier = Modifier.testTag("next_pose_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Pose",
                        tint = TextWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }

    // Workout Finished Dialog
    if (isFinished) {
        AlertDialog(
            onDismissRequest = {
                viewModel.exitWorkout()
                onFinishWorkout()
            },
            containerColor = ObsidianSurface,
            title = {
                Text(
                    text = "FLOW MASTERED",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = GoldLight,
                        letterSpacing = 1.2.sp
                    )
                )
            },
            text = {
                Column {
                    Text(
                        text = "You completed ${module?.title}!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your mobility and centerline balance scores have been recorded to your martial artist progress dashboard.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E2633),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WaterCyan)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "+15% Mobility & Centerline Equilibrium",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WaterCyan
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.exitWorkout()
                        onFinishWorkout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("Return to Dashboard", color = ObsidianBlack, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
