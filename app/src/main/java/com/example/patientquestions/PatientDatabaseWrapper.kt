package com.example.patientquestions

import android.app.Application
import androidx.room.Room
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatientDatabaseWrapper
@Inject constructor(
    context: Application
) : SynchronizedDatabaseWrapper<PatientDatabase>(context)
{
    override fun createDatabase(): PatientDatabase {
        return Room.databaseBuilder(context, PatientDatabase::class.java, PatientDatabase.DATABASE_FILENAME)
            .build()
    }

    fun getPatientDao() = getDatabase().patientDao
}
