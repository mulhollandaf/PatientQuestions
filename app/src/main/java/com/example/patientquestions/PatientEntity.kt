package com.example.patientquestions

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class PatientEntity(
    @PrimaryKey
    val id: Long = 0,
    val externalId: String,
    val fullName: String,
    val familyName: String,
    val givenName: String
)

@Entity
data class DoctorEntity(
    @PrimaryKey
    val id: Long = 0,
    val externalId: String,
    val familyName: String,
    val givenName: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = PatientEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("patientId"),
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = DoctorEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("doctorId"),
            onDelete = ForeignKey.CASCADE
        )]
)
data class AppointmentEntity(
    @PrimaryKey
    val id: Long = 0,
    val externalId: String,
    val type: String,
    val patientId: Long,
    val doctorId: Long
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = AppointmentEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("appointmentId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class DiagnosisEntity(
    @PrimaryKey
    val id: Long = 0,
    val externalId: String,
    val codingName: String,
    val appointmentId: Long
)


