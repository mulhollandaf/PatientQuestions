package com.example.patientquestions

class JsonConversionHelper(private val patientDao: PatientDao) {
    fun convertToPatient(resourceJson: ResourceJson): PatientEntity {
        return PatientEntity(
            externalId = resourceJson.id,
            familyName = resourceJson.name?.getOrNull(0)?.family ?: "",
            givenName = resourceJson.name?.getOrNull(0)?.given?.getOrNull(0) ?: "",
            fullName = resourceJson.name?.getOrNull(0)?.text ?: ""
        )
    }

    fun convertToDoctor(resourceJson: ResourceJson): DoctorEntity {
        return DoctorEntity(
            externalId = resourceJson.id,
            familyName = resourceJson.name?.getOrNull(0)?.family ?: "",
            givenName = resourceJson.name?.getOrNull(0)?.given?.getOrNull(0) ?: "",
        )
    }

    suspend fun convertToAppointment(resourceJson: ResourceJson): AppointmentEntity {
        val patientExternalId = afterSlash(resourceJson.subject?.reference ?: "/")
        val doctorExternalId = afterSlash(resourceJson.actor?.reference ?: "/")
        val patientId = patientDao.getPatientIdByExternalId(patientExternalId)
        val doctorId = patientDao.getDoctorIdByExternalId(doctorExternalId)
        return AppointmentEntity(
            externalId = resourceJson.id,
            type = resourceJson.type?.getOrNull(0)?.text ?: "",
            patientId = patientId,
            doctorId = doctorId
        )
    }

    private fun afterSlash(longId: String): String {
        return longId.substringAfter("/")
    }

    suspend fun convertToDiagnosis(resourceJson: ResourceJson): DiagnosisEntity {
        val appointmentExternalId = afterSlash(resourceJson.appointment?.reference ?: "/")
        val appointmentId = patientDao.getAppointmentIdByExternalId(appointmentExternalId)
        return DiagnosisEntity(
            externalId = resourceJson.id,
            appointmentId = appointmentId,
            codingName = resourceJson.code?.coding?.getOrNull(0)?.name ?: ""
        )
    }
}