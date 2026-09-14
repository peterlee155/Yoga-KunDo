package com.example.ui.social

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConnectWithoutContact
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LocalPartnerEntity
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
fun PartnerFinderScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val partners by viewModel.displayedPartners.collectAsStateWithLifecycle()
    val activeDiscipline by viewModel.partnerDisciplineFilter.collectAsStateWithLifecycle()
    val maxDist by viewModel.partnerMaxDistance.collectAsStateWithLifecycle()

    var showPostMeetupDialog by remember { mutableStateOf(false) }
    var invitePartnerTarget by remember { mutableStateOf<LocalPartnerEntity?>(null) }
    var inviteSentNotification by remember { mutableStateOf<String?>(null) }

    val disciplines = listOf("All", "Jeet Kune Do", "BJJ", "Muay Thai", "Boxing", "Flow")
    val distances = listOf(3.0 to "3 mi", 5.0 to "5 mi", 10.0 to "10 mi", 25.0 to "25 mi")

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBlack)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            text = "LOCAL TRAINING PARTNERS",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = TextWhite
                            )
                        )
                        Text(
                            text = "Find Martial Artists & Yogis for Flow Sparring",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }

                    Button(
                        onClick = { showPostMeetupDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = ObsidianBlack
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("post_meetup_call_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Post Meetup", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Post Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Notification Banner if invite sent
            if (inviteSentNotification != null) {
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
                                text = inviteSentNotification ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WaterCyan
                                )
                            )
                            TextButton(onClick = { inviteSentNotification = null }) {
                                Text("Dismiss", color = TextWhite, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Discipline Filters
            item {
                Column {
                    Text(
                        text = "DISCIPLINE FILTER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextMuted
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(disciplines) { disc ->
                            val isSelected = activeDiscipline == disc
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.partnerDisciplineFilter.value = disc },
                                label = { Text(disc, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = ObsidianBlack,
                                    containerColor = ObsidianSurface,
                                    labelColor = TextMuted
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = ObsidianCardBorder,
                                    selectedBorderColor = GoldLight
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            // Proximity Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = "Radius",
                        tint = WaterCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Radius:",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    distances.forEach { (dist, label) ->
                        val isSelected = maxDist == dist
                        Surface(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clickable { viewModel.partnerMaxDistance.value = dist },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) WaterCyan.copy(alpha = 0.25f) else Color(0xFF1E2633),
                            border = androidx.compose.foundation.BorderStroke(
                                0.8.dp,
                                if (isSelected) WaterCyan else ObsidianCardBorder
                            )
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) WaterCyan else Color(0xFF94A3B8),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Partner count
            item {
                Text(
                    text = "DISCOVERED MARTIAL ARTISTS (${partners.size})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = TextMuted
                    )
                )
            }

            // Partners List
            items(partners) { partner ->
                PartnerCard(
                    partner = partner,
                    onToggleConnect = {
                        viewModel.togglePartnerConnect(partner.id, partner.isConnected)
                    },
                    onInvite = {
                        invitePartnerTarget = partner
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Schedule Invite Dialog
    if (invitePartnerTarget != null) {
        val target = invitePartnerTarget!!
        var selectedLocation by remember { mutableStateOf(target.locationName) }
        var inviteNote by remember { mutableStateOf("Hey ${target.name}! Would love to get together for 45 mins of JKD footwork and hip opening flow.") }

        AlertDialog(
            onDismissRequest = { invitePartnerTarget = null },
            containerColor = ObsidianSurface,
            title = {
                Text(
                    text = "Invite ${target.name} to Train",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Availability: ${target.availability}",
                        style = MaterialTheme.typography.bodySmall.copy(color = GoldLight, fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = selectedLocation,
                        onValueChange = { selectedLocation = it },
                        label = { Text("Meeting Spot / Dojo") },
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
                        value = inviteNote,
                        onValueChange = { inviteNote = it },
                        label = { Text("Training Focus Note") },
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
                        viewModel.invitePartner(target.id)
                        inviteSentNotification = "Training invite sent to ${target.name}! They will be notified."
                        invitePartnerTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Send Invite", color = ObsidianBlack, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { invitePartnerTarget = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // Post Custom Training Call Dialog
    if (showPostMeetupDialog) {
        var postName by remember { mutableStateOf("Bruce D.") }
        var postDiscipline by remember { mutableStateOf("Jeet Kune Do / Striking") }
        var postYogaExp by remember { mutableStateOf("Power Yoga & Mobility (2 yrs)") }
        var postLocation by remember { mutableStateOf("City Dojo & Central Park") }
        var postBio by remember { mutableStateOf("Seeking partner for weekend centerline Bai Jong sparring and Yin recovery.") }
        var postAvailability by remember { mutableStateOf("Saturdays 10 AM & Wed Evenings") }

        AlertDialog(
            onDismissRequest = { showPostMeetupDialog = false },
            containerColor = ObsidianSurface,
            title = {
                Text(
                    text = "Post Training Call",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Broadcast your availability to local martial artists practicing yoga in your area.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = postLocation,
                        onValueChange = { postLocation = it },
                        label = { Text("Your City / Training Location") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianCardBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = postAvailability,
                        onValueChange = { postAvailability = it },
                        label = { Text("Availability (e.g. Sat Mornings)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianCardBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = postBio,
                        onValueChange = { postBio = it },
                        label = { Text("Training Goals / Sparring Flow") },
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
                        viewModel.postTrainingCall(
                            name = postName,
                            discipline = postDiscipline,
                            yogaExp = postYogaExp,
                            location = postLocation,
                            bio = postBio,
                            availability = postAvailability
                        )
                        showPostMeetupDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("Publish Call", color = ObsidianBlack, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPostMeetupDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}

@Composable
private fun PartnerCard(
    partner: LocalPartnerEntity,
    onToggleConnect: () -> Unit,
    onInvite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("partner_card_${partner.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                Color(0xFF1E2738),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = partner.name,
                            tint = GoldLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = partner.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Distance",
                                tint = WaterCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${partner.distanceMiles} mi away • ${partner.locationName}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Connect Status Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (partner.isConnected) WaterCyan.copy(alpha = 0.2f) else Color(0xFF1E2633),
                    border = androidx.compose.foundation.BorderStroke(
                        0.8.dp,
                        if (partner.isConnected) WaterCyan else ObsidianCardBorder
                    ),
                    modifier = Modifier.clickable { onToggleConnect() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (partner.isConnected) Icons.Default.Check else Icons.Default.ConnectWithoutContact,
                            contentDescription = if (partner.isConnected) "Connected" else "Connect",
                            tint = if (partner.isConnected) WaterCyan else TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (partner.isConnected) "Connected" else "Connect",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (partner.isConnected) WaterCyan else TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badges
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E283A),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, Color(0xFF2E3D54))
                ) {
                    Text(
                        text = partner.discipline,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GoldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = WaterCyan.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, WaterCyan.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "Yoga: ${partner.yogaExperience}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = WaterCyan,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = partner.bio,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Focus & Availability
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = ObsidianSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Focus: ${partner.sparringFocus}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "Availability: ${partner.availability}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action: Invite to Train
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onInvite,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = ObsidianBlack
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("invite_partner_${partner.id}")
                ) {
                    Icon(
                        imageVector = if (partner.hasSentInvite) Icons.Default.Check else Icons.Default.Send,
                        contentDescription = "Invite to Flow",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (partner.hasSentInvite) "Invite Sent" else "Invite to Flow",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
