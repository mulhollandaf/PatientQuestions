package com.example.patientquestions

import javax.inject.Inject

class QuestionHelper
@Inject constructor(){
    private val question0Text = "Hi [Patient First Name], on a scale of 1-10, would you recommend Dr [Doctor Last Name] to a friend or family member? 1 = Would not recommend, 10 = Would strongly recommend"
    private val question1Text = "Thank you. You were diagnosed with [Diagnosis]. Did Dr [Doctor Last Name] explain how to manage this diagnosis in a way you could understand?"
    private val question2Text = "We appreciate the feedback, one last question: how do you feel about being diagnosed with [Diagnosis]?"

    private val questionTexts = listOf(question0Text, question1Text, question2Text)
    private val answers = listOf(
        (1..10).map { it.toString() },
        listOf("Yes", "No"),
        emptyList()
    )



    fun getAnswers(questionNumber: Int): List<String> {
        return answers[questionNumber]
    }

    fun getQuestionText(questionNumber: Int): String {
        return questionTexts[questionNumber]
    }

    fun getQuestions(): List<String> {
        return questionTexts
    }
}