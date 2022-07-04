package com.example.patientquestions

data class Question(val text: String, val answers: List<String>)

data class Summary(val summaryText: String, val questions: List<String>, val answers: Array<String>)
