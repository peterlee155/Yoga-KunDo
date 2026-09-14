package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.CoachingConsultationEntity
import com.example.data.model.LocalPartnerEntity
import com.example.data.model.TrainingModuleEntity
import com.example.data.model.TrainingProgressEntity
import com.example.data.model.UserMartialProfileEntity

@Database(
    entities = [
        TrainingModuleEntity::class,
        TrainingProgressEntity::class,
        UserMartialProfileEntity::class,
        LocalPartnerEntity::class,
        CoachingConsultationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingDao(): TrainingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jeet_yoga_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
