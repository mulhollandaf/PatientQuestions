package com.example.patientquestions

import javax.inject.Inject

class QuestionDataHelper
    @Inject constructor(databaseWrapper: PatientDatabaseWrapper) {
    private val patientDao = databaseWrapper.getPatientDao()
    suspend fun fillInData(text: String, appointmentId: String): String {
        val appointmentEntity = patientDao.getAppointmentByExternalId(appointmentId)
        val patientEntity = patientDao.getPatientById(appointmentEntity.patientId)
        val doctorEntity = patientDao.getDoctorById(appointmentEntity.doctorId)
        val diagnosisEntity = patientDao.getDiagnosisByAppointmentId(appointmentEntity.id)

        return text
            .replace(PATIENT_FIRST, patientEntity.givenName)
            .replace(DOCTOR_LAST, doctorEntity.familyName)
            .replace(DIAGNOSIS, diagnosisEntity.codingName)
    }

    companion object {
        const val PATIENT_FIRST = "[Patient First Name]"
        const val DOCTOR_LAST = "[Doctor Last Name]"
        const val DIAGNOSIS = "[Diagnosis]"
    }
}