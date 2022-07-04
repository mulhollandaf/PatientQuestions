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
    private val jsonParser: JsonParser,
    private val jsonConversionHelper: JsonConversionHelper,
    private val questionHelper: QuestionHelper,
    private val questionDataHelper: QuestionDataHelper,
) {
    private val appointmentExternalId = "be142dc6-93bd-11eb-a8b3-0242ac130003"


    private val summary = "Thanks again! Here’s what we heard:"


    val nQuestions = 3

    private val answers = Array(nQuestions) { "" }

    suspend fun getQuestion(questionNumber: Int): Question {
        val questionText = questionHelper.getQuestionText(questionNumber)
        val questionFilledText = questionDataHelper.fillInData(questionText, appointmentExternalId)
        val answers = questionHelper.getAnswers(questionNumber)
        return Question(questionFilledText, answers)
    }

    fun saveAnswer(questionNumber: Int, answer: String) {
        answers[questionNumber] = answer
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

    suspend fun getSummary(): Summary {
        val questions = questionHelper.getQuestions()
            .map { questionDataHelper.fillInData(it, appointmentExternalId) }
        return Summary(summary, questions, answers)
    }
}
