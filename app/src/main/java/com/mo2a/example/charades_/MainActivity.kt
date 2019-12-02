package com.mo2a.example.charades_

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    init {
        initializeDatabase()
    }

    private fun initializeDatabase() {
        uiScope.launch {
            initialize()
        }
    }

    private suspend fun initialize(){
        withContext(Dispatchers.IO){
            MovieDatabase.getInstance(application)
        }
    }
}
