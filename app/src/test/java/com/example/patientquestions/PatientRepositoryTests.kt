package com.example.patientquestions

import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test


class PatientRepositoryTests {
    private val mockkDatabase = mockk<PatientDatabaseWrapper>(relaxed = true)
    private val mockkJsonParser = mockk<JsonParser>(relaxed = true)
    private val sut = PatientRepository(mockkDatabase, mockkJsonParser)
    @Test
    fun `getQuestion 0`() {
        val questionText = "Hi John, on a scale of 1-10, would you recommend Dr Care to a friend or family member? 1 = Would not recommend, 10 = Would strongly recommend"
        runBlocking {
            val question = sut.getQuestion(0)
            assertEquals(questionText, question.text)
            assertEquals(10, question.answers.size)
        }
    }

    @Test
    fun `test savePatient`() {
        runBlocking {
            val patientEntity = PatientEntity(0, "0", "", "", "")
            sut.savePatient(patientEntity)
            coVerify { mockkDatabase.getPatientDao().savePatient(patientEntity) }
        }
    }

    @Test
    fun `test saveDoctor`() {
        runBlocking {
            val doctorEntity = DoctorEntity(0, "0", "", "")
            sut.saveDoctor(doctorEntity)
            coVerify { mockkDatabase.getPatientDao().saveDoctor(doctorEntity) }
        }
    }

    @Test
    fun `test saveAppointment`() {
        runBlocking {
            val appointmentEntity = AppointmentEntity(0, "0", "", 0, 0)
            sut.saveAppointment(appointmentEntity)
            coVerify { mockkDatabase.getPatientDao().saveAppointment(appointmentEntity) }
        }
    }

    @Test
    fun `test saveDiagnosis`() {
        runBlocking {
            val diagnosisEntity = DiagnosisEntity(0, "0", "", 0)
            sut.saveDiagnosis(diagnosisEntity)
            coVerify { mockkDatabase.getPatientDao().saveDiagnosis(diagnosisEntity) }
        }
    }
}