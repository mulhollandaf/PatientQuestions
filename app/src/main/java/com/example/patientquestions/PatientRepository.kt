package com.example.patientquestions
import android.content.Context
import android.util.Log
import java.util.InputMismatchException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatientRepository
@Inject constructor(
    private val database: PatientDatabaseWrapper,
    private val jsonParser: JsonParser
) {
    private val jsonConversionHelper = JsonConversionHelper(database.getPatientDao())
    private  val questionDataHelper = QuestionDataHelper(database.getPatientDao())

    private val appointmentExternalId = "be142dc6-93bd-11eb-a8b3-0242ac130003"


    suspend fun getQuestion(questionNumber: Int): Question {
        val questionText = getQuestionText(questionNumber)
        val questionFilledText = questionDataHelper.fillInData(questionText, appointmentExternalId)
        return Question(questionFilledText, (1..10).map { it.toString() })
    }

    private fun getQuestionText(questionNumber: Int): String {
        return question0Text
    }

    fun saveAnswer(questionNumber: Int, answer: String) {
        TODO("Not yet implemented")
    }

    suspend fun init(context: Context) {
        val parsedData = jsonParser.loadJson(context)
        convertAndAddToDatabase(parsedData)
    }



    private suspend fun convertAndAddToDatabase(parsedJson: ResourceBundleJson) {
        Log.i("Patient", parsedJson.toString())
        parsedJson.entry.forEach { entryJson ->
            Log.i("Patient", entryJson.toString())
            when (entryJson.resource.resourceType) {
                "Patient" ->
                    savePatient(jsonConversionHelper.convertToPatient(entryJson.resource))
                "Doctor" ->
                    saveDoctor(jsonConversionHelper.convertToDoctor(entryJson.resource))
                "Appointment" ->
                    saveAppointment(jsonConversionHelper.convertToAppointment(entryJson.resource))
                "Diagnosis" ->
                    saveDiagnosis(jsonConversionHelper.convertToDiagnosis(entryJson.resource))
                else -> throw InputMismatchException("Invalid resource type ${entryJson.resource.resourceType}")
            }
        }
    }

    suspend fun saveDiagnosis(diagnosisEntity: DiagnosisEntity) {
        database.getPatientDao().saveDiagnosis(diagnosisEntity)
    }

    suspend fun saveAppointment(appointmentEntity: AppointmentEntity) {
        database.getPatientDao().saveAppointment(appointmentEntity)
    }

    suspend fun saveDoctor(doctorEntity: DoctorEntity) {
        database.getPatientDao().saveDoctor(doctorEntity)
    }

    suspend fun savePatient(patientEntity: PatientEntity) {
        database.getPatientDao().savePatient(patientEntity)
    }

    val question0Text = "Hi [Patient First Name], on a scale of 1-10, would you recommend Dr [Doctor Last Name] to a friend or family member? 1 = Would not recommend, 10 = Would strongly recommend"
    val question1Text = "Thank you. You were diagnosed with [Diagnosis]. Did Dr [Doctor Last Name] explain how to manage this diagnosis in a way you could understand?"
    val question3Text = "We appreciate the feedback, one last question: how do you feel about being diagnosed with [Diagnosis]?"
    val summary = "Thanks again! Here’s what we heard:"
}
