package com.example.patientquestions

import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test

class JsonConversionHelperTests {
    private val mockPatientDatabaseWrapper = mockk<PatientDatabaseWrapper>(relaxed = true)
    private val sut = JsonConversionHelper(mockPatientDatabaseWrapper)
    @Test
    fun `convert patient`() {
        val nameJson = NameJson(
            text = "John Doe",
            family = "Doe",
            given = listOf("John")
        )
        val patientJson = ResourceJson(
            resourceType = "Patient",
            id = "1",
            name = listOf(nameJson),
        )
        val patientEntity = sut.convertToPatient(patientJson)
        assertEquals("1", patientEntity.externalId)
        assertEquals("John", patientEntity.givenName)
        assertEquals("Doe", patientEntity.familyName)
        assertEquals("John Doe", patientEntity.fullName)
    }

    @Test
    fun `convert patient missing data`() {
        val patientJson = ResourceJson(
            resourceType = "Patient",
            id = "1",
            name = null,
        )
        val patientEntity = sut.convertToPatient(patientJson)
        assertEquals("1", patientEntity.externalId)
        assertEquals("", patientEntity.givenName)
        assertEquals("", patientEntity.familyName)
        assertEquals("", patientEntity.fullName)
    }

    @Test
    fun `convert patient missing name list`() {
        val patientJson = ResourceJson(
            resourceType = "Patient",
            id = "1",
            name = listOf(),
        )
        val patientEntity = sut.convertToPatient(patientJson)
        assertEquals("1", patientEntity.externalId)
        assertEquals("", patientEntity.givenName)
        assertEquals("", patientEntity.familyName)
        assertEquals("", patientEntity.fullName)
    }

    @Test
    fun `convert patient missing given names`() {
        val nameJson = NameJson(
            text = "Doe",
            family = "Doe",
            given = listOf()
        )
        val patientJson = ResourceJson(
            resourceType = "Patient",
            id = "1",
            name = listOf(nameJson),
        )
        val patientEntity = sut.convertToPatient(patientJson)
        assertEquals("1", patientEntity.externalId)
        assertEquals("", patientEntity.givenName)
        assertEquals("Doe", patientEntity.familyName)
        assertEquals("Doe", patientEntity.fullName)
    }


    @Test
    fun `convert doctor`() {
        val nameJson = NameJson(
            family = "Doe",
            given = listOf("John")
        )
        val doctorJson = ResourceJson(
            resourceType = "Doctor",
            id = "1",
            name = listOf(nameJson),
        )
        val doctorEntity = sut.convertToDoctor(doctorJson)
        assertEquals("1", doctorEntity.externalId)
        assertEquals("John", doctorEntity.givenName)
        assertEquals("Doe", doctorEntity.familyName)
    }

    @Test
    fun `convert doctor missing data`() {
        val doctorJson = ResourceJson(
            resourceType = "Doctor",
            id = "1",
            name = null,
        )
        val doctorEntity = sut.convertToDoctor(doctorJson)
        assertEquals("1", doctorEntity.externalId)
        assertEquals("", doctorEntity.givenName)
        assertEquals("", doctorEntity.familyName)
    }

    @Test
    fun `convert patient doctor name list`() {
        val doctorJson = ResourceJson(
            resourceType = "Doctor",
            id = "1",
            name = listOf(),
        )
        val doctorEntity = sut.convertToDoctor(doctorJson)
        assertEquals("1", doctorEntity.externalId)
        assertEquals("", doctorEntity.givenName)
        assertEquals("", doctorEntity.familyName)
    }

    @Test
    fun `convert appointment`() {
        val subjectJson = ReferenceJson(
            reference = "Patient/1"
        )
        val actorJson = ReferenceJson(
            reference = "Doctor/2"
        )

        val typeJson = TypeJson(
            text = "visit"
        )

        val appointmentJson = ResourceJson(
            resourceType = "Appointment",
            id = "123",
            type = listOf(typeJson),
            subject = subjectJson,
            actor = actorJson
        )

        coEvery { mockPatientDatabaseWrapper.getPatientDao().getPatientIdByExternalId("1") } returns 1L
        coEvery { mockPatientDatabaseWrapper.getPatientDao().getDoctorIdByExternalId("2") } returns 2L


        runBlocking {
            val appointmentEntity = sut.convertToAppointment(appointmentJson)
            assertEquals("123", appointmentEntity.externalId)
            assertEquals("visit", appointmentEntity.type)
            assertEquals(1, appointmentEntity.patientId)
            assertEquals(2, appointmentEntity.doctorId)
        }
    }

    @Test
    fun `convert appointment no type`() {
        val subjectJson = ReferenceJson(
            reference = "Patient/1"
        )
        val actorJson = ReferenceJson(
            reference = "Doctor/2"
        )

        coEvery { mockPatientDatabaseWrapper.getPatientDao().getPatientIdByExternalId("1") } returns 1L
        coEvery { mockPatientDatabaseWrapper.getPatientDao().getDoctorIdByExternalId("2") } returns 2L

        val appointmentJson = ResourceJson(
            resourceType = "Appointment",
            id = "123",
            type = listOf(),
            subject = subjectJson,
            actor = actorJson
        )
        runBlocking {
            val appointmentEntity = sut.convertToAppointment(appointmentJson)
            assertEquals("", appointmentEntity.type)
        }
    }

    @Test
    fun `convert diagnosis`() {
        val codingJson = ParentCodeJson(
            coding = listOf(CodeJson(
                system = "system",
                code = "code",
                name = "hired"
            )),
        )
        val appointmentJson = ReferenceJson(
            reference = "Appointment/1"
        )

        val diagnosisJson = ResourceJson(
            resourceType = "Appointment",
            id = "123",
            code = codingJson,
            appointment = appointmentJson,
        )

        coEvery { mockPatientDatabaseWrapper.getPatientDao().getAppointmentIdByExternalId("1") } returns 1L

        runBlocking {
            val diagnosisEntity = sut.convertToDiagnosis(diagnosisJson)
            assertEquals("123", diagnosisEntity.externalId)
            assertEquals("hired", diagnosisEntity.codingName)
            assertEquals(1, diagnosisEntity.appointmentId)
        }
    }


}