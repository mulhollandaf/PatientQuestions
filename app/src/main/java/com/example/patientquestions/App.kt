package com.example.patientquestions

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App
    @Inject constructor(): Application() {
}