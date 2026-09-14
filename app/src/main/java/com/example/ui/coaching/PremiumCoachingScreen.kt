package com.example.ui.coaching

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VideoCameraFront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.CoachingConsultationEntity
import com.example.data.revenuecat.CoachingPackage
import com.example.data.revenuecat.MembershipPlan
import com.example.data.revenuecat.RevenueCatManager
import com.example.ui.MainViewModel
import com.example.ui.theme.DragonCrimson
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
fun PremiumCoachingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
    val currentTier by viewModel.currentTier.collectAsStateWithLifecycle()
    val purchaseStatus by viewModel.purchaseStatus.collectAsStateWithLifecycle()
    val consultations by viewModel.consultations.collectAsStateWithLifecycle()

    var selectedCoachingPackage by remember { mutableStateOf<CoachingPackage?>(null) }
    var restoreFeedback by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PREMIUM & COACHING",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = TextWhite
                        )
                    )
                    Text(
                        text = "RevenueCat Gateway • Subscriptions & Master Consultations",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E2634),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, ObsidianCardBorder),
                    modifier = Modifier.clickable {
                        viewModel.restorePurchases()
                        restoreFeedback = "Restored RevenueCat entitlements successfully."
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Restore, contentDescription = "Restore", tint = TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Restore", fontSize = 11.sp, color = Color(0xFFCBD5E1), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Status / Banner
        if (purchaseStatus != null || restoreFeedback != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WaterCyan.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WaterCyan)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = purchaseStatus ?: restoreFeedback ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = WaterCyan
                            )
                        )
                        TextButton(onClick = {
                            viewModel.dismissPurchaseStatus()
                            restoreFeedback = null
                        }) {
                            Text("OK", color = TextWhite, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Active Membership Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("membership_status_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isPremium) WaterCyan else GoldPrimary.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (isPremium) WaterCyan.copy(alpha = 0.2f) else GoldPrimary.copy(alpha = 0.2f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPremium) Icons.Default.VerifiedUser else Icons.Default.Star,
                                    contentDescription = "Tier",
                                    tint = if (isPremium) WaterCyan else GoldLight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isPremium) "ACTIVE SUBSCRIPTION" else "CURRENT PLAN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 1.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPremium) WaterCyan else GoldLight
                                    )
                                )
                                Text(
                                    text = currentTier,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = TextWhite
                                    )
                                )
                            }
                        }

                        // RevenueCat Security Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF161E2B),
                            border = androidx.compose.foundation.BorderStroke(0.6.dp, Color(0xFF28364D))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Security, contentDescription = "Secure", tint = WaterCyan, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("RevenueCat Gateway", fontSize = 9.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }
        }

        // Membership Tiers Section
        item {
            Text(
                text = "DRAGON MEMBERSHIP TIERS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = TextMuted
                )
            )
        }

        items(RevenueCatManager.MEMBERSHIP_PLANS) { plan ->
            MembershipPlanCard(
                plan = plan,
                isCurrent = isPremium && currentTier.contains(plan.title, ignoreCase = true),
                onSubscribe = {
                    viewModel.purchaseMembershipPlan(activity, plan)
                }
            )
        }

        // Personalized Coaching Consultations Section
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "1-ON-1 SIFU COACHING CONSULTATIONS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = GoldLight
                        )
                    )
                    Text(
                        text = "Private video biomechanics and posture analysis",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }
        }

        items(RevenueCatManager.COACHING_PACKAGES) { pkg ->
            CoachingPackageCard(
                pkg = pkg,
                onBook = { selectedCoachingPackage = pkg }
            )
        }

        // Past / Confirmed Consultations
        if (consultations.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "CONFIRMED COACHING SESSIONS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = TextMuted
                    )
                )
            }

            items(consultations) { booking ->
                ConfirmedConsultationRow(booking)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Booking Consultation Dialog
    if (selectedCoachingPackage != null) {
        val pkg = selectedCoachingPackage!!
        var dateText by remember { mutableStateOf("Oct 14, 2026") }
        var timeText by remember { mutableStateOf("10:00 AM PST") }
        var consultationNotes by remember { mutableStateOf("Reviewing lead leg chambering for JKD side kick and hamstring tightness.") }

        AlertDialog(
            onDismissRequest = { selectedCoachingPackage = null },
            containerColor = ObsidianSurface,
            title = {
                Column {
                    Text(
                        text = "Book 1-on-1 Consultation",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextWhite)
                    )
                    Text(
                        text = "With ${pkg.coachName} • ${pkg.price}",
                        style = MaterialTheme.typography.bodySmall.copy(color = GoldLight)
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Secure payment will be processed via the RevenueCat payment gateway.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        label = { Text("Consultation Date") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianCardBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = timeText,
                        onValueChange = { timeText = it },
                        label = { Text("Consultation Time") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianCardBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = consultationNotes,
                        onValueChange = { consultationNotes = it },
                        label = { Text("Biomechanical Goals / Focus") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianCardBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.bookConsultation(pkg, dateText, timeText)
                        selectedCoachingPackage = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    modifier = Modifier.testTag("confirm_coaching_booking_button")
                ) {
                    Icon(imageVector = Icons.Default.Payments, contentDescription = "Pay", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Checkout via RevenueCat (${pkg.price})", color = ObsidianBlack, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCoachingPackage = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}

@Composable
private fun MembershipPlanCard(
    plan: MembershipPlan,
    isCurrent: Boolean,
    onSubscribe: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("plan_card_${plan.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            if (plan.isPopular) GoldPrimary else ObsidianCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with optional popular badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plan.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = TextWhite
                    )
                )

                if (plan.badge != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldPrimary.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, GoldLight)
                    ) {
                        Text(
                            text = plan.badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = plan.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Price Row
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = plan.price,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = GoldLight
                    )
                )
                Text(
                    text = " ${plan.period}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Perks
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                plan.perks.forEach { perk ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Included",
                            tint = WaterCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = perk,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFE2E8F0),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onSubscribe,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("subscribe_button_${plan.id}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCurrent) WaterCyan else GoldPrimary,
                    contentColor = ObsidianBlack
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isCurrent) Icons.Default.CheckCircle else Icons.Default.LockOpen,
                    contentDescription = "Subscribe",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isCurrent) "Current Plan Active" else "Subscribe via RevenueCat",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CoachingPackageCard(
    pkg: CoachingPackage,
    onBook: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("coaching_pkg_${pkg.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_coaching_master),
                    contentDescription = pkg.coachName,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .border(1.2.dp, GoldLight, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pkg.coachName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    )
                    Text(
                        text = pkg.coachTitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = WaterCyan,
                            fontSize = 11.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E2838),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, GoldLight.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = pkg.price,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = GoldLight
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = pkg.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = TextWhite
                )
            )

            Text(
                text = pkg.durationText,
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = pkg.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Inclusions
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                pkg.inclusions.forEach { inc ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(GoldLight, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = inc,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBook,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("book_session_${pkg.id}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF222B3B),
                    contentColor = GoldLight
                ),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f))
            ) {
                Icon(
                    imageVector = Icons.Default.VideoCameraFront,
                    contentDescription = "Schedule",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Schedule & Checkout via RevenueCat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ConfirmedConsultationRow(consultation: CoachingConsultationEntity) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = ObsidianSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, WaterCyan.copy(alpha = 0.4f))
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
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Consultation",
                        tint = WaterCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = consultation.sessionType,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    )
                    Text(
                        text = "${consultation.coachName} • ${consultation.scheduledDate} at ${consultation.scheduledTime}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = WaterCyan.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "CONFIRMED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = WaterCyan,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}
