package com.example.patientquestions

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val patientRepository: PatientRepository
    ) : ViewModel() {

    private val _eventChannel = ViewModelChannel<Event>(this)
    val eventChannel: ReceiveChannel<Event> = _eventChannel

    suspend fun loadData(baseContext: Context) {
        patientRepository.init(baseContext)
    }

    var questionNumber = 0
    suspend fun getQuestion(): Question {
        return patientRepository.getQuestion(questionNumber)
    }

    suspend fun getSummary(): Summary {
        return patientRepository.getSummary()
    }

    fun recordAnswer(answer: String) {
        patientRepository.saveAnswer(questionNumber, answer)
        questionNumber++
        if (questionNumber >= patientRepository.nQuestions) {
            _eventChannel.sendAsync(Event.ShowSummary)
        } else {
            _eventChannel.sendAsync(Event.NextQuestion)
        }
    }

    sealed class Event {
        object NextQuestion: Event()
        object ShowSummary: Event()
    }
}

class ViewModelChannel<E>(
    private val viewModel: ViewModel,
    private val channel: Channel<E> = Channel()
) : ReceiveChannel<E> by channel, SendChannel<E> by channel {
    fun sendAsync(element: E) = viewModel.viewModelScope.launch {
        channel.send(element)
    }
}

