package com.example.patientquestions

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.patientquestions.ui.theme.PatientQuestionsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.loadData(baseContext)
            val question = viewModel.getQuestion()
            displayQuestion(question)
        }
        lifecycleScope.launch {
            for (event in viewModel.eventChannel) {
                when (event) {
                    is MainViewModel.Event.NextQuestion -> loadNextQuestion()
                    is MainViewModel.Event.ShowSummary -> loadSummary()
                }
            }
        }

        requestPermissions()
    }

    private fun requestPermissions() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
            }
            }
        }
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION))

    }

    private fun loadSummary() {
        lifecycleScope.launch {
            val summary = viewModel.getSummary()
            displaySummary(summary)
        }
    }

    private fun displaySummary(summary: Summary) {
        setContent {
            PatientQuestionsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DisplaySummary(summary)
                }
            }
        }
    }

    private fun loadNextQuestion() {
        lifecycleScope.launch {
            val question = viewModel.getQuestion()
            displayQuestion(question)
        }
    }

    private fun displayQuestion(question: Question) {
        setContent {
            PatientQuestionsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DisplayQuestion(question)
                }
            }
        }
    }

    @Composable
    fun DisplaySummary(summary: Summary) {
        Column() {
            Text(text = summary.summaryText)
            summary.questions.forEachIndexed { index, question ->
                DisplaySummaryQuestionAnswer(question, summary.answers[index])
            }
        }

    }

    @Composable
    fun DisplaySummaryQuestionAnswer(question: String, answer: String) {
        Text("$question - $answer", modifier = Modifier.padding(16.dp))
    }

    @Composable
    fun DisplayQuestion(question: Question) {
        Column() {
            Text(text = question.text)
            if (question.answers.isEmpty()) {
                DisplayFreeAnswer()
            } else {
                question.answers.forEach {
                    DisplayAnswer(it)
                }
            }
        }

    }

    @Composable
    fun DisplayFreeAnswer() {
        var text by rememberSaveable { mutableStateOf("") }

        Column() {
            TextField(
                value = text,
                onValueChange = {
                    text = it
                },
                label = { Text("Answer") }
            )
            val shape = CircleShape
            TextButton(onClick = {viewModel.recordAnswer(text)},
                Modifier.fillMaxWidth()
                    .border(2.dp, MaterialTheme.colors.secondary, shape)) {
                Text(
                    text = "Submit"
                )
            }
        }

    }

    @Composable
    fun DisplayAnswer(answer: String) {
        val shape = CircleShape
        TextButton(onClick = {viewModel.recordAnswer(answer)},
            Modifier.fillMaxWidth()
                .border(2.dp, MaterialTheme.colors.secondary, shape)
        ) {
            Text(
                text = answer
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        PatientQuestionsTheme {
            DisplayQuestion(Question("Android?", listOf("yes", "no")))
        }
    }
}
