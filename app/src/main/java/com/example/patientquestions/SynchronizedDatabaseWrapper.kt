package com.example.patientquestions

import android.content.Context
import androidx.room.RoomDatabase
import java.util.concurrent.atomic.AtomicReference

abstract class SynchronizedDatabaseWrapper<out T: RoomDatabase>(protected val context: Context) {
    private var _database = AtomicReference<T?>()

    @Synchronized
    fun getDatabase() = _database.get() ?: createAndSetDatabase()

    protected abstract fun createDatabase(): T

    private fun createAndSetDatabase(): T {
        val database = createDatabase()
        _database.set(database)
        return database
    }

}
