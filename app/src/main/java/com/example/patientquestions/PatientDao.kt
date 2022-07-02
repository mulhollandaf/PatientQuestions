package com.example.patientquestions

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PatientDao {
    @Query("SELECT id FROM PatientEntity WHERE externalId = :externalId")
    suspend fun getPatientIdByExternalId(externalId: String): Long

    @Query("SELECT * FROM PatientEntity WHERE id = :id")
    suspend fun getPatientById(id: Long): PatientEntity

    @Query("SELECT id FROM DoctorEntity WHERE externalId = :externalId")
    suspend fun getDoctorIdByExternalId(externalId: String): Long

    @Query("SELECT * FROM DoctorEntity WHERE id = :id")
    suspend fun getDoctorById(id: Long): DoctorEntity

    @Query("SELECT id FROM AppointmentEntity WHERE externalId = :externalId")
    suspend fun getAppointmentIdByExternalId(externalId: String): Long

    @Query("SELECT * FROM AppointmentEntity WHERE externalId = :externalId")
    suspend fun getAppointmentByExternalId(externalId: String): AppointmentEntity

    @Query("SELECT * FROM DiagnosisEntity WHERE appointmentId = :appointmentId")
    suspend fun getDiagnosisByAppointmentId(appointmentId: Long): DiagnosisEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePatient(patientEntity: PatientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDoctor(doctorEntity: DoctorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAppointment(appointmentEntity: AppointmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDiagnosis(diagnosisEntity: DiagnosisEntity)

}
