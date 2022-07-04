package com.example.patientquestions

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test


class PatientRepositoryTests {
    private val mockkDatabase = mockk<PatientDatabaseWrapper>(relaxed = true)
    private val mockkJsonParser = mockk<JsonParser>(relaxed = true)
    private val mockkQuestionHelper = mockk<QuestionHelper>(relaxed = true)
    private val mockkQuestionDataHelper = mockk<QuestionDataHelper>(relaxed = true)
    private val mockJsonConversionHelper = mockk<JsonConversionHelper>(relaxed = true)
    private val sut = PatientRepository(mockkDatabase, mockkJsonParser, mockJsonConversionHelper, mockkQuestionHelper, mockkQuestionDataHelper)

    private val appointmentExternalId = "be142dc6-93bd-11eb-a8b3-0242ac130003"

    @Test
    fun `getQuestion 0`() {
        val questionText = "Hello Android"
        val answers = listOf("1", "2")
        coEvery { mockkQuestionHelper.getQuestionText(0) } returns questionText
        coEvery { mockkQuestionHelper.getAnswers(0) } returns answers

        coEvery { mockkQuestionDataHelper.fillInData(questionText, appointmentExternalId) } returns questionText
        runBlocking {
            val question = sut.getQuestion(0)
            assertEquals(questionText, question.text)
            assertEquals(answers, question.answers)
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

    @Test
    fun `test getSummary`() {
        runBlocking {
            val questions = listOf("1", "2")
            val filledInQuestions = listOf("1a", "2b")
            val answers = arrayOf("Yes", "No")
            val summaryText = "Thanks again! Here’s what we heard:"
            coEvery { mockkQuestionHelper.getQuestions() } returns questions
            coEvery { mockkQuestionDataHelper.fillInData("1", appointmentExternalId) } returns filledInQuestions[0]
            coEvery { mockkQuestionDataHelper.fillInData("2", appointmentExternalId) } returns filledInQuestions[1]
            sut.saveAnswer(0, answers[0])
            sut.saveAnswer(1, answers[1])
            val summary = sut.getSummary()
            assertEquals(summaryText, summary.summaryText)
            assertEquals(filledInQuestions[0], summary.questions[0])
            assertEquals(filledInQuestions[1], summary.questions[1])
            assertEquals(answers[0], summary.answers[0])
            assertEquals(answers[1], summary.answers[1])
        }

    }
}