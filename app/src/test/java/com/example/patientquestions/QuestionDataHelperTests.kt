package com.example.patientquestions

import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class QuestionDataHelperTests {
    private val mockPatientDatabaseWrapper = mockk<PatientDatabaseWrapper>(relaxed = true)
    private val sut = QuestionDataHelper(mockPatientDatabaseWrapper)
    private val appointmentExternalId = "be142dc6-93bd-11eb-a8b3-0242ac130003"

    @Before
    fun setup() {
        coEvery {
            mockPatientDatabaseWrapper.getPatientDao().getAppointmentByExternalId(appointmentExternalId)
        } returns AppointmentEntity(
            1,
            appointmentExternalId,
            "Endocrinologist visit",
            patientId = 1,
            doctorId = 1
        )

        coEvery {
            mockPatientDatabaseWrapper.getPatientDao().getPatientById(1)
        } returns PatientEntity(
            1,
            "",
            "John Doe",
            "Doe",
            "John"
        )

        coEvery {
            mockPatientDatabaseWrapper.getPatientDao().getDoctorById(1)
        } returns DoctorEntity(
            1,
            "",
            "Kotlin",
            "Jeff",
        )

        coEvery {
            mockPatientDatabaseWrapper.getPatientDao().getDiagnosisByAppointmentId(1)
        } returns DiagnosisEntity(
            1,
            "",
            "Fatigue",
            1,
        )
    }


    @Test
    fun `fill in first name`() {
        val question = "Hello [Patient First Name]"

        runBlocking {
            val filledQuestion = sut.fillInData(question, appointmentExternalId)
            assertEquals(filledQuestion, "Hello John")
        }
    }

    @Test
    fun `fill in doctor name`() {
        val question = "Hello [Doctor Last Name]"

        runBlocking {
            val filledQuestion = sut.fillInData(question, appointmentExternalId)
            assertEquals(filledQuestion, "Hello Kotlin")
        }
    }

    @Test
    fun `fill in diagnosis`() {
        val question = "Hello [Diagnosis]"

        runBlocking {
            val filledQuestion = sut.fillInData(question, appointmentExternalId)
            assertEquals(filledQuestion, "Hello Fatigue")
        }
    }
}