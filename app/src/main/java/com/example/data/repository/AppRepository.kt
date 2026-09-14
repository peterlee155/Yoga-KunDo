package com.example.data.repository

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.db.TrainingDao
import com.example.data.model.CoachingConsultationEntity
import com.example.data.model.LocalPartnerEntity
import com.example.data.model.PoseStep
import com.example.data.model.TrainingModuleEntity
import com.example.data.model.TrainingProgressEntity
import com.example.data.model.UserMartialProfileEntity
import com.example.data.revenuecat.RevenueCatManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AppRepository(
    private val context: Context,
    private val dao: TrainingDao,
    val revenueCatManager: RevenueCatManager
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val poseListType = Types.newParameterizedType(List::class.java, PoseStep::class.java)
    private val poseAdapter = moshi.adapter<List<PoseStep>>(poseListType)

    val allModules: Flow<List<TrainingModuleEntity>> = dao.getAllModules()
    val offlineModules: Flow<List<TrainingModuleEntity>> = dao.getOfflineModules()
    val allProgress: Flow<List<TrainingProgressEntity>> = dao.getAllProgress()
    val userProfile: Flow<UserMartialProfileEntity?> = dao.getUserProfile()
    val localPartners: Flow<List<LocalPartnerEntity>> = dao.getAllPartners()
    val coachingConsultations: Flow<List<CoachingConsultationEntity>> = dao.getAllConsultations()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    suspend fun getModuleById(id: String): TrainingModuleEntity? {
        return dao.getModuleById(id)
    }

    fun parsePoses(json: String): List<PoseStep> {
        return try {
            poseAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun toggleModuleDownload(moduleId: String, currentDownloaded: Boolean) {
        if (currentDownloaded) {
            dao.updateDownloadStatus(moduleId, false, 0f)
        } else {
            // Simulate smooth offline package caching
            dao.updateDownloadStatus(moduleId, true, 1.0f)
        }
    }

    suspend fun completeWorkoutSession(
        moduleId: String,
        moduleTitle: String,
        durationMinutes: Int,
        focusArea: String
    ) {
        val mobilityBonus = (10..15).random()
        val progress = TrainingProgressEntity(
            moduleId = moduleId,
            moduleTitle = moduleTitle,
            timestamp = System.currentTimeMillis(),
            durationMinutes = durationMinutes,
            mobilityScoreAwarded = mobilityBonus,
            focusArea = focusArea
        )
        dao.insertProgress(progress)

        // Update User Profile metrics
        val currentProfile = dao.getUserProfile().firstOrNull() ?: UserMartialProfileEntity()
        val updated = currentProfile.copy(
            totalMinutesTrained = currentProfile.totalMinutesTrained + durationMinutes,
            sessionsCompleted = currentProfile.sessionsCompleted + 1,
            hipMobility = (currentProfile.hipMobility + 0.8f).coerceAtMost(99f),
            centerlineStability = (currentProfile.centerlineStability + 0.6f).coerceAtMost(99f),
            thoracicMobility = (currentProfile.thoracicMobility + 0.7f).coerceAtMost(99f),
            kineticTorque = (currentProfile.kineticTorque + 0.9f).coerceAtMost(99f),
            recoveryReadiness = (currentProfile.recoveryReadiness + 1.2f).coerceAtMost(100f)
        )
        dao.insertOrUpdateProfile(updated)
    }

    suspend fun updateUserAssessment(
        hipMobility: Float,
        centerlineStability: Float,
        thoracicMobility: Float,
        kineticTorque: Float,
        recoveryReadiness: Float,
        breathControl: Float
    ) {
        val current = dao.getUserProfile().firstOrNull() ?: UserMartialProfileEntity()
        val updated = current.copy(
            hipMobility = hipMobility,
            centerlineStability = centerlineStability,
            thoracicMobility = thoracicMobility,
            kineticTorque = kineticTorque,
            recoveryReadiness = recoveryReadiness,
            breathControl = breathControl
        )
        dao.insertOrUpdateProfile(updated)
    }

    suspend fun togglePartnerConnection(partnerId: String, currentConnected: Boolean) {
        dao.updatePartnerConnection(partnerId, !currentConnected)
    }

    suspend fun sendPartnerInvite(partnerId: String) {
        dao.updatePartnerInvite(partnerId, true)
    }

    suspend fun addCustomPartnerMeetup(
        name: String,
        discipline: String,
        yogaExp: String,
        location: String,
        bio: String,
        availability: String
    ) {
        val partner = LocalPartnerEntity(
            id = "meetup_${System.currentTimeMillis()}",
            name = name,
            discipline = discipline,
            yogaExperience = yogaExp,
            distanceMiles = 0.5,
            locationName = location,
            bio = bio,
            sparringFocus = "Technical Sparring & JKD Centerline Flow",
            availability = availability,
            isConnected = true,
            hasSentInvite = false
        )
        dao.insertPartner(partner)
    }

    suspend fun recordCoachingConsultation(
        coachName: String,
        coachRole: String,
        sessionType: String,
        scheduledDate: String,
        scheduledTime: String,
        priceDollars: Int
    ) {
        val consultation = CoachingConsultationEntity(
            id = "coach_${System.currentTimeMillis()}",
            coachName = coachName,
            coachRole = coachRole,
            sessionType = sessionType,
            scheduledDate = scheduledDate,
            scheduledTime = scheduledTime,
            priceDollars = priceDollars,
            bookingStatus = "CONFIRMED",
            timestamp = System.currentTimeMillis()
        )
        dao.insertConsultation(consultation)
    }

    private suspend fun seedInitialDataIfNeeded() {
        val existingModules = dao.getAllModules().firstOrNull()
        if (existingModules.isNullOrEmpty()) {
            val initialModules = getSampleModules()
            dao.insertModules(initialModules)
        }

        val existingProfile = dao.getUserProfile().firstOrNull()
        if (existingProfile == null) {
            dao.insertOrUpdateProfile(UserMartialProfileEntity())
        }

        val existingPartners = dao.getAllPartners().firstOrNull()
        if (existingPartners.isNullOrEmpty()) {
            dao.insertPartners(getSamplePartners())
        }
    }

    private fun getSampleModules(): List<TrainingModuleEntity> {
        val m1Poses = listOf(
            PoseStep(
                name = "Centerline Rooting",
                sanskritOrMartialName = "Tadasana to Bai Jong Stance",
                durationSeconds = 45,
                instructions = "Root both feet shoulder-width. Lower center of gravity 3 inches into an intercepting Bai Jong ready posture with hands guarding centerline.",
                jkdPrincipleCue = "Economy of motion: No unnecessary muscular tension. Stance is a coiled spring, ready to intercept instantly without telegraphing.",
                breathingCue = "Deep diaphragmatic inhale through nose, sharp rooted exhale settling into pelvis.",
                targetMuscles = "Quadriceps, Calves, Core Stabilizers"
            ),
            PoseStep(
                name = "Lead Intercept Warrior",
                sanskritOrMartialName = "Virabhadrasana II Modified",
                durationSeconds = 60,
                instructions = "Step lead foot forward into deep Warrior II. Align lead fingers directly along opponent's visual centerline. Back foot anchored 45 degrees.",
                jkdPrincipleCue = "Centerline dominance: Your lead hand covers 80% of incoming strikes while holding spring-loaded striking distance.",
                breathingCue = "Inhale expanding ribcage, exhale sinking deeper into lead hip chamber.",
                targetMuscles = "Hip Abductors, Deltoids, Pelvic Floor"
            ),
            PoseStep(
                name = "Evading Serpent Flow",
                sanskritOrMartialName = "Viparita Virabhadrasana (Reverse Warrior)",
                durationSeconds = 60,
                instructions = "Sweep rear hand down back leg while lead arm arches overhead. Maintain rooted low stance without rising.",
                jkdPrincipleCue = "Flowing like water: Slip the high jab while maintaining chambered torque for a counter-attack.",
                breathingCue = "Inhale lengthening the intercostal fascia, exhale sustaining pelvic stability.",
                targetMuscles = "Obliques, Intercostals, Latissimus Dorsi"
            ),
            PoseStep(
                name = "Centerline Balance Lock",
                sanskritOrMartialName = "Garudasana (Eagle Pose)",
                durationSeconds = 60,
                instructions = "Wrap left leg over right, sink hips, and entwine forearms. Keep spine vertical and eyes focused on a single point.",
                jkdPrincipleCue = "Internal compaction: Binding the limbs compresses energy into the core, training balance recovery under combat destabilization.",
                breathingCue = "Steady, unhurried breaths into the upper thoracic spine.",
                targetMuscles = "Ankles, Glute Medius, Upper Back"
            ),
            PoseStep(
                name = "Horse Stance Power Decompression",
                sanskritOrMartialName = "Utkata Konasana (Goddess Stance)",
                durationSeconds = 60,
                instructions = "Open wide, turn toes out 45 degrees, sink hips to knee height. Extend palms forward in palm-strike alignment.",
                jkdPrincipleCue = "Rooted explosive base: Cultivates the grounded pelvic strength required for explosive straight blast punches.",
                breathingCue = "Rhythmic rhythmic nasal breathing keeping facial muscles relaxed.",
                targetMuscles = "Adductors, Quadriceps, Core"
            ),
            PoseStep(
                name = "Martial Restoration",
                sanskritOrMartialName = "Balasana (Extended Child's Pose)",
                durationSeconds = 45,
                instructions = "Knees wide, big toes touching, hips resting back on heels. Extend arms forward, forehead resting on mat.",
                jkdPrincipleCue = "The art of non-effort: True power flows from absolute muscular relaxation between explosive outputs.",
                breathingCue = "Slow, restorative parasympathetic breathing into the lower back.",
                targetMuscles = "Spine Extensors, Latissimus, Hip Joints"
            )
        )

        val m2Poses = listOf(
            PoseStep(
                name = "Crescent Chamber Lunge",
                sanskritOrMartialName = "Anjaneyasana",
                durationSeconds = 60,
                instructions = "Step right foot forward, drop left knee down, un-tuck toes. Sink pelvis forward and sweep arms high.",
                jkdPrincipleCue = "Psoas release: A flexible hip flexor allows instantaneous high-line kicks without lower back compensation.",
                breathingCue = "Inhale elevating thoracic cage, exhale releasing tension in anterior hip.",
                targetMuscles = "Iliopsoas, Rectus Femoris"
            ),
            PoseStep(
                name = "Low Lizard Kick Opener",
                sanskritOrMartialName = "Utthan Pristhasana",
                durationSeconds = 75,
                instructions = "Walk right foot to outer edge of mat. Lower onto forearms inside the foot. Keep chest open.",
                jkdPrincipleCue = "Adductor elasticity: Bruce Lee utilized deep frog and lizard stretches to eliminate kicking friction.",
                breathingCue = "Long, steady 5-second exhales directly into the hip capsule.",
                targetMuscles = "Hip Adductors, Hamstrings"
            ),
            PoseStep(
                name = "Skandasana Striking Transition",
                sanskritOrMartialName = "Skandasana (Side Lunge)",
                durationSeconds = 60,
                instructions = "Shift weight into a deep side lunge on right leg, left leg extended with toes pointed up. Hands in defensive guard.",
                jkdPrincipleCue = "Multi-directional agility: Fluid level changes and lateral evasion to exploit opponent's blind angles.",
                breathingCue = "Exhale as you glide smoothly from side to side without bobbing up.",
                targetMuscles = "Gracilis, Hamstrings, Ankles"
            ),
            PoseStep(
                name = "Combat Pigeon Decompression",
                sanskritOrMartialName = "Eka Pada Rajakapotasana",
                durationSeconds = 90,
                instructions = "Bring right shin across top of mat, slide left leg back straight. Fold torso over front shin.",
                jkdPrincipleCue = "Rotational release: Releases tension in deep external rotators (piriformis) following heavy bag kicking sessions.",
                breathingCue = "Slow 4-count inhale, 6-count calming exhale.",
                targetMuscles = "Piriformis, Glute Max, Sciatic Pathway"
            )
        )

        val m3Poses = listOf(
            PoseStep(
                name = "Revolved Torque Angle",
                sanskritOrMartialName = "Parivrtta Parsvakonasana",
                durationSeconds = 60,
                instructions = "From deep lunge, twist torso towards front thigh, hooking opposite elbow outside the knee. Palms press together.",
                jkdPrincipleCue = "Kinetic spiral: Torque generation travels from rear foot anchor through hips into shoulder whip.",
                breathingCue = "Exhale deeper into the spinal revolution.",
                targetMuscles = "Spinal Rotators, Obliques, Quads"
            ),
            PoseStep(
                name = "Vasisthasana Side Plank Chamber",
                sanskritOrMartialName = "Vasisthasana Side Balance",
                durationSeconds = 45,
                instructions = "Balance on left hand and outer edge of left foot. Chamber top knee towards chest in high hook kick chamber.",
                jkdPrincipleCue = "Lateral anti-rotation: Unshakable core prevents being pushed off centerline during clinch and trapping.",
                breathingCue = "Controlled steady sips of breath.",
                targetMuscles = "Obliques, Serratus Anterior, Glute Medius"
            ),
            PoseStep(
                name = "Navasana Striking Pulses",
                sanskritOrMartialName = "Navasana (Boat Pose)",
                durationSeconds = 60,
                instructions = "Balance on sitting bones with shins parallel to ground. Perform controlled rotational cross-strikes with core engaged.",
                jkdPrincipleCue = "Water core: Transmitting energy without energy leaks across the abdominal wall.",
                breathingCue = "Sharp exhale on each rotational punch extension.",
                targetMuscles = "Rectus Abdominis, Transverse Abdominis"
            )
        )

        val m4Poses = listOf(
            PoseStep(
                name = "Reclining Goddess Rest",
                sanskritOrMartialName = "Supta Baddha Konasana",
                durationSeconds = 90,
                instructions = "Lie on your back, soles of feet together, knees falling open like a butterfly. Hands rest on abdomen.",
                jkdPrincipleCue = "Total surrender of tension: The body only adapts and repairs when the sympathetic fighting drive shuts off.",
                breathingCue = "Natural belly expansion with every breath.",
                targetMuscles = "Pelvic Diaphragm, Inner Thighs"
            ),
            PoseStep(
                name = "Thread the Needle Scapular Release",
                sanskritOrMartialName = "Parsva Balasana",
                durationSeconds = 75,
                instructions = "From tabletop, slide right arm under torso across mat. Rest right shoulder and temple on floor.",
                jkdPrincipleCue = "Puncher's shoulder recovery: Relieves tightness in posterior rotator cuff caused by impact recoil.",
                breathingCue = "Inhale between shoulder blades.",
                targetMuscles = "Rhomboids, Infraspinatus, Trapezius"
            ),
            PoseStep(
                name = "Combat Savasana & Stillness",
                sanskritOrMartialName = "Savasana (Mind of No-Mind)",
                durationSeconds = 120,
                instructions = "Lie completely flat on mat, feet relaxed open, palms facing ceiling. Close eyes and observe sensation.",
                jkdPrincipleCue = "Mushin (No-Mind): When the mind is free of rigid concepts, natural instinct and water-like adaptability take over.",
                breathingCue = "Effortless, automatic natural respiration.",
                targetMuscles = "Full Body Relaxation"
            )
        )

        return listOf(
            TrainingModuleEntity(
                id = "mod_bai_jong_centerline",
                title = "Bai Jong & Rooted Centerline",
                subtitle = "Warrior Stance, Balance & Centerline Control",
                martialPrinciple = "Economy of Motion & Centerline Dominance",
                category = "Mobility & Stance",
                durationMinutes = 18,
                difficulty = "All Levels",
                isDownloadedOffline = true,
                downloadProgress = 1.0f,
                coverResName = "img_hero_flow",
                philosophicalQuote = "Empty your mind, be formless, shapeless — like water.",
                posesJson = poseAdapter.toJson(m1Poses)
            ),
            TrainingModuleEntity(
                id = "mod_fluid_hip_opening",
                title = "High Kick Fluidity & Hip Chambering",
                subtitle = "Lizard, Pigeon & Skandasana for Striking Range",
                martialPrinciple = "Having No Limitation as Limitation",
                category = "Dynamic Flexibility",
                durationMinutes = 22,
                difficulty = "Adept",
                isDownloadedOffline = true,
                downloadProgress = 1.0f,
                coverResName = "img_hero_flow",
                philosophicalQuote = "I fear not the man who has practiced 10,000 kicks once, but I fear the man who has practiced one kick 10,000 times.",
                posesJson = poseAdapter.toJson(m2Poses)
            ),
            TrainingModuleEntity(
                id = "mod_kinetic_rotational_power",
                title = "The Water Core: Rotational Strike Torque",
                subtitle = "Developing Whiplike Elastic Power from the Pelvis",
                martialPrinciple = "Direct Interception & Kinetic Chain",
                category = "Core & Power",
                durationMinutes = 20,
                difficulty = "Mastery",
                isDownloadedOffline = false,
                downloadProgress = 0f,
                coverResName = "img_hero_flow",
                philosophicalQuote = "Be soft, yet not yielding. Be firm, yet not hard.",
                posesJson = poseAdapter.toJson(m3Poses)
            ),
            TrainingModuleEntity(
                id = "mod_yin_sparring_recovery",
                title = "Yin Recovery for Combat Athletes",
                subtitle = "Post-Sparring Fascial Release & Parasympathetic Reset",
                martialPrinciple = "Relaxation as the Ultimate Weapon",
                category = "Fascial Recovery",
                durationMinutes = 25,
                difficulty = "All Levels",
                isDownloadedOffline = true,
                downloadProgress = 1.0f,
                coverResName = "img_hero_flow",
                philosophicalQuote = "The stiffest tree is most easily cracked, while the bamboo or willow survives by bending with the wind.",
                posesJson = poseAdapter.toJson(m4Poses)
            )
        )
    }

    private fun getSamplePartners(): List<LocalPartnerEntity> {
        return listOf(
            LocalPartnerEntity(
                id = "partner_1",
                name = "Ray Tanaka",
                discipline = "Jeet Kune Do & Wing Chun",
                yogaExperience = "Vinyasa (3 years)",
                distanceMiles = 1.4,
                locationName = "Downtown / Metro Park Dojo",
                bio = "Practicing JKD for 6 years, integrating daily Vinyasa to eliminate shoulder tension and increase lead jab speed. Looking for light flow sparring.",
                sparringFocus = "Light technical sparring, centerline trapping, hip mobility flow",
                availability = "Weekday Mornings & Saturday 9 AM",
                isConnected = false,
                hasSentInvite = false
            ),
            LocalPartnerEntity(
                id = "partner_2",
                name = "Elena Vance",
                discipline = "BJJ Purple Belt & Muay Thai",
                yogaExperience = "Yin Yoga & Ashtanga (4 years)",
                distanceMiles = 2.8,
                locationName = "Northside Athletic Club",
                bio = "Competitive grappler using martial yoga for hip mobility, ribcage flexibility, and recovery between intense sparring camps.",
                sparringFocus = "Flow rolling, Skandasana mobility, high-kick chambering drills",
                availability = "Tue / Thu Evenings & Sunday Afternoons",
                isConnected = true,
                hasSentInvite = false
            ),
            LocalPartnerEntity(
                id = "partner_3",
                name = "Marcus Reed",
                discipline = "Boxing & JKD Footwork",
                yogaExperience = "Power Yoga (2 years)",
                distanceMiles = 4.1,
                locationName = "East River Park",
                bio = "Focused on footwork agility, pendulum stepping, and cultivating elastic core power without muscular stiffness.",
                sparringFocus = "Pendulum drills, slip-and-counter flow, warrior balance",
                availability = "Weekend Mornings",
                isConnected = false,
                hasSentInvite = false
            ),
            LocalPartnerEntity(
                id = "partner_4",
                name = "Kenji Sato",
                discipline = "Kyokushin Karate & Sanda",
                yogaExperience = "Hatha Yoga (1.5 years)",
                distanceMiles = 5.6,
                locationName = "Westside Martial Arts Hub",
                bio = "Working on opening deep hip capsules to deliver effortless roundhouse and side kicks with zero hip pinching.",
                sparringFocus = "Full-range kicks, deep lunge breathing, post-training joint decompression",
                availability = "Monday & Wednesday 6 PM",
                isConnected = false,
                hasSentInvite = false
            )
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: AppRepository? = null

        fun getInstance(context: Context): AppRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context)
                val rc = RevenueCatManager.getInstance(context)
                val instance = AppRepository(context.applicationContext, db.trainingDao(), rc)
                INSTANCE = instance
                instance
            }
        }
    }
}
