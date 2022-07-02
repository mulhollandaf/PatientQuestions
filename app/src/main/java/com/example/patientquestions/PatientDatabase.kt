package com.example.patientquestions

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        PatientEntity::class,
        DoctorEntity::class,
        AppointmentEntity::class,
        DiagnosisEntity::class
    ],
    version = 1
)
abstract class PatientDatabase : RoomDatabase() {

    abstract val patientDao: PatientDao

    companion object {
        const val DATABASE_FILENAME = "patient.db"
    }
}
