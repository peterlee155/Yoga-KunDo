package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CoachingConsultationEntity
import com.example.data.model.LocalPartnerEntity
import com.example.data.model.TrainingModuleEntity
import com.example.data.model.TrainingProgressEntity
import com.example.data.model.UserMartialProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {

    // Training Modules
    @Query("SELECT * FROM training_modules")
    fun getAllModules(): Flow<List<TrainingModuleEntity>>

    @Query("SELECT * FROM training_modules WHERE isDownloadedOffline = 1")
    fun getOfflineModules(): Flow<List<TrainingModuleEntity>>

    @Query("SELECT * FROM training_modules WHERE id = :id")
    suspend fun getModuleById(id: String): TrainingModuleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<TrainingModuleEntity>)

    @Query("UPDATE training_modules SET isDownloadedOffline = :isDownloaded, downloadProgress = :progress WHERE id = :id")
    suspend fun updateDownloadStatus(id: String, isDownloaded: Boolean, progress: Float)

    // Progress
    @Query("SELECT * FROM training_progress ORDER BY timestamp DESC")
    fun getAllProgress(): Flow<List<TrainingProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: TrainingProgressEntity)

    // User Profile
    @Query("SELECT * FROM user_martial_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserMartialProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserMartialProfileEntity)

    // Local Partners
    @Query("SELECT * FROM local_partners ORDER BY distanceMiles ASC")
    fun getAllPartners(): Flow<List<LocalPartnerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartners(partners: List<LocalPartnerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartner(partner: LocalPartnerEntity)

    @Query("UPDATE local_partners SET isConnected = :connected WHERE id = :id")
    suspend fun updatePartnerConnection(id: String, connected: Boolean)

    @Query("UPDATE local_partners SET hasSentInvite = :invited WHERE id = :id")
    suspend fun updatePartnerInvite(id: String, invited: Boolean)

    // Coaching Consultations
    @Query("SELECT * FROM coaching_consultations ORDER BY timestamp DESC")
    fun getAllConsultations(): Flow<List<CoachingConsultationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsultation(consultation: CoachingConsultationEntity)
}
