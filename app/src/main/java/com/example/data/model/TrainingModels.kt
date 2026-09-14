package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class PoseStep(
    val name: String,
    val sanskritOrMartialName: String,
    val durationSeconds: Int,
    val instructions: String,
    val jkdPrincipleCue: String,
    val breathingCue: String,
    val targetMuscles: String
)

@Entity(tableName = "training_modules")
data class TrainingModuleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val martialPrinciple: String,
    val category: String,
    val durationMinutes: Int,
    val difficulty: String,
    val isDownloadedOffline: Boolean = true, // Offline-first
    val downloadProgress: Float = 1.0f,
    val coverResName: String = "img_hero_flow",
    val philosophicalQuote: String,
    val posesJson: String
)

@Entity(tableName = "training_progress")
data class TrainingProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val moduleId: String,
    val moduleTitle: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMinutes: Int,
    val mobilityScoreAwarded: Int,
    val focusArea: String
)

@Entity(tableName = "user_martial_profile")
data class UserMartialProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Bruce D.",
    val primaryArt: String = "Jeet Kune Do / Striking",
    val experienceLevel: String = "Adept Practitioner",
    val hipMobility: Float = 78f,
    val centerlineStability: Float = 84f,
    val thoracicMobility: Float = 72f,
    val kineticTorque: Float = 88f,
    val recoveryReadiness: Float = 82f,
    val breathControl: Float = 76f,
    val currentStreakDays: Int = 12,
    val totalMinutesTrained: Int = 430,
    val sessionsCompleted: Int = 18,
    val isPremiumActive: Boolean = false
)

@Entity(tableName = "local_partners")
data class LocalPartnerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val discipline: String,
    val yogaExperience: String,
    val distanceMiles: Double,
    val locationName: String,
    val bio: String,
    val sparringFocus: String,
    val availability: String,
    val isConnected: Boolean = false,
    val hasSentInvite: Boolean = false
)

@Entity(tableName = "coaching_consultations")
data class CoachingConsultationEntity(
    @PrimaryKey val id: String,
    val coachName: String,
    val coachRole: String,
    val sessionType: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val priceDollars: Int,
    val bookingStatus: String, // "CONFIRMED", "PENDING_CHECKOUT"
    val timestamp: Long = System.currentTimeMillis()
)
