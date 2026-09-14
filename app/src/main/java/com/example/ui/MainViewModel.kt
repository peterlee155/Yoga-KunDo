package com.example.ui

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CoachingConsultationEntity
import com.example.data.model.LocalPartnerEntity
import com.example.data.model.PoseStep
import com.example.data.model.TrainingModuleEntity
import com.example.data.model.TrainingProgressEntity
import com.example.data.model.UserMartialProfileEntity
import com.example.data.repository.AppRepository
import com.example.data.revenuecat.CoachingPackage
import com.example.data.revenuecat.MembershipPlan
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository.getInstance(application)
    val revenueCat = repository.revenueCatManager

    // Modules & Offline Filtering
    val allModules: StateFlow<List<TrainingModuleEntity>> = repository.allModules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val offlineFilter = MutableStateFlow(false)
    val categoryFilter = MutableStateFlow("All")

    val displayedModules: StateFlow<List<TrainingModuleEntity>> = combine(
        allModules,
        offlineFilter,
        categoryFilter
    ) { modules, offlineOnly, category ->
        modules.filter { module ->
            val matchesOffline = !offlineOnly || module.isDownloadedOffline
            val matchesCategory = category == "All" || module.category.equals(category, ignoreCase = true)
            matchesOffline && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Workout / Training Flow Player
    private val _activeModule = MutableStateFlow<TrainingModuleEntity?>(null)
    val activeModule: StateFlow<TrainingModuleEntity?> = _activeModule.asStateFlow()

    private val _activePoses = MutableStateFlow<List<PoseStep>>(emptyList())
    val activePoses: StateFlow<List<PoseStep>> = _activePoses.asStateFlow()

    private val _currentPoseIndex = MutableStateFlow(0)
    val currentPoseIndex: StateFlow<Int> = _currentPoseIndex.asStateFlow()

    private val _secondsRemaining = MutableStateFlow(0)
    val secondsRemaining: StateFlow<Int> = _secondsRemaining.asStateFlow()

    private val _isWorkoutPaused = MutableStateFlow(false)
    val isWorkoutPaused: StateFlow<Boolean> = _isWorkoutPaused.asStateFlow()

    private val _isWorkoutFinished = MutableStateFlow(false)
    val isWorkoutFinished: StateFlow<Boolean> = _isWorkoutFinished.asStateFlow()

    private var timerJob: Job? = null

    // Profile & Progress
    val userProfile: StateFlow<UserMartialProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allProgress: StateFlow<List<TrainingProgressEntity>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Local Training Partners
    val allPartners: StateFlow<List<LocalPartnerEntity>> = repository.localPartners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val partnerDisciplineFilter = MutableStateFlow("All")
    val partnerMaxDistance = MutableStateFlow(10.0)

    val displayedPartners: StateFlow<List<LocalPartnerEntity>> = combine(
        allPartners,
        partnerDisciplineFilter,
        partnerMaxDistance
    ) { partners, discipline, maxDist ->
        partners.filter { p ->
            val matchesDist = p.distanceMiles <= maxDist
            val matchesDiscipline = discipline == "All" ||
                    p.discipline.contains(discipline, ignoreCase = true) ||
                    p.sparringFocus.contains(discipline, ignoreCase = true)
            matchesDist && matchesDiscipline
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Coaching Consultations
    val consultations: StateFlow<List<CoachingConsultationEntity>> = repository.coachingConsultations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // RevenueCat States
    val isPremium: StateFlow<Boolean> = revenueCat.isPremium
    val currentTier: StateFlow<String> = revenueCat.currentTier
    val purchaseStatus: StateFlow<String?> = revenueCat.purchaseStatus

    // Module Actions
    fun toggleModuleDownload(moduleId: String, currentDownloaded: Boolean) {
        viewModelScope.launch {
            repository.toggleModuleDownload(moduleId, currentDownloaded)
        }
    }

    // Workout Flow Execution
    fun startWorkout(module: TrainingModuleEntity) {
        _activeModule.value = module
        val poses = repository.parsePoses(module.posesJson)
        _activePoses.value = poses
        _currentPoseIndex.value = 0
        _isWorkoutPaused.value = false
        _isWorkoutFinished.value = false

        if (poses.isNotEmpty()) {
            _secondsRemaining.value = poses[0].durationSeconds
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                if (!_isWorkoutPaused.value) {
                    if (_secondsRemaining.value > 1) {
                        _secondsRemaining.value -= 1
                    } else {
                        // Pose complete -> advance to next pose or complete
                        if (_currentPoseIndex.value < _activePoses.value.lastIndex) {
                            _currentPoseIndex.value += 1
                            _secondsRemaining.value = _activePoses.value[_currentPoseIndex.value].durationSeconds
                        } else {
                            completeWorkout()
                            break
                        }
                    }
                }
            }
        }
    }

    fun togglePauseWorkout() {
        _isWorkoutPaused.value = !_isWorkoutPaused.value
    }

    fun nextPose() {
        if (_currentPoseIndex.value < _activePoses.value.lastIndex) {
            _currentPoseIndex.value += 1
            _secondsRemaining.value = _activePoses.value[_currentPoseIndex.value].durationSeconds
        } else {
            completeWorkout()
        }
    }

    fun prevPose() {
        if (_currentPoseIndex.value > 0) {
            _currentPoseIndex.value -= 1
            _secondsRemaining.value = _activePoses.value[_currentPoseIndex.value].durationSeconds
        }
    }

    private fun completeWorkout() {
        timerJob?.cancel()
        _isWorkoutFinished.value = true
        val mod = _activeModule.value ?: return
        viewModelScope.launch {
            repository.completeWorkoutSession(
                moduleId = mod.id,
                moduleTitle = mod.title,
                durationMinutes = mod.durationMinutes,
                focusArea = mod.category
            )
        }
    }

    fun exitWorkout() {
        timerJob?.cancel()
        _activeModule.value = null
        _activePoses.value = emptyList()
        _isWorkoutFinished.value = false
    }

    // Daily Assessment Updates
    fun updateAssessment(
        hip: Float,
        centerline: Float,
        thoracic: Float,
        torque: Float,
        recovery: Float,
        breath: Float
    ) {
        viewModelScope.launch {
            repository.updateUserAssessment(hip, centerline, thoracic, torque, recovery, breath)
        }
    }

    // Partner Actions
    fun togglePartnerConnect(partnerId: String, currentConnected: Boolean) {
        viewModelScope.launch {
            repository.togglePartnerConnection(partnerId, currentConnected)
        }
    }

    fun invitePartner(partnerId: String) {
        viewModelScope.launch {
            repository.sendPartnerInvite(partnerId)
        }
    }

    fun postTrainingCall(
        name: String,
        discipline: String,
        yogaExp: String,
        location: String,
        bio: String,
        availability: String
    ) {
        viewModelScope.launch {
            repository.addCustomPartnerMeetup(name, discipline, yogaExp, location, bio, availability)
        }
    }

    // RevenueCat & Coaching
    fun purchaseMembershipPlan(activity: Activity?, plan: MembershipPlan) {
        revenueCat.purchaseMembership(
            activity = activity,
            plan = plan,
            onSuccess = {
                // Handled in RevenueCatManager
            },
            onError = {
                // Handled in RevenueCatManager
            }
        )
    }

    fun bookConsultation(
        pkg: CoachingPackage,
        date: String,
        time: String
    ) {
        revenueCat.purchaseCoaching(pkg, date, time) {
            viewModelScope.launch {
                repository.recordCoachingConsultation(
                    coachName = pkg.coachName,
                    coachRole = pkg.coachTitle,
                    sessionType = pkg.title,
                    scheduledDate = date,
                    scheduledTime = time,
                    priceDollars = pkg.priceNumber
                )
            }
        }
    }

    fun restorePurchases() {
        revenueCat.restorePurchases { _, _ -> }
    }

    fun dismissPurchaseStatus() {
        revenueCat.clearStatus()
    }
}
